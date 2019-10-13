package talkapp.org.talkappmobile.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.mappings.SentenceIdMapping;
import talkapp.org.talkappmobile.mappings.SentenceMapping;
import talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.MigrationService;
import talkapp.org.talkappmobile.service.mapper.WordSetMapper;

public class MigrationServiceImpl implements MigrationService {
    private final WordRepetitionProgressDao exerciseDao;
    private final WordSetDao wordSetDao;
    private final SentenceDao sentenceDao;
    private final ObjectMapper mapper;
    private final WordSetMapper wordSetMapper;

    public MigrationServiceImpl(WordRepetitionProgressDao exerciseDao, WordSetDao wordSetDao, SentenceDao sentenceDao, ObjectMapper mapper) {
        this.exerciseDao = exerciseDao;
        this.wordSetDao = wordSetDao;
        this.sentenceDao = sentenceDao;
        this.mapper = mapper;
        this.wordSetMapper = new WordSetMapper(mapper);
    }

    public void doMigration43() {
        List<WordRepetitionProgressMapping> progresses = exerciseDao.findAll();
        for (WordRepetitionProgressMapping progress : progresses) {
            int wordSetId = progress.getWordSetId();
            Word2Tokens word2Tokens;
            try {
                word2Tokens = mapper.readValue(progress.getWordJSON(), Word2Tokens.class);
            } catch (Exception e) {
                continue;
            }

            WordSetMapping mapping = wordSetDao.findById(wordSetId);
            if (mapping == null) {
                continue;
            }
            WordSet wordSet = wordSetMapper.toDto(mapping);
            List<Word2Tokens> words = wordSet.getWords();
            for (int i = 0; i < words.size(); i++) {
                if (word2Tokens.equals(words.get(i))) {
                    progress.setWordIndex(i);
                }
            }

            ArrayList<SentenceIdMapping> sentenceIdMappings = new ArrayList<>();
            if (progress.getSentenceIds() != null) {
                String[] sentenceIds = progress.getSentenceIds().split("#");
                String id = null;
                String word = null;
                int numberOfWords;
                for (int i = 0; i < sentenceIds.length; i++) {
                    if (i == 0) {
                        id = sentenceIds[i];
                    } else if (i == 1) {
                        word = sentenceIds[i];
                    } else if (i % 2 == 0) {
                        String[] split = sentenceIds[i].split(",");
                        numberOfWords = Integer.parseInt(split[0]);
                        sentenceIdMappings.add(new SentenceIdMapping(id, word, numberOfWords));
                        if (split.length > 1) {
                            id = split[1];
                        }
                    } else if (i % 2 == 1) {
                        word = sentenceIds[i];
                    } else {
                        throw new RuntimeException();
                    }
                }
            }
            try {
                progress.setSentenceIds(mapper.writeValueAsString(sentenceIdMappings));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        for (WordRepetitionProgressMapping progress : progresses) {
            exerciseDao.createNewOrUpdate(progress);
        }

        List<SentenceMapping> all = sentenceDao.findAll();
        for (SentenceMapping mapping : all) {
            String sentenceId = mapping.getId();
            String[] split = sentenceId.split("#");
            SentenceIdMapping idMapping = new SentenceIdMapping(split[0], split[1], Integer.valueOf(split[2]));
            try {
                mapping.setId(mapper.writeValueAsString(idMapping));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        sentenceDao.save(all);
    }

    public void doMigration44() {
        List<SentenceMapping> all = sentenceDao.findAll();
        for (SentenceMapping mapping : all) {
            String sentenceId = mapping.getId();
            try {
                mapper.readValue(sentenceId, SentenceIdMapping.class);
            } catch (IOException e) {
                sentenceDao.deleteById(mapping.getId());
            }
        }
    }

    @Override
    public void migrate(int oldVer) {
        switch (oldVer) {
            case 43: {
                doMigration43();
                break;
            }
            case 44: {
                doMigration44();
                break;
            }
            default:
        }
    }
}
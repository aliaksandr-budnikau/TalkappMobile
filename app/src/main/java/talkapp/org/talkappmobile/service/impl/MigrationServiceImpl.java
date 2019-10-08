package talkapp.org.talkappmobile.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import talkapp.org.talkappmobile.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.MigrationService;
import talkapp.org.talkappmobile.service.mapper.WordSetMapper;

public class MigrationServiceImpl implements MigrationService {
    private final WordRepetitionProgressDao exerciseDao;
    private final WordSetDao wordSetDao;
    private final ObjectMapper mapper;
    private final WordSetMapper wordSetMapper;

    public MigrationServiceImpl(WordRepetitionProgressDao exerciseDao, WordSetDao wordSetDao, ObjectMapper mapper) {
        this.exerciseDao = exerciseDao;
        this.wordSetDao = wordSetDao;
        this.mapper = mapper;
        this.wordSetMapper = new WordSetMapper(mapper);
    }

    public void doMigration40() {
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
        }

        for (WordRepetitionProgressMapping progress : progresses) {
            exerciseDao.createNewOrUpdate(progress);
        }
    }

    @Override
    public void migrate(int oldVer) {
        switch (oldVer) {
            case 40: {
                doMigration40();
            }
            default:
        }
    }
}
package talkapp.org.talkappmobile.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.mappings.SentenceIdMapping;
import talkapp.org.talkappmobile.mappings.SentenceMapping;
import talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.repository.MigrationService;

import static java.util.Arrays.asList;

public class MigrationServiceImpl implements MigrationService {
    private final WordRepetitionProgressDao exerciseDao;
    private final WordSetDao wordSetDao;
    private final SentenceDao sentenceDao;
    private final ObjectMapper mapper;
    private final WordSetMapper wordSetMapper;
    private final SentenceMapper sentenceMapper;

    public MigrationServiceImpl(WordRepetitionProgressDao exerciseDao, WordSetDao wordSetDao, SentenceDao sentenceDao, ObjectMapper mapper) {
        this.exerciseDao = exerciseDao;
        this.wordSetDao = wordSetDao;
        this.sentenceDao = sentenceDao;
        this.mapper = mapper;
        this.wordSetMapper = new WordSetMapper(mapper);
        this.sentenceMapper = new SentenceMapper(mapper);
    }

    @Override
    public void migrate(int oldVer) {
        switch (oldVer) {
            case 48: {
                doMigration48();
                break;
            }
            case 49: {
                doMigration49();
                break;
            }
            case 52: {
                doMigration52();
                break;
            }
            default:
        }
    }

    private void doMigration52() {
        List<SentenceMapping> all = sentenceDao.findAll();
        for (SentenceMapping mapping : all) {
            try {
                SentenceIdMapping sentenceIdMappings = mapper.readValue(mapping.getId(), SentenceIdMapping.class);
                sentenceDao.deleteById(mapping.getId());
            } catch (IOException e) {

            }
        }
    }

    private void doMigration49() {
        List<WordRepetitionProgressMapping> all = exerciseDao.findAll();
        for (WordRepetitionProgressMapping mapping : all) {
            int oldId = mapping.getId();
            List<SentenceIdMapping> sentenceIdMappings = null;
            try {
                sentenceIdMappings = sentenceMapper.toSentenceIdMapping(mapping.getSentenceIds());
            } catch (Exception e) {
                sentenceIdMappings = new ArrayList<>();
            }
            List<String> result = new LinkedList<>();
            for (SentenceIdMapping sentenceIdMapping : sentenceIdMappings) {
                result.add(sentenceIdMapping.getSentenceId());
            }
            try {
                mapping.setSentenceIds(mapper.writeValueAsString(result));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            exerciseDao.createNewOrUpdate(mapping);
        }
    }

    private void doMigration48() {
        List<SentenceMapping> all = sentenceDao.findAll();
        for (SentenceMapping mapping : all) {
            SentenceIdMapping sentenceIdMappings = null;
            try {
                sentenceIdMappings = mapper.readValue(mapping.getId(), SentenceIdMapping.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String sentenceId = sentenceIdMappings.getSentenceId();
            mapping.setId(sentenceId);
            sentenceDao.save(asList(mapping));
        }
    }
}
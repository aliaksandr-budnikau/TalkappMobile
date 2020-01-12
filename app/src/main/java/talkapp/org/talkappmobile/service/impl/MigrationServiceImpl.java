package talkapp.org.talkappmobile.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.dao.WordSetDao;
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

    @Override
    public void migrate(int oldVer) {
        switch (oldVer) {
            case 43: {
                //doMigration43();
                break;
            }
            case 44: {
                //doMigration44();
                break;
            }
            default:
        }
    }
}
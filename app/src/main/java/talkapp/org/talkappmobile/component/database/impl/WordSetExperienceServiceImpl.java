package talkapp.org.talkappmobile.component.database.impl;

import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.database.DatabaseHelper;
import talkapp.org.talkappmobile.component.database.WordSetExperienceService;
import talkapp.org.talkappmobile.component.database.dao.WordSetDao;
import talkapp.org.talkappmobile.component.database.dao.WordSetExperienceDao;
import talkapp.org.talkappmobile.component.database.mappings.WordSetExperienceMapping;
import talkapp.org.talkappmobile.component.database.mappings.local.WordSetMapping;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;
import talkapp.org.talkappmobile.model.WordSetExperienceStatus;

import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.FIRST_CYCLE;

public class WordSetExperienceServiceImpl implements WordSetExperienceService {
    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private final WordSetExperienceDao experienceDao;
    private final WordSetDao wordSetDao;
    private final Logger logger;

    public WordSetExperienceServiceImpl(WordSetExperienceDao experienceDao, WordSetDao wordSetDao, Logger logger) {
        this.experienceDao = experienceDao;
        this.wordSetDao = wordSetDao;
        this.logger = logger;
    }

    @Override
    public WordSetExperience findById(int id) {
        WordSetExperienceMapping mapping = experienceDao.findById(id);
        WordSetMapping wordSetMapping = wordSetDao.findById(id);
        if (mapping == null || wordSetMapping == null) {
            return null;
        }

        return toDto(mapping, wordSetMapping);
    }

    @Override
    public WordSetExperience createNew(WordSet wordSet) {
        WordSetExperienceMapping mapping = new WordSetExperienceMapping();
        mapping.setStatus(FIRST_CYCLE);
        mapping.setId(wordSet.getId());
        mapping.setMaxTrainingExperience(wordSet.getWords().size() * 2);
        experienceDao.createNewOrUpdate(mapping);

        WordSetMapping wordSetMapping = wordSetDao.findById(wordSet.getId());
        wordSetMapping.setTrainingExperience(0);
        wordSetDao.createNewOrUpdate(wordSetMapping);

        return toDto(mapping, wordSetMapping);
    }

    @Override
    public WordSetExperience increaseExperience(int id, int value) {
        WordSetExperienceMapping mapping = experienceDao.findById(id);
        WordSetMapping wordSetMapping = wordSetDao.findById(id);
        int experience = wordSetMapping.getTrainingExperience() + value;
        if (experience > mapping.getMaxTrainingExperience()) {
            logger.w(TAG, "Experience {} + value {} > then max value!", mapping, value);
            wordSetMapping.setTrainingExperience(mapping.getMaxTrainingExperience());
        } else {
            wordSetMapping.setTrainingExperience(experience);
        }
        wordSetDao.createNewOrUpdate(wordSetMapping);
        return toDto(mapping, wordSetMapping);
    }

    @Override
    public WordSetExperience moveToAnotherState(int id, WordSetExperienceStatus value) {
        WordSetExperienceMapping mapping = experienceDao.findById(id);
        mapping.setStatus(value);
        experienceDao.createNewOrUpdate(mapping);
        WordSetMapping wordSetMapping = wordSetDao.findById(id);
        return toDto(mapping, wordSetMapping);
    }

    private WordSetExperience toDto(WordSetExperienceMapping mapping, WordSetMapping wordSetMapping) {
        WordSetExperience wordSetExperience = new WordSetExperience();
        wordSetExperience.setId(mapping.getId());
        wordSetExperience.setStatus(mapping.getStatus());
        wordSetExperience.setTrainingExperience(wordSetMapping.getTrainingExperience());
        wordSetExperience.setMaxTrainingExperience(mapping.getMaxTrainingExperience());
        return wordSetExperience;
    }
}
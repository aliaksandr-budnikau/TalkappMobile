package talkapp.org.talkappmobile.component.database.impl;

import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.database.DatabaseHelper;
import talkapp.org.talkappmobile.component.database.WordSetExperienceRepository;
import talkapp.org.talkappmobile.component.database.dao.WordSetExperienceDao;
import talkapp.org.talkappmobile.component.database.mappings.WordSetExperienceMapping;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;
import talkapp.org.talkappmobile.model.WordSetExperienceStatus;

import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.REPETITION;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.STUDYING;

public class WordSetExperienceRepositoryImpl implements WordSetExperienceRepository {
    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private final WordSetExperienceDao experienceDao;
    private final Logger logger;

    public WordSetExperienceRepositoryImpl(WordSetExperienceDao experienceDao, Logger logger) {
        this.experienceDao = experienceDao;
        this.logger = logger;
    }

    @Override
    public WordSetExperience findById(String id) {
        WordSetExperienceMapping mapping = experienceDao.findById(id);
        if (mapping == null) {
            return null;
        }
        return toDto(mapping);
    }

    @Override
    public WordSetExperience createNew(WordSet wordSet) {
        WordSetExperienceMapping mapping = new WordSetExperienceMapping();
        mapping.setStatus(STUDYING);
        mapping.setId(wordSet.getId());
        mapping.setTrainingExperience(0);
        mapping.setMaxTrainingExperience(wordSet.getWords().size() * 2);

        experienceDao.createNewOrUpdate(mapping);
        return toDto(mapping);
    }

    @Override
    public WordSetExperience increaseExperience(String id, int value) {
        WordSetExperienceMapping mapping = experienceDao.findById(id);
        int experience = mapping.getTrainingExperience() + value;
        if (experience > mapping.getMaxTrainingExperience()) {
            logger.w(TAG, "Experience {} + value {} > then max value!", mapping, value);
            mapping.setTrainingExperience(mapping.getMaxTrainingExperience());
        } else {
            mapping.setTrainingExperience(experience);
        }
        experienceDao.createNewOrUpdate(mapping);
        return toDto(mapping);
    }

    @Override
    public WordSetExperience moveToAnotherState(String id, WordSetExperienceStatus value) {
        WordSetExperienceMapping mapping = experienceDao.findById(id);
        mapping.setStatus(value);
        experienceDao.createNewOrUpdate(mapping);
        return toDto(mapping);
    }

    private WordSetExperience toDto(WordSetExperienceMapping mapping) {
        WordSetExperience wordSetExperience = new WordSetExperience();
        wordSetExperience.setId(mapping.getId());
        wordSetExperience.setStatus(mapping.getStatus());
        wordSetExperience.setTrainingExperience(mapping.getTrainingExperience());
        wordSetExperience.setMaxTrainingExperience(mapping.getMaxTrainingExperience());
        return wordSetExperience;
    }
}
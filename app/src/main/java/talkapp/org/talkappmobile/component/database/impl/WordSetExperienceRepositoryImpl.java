package talkapp.org.talkappmobile.component.database.impl;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.database.DatabaseHelper;
import talkapp.org.talkappmobile.component.database.WordSetExperienceRepository;
import talkapp.org.talkappmobile.component.database.dao.WordSetExperienceDao;
import talkapp.org.talkappmobile.component.database.mappings.WordSetExperienceMapping;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

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
    public List<WordSetExperience> findAll() {
        List<WordSetExperienceMapping> all = experienceDao.findAll();
        LinkedList<WordSetExperience> result = new LinkedList<>();
        for (WordSetExperienceMapping wordSetExperienceMapping : all) {
            result.addLast(toDto(wordSetExperienceMapping));
        }
        return result;
    }

    @Override
    public WordSetExperience createNew(WordSet wordSet) {
        WordSetExperienceMapping mapping = new WordSetExperienceMapping();
        mapping.setStatus(STUDYING);
        mapping.setWordSetId(wordSet.getId());
        mapping.setTrainingExperience(0);
        mapping.setMaxTrainingExperience(wordSet.getWords().size() * 2);

        experienceDao.createNewOrUpdate(mapping);
        return toDto(mapping);
    }

    @Override
    public int increaseExperience(int id, int value) {
        WordSetExperienceMapping mapping = experienceDao.findById(id);
        int experience = mapping.getTrainingExperience() + value;
        if (experience > mapping.getMaxTrainingExperience()) {
            logger.w(TAG, "Experience {} + value {} > then max value!", mapping, value);
            mapping.setTrainingExperience(mapping.getMaxTrainingExperience());
        } else {
            mapping.setTrainingExperience(experience);
        }
        experienceDao.createNewOrUpdate(mapping);
        return experience;
    }

    private WordSetExperience toDto(WordSetExperienceMapping mapping) {
        WordSetExperience wordSetExperience = new WordSetExperience();
        wordSetExperience.setWordSetId(mapping.getWordSetId());
        wordSetExperience.setStatus(mapping.getStatus());
        wordSetExperience.setTrainingExperience(mapping.getTrainingExperience());
        wordSetExperience.setMaxTrainingExperience(mapping.getMaxTrainingExperience());
        return wordSetExperience;
    }
}
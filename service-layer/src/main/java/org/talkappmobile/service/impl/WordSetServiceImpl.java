package org.talkappmobile.service.impl;

import org.talkappmobile.dao.WordSetDao;
import org.talkappmobile.mappings.WordSetMapping;
import org.talkappmobile.model.WordSet;
import org.talkappmobile.model.WordSetProgressStatus;
import org.talkappmobile.service.WordSetExperienceUtils;
import org.talkappmobile.service.WordSetService;

import static org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;

public class WordSetServiceImpl implements WordSetService {
    private final int CUSTOM_WORD_SETS_STARTS_SINCE = 1000000;
    private final WordSetDao wordSetDao;
    private final WordSetExperienceUtils experienceUtils;

    public WordSetServiceImpl(WordSetDao wordSetDao, WordSetExperienceUtils experienceUtils) {
        this.wordSetDao = wordSetDao;
        this.experienceUtils = experienceUtils;
    }

    @Override
    public void resetProgress(WordSet wordSet) {
        WordSetMapping wordSetMapping = wordSetDao.findById(wordSet.getId());
        wordSetMapping.setTrainingExperience(0);
        wordSetMapping.setStatus(FIRST_CYCLE.name());
        wordSetDao.createNewOrUpdate(wordSetMapping);
    }

    @Override
    public int increaseExperience(WordSet wordSet, int value) {
        WordSetMapping wordSetMapping = wordSetDao.findById(wordSet.getId());
        int experience = wordSetMapping.getTrainingExperience() + value;
        if (experience > experienceUtils.getMaxTrainingProgress(wordSet)) {
            wordSetMapping.setTrainingExperience(experienceUtils.getMaxTrainingProgress(wordSet));
        } else {
            wordSetMapping.setTrainingExperience(experience);
        }
        wordSetDao.createNewOrUpdate(wordSetMapping);
        return wordSetMapping.getTrainingExperience();
    }

    @Override
    public void moveToAnotherState(int id, WordSetProgressStatus value) {
        WordSetMapping wordSetMapping = wordSetDao.findById(id);
        wordSetMapping.setStatus(value.name());
        wordSetDao.createNewOrUpdate(wordSetMapping);
    }

    @Override
    public void remove(WordSet wordSet) {
        wordSetDao.removeById(wordSet.getId());
    }

    @Override
    public int getCustomWordSetsStartsSince() {
        return CUSTOM_WORD_SETS_STARTS_SINCE;
    }
}
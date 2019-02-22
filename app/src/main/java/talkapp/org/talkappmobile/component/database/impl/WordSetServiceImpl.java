package talkapp.org.talkappmobile.component.database.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import talkapp.org.talkappmobile.component.WordSetExperienceUtils;
import talkapp.org.talkappmobile.component.database.WordSetMapper;
import talkapp.org.talkappmobile.component.database.WordSetService;
import talkapp.org.talkappmobile.component.database.dao.WordSetDao;
import talkapp.org.talkappmobile.component.database.mappings.local.WordSetMapping;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperienceStatus;

import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.FIRST_CYCLE;

public class WordSetServiceImpl implements WordSetService {
    private final WordSetDao wordSetDao;
    private final WordSetMapper wordSetMapper;
    private final WordSetExperienceUtils experienceUtils;

    public WordSetServiceImpl(WordSetDao wordSetDao, WordSetExperienceUtils experienceUtils, ObjectMapper mapper) {
        this.wordSetDao = wordSetDao;
        this.experienceUtils = experienceUtils;
        this.wordSetMapper = new WordSetMapper(mapper);
    }

    @Override
    public WordSet findById(int id) {
        WordSetMapping wordSetMapping = wordSetDao.findById(id);
        if (wordSetMapping == null) {
            return null;
        }
        return wordSetMapper.toDto(wordSetMapping);
    }

    @Override
    public void resetProgress(WordSet wordSet) {
        WordSetMapping wordSetMapping = wordSetDao.findById(wordSet.getId());
        wordSetMapping.setTrainingExperience(0);
        wordSetMapping.setStatus(FIRST_CYCLE);
        wordSetDao.createNewOrUpdate(wordSetMapping);
    }

    @Override
    public WordSet increaseExperience(WordSet wordSet, int value) {
        WordSetMapping wordSetMapping = wordSetDao.findById(wordSet.getId());
        int experience = wordSetMapping.getTrainingExperience() + value;
        if (experience > experienceUtils.getMaxTrainingProgress(wordSet)) {
            wordSetMapping.setTrainingExperience(experienceUtils.getMaxTrainingProgress(wordSet));
            wordSet.setTrainingExperience(experienceUtils.getMaxTrainingProgress(wordSet));
        } else {
            wordSetMapping.setTrainingExperience(experience);
            wordSet.setTrainingExperience(experience);
        }
        wordSetDao.createNewOrUpdate(wordSetMapping);
        return wordSetMapper.toDto(wordSetMapping);
    }

    @Override
    public WordSet moveToAnotherState(int id, WordSetExperienceStatus value) {
        WordSetMapping wordSetMapping = wordSetDao.findById(id);
        wordSetMapping.setStatus(value);
        wordSetDao.createNewOrUpdate(wordSetMapping);
        return wordSetMapper.toDto(wordSetMapping);
    }
}
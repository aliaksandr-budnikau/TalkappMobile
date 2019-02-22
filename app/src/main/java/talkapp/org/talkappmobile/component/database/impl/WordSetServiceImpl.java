package talkapp.org.talkappmobile.component.database.impl;

import talkapp.org.talkappmobile.component.database.WordSetService;
import talkapp.org.talkappmobile.component.database.dao.WordSetDao;
import talkapp.org.talkappmobile.component.database.mappings.local.WordSetMapping;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperienceStatus;

import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.FIRST_CYCLE;

public class WordSetServiceImpl implements WordSetService {
    private final WordSetDao wordSetDao;

    public WordSetServiceImpl(WordSetDao wordSetDao) {
        this.wordSetDao = wordSetDao;
    }

    @Override
    public WordSet findById(int id) {
        WordSetMapping wordSetMapping = wordSetDao.findById(id);
        if (wordSetMapping == null) {
            return null;
        }
        return toDto(wordSetMapping);
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
        if (experience > getMaxTrainingProgress(wordSet)) {
            wordSetMapping.setTrainingExperience(getMaxTrainingProgress(wordSet));
            wordSet.setTrainingExperience(getMaxTrainingProgress(wordSet));
        } else {
            wordSetMapping.setTrainingExperience(experience);
            wordSet.setTrainingExperience(experience);
        }
        wordSetDao.createNewOrUpdate(wordSetMapping);
        return toDto(wordSetMapping);
    }

    @Override
    public WordSet moveToAnotherState(int id, WordSetExperienceStatus value) {
        WordSetMapping wordSetMapping = wordSetDao.findById(id);
        wordSetMapping.setStatus(value);
        wordSetDao.createNewOrUpdate(wordSetMapping);
        return toDto(wordSetMapping);
    }

    @Override
    public int getMaxTrainingProgress(WordSet wordSet) {
        return wordSet.getWords().size() * 2;
    }

    private WordSet toDto(WordSetMapping mapping) {
        WordSet wordSet = new WordSet();
        wordSet.setId(Integer.valueOf(mapping.getId()));
        wordSet.setTopicId(mapping.getTopicId());
        wordSet.setTop(mapping.getTop());
        wordSet.setTrainingExperience(mapping.getTrainingExperience());
        wordSet.setStatus(mapping.getStatus());
        return wordSet;
    }
}
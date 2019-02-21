package talkapp.org.talkappmobile.component.database.impl;

import talkapp.org.talkappmobile.component.database.WordSetService;
import talkapp.org.talkappmobile.component.database.dao.WordSetDao;
import talkapp.org.talkappmobile.component.database.mappings.local.WordSetMapping;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;
import talkapp.org.talkappmobile.model.WordSetExperienceStatus;

import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.FIRST_CYCLE;

public class WordSetServiceImpl implements WordSetService {
    private final WordSetDao wordSetDao;

    public WordSetServiceImpl(WordSetDao wordSetDao) {
        this.wordSetDao = wordSetDao;
    }

    @Override
    public WordSetExperience findById(int id) {
        WordSetMapping wordSetMapping = wordSetDao.findById(id);
        if (wordSetMapping == null) {
            return null;
        }
        return toDto(wordSetMapping);
    }

    @Override
    public WordSetExperience createNew(WordSet wordSet) {
        WordSetMapping wordSetMapping = wordSetDao.findById(wordSet.getId());
        wordSetMapping.setTrainingExperience(0);
        wordSetMapping.setMaxTrainingExperience(wordSet.getWords().size() * 2);
        wordSetMapping.setStatus(FIRST_CYCLE);
        wordSetDao.createNewOrUpdate(wordSetMapping);
        wordSet.setMaxTrainingExperience(wordSet.getWords().size() * 2);
        wordSet.setStatus(FIRST_CYCLE);
        return toDto(wordSetMapping);
    }

    @Override
    public WordSetExperience increaseExperience(WordSet wordSet, int value) {
        WordSetMapping wordSetMapping = wordSetDao.findById(wordSet.getId());
        int experience = wordSetMapping.getTrainingExperience() + value;
        if (experience > wordSetMapping.getMaxTrainingExperience()) {
            wordSetMapping.setTrainingExperience(wordSetMapping.getMaxTrainingExperience());
            wordSet.setTrainingExperience(wordSetMapping.getMaxTrainingExperience());
        } else {
            wordSetMapping.setTrainingExperience(experience);
            wordSet.setTrainingExperience(experience);
        }
        wordSetDao.createNewOrUpdate(wordSetMapping);
        return toDto(wordSetMapping);
    }

    @Override
    public WordSetExperience moveToAnotherState(int id, WordSetExperienceStatus value) {
        WordSetMapping wordSetMapping = wordSetDao.findById(id);
        wordSetMapping.setStatus(value);
        wordSetDao.createNewOrUpdate(wordSetMapping);
        return toDto(wordSetMapping);
    }

    private WordSetExperience toDto(WordSetMapping wordSetMapping) {
        WordSetExperience wordSetExperience = new WordSetExperience();
        wordSetExperience.setId(Integer.parseInt(wordSetMapping.getId()));
        return wordSetExperience;
    }
}
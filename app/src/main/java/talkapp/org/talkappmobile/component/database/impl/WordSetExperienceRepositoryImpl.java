package talkapp.org.talkappmobile.component.database.impl;

import talkapp.org.talkappmobile.component.database.WordSetExperienceRepository;
import talkapp.org.talkappmobile.component.database.dao.WordSetExperienceDao;
import talkapp.org.talkappmobile.component.database.mappings.WordSetExperienceMapping;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.STUDYING;

public class WordSetExperienceRepositoryImpl implements WordSetExperienceRepository {
    private final WordSetExperienceDao experienceDao;

    public WordSetExperienceRepositoryImpl(WordSetExperienceDao experienceDao) {
        this.experienceDao = experienceDao;
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

    private WordSetExperience toDto(WordSetExperienceMapping mapping) {
        WordSetExperience wordSetExperience = new WordSetExperience();
        wordSetExperience.setId(mapping.getId());
        wordSetExperience.setWordSetId(mapping.getWordSetId());
        wordSetExperience.setStatus(mapping.getStatus());
        wordSetExperience.setTrainingExperience(mapping.getTrainingExperience());
        wordSetExperience.setMaxTrainingExperience(mapping.getMaxTrainingExperience());
        return wordSetExperience;
    }
}
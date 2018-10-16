package talkapp.org.talkappmobile.component.database.dao;

import talkapp.org.talkappmobile.component.database.mappings.WordSetExperienceMapping;

public interface WordSetExperienceDao {

    void createNewOrUpdate(WordSetExperienceMapping experience);

    WordSetExperienceMapping findById(String id);
}
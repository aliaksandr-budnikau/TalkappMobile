package talkapp.org.talkappmobile.component.database.dao;

import java.util.List;

import talkapp.org.talkappmobile.component.database.mappings.WordSetExperienceMapping;

public interface WordSetExperienceDao {

    void createNewOrUpdate(WordSetExperienceMapping experience);

    WordSetExperienceMapping findById(int id);

    List<WordSetExperienceMapping> findAll();
}
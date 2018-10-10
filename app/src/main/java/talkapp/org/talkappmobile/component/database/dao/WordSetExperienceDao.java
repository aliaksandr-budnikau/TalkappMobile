package talkapp.org.talkappmobile.component.database.dao;

import com.j256.ormlite.dao.Dao;

import talkapp.org.talkappmobile.component.database.mappings.WordSetExperienceMapping;

public interface WordSetExperienceDao {

    Dao.CreateOrUpdateStatus createNewOrUpdate(WordSetExperienceMapping experience);
}
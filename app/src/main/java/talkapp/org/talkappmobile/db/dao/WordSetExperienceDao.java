package talkapp.org.talkappmobile.db.dao;

import com.j256.ormlite.dao.Dao;

import talkapp.org.talkappmobile.db.mappings.WordSetExperienceMapping;

public interface WordSetExperienceDao {

    Dao.CreateOrUpdateStatus createNewOrUpdate(WordSetExperienceMapping experience);
}
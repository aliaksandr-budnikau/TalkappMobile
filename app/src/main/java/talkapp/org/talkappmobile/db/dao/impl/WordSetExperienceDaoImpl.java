package talkapp.org.talkappmobile.db.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import talkapp.org.talkappmobile.db.dao.WordSetExperienceDao;
import talkapp.org.talkappmobile.db.mappings.WordSetExperienceMapping;

public class WordSetExperienceDaoImpl extends BaseDaoImpl<WordSetExperienceMapping, Integer> implements WordSetExperienceDao {

    public WordSetExperienceDaoImpl(ConnectionSource connectionSource, Class<WordSetExperienceMapping> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    @Override
    public Dao.CreateOrUpdateStatus createNewOrUpdate(WordSetExperienceMapping experience) {
        try {
            return super.createOrUpdate(experience);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
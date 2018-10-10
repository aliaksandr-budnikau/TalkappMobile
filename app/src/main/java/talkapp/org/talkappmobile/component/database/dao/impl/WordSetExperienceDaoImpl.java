package talkapp.org.talkappmobile.component.database.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import talkapp.org.talkappmobile.component.database.dao.WordSetExperienceDao;
import talkapp.org.talkappmobile.component.database.mappings.WordSetExperienceMapping;

public class WordSetExperienceDaoImpl extends BaseDaoImpl<WordSetExperienceMapping, Integer> implements WordSetExperienceDao {

    public WordSetExperienceDaoImpl(ConnectionSource connectionSource, Class<WordSetExperienceMapping> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    @Override
    public void createNewOrUpdate(WordSetExperienceMapping experience) {
        try {
            this.createOrUpdate(experience);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public WordSetExperienceMapping findById(int id) {
        try {
            return this.queryForId(id);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
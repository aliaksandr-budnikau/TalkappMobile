package talkapp.org.talkappmobile.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import talkapp.org.talkappmobile.dao.CurrentWordSetDao;
import talkapp.org.talkappmobile.mappings.CurrentWordSetMapping;

public class CurrentWordSetDaoImpl extends BaseDaoImpl<CurrentWordSetMapping, String> implements CurrentWordSetDao {

    public CurrentWordSetDaoImpl(ConnectionSource connectionSource, Class<CurrentWordSetMapping> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    @Override
    public CurrentWordSetMapping getById(String id) {
        try {
            return this.queryForId(id);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void save(CurrentWordSetMapping mapping) {
        try {
            this.createOrUpdate(mapping);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
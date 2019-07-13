package talkapp.org.talkappmobile.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import talkapp.org.talkappmobile.dao.NewWordSetDraftDao;
import talkapp.org.talkappmobile.mappings.NewWordSetDraftMapping;

public class NewWordSetDraftDaoImpl extends BaseDaoImpl<NewWordSetDraftMapping, Integer> implements NewWordSetDraftDao {

    public NewWordSetDraftDaoImpl(ConnectionSource connectionSource, Class<NewWordSetDraftMapping> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    @Override
    public NewWordSetDraftMapping getNewWordSetDraftById(int id) {
        try {
            return this.queryForId(id);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void createNewOrUpdate(NewWordSetDraftMapping mapping) {
        try {
            this.createOrUpdate(mapping);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
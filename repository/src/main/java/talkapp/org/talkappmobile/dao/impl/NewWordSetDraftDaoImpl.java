package talkapp.org.talkappmobile.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;

import java.sql.SQLException;

import javax.inject.Inject;

import talkapp.org.talkappmobile.dao.DatabaseHelper;
import talkapp.org.talkappmobile.dao.NewWordSetDraftDao;
import talkapp.org.talkappmobile.mappings.NewWordSetDraftMapping;

public class NewWordSetDraftDaoImpl implements NewWordSetDraftDao {

    private final BaseDaoImpl<NewWordSetDraftMapping, Integer> dao;

    @Inject
    public NewWordSetDraftDaoImpl(DatabaseHelper databaseHelper) {
        try {
            dao = new BaseDaoImpl<NewWordSetDraftMapping, Integer>(databaseHelper.getConnectionSource(), NewWordSetDraftMapping.class) {
            };
            dao.initialize();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public NewWordSetDraftMapping getNewWordSetDraftById(int id) {
        try {
            NewWordSetDraftMapping mapping = dao.queryForId(id);
            if (mapping == null) {
                mapping = new NewWordSetDraftMapping();
                mapping.setWords("");
            }
            return mapping;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void createNewOrUpdate(NewWordSetDraftMapping mapping) {
        try {
            dao.createOrUpdate(mapping);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
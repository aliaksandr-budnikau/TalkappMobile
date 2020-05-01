package talkapp.org.talkappmobile.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.dao.DatabaseHelper;
import talkapp.org.talkappmobile.dao.TopicDao;
import talkapp.org.talkappmobile.mappings.TopicMapping;

public class TopicDaoImpl implements TopicDao {

    private final BaseDaoImpl<TopicMapping, Integer> dao;
    private List<TopicMapping> topics = new LinkedList<>();

    @Inject
    public TopicDaoImpl(DatabaseHelper databaseHelper) {
        try {
            dao = new BaseDaoImpl<TopicMapping, Integer>(databaseHelper.getConnectionSource(), TopicMapping.class) {
            };
            dao.initialize();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<TopicMapping> findAll() {
        if (topics != null && !topics.isEmpty()) {
            return topics;
        }
        List<TopicMapping> mappings = null;
        try {
            mappings = dao.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        topics = mappings;
        return topics;
    }

    @Override
    public void save(List<TopicMapping> mappings) {
        if (topics != null && !topics.isEmpty()) {
            return;
        }
        for (TopicMapping mapping : mappings) {
            try {
                dao.createOrUpdate(mapping);
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        topics = mappings;
    }
}
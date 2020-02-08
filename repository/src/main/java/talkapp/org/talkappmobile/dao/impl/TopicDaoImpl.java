package talkapp.org.talkappmobile.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import talkapp.org.talkappmobile.dao.TopicDao;
import talkapp.org.talkappmobile.mappings.TopicMapping;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class TopicDaoImpl extends BaseDaoImpl<TopicMapping, Integer> implements TopicDao {

    private List<TopicMapping> topics = new LinkedList<>();

    public TopicDaoImpl(ConnectionSource connectionSource, Class<TopicMapping> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    @Override
    public List<TopicMapping> findAll() {
        if (topics != null && !topics.isEmpty()) {
            return topics;
        }
        List<TopicMapping> mappings = null;
        try {
            mappings = this.queryForAll();
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
                super.createOrUpdate(mapping);
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        topics = mappings;
    }
}
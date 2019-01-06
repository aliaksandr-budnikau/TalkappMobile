package talkapp.org.talkappmobile.component.database.dao.impl.local;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

import talkapp.org.talkappmobile.component.database.dao.TopicDao;
import talkapp.org.talkappmobile.component.database.mappings.local.TopicMapping;

public class TopicDaoImpl extends BaseDaoImpl<TopicMapping, Integer> implements TopicDao {

    public TopicDaoImpl(ConnectionSource connectionSource, Class<TopicMapping> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    @Override
    public List<TopicMapping> findAll() {
        try {
            return this.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void save(List<TopicMapping> mappings) {
        for (TopicMapping mapping : mappings) {
            try {
                super.createOrUpdate(mapping);
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
}
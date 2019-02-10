package talkapp.org.talkappmobile.component.database.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

import talkapp.org.talkappmobile.component.database.dao.ExpAuditDao;
import talkapp.org.talkappmobile.component.database.mappings.ExpAuditMapping;

public class ExpAuditDaoImpl extends BaseDaoImpl<ExpAuditMapping, Integer> implements ExpAuditDao {

    public ExpAuditDaoImpl(ConnectionSource connectionSource, Class<ExpAuditMapping> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    @Override
    public List<ExpAuditMapping> findAll() {
        try {
            return this.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
package talkapp.org.talkappmobile.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import talkapp.org.talkappmobile.dao.ExpAuditDao;
import talkapp.org.talkappmobile.mappings.ExpAuditMapping;

import static talkapp.org.talkappmobile.mappings.ExpAuditMapping.ACTIVITY_TYPE_FN;
import static talkapp.org.talkappmobile.mappings.ExpAuditMapping.DATE_FN;

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

    @Override
    public void save(ExpAuditMapping mapping) {
        try {
            this.createOrUpdate(mapping);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public ExpAuditMapping findByDateAndActivityType(Date today, String type) {
        try {
            PreparedQuery<ExpAuditMapping> prepare = queryBuilder()
                    .where()
                    .eq(DATE_FN, today)
                    .and()
                    .eq(ACTIVITY_TYPE_FN, type).prepare();
            return this.queryForFirst(prepare);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public List<ExpAuditMapping> findAllByType(String type) {
        try {
            return queryBuilder()
                    .orderBy(DATE_FN, true)
                    .where()
                    .eq(ACTIVITY_TYPE_FN, type).query();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
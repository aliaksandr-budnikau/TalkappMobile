package talkapp.org.talkappmobile.component.database.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import talkapp.org.talkappmobile.component.database.dao.ExpAuditDao;
import talkapp.org.talkappmobile.component.database.mappings.ExpAuditMapping;

import static talkapp.org.talkappmobile.component.database.mappings.ExpAuditMapping.ACTIVITY_TYPE_FN;
import static talkapp.org.talkappmobile.component.database.mappings.ExpAuditMapping.DATE_FN;

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
}
package talkapp.org.talkappmobile.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.dao.DatabaseHelper;
import talkapp.org.talkappmobile.dao.ExpAuditDao;
import talkapp.org.talkappmobile.exceptions.ObjectNotFoundException;
import talkapp.org.talkappmobile.mappings.ExpAuditMapping;

import static java.lang.String.format;
import static talkapp.org.talkappmobile.mappings.ExpAuditMapping.ACTIVITY_TYPE_FN;
import static talkapp.org.talkappmobile.mappings.ExpAuditMapping.DATE_FN;

public class ExpAuditDaoImpl implements ExpAuditDao {

    private final BaseDaoImpl<ExpAuditMapping, Integer> dao;

    @Inject
    public ExpAuditDaoImpl(DatabaseHelper databaseHelper) {
        try {
            dao = new BaseDaoImpl<ExpAuditMapping, Integer>(databaseHelper.getConnectionSource(), ExpAuditMapping.class) {
            };
            dao.initialize();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ExpAuditMapping> findAll() {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void save(ExpAuditMapping mapping) {
        try {
            dao.createOrUpdate(mapping);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public ExpAuditMapping findByDateAndActivityType(Date today, String type) {
        try {
            PreparedQuery<ExpAuditMapping> prepare = dao.queryBuilder()
                    .where()
                    .eq(DATE_FN, today)
                    .and()
                    .eq(ACTIVITY_TYPE_FN, type).prepare();
            ExpAuditMapping mapping = dao.queryForFirst(prepare);
            if (mapping == null) {
                throw new ObjectNotFoundException(format("ExpAudit date '%s' type '%s' was not found", today, type));
            }
            return mapping;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public List<ExpAuditMapping> findAllByType(String type) {
        try {
            return dao.queryBuilder()
                    .orderBy(DATE_FN, true)
                    .where()
                    .eq(ACTIVITY_TYPE_FN, type).query();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
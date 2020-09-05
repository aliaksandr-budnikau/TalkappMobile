package talkapp.org.talkappmobile.repository;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.dao.ExpAuditDao;
import talkapp.org.talkappmobile.exceptions.ObjectNotFoundException;
import talkapp.org.talkappmobile.mappings.ExpAuditMapping;
import talkapp.org.talkappmobile.model.ExpActivityType;
import talkapp.org.talkappmobile.model.ExpAudit;

public class ExpAuditRepositoryImpl implements ExpAuditRepository {
    private final ExpAuditDao expAuditDao;
    private final ExpAuditMapper expAuditMapper;

    @Inject
    public ExpAuditRepositoryImpl(ExpAuditDao expAuditDao) {
        this.expAuditDao = expAuditDao;
        this.expAuditMapper = new ExpAuditMapper();
    }

    @Override
    public double getOverallExp() {
        double sum = 0;
        for (ExpAuditMapping exp : expAuditDao.findAll()) {
            sum += exp.getExpScore();
        }
        return sum;
    }

    @Override
    public ExpAudit findByDateAndActivityType(Date date, ExpActivityType activityType) {
        ExpAuditMapping mapping = expAuditDao.findByDateAndActivityType(date, activityType.name());
        return expAuditMapper.toDto(mapping);
    }

    @Override
    public void createNewOrUpdate(ExpAudit expAudit) {
        ExpAuditMapping mapping;
        try {
            mapping = expAuditDao.findByDateAndActivityType(expAudit.getDate(), expAudit.getActivityType().name());
        } catch (ObjectNotFoundException e) {
            expAuditDao.save(expAuditMapper.toMapping(expAudit));
            return;
        }
        mapping.setExpScore(expAudit.getExpScore());
        expAuditDao.save(mapping);
    }

    @Override
    public List<ExpAudit> findAllByType(ExpActivityType activityType) {
        LinkedList<ExpAudit> result = new LinkedList<>();
        List<ExpAuditMapping> all = expAuditDao.findAllByType(activityType.name());
        for (ExpAuditMapping mapping : all) {
            result.add(expAuditMapper.toDto(mapping));
        }
        return result;
    }
}
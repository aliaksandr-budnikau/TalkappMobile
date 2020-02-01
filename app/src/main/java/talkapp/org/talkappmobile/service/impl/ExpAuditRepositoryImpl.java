package talkapp.org.talkappmobile.service.impl;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.dao.ExpAuditDao;
import talkapp.org.talkappmobile.mappings.ExpAuditMapping;
import talkapp.org.talkappmobile.model.ExpActivityType;
import talkapp.org.talkappmobile.model.ExpAudit;
import talkapp.org.talkappmobile.service.ExpAuditRepository;
import talkapp.org.talkappmobile.service.mapper.ExpAuditMapper;

public class ExpAuditRepositoryImpl implements ExpAuditRepository {
    private final ExpAuditDao expAuditDao;
    private final ExpAuditMapper expAuditMapper;

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
        if (mapping == null) {
            return null;
        }
        return expAuditMapper.toDto(mapping);
    }

    @Override
    public void createNewOrUpdate(ExpAudit expAudit) {
        expAuditDao.save(expAuditMapper.toMapping(expAudit));
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
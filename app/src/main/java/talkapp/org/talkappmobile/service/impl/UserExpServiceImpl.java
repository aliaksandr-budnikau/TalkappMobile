package talkapp.org.talkappmobile.service.impl;

import talkapp.org.talkappmobile.dao.ExpAuditDao;
import talkapp.org.talkappmobile.mappings.ExpAuditMapping;
import talkapp.org.talkappmobile.model.ExpActivityType;
import talkapp.org.talkappmobile.service.UserExpService;

import java.util.Date;

public class UserExpServiceImpl implements UserExpService {
    private final ExpAuditDao expAuditDao;

    public UserExpServiceImpl(ExpAuditDao expAuditDao) {
        this.expAuditDao = expAuditDao;
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
    public double increaseForRepetition(int repetitionCounter, ExpActivityType type) {
        Date today = new Date();
        ExpAuditMapping mapping = expAuditDao.findByDateAndActivityType(today, type.name());
        if (mapping == null) {
            mapping = new ExpAuditMapping();
            mapping.setActivityType(type.name());
            mapping.setDate(today);
            mapping.setExpScore(repetitionCounter);
        } else {
            mapping.setExpScore(mapping.getExpScore() + repetitionCounter);
        }
        expAuditDao.save(mapping);
        return repetitionCounter;
    }
}
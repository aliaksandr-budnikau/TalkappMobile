package talkapp.org.talkappmobile.component.database.impl;

import java.util.Date;

import talkapp.org.talkappmobile.component.database.UserExpService;
import talkapp.org.talkappmobile.component.database.dao.ExpAuditDao;
import talkapp.org.talkappmobile.component.database.mappings.ExpAuditMapping;
import talkapp.org.talkappmobile.model.ExpActivityType;

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
        int newValue = 1 + repetitionCounter;
        ExpAuditMapping mapping = expAuditDao.findByDateAndActivityType(today, type);
        if (mapping == null) {
            mapping = new ExpAuditMapping();
            mapping.setActivityType(type);
            mapping.setDate(today);
            mapping.setExpScore(newValue);
        } else {
            mapping.setExpScore(mapping.getExpScore() + newValue);
        }
        expAuditDao.save(mapping);
        return newValue;
    }
}
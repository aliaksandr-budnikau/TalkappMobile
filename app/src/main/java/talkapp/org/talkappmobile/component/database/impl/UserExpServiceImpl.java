package talkapp.org.talkappmobile.component.database.impl;

import org.talkappmobile.dao.ExpAuditDao;
import org.talkappmobile.mappings.ExpAuditMapping;
import org.talkappmobile.model.ExpActivityType;

import java.util.Date;

import talkapp.org.talkappmobile.component.database.UserExpService;

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
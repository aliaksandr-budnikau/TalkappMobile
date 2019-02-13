package talkapp.org.talkappmobile.component.database.impl;

import java.util.Date;

import talkapp.org.talkappmobile.component.database.UserExpService;
import talkapp.org.talkappmobile.component.database.dao.ExpAuditDao;
import talkapp.org.talkappmobile.component.database.mappings.ExpAuditMapping;

import static talkapp.org.talkappmobile.model.ExpActivityType.WORD_SET_PRACTICE;

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
    public double increaseForRepetition(int repetitionCounter) {
        ExpAuditMapping mapping = new ExpAuditMapping();
        mapping.setActivityType(WORD_SET_PRACTICE);
        mapping.setDate(new Date());
        mapping.setExpScore(1 + repetitionCounter);
        expAuditDao.save(mapping);
        return mapping.getExpScore();
    }
}
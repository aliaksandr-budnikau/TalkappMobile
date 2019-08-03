package talkapp.org.talkappmobile.service.impl;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.dao.ExpAuditDao;
import talkapp.org.talkappmobile.mappings.ExpAuditMapping;
import talkapp.org.talkappmobile.model.ExpActivityType;
import talkapp.org.talkappmobile.model.ExpAudit;
import talkapp.org.talkappmobile.service.UserExpService;
import talkapp.org.talkappmobile.service.mapper.ExpAuditMapper;

public class UserExpServiceImpl implements UserExpService {
    private final ExpAuditDao expAuditDao;
    private final ExpAuditMapper expAuditMapper;

    public UserExpServiceImpl(@NonNull ExpAuditDao expAuditDao, @NonNull ExpAuditMapper expAuditMapper) {
        this.expAuditDao = expAuditDao;
        this.expAuditMapper = expAuditMapper;
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

    @Override
    public List<ExpAudit> findAllByTypeOrderedByDate(ExpActivityType type) {
        List<ExpAudit> result = new LinkedList<>();
        List<ExpAuditMapping> mappings = expAuditDao.findAllByType(type.name());
        for (ExpAuditMapping mapping : mappings) {
            ExpAudit expAudit = expAuditMapper.toDto(mapping);
            result.add(expAudit);
        }
        return result;
    }
}
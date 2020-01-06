package talkapp.org.talkappmobile.service.impl;

import android.support.annotation.NonNull;

import java.util.Calendar;
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
    public static final int AMOUNT = 10;
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
        LinkedList<ExpAudit> result = new LinkedList<>();
        List<ExpAuditMapping> mappings = expAuditDao.findAllByType(type.name());

        if (mappings.isEmpty()) {
            return getListOfLast10Days(type);
        }

        Calendar calendar = getCalendarWithoutTime();
        calendar.setTime(mappings.get(0).getDate());

        for (ExpAuditMapping mapping : mappings) {
            ExpAudit expAudit;
            if (calendar.getTime().equals(mapping.getDate())) {
                expAudit = expAuditMapper.toDto(mapping);
                result.add(expAudit);
                calendar.add(Calendar.DATE, 1);
            } else {
                do {
                    expAudit = getLazyExpAudit(type, calendar);
                    result.add(expAudit);
                    calendar.add(Calendar.DATE, 1);
                } while (!calendar.getTime().equals(mapping.getDate()));
                expAudit = expAuditMapper.toDto(mapping);
                result.add(expAudit);
                calendar.add(Calendar.DATE, 1);
            }
        }

        calendar = getCalendarWithoutTime();
        Date today = calendar.getTime();

        if (result.getLast().getDate().equals(today)) {
            return result;
        }

        calendar.setTime(result.getLast().getDate());

        while (!calendar.getTime().equals(today)) {
            calendar.add(Calendar.DATE, 1);
            result.add(new ExpAudit(calendar.getTime(), 0, type));
        }

        return result;
    }

    @Override
    public void save(ExpAudit expAudit) {
        expAuditDao.save(expAuditMapper.toMapping(expAudit));
    }

    @NonNull
    private Calendar getCalendarWithoutTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    @NonNull
    private List<ExpAudit> getListOfLast10Days(ExpActivityType type) {
        List<ExpAudit> result = new LinkedList<>();
        Calendar calendar = getCalendarWithoutTime();
        calendar.add(Calendar.DATE, -AMOUNT);
        for (int i = 1; i <= AMOUNT; i++) {
            calendar.add(Calendar.DATE, 1);
            result.add(new ExpAudit(calendar.getTime(), 0, type));
        }
        return result;
    }

    private ExpAudit getLazyExpAudit(ExpActivityType type, Calendar calendar) {
        ExpAudit expAudit;
        ExpAuditMapping lazyDay = new ExpAuditMapping();
        lazyDay.setActivityType(type.name());
        lazyDay.setDate(calendar.getTime());
        lazyDay.setExpScore(0);
        expAudit = expAuditMapper.toDto(lazyDay);
        return expAudit;
    }
}
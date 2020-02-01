package talkapp.org.talkappmobile.service.impl;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.model.ExpActivityType;
import talkapp.org.talkappmobile.model.ExpAudit;
import talkapp.org.talkappmobile.service.ExpAuditRepository;
import talkapp.org.talkappmobile.service.UserExpService;

public class UserExpServiceImpl implements UserExpService {
    public static final int AMOUNT = 10;
    private final ExpAuditRepository expAuditRepository;

    public UserExpServiceImpl(ExpAuditRepository expAuditRepository) {
        this.expAuditRepository = expAuditRepository;
    }

    @Override
    public double getOverallExp() {
        return expAuditRepository.getOverallExp();
    }

    @Override
    public double increaseForRepetition(int repetitionCounter, ExpActivityType type) {
        Date today = new Date();
        ExpAudit expAudit = expAuditRepository.findByDateAndActivityType(today, type);
        if (expAudit == null) {
            expAudit = new ExpAudit(today, repetitionCounter, type);
        } else {
            expAudit.increaseExpScore(repetitionCounter);
        }
        expAuditRepository.createNewOrUpdate(expAudit);
        return repetitionCounter;
    }

    @Override
    public List<ExpAudit> findAllByTypeOrderedByDate(ExpActivityType type) {
        LinkedList<ExpAudit> result = new LinkedList<>();
        List<ExpAudit> all = expAuditRepository.findAllByType(type);

        if (all.isEmpty()) {
            return getListOfLast10Days(type);
        }

        Calendar calendar = getCalendarWithoutTime();
        calendar.setTime(all.get(0).getDate());

        for (ExpAudit item : all) {
            ExpAudit expAudit;
            if (calendar.getTime().equals(item.getDate())) {
                expAudit = item;
                result.add(expAudit);
                calendar.add(Calendar.DATE, 1);
            } else {
                do {
                    expAudit = new ExpAudit(calendar.getTime(), 0, type);
                    result.add(expAudit);
                    calendar.add(Calendar.DATE, 1);
                } while (!calendar.getTime().equals(item.getDate()));
                expAudit = item;
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
        expAuditRepository.createNewOrUpdate(expAudit);
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
}
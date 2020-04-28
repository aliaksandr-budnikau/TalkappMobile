package talkapp.org.talkappmobile.interactor;

import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.listener.OnStatisticActivityListener;
import talkapp.org.talkappmobile.model.ExpActivityType;
import talkapp.org.talkappmobile.model.ExpAudit;
import talkapp.org.talkappmobile.model.ExpAuditMonthly;
import talkapp.org.talkappmobile.service.UserExpService;

public class StatisticActivityInteractor {
    private final UserExpService userExpService;

    public StatisticActivityInteractor(UserExpService userExpService) {
        this.userExpService = userExpService;
    }

    public void loadMonthlyStat(final ExpActivityType type, final int year, OnStatisticActivityListener listener) {
        List<ExpAuditMonthly> stat = findAllByTypeAndByYearOrderedByMonth(type, year);
        listener.onMonthlyStatLoaded(stat);
    }

    public void loadDailyStat(final ExpActivityType type, final int year, final int month, OnStatisticActivityListener listener) {
        List<ExpAudit> stat = findAllByTypeAndByYearAndByMonthOrderedByMonth(type, year, month);
        listener.onDailyStatLoaded(stat);
    }

    private List<ExpAudit> findAllByTypeAndByYearAndByMonthOrderedByMonth(final ExpActivityType type, final int year, final int month) {
        Calendar calendar = Calendar.getInstance();

        int initialCapacity = calendar.get(Calendar.DAY_OF_MONTH);
        ExpAudit[] result = new ExpAudit[initialCapacity];
        for (ExpAudit expAudit : userExpService.findAllByTypeOrderedByDate(type)) {
            calendar.setTime(expAudit.getDate());
            if (calendar.get(Calendar.YEAR) != year) {
                continue;
            }
            if (calendar.get(Calendar.MONTH) != month) {
                continue;
            }

            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            result[dayOfMonth - 1] = expAudit;
        }

        LinkedList<ExpAudit> resultAsList = new LinkedList<>();
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -(initialCapacity - 1));
        for (ExpAudit expAudit : result) {
            try {
                if (expAudit == null) {
                    if (resultAsList.isEmpty()) {
                        continue;
                    }
                    resultAsList.add(new ExpAudit(calendar.getTime(), 0, type));
                } else {
                    resultAsList.add(expAudit);
                }
            } finally {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        return resultAsList;
    }

    private List<ExpAuditMonthly> findAllByTypeAndByYearOrderedByMonth(final ExpActivityType type, final int year) {
        Calendar calendar = Calendar.getInstance();

        int initialCapacity = calendar.get(Calendar.MONTH) + 1;
        ExpAuditMonthly[] result = new ExpAuditMonthly[initialCapacity];
        for (int i = 0; i < initialCapacity; i++) {
            result[i] = new ExpAuditMonthly(i, year, 0, type);
        }

        for (ExpAudit expAudit : userExpService.findAllByTypeOrderedByDate(type)) {
            calendar.setTime(expAudit.getDate());
            if (calendar.get(Calendar.YEAR) != year) {
                continue;
            }

            int month = calendar.get(Calendar.MONTH);
            result[month] = addToMonth(result[month], expAudit);
        }
        return Arrays.asList(result);
    }

    private ExpAuditMonthly addToMonth(ExpAuditMonthly month, ExpAudit expAudit) {
        return new ExpAuditMonthly(month.getMonth(), month.getYear(),
                month.getExpScore() + expAudit.getExpScore(), month.getActivityType()
        );
    }
}
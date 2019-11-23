package talkapp.org.talkappmobile.activity.interactor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import talkapp.org.talkappmobile.activity.listener.OnStatisticActivityListener;
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
        return Arrays.asList(result);
    }

    private List<ExpAuditMonthly> findAllByTypeAndByYearOrderedByMonth(final ExpActivityType type, final int year) {
        Calendar calendar = Calendar.getInstance();

        int initialCapacity = calendar.get(Calendar.MONTH) + 1;
        ArrayList<ExpAuditMonthly> result = new ArrayList<>(initialCapacity);
        for (int i = 0; i < initialCapacity; i++) {
            result.set(i, new ExpAuditMonthly(i, year, 0, type));
        }

        for (ExpAudit expAudit : userExpService.findAllByTypeOrderedByDate(type)) {
            calendar.setTime(expAudit.getDate());
            if (calendar.get(Calendar.YEAR) != year) {
                continue;
            }

            int month = calendar.get(Calendar.MONTH);
            result.set(month, addToMonth(result.get(month), expAudit));
        }
        return result;
    }

    private ExpAuditMonthly addToMonth(ExpAuditMonthly month, ExpAudit expAudit) {
        return new ExpAuditMonthly(month.getMonth(), month.getYear(),
                month.getExpScore() + expAudit.getExpScore(), month.getActivityType()
        );
    }
}
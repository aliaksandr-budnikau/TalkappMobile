package talkapp.org.talkappmobile.activity.presenter;

import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.StatisticActivityInteractor;
import talkapp.org.talkappmobile.activity.listener.OnStatisticActivityListener;
import talkapp.org.talkappmobile.activity.view.StatisticActivityView;
import talkapp.org.talkappmobile.model.ExpActivityType;
import talkapp.org.talkappmobile.model.ExpAudit;
import talkapp.org.talkappmobile.model.ExpAuditMonthly;

public class StatisticActivityPresenter implements OnStatisticActivityListener {

    private final StatisticActivityView view;
    private final StatisticActivityInteractor interactor;

    public StatisticActivityPresenter(StatisticActivityView view, StatisticActivityInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

    @Override
    public void onMonthlyStatLoaded(List<ExpAuditMonthly> stat) {
        view.setMonthlyStat(stat);
    }

    @Override
    public void onDailyStatLoaded(List<ExpAudit> stat) {
        view.setDailyStat(stat);
    }

    public void loadDailyStat(final ExpActivityType type, final int year, final int month) {
        interactor.loadDailyStat(type, year, month, this);
    }

    public void loadMonthlyStat(final ExpActivityType type, final int year) {
        interactor.loadMonthlyStat(type, year, this);
    }
}
package talkapp.org.talkappmobile.presenter;

import java.util.List;

import talkapp.org.talkappmobile.interactor.StatisticActivityInteractor;
import talkapp.org.talkappmobile.listener.OnStatisticActivityListener;
import talkapp.org.talkappmobile.model.ExpActivityType;
import talkapp.org.talkappmobile.model.ExpAudit;
import talkapp.org.talkappmobile.model.ExpAuditMonthly;
import talkapp.org.talkappmobile.view.StatisticActivityView;

public class StatisticActivityPresenterImpl implements OnStatisticActivityListener, StatisticActivityPresenter {

    private final StatisticActivityView view;
    private final StatisticActivityInteractor interactor;

    public StatisticActivityPresenterImpl(StatisticActivityView view, StatisticActivityInteractor interactor) {
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

    @Override
    public void loadDailyStat(final ExpActivityType type, final int year, final int month) {
        interactor.loadDailyStat(type, year, month, this);
    }

    @Override
    public void loadMonthlyStat(final ExpActivityType type, final int year) {
        interactor.loadMonthlyStat(type, year, this);
    }
}
package talkapp.org.talkappmobile.activity.presenter;

import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.StatisticActivityInteractor;
import talkapp.org.talkappmobile.activity.listener.OnStatisticActivityListener;
import talkapp.org.talkappmobile.activity.view.StatisticActivityView;
import talkapp.org.talkappmobile.model.ExpAudit;

public class StatisticActivityPresenter implements OnStatisticActivityListener {

    private final StatisticActivityView view;
    private final StatisticActivityInteractor interactor;

    public StatisticActivityPresenter(StatisticActivityView view, StatisticActivityInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

    @Override
    public void onExpAuditLoadedEM(List<ExpAudit> allByType) {
        view.setStat(allByType);
    }

    public void loadStat() {
        interactor.loadStat(this);
    }
}
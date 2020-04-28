package talkapp.org.talkappmobile.presenter;

import talkapp.org.talkappmobile.model.ExpActivityType;

public interface StatisticActivityPresenter {
    void loadDailyStat(ExpActivityType type, int year, int month);

    void loadMonthlyStat(ExpActivityType type, int year);
}

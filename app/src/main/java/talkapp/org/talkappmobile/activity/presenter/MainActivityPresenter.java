package talkapp.org.talkappmobile.activity.presenter;

import talkapp.org.talkappmobile.activity.interactor.MainActivityInteractor;
import talkapp.org.talkappmobile.activity.listener.OnMainActivityListener;
import talkapp.org.talkappmobile.activity.view.MainActivityView;

public class MainActivityPresenter implements OnMainActivityListener {
    private final MainActivityView view;
    private final MainActivityInteractor interactor;

    public MainActivityPresenter(MainActivityView view, MainActivityInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

    public void checkServerAvailability() {
        interactor.checkServerAvailability();
    }

    public void initAppVersion() {
        interactor.initAppVersion(this);
    }

    @Override
    public void onAppVersionInitialized(String packageName) {
        view.setAppVersion("v" + packageName);
    }

    public void initYourExp() {
        interactor.initYourExp(this);
    }

    @Override
    public void onYourExpInitialized(int exp) {
        view.setYourExp("EXP " + exp);
    }
}
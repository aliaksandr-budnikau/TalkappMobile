package talkapp.org.talkappmobile.presenter;

import talkapp.org.talkappmobile.interactor.MainActivityInteractor;
import talkapp.org.talkappmobile.listener.OnMainActivityListener;
import talkapp.org.talkappmobile.view.MainActivityView;

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
    public void onYourExpInitialized(double exp) {
        view.setYourExp("EXP " + exp);
    }
}
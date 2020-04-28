package talkapp.org.talkappmobile.presenter;

import talkapp.org.talkappmobile.interactor.MainActivityInteractor;
import talkapp.org.talkappmobile.listener.OnMainActivityListener;
import talkapp.org.talkappmobile.view.MainActivityView;

public class MainActivityPresenterImpl implements OnMainActivityListener, MainActivityPresenter {
    private final MainActivityView view;
    private final MainActivityInteractor interactor;

    public MainActivityPresenterImpl(MainActivityView view, MainActivityInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

    @Override
    public void checkServerAvailability() {
        interactor.checkServerAvailability();
    }

    @Override
    public void initAppVersion() {
        interactor.initAppVersion(this);
    }

    @Override
    public void onAppVersionInitialized(String packageName) {
        view.setAppVersion("v" + packageName);
    }

    @Override
    public void initYourExp() {
        interactor.initYourExp(this);
    }

    @Override
    public void onYourExpInitialized(double exp) {
        view.setYourExp("EXP " + exp);
    }
}
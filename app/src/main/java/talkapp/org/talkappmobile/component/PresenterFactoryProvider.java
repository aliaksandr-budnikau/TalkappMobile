package talkapp.org.talkappmobile.component;

import android.content.Context;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import talkapp.org.talkappmobile.presenter.PresenterFactory;

@EBean(scope = EBean.Scope.Singleton)
public class PresenterFactoryProvider {
    @RootContext
    Context context;
    private PresenterFactory presenterFactory;

    @AfterInject
    public void init() {
        presenterFactory = talkapp.org.talkappmobile.presenter.PresenterFactoryProvider.get(context);
    }

    public PresenterFactory get() {
        return presenterFactory;
    }

    public void setPresenterFactory(PresenterFactory presenterFactory) {
        this.presenterFactory = presenterFactory;
    }
}

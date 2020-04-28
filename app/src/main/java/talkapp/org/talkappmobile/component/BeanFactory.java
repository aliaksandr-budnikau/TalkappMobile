package talkapp.org.talkappmobile.component;

import android.content.Context;

import talkapp.org.talkappmobile.presenter.PresenterFactory;
import talkapp.org.talkappmobile.presenter.PresenterFactoryProvider;

public class BeanFactory {
    private static PresenterFactory presenterFactory;

    public BeanFactory(PresenterFactory presenterFactory) {
        BeanFactory.presenterFactory = presenterFactory;
    }

    public static PresenterFactory presenterFactory(Context context) {
        if (presenterFactory == null) {
            presenterFactory = PresenterFactoryProvider.get(context);
        }
        return presenterFactory;
    }
}

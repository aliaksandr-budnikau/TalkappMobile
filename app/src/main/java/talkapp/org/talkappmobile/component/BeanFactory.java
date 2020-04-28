package talkapp.org.talkappmobile.component;

import android.content.Context;

import talkapp.org.talkappmobile.PresenterFactory;

public class BeanFactory {
    private static PresenterFactory presenterFactory;

    public BeanFactory(PresenterFactory presenterFactory) {
        BeanFactory.presenterFactory = presenterFactory;
    }

    public static PresenterFactory presenterFactory(Context context) {
        if (presenterFactory == null) {
            presenterFactory = new PresenterFactory(context);
        }
        return presenterFactory;
    }
}

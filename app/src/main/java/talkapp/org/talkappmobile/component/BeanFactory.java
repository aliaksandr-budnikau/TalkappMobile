package talkapp.org.talkappmobile.component;

import android.content.Context;

import talkapp.org.talkappmobile.PresenterFactory;
import talkapp.org.talkappmobile.service.ServiceFactory;

public class BeanFactory {
    private static ServiceFactory serviceFactory;
    private static PresenterFactory presenterFactory;

    public BeanFactory(ServiceFactory serviceFactory, PresenterFactory presenterFactory) {
        BeanFactory.serviceFactory = serviceFactory;
        BeanFactory.presenterFactory = presenterFactory;
    }

    public static PresenterFactory presenterFactory(Context context) {
        if (presenterFactory == null) {
            presenterFactory = new PresenterFactory(serviceFactory);
        }
        return presenterFactory;
    }
}

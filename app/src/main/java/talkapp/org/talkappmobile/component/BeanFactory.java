package talkapp.org.talkappmobile.component;

import android.content.Context;

import talkapp.org.talkappmobile.PresenterFactory;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.ServiceFactoryProvider;

public class BeanFactory {
    private static ServiceFactory serviceFactory;
    private static PresenterFactory presenterFactory;

    public BeanFactory(ServiceFactory serviceFactory, PresenterFactory presenterFactory) {
        BeanFactory.serviceFactory = serviceFactory;
        BeanFactory.presenterFactory = presenterFactory;
    }

    public static ServiceFactory serviceFactory(Context context) {
        if (serviceFactory == null) {
            serviceFactory = ServiceFactoryProvider.get(context);
        }
        return serviceFactory;
    }

    public static PresenterFactory presenterFactory(Context context) {
        if (presenterFactory == null) {
            presenterFactory = new PresenterFactory(serviceFactory);
        }
        return presenterFactory;
    }
}

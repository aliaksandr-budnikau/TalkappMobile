package talkapp.org.talkappmobile.service;

import java.lang.reflect.Constructor;

import static java.lang.Class.forName;

public class ServiceFactoryProvider {

    private static ServiceFactory serviceFactory;

    public static <T> ServiceFactory getOrCreateNew(T context) {
        if (serviceFactory != null) {
            return serviceFactory;
        }
        return createNew(context);
    }

    public static <T> ServiceFactory createNew(T context) {
        try {
            String contextClassFullName = "android.content.Context";
            Class<?> contextClass = forName(contextClassFullName);
            if (!contextClass.isAssignableFrom(context.getClass())) {
                throw new RuntimeException("argument is not instance of " + contextClassFullName);
            }
            Constructor<? extends ServiceFactory> factoryConstructor = forName("talkapp.org.talkappmobile.service.ServiceFactoryImpl")
                    .asSubclass(ServiceFactory.class)
                    .getConstructor(contextClass);
            serviceFactory = factoryConstructor.newInstance(context);
            return serviceFactory;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

package talkapp.org.talkappmobile.service;

import java.lang.reflect.Constructor;

import static java.lang.Class.forName;

public class ServiceFactoryProvider {
    public static <T> ServiceFactory get(T context) {
        try {
            String contextClassFullName = "android.content.Context";
            Class<?> contextClass = forName(contextClassFullName);
            if (!contextClass.isAssignableFrom(context.getClass())) {
                throw new RuntimeException("argument is not instance of " + contextClassFullName);
            }
            Constructor<? extends ServiceFactory> factoryConstructor = forName("talkapp.org.talkappmobile.service.ServiceFactoryImpl")
                    .asSubclass(ServiceFactory.class)
                    .getConstructor(contextClass);
            return factoryConstructor.newInstance(context);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

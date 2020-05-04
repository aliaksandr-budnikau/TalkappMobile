package talkapp.org.talkappmobile.presenter;

import java.lang.reflect.Constructor;

import static java.lang.Class.forName;

public class PresenterFactoryProvider {
    public static <T> PresenterFactory get(T context) {
        try {
            String contextClassFullName = "android.content.Context";
            Class<?> contextClass = forName(contextClassFullName);
            if (!contextClass.isAssignableFrom(context.getClass())) {
                throw new RuntimeException("argument is not instance of " + contextClassFullName);
            }
            Constructor<? extends PresenterFactory> factoryConstructor = forName("talkapp.org.talkappmobile.presenter.PresenterFactoryImpl")
                    .asSubclass(PresenterFactory.class)
                    .getConstructor(contextClass);
            return factoryConstructor.newInstance(context);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

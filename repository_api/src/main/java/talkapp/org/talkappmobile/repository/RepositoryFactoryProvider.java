package talkapp.org.talkappmobile.repository;

import java.lang.reflect.Constructor;

import static java.lang.Class.forName;

public class RepositoryFactoryProvider {
    public static <T> RepositoryFactory get(T context) {
        try {
            String contextClassFullName = "android.content.Context";
            Class<?> contextClass = forName(contextClassFullName);
            if (!contextClass.isAssignableFrom(context.getClass())) {
                throw new RuntimeException("argument is not instance of " + contextClassFullName);
            }
            Constructor<? extends RepositoryFactory> factoryConstructor = forName("talkapp.org.talkappmobile.repository.RepositoryFactoryImpl")
                    .asSubclass(RepositoryFactory.class)
                    .getConstructor(contextClass);
            return factoryConstructor.newInstance(context);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

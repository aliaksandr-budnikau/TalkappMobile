package talkapp.org.talkappmobile.repository;

import java.lang.reflect.Constructor;

import static java.lang.Class.forName;

public class RepositoryFactoryProvider {
    public static <T> RepositoryFactory get(T context) {
        try {
            Constructor<? extends RepositoryFactory> factoryConstructor = forName("talkapp.org.talkappmobile.repository.RepositoryFactoryImpl")
                    .asSubclass(RepositoryFactory.class)
                    .getConstructor(context.getClass());
            return factoryConstructor.newInstance(context);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

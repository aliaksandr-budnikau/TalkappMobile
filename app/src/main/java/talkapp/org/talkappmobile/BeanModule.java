package talkapp.org.talkappmobile;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import lombok.Setter;
import talkapp.org.talkappmobile.presenter.PresenterFactory;

@Module
public class BeanModule {
    private static BeanModule beanModule;
    @Setter
    private PresenterFactory presenterFactory;

    private BeanModule(PresenterFactory presenterFactory) {
        this.presenterFactory = presenterFactory;
    }

    public synchronized static BeanModule getInstance(PresenterFactory presenterFactory) {
        if (beanModule == null) {
            beanModule = new BeanModule(presenterFactory);
        }
        return beanModule;
    }

    public synchronized static BeanModule getInstance() {
        return beanModule;
    }

    @Provides
    @Singleton
    public PresenterFactory presenterFactory() {
        return presenterFactory;
    }
}

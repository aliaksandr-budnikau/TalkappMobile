package talkapp.org.talkappmobile.module;

import javax.inject.Singleton;

import dagger.Component;
import talkapp.org.talkappmobile.activity.presenter.ClassForInjection;

@Singleton
@Component(modules = {
        BackEndServiceModule.class,
        GameplayModule.class,
        ConcurrentModule.class,
        AndroidModule.class,
        DataModule.class,
        AudioModule.class,
        InfraModule.class,
        LanguageModule.class,
        DatabaseModule.class,
        ItemsListModule.class
})
public interface TestDIContext {
    void inject(ClassForInjection target);
}
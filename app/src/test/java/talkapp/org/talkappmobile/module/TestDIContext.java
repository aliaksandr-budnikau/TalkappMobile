package talkapp.org.talkappmobile.module;

import javax.inject.Singleton;

import dagger.Component;
import talkapp.org.talkappmobile.activity.presenter.ClassForInjection;

@Singleton
@Component(modules = {
        BackEndServiceModule.class,
        GameplayModule.class,
        AndroidModule.class,
        AudioModule.class,
        InfraModule.class,
        LanguageModule.class,
        DatabaseModule.class
})
public interface TestDIContext {
    void inject(ClassForInjection target);
}
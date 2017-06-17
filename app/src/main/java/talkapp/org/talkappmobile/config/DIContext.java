package talkapp.org.talkappmobile.config;

import javax.inject.Singleton;

import dagger.Component;
import talkapp.org.talkappmobile.activity.ExerciseActivity;
import talkapp.org.talkappmobile.bean.BackEndServiceModule;

@Singleton
@Component(modules = {
        BackEndServiceModule.class
})
public abstract class DIContext {

    private static DIContext instance;

    public static DIContext get() {
        if (instance == null) {
            instance = DaggerDIContext.builder()
                    .backEndServiceModule(new BackEndServiceModule())
                    .build();
        }
        return instance;
    }

    abstract public void inject(ExerciseActivity target);
}

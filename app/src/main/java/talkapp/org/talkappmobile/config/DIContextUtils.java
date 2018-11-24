package talkapp.org.talkappmobile.config;

import android.app.Application;

import talkapp.org.talkappmobile.module.AndroidModule;
import talkapp.org.talkappmobile.module.AudioModule;
import talkapp.org.talkappmobile.module.BackEndServiceModule_;
import talkapp.org.talkappmobile.module.DataModule;
import talkapp.org.talkappmobile.module.DatabaseModule;
import talkapp.org.talkappmobile.module.GameplayModule_;
import talkapp.org.talkappmobile.module.InfraModule;
import talkapp.org.talkappmobile.module.ItemsListModule;
import talkapp.org.talkappmobile.module.LanguageModule;
import talkapp.org.talkappmobile.module.ViewModule;

public class DIContextUtils {

    private static DIContext instance;

    public static DIContext init(Application application) {
        if (instance == null && application != null) {
            instance = DaggerDIContext.builder()
                    .androidModule(new AndroidModule(application))
                    .audioModule(new AudioModule())
                    .viewModule(new ViewModule())
                    .gameplayModule(GameplayModule_.getInstance_(application))
                    .dataModule(new DataModule())
                    .infraModule(new InfraModule())
                    .languageModule(new LanguageModule())
                    .databaseModule(new DatabaseModule())
                    .backEndServiceModule(BackEndServiceModule_.getInstance_(application))
                    .itemsListModule(new ItemsListModule())
                    .build();
        }
        return instance;
    }

    public static DIContext get() {
        if (instance == null) {
            throw new RuntimeException("DIContext wasn't initialized yet");
        }
        return instance;
    }
}
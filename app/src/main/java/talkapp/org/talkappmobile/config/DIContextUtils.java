package talkapp.org.talkappmobile.config;

import android.app.Application;

import talkapp.org.talkappmobile.module.DatabaseModule_;
import talkapp.org.talkappmobile.module.GameplayModule_;

public class DIContextUtils {

    private static DIContext instance;

    public static DIContext init(Application application) {
        if (instance == null && application != null) {
            instance = DaggerDIContext.builder()
                    .gameplayModule(GameplayModule_.getInstance_(application))
                    .databaseModule(DatabaseModule_.getInstance_(application))
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
package talkapp.org.talkappmobile.app;

import android.app.Application;

import talkapp.org.talkappmobile.config.DIContext;

/**
 * @author Budnikau Aliaksandr
 */
public class TalkappMobileApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DIContext.init(this);
    }
}

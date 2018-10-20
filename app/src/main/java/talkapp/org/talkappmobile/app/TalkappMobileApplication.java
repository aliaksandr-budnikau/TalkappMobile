package talkapp.org.talkappmobile.app;

import android.app.Application;

import talkapp.org.talkappmobile.config.DIContextUtils;

/**
 * @author Budnikau Aliaksandr
 */
public class TalkappMobileApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DIContextUtils.init(this);
    }
}

package talkapp.org.talkappmobile.module;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.component.view.WaitingForProgressBarManagerFactory;

@Module
public class ViewModule {
    @Provides
    @Singleton
    public WaitingForProgressBarManagerFactory provideWaitingForProgressBarManagerFactory(Context context) {
        int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);
        return new WaitingForProgressBarManagerFactory(shortAnimTime);
    }
}
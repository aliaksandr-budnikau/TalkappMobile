package talkapp.org.talkappmobile.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.activity.adapter.AdaptersFactory;
import talkapp.org.talkappmobile.activity.adapter.impl.AdaptersFactoryImpl;

/**
 * @author Budnikau Aliaksandr
 */
@Module
public class ItemsListModule {

    @Provides
    @Singleton
    public AdaptersFactory provideAdaptersFactory() {
        return new AdaptersFactoryImpl();
    }
}
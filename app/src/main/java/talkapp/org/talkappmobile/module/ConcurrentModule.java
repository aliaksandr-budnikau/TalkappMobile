package talkapp.org.talkappmobile.module;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Budnikau Aliaksandr
 */
@Module
public class ConcurrentModule {
    @Provides
    @Singleton
    public Executor provideExecutor() {
        return Executors.newFixedThreadPool(2);
    }
}
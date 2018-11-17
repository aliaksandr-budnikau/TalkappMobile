package talkapp.org.talkappmobile.module;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.inject.Named;
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
    @Named("executor")
    public Executor provideExecutor() {
        return Executors.newFixedThreadPool(2);
    }

    @Provides
    @Singleton
    @Named("hiddenExecutor")
    public Executor provideHiddenExecutor() {
        return Executors.newFixedThreadPool(1);
    }

    @Provides
    @Singleton
    public BlockingQueue<String> provideHiddenExecutorQueue() {
        return new LinkedBlockingQueue<>();
    }
}
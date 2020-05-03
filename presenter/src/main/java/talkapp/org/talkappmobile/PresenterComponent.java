package talkapp.org.talkappmobile;

import javax.inject.Singleton;

import dagger.Component;
import talkapp.org.talkappmobile.presenter.PresenterFactoryImpl;

@Singleton
@Component(modules = {PresenterModule.class, PresenterBindModule.class})
public interface PresenterComponent {

    void inject(PresenterFactoryImpl presenterFactory);
}
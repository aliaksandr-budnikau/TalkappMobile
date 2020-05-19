package talkapp.org.talkappmobile;

import javax.inject.Singleton;

import dagger.Component;
import talkapp.org.talkappmobile.activity.BaseActivity;

@Singleton
@Component(modules = {BeanModule.class})
public interface BeanComponent {
    void inject(BaseActivity target);
}
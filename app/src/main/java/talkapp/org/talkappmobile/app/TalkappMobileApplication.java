package talkapp.org.talkappmobile.app;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

import lombok.Getter;
import talkapp.org.talkappmobile.BeanComponent;
import talkapp.org.talkappmobile.BeanModule;
import talkapp.org.talkappmobile.DaggerBeanComponent;
import talkapp.org.talkappmobile.presenter.PresenterFactory;
import talkapp.org.talkappmobile.presenter.PresenterFactoryProvider;

/**
 * @author Budnikau Aliaksandr
 */
public class TalkappMobileApplication extends MultiDexApplication {

    @Getter
    private BeanComponent beanInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();
        PresenterFactory presenterFactory = PresenterFactoryProvider.get(context);
        BeanModule beanModule = BeanModule.getInstance(presenterFactory);
        beanInjector = DaggerBeanComponent.builder()
                .beanModule(beanModule).build();
    }
}

package talkapp.org.talkappmobile;

import javax.inject.Singleton;

import dagger.Component;
import talkapp.org.talkappmobile.service.ServiceFactoryImpl;

@Singleton
@Component(modules = {ServiceModule.class, ServiceBindModule.class})
public interface ServiceComponent {
    void inject(ServiceFactoryImpl target);
}
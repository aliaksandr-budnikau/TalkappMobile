package talkapp.org.talkappmobile.controller;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.greenrobot.eventbus.EventBus;

import talkapp.org.talkappmobile.events.PhraseTranslationInputPopupOkClickedEM;
import talkapp.org.talkappmobile.service.AddingEditingNewWordSetsService;
import talkapp.org.talkappmobile.service.BackendServerFactory;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.impl.AddingEditingNewWordSetsServiceImpl;
import talkapp.org.talkappmobile.service.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;

@EBean(scope = EBean.Scope.Singleton)
public class AddingEditingNewWordSetsController {

    @Bean(ServiceFactoryBean.class)
    ServiceFactory serviceFactory;
    @Bean(BackendServerFactoryBean.class)
    BackendServerFactory backendServerFactory;
    @EventBusGreenRobot
    EventBus eventBus;
    private AddingEditingNewWordSetsService addingEditingNewWordSetsService;

    @AfterInject
    public void init() {
        addingEditingNewWordSetsService = new AddingEditingNewWordSetsServiceImpl(eventBus,
                backendServerFactory.get(),
                serviceFactory.getWordTranslationService());
    }

    public void onMessageEvent(PhraseTranslationInputPopupOkClickedEM event) {
        addingEditingNewWordSetsService.saveNewWordTranslation(event.getPhrase(), event.getTranslation());
    }
}
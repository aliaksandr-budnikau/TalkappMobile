package talkapp.org.talkappmobile.controller;

import android.content.Context;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.greenrobot.eventbus.EventBus;

import talkapp.org.talkappmobile.events.PhraseTranslationInputPopupOkClickedEM;
import talkapp.org.talkappmobile.service.AddingEditingNewWordSetsService;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.impl.AddingEditingNewWordSetsServiceImpl;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;

@EBean(scope = EBean.Scope.Singleton)
public class AddingEditingNewWordSetsController {

    @EventBusGreenRobot
    EventBus eventBus;
    @RootContext
    Context context;
    private AddingEditingNewWordSetsService addingEditingNewWordSetsService;

    @AfterInject
    public void init() {
        ServiceFactory serviceFactory = ServiceFactoryBean.getInstance(context);
        addingEditingNewWordSetsService = new AddingEditingNewWordSetsServiceImpl(eventBus,
                serviceFactory.getDataServer(),
                serviceFactory.getWordTranslationService());
    }

    public void onMessageEvent(PhraseTranslationInputPopupOkClickedEM event) {
        addingEditingNewWordSetsService.saveNewWordTranslation(event.getPhrase(), event.getTranslation());
    }
}
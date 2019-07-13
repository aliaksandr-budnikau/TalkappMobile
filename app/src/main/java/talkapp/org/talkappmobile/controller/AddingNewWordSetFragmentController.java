package talkapp.org.talkappmobile.controller;

import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import talkapp.org.talkappmobile.events.AddingNewWordSetFragmentReadyEM;
import talkapp.org.talkappmobile.events.NewWordSetDraftChangedEM;
import talkapp.org.talkappmobile.events.NewWordSetDraftLoadedEM;
import talkapp.org.talkappmobile.model.NewWordSetDraft;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.WordSetService;

public class AddingNewWordSetFragmentController {

    private final EventBus eventBus;
    private final WordSetService wordSetService;

    public AddingNewWordSetFragmentController(@NonNull EventBus eventBus, @NonNull ServiceFactory factory) {
        this.eventBus = eventBus;
        this.wordSetService = factory.getWordSetExperienceRepository();
    }


    public void handle(AddingNewWordSetFragmentReadyEM event) {
        NewWordSetDraft newWordSetDraft = wordSetService.getNewWordSetDraft();
        eventBus.post(new NewWordSetDraftLoadedEM(newWordSetDraft));
    }

    public void handle(NewWordSetDraftChangedEM event) {
        wordSetService.save(new NewWordSetDraft(event.getWords()));
    }
}
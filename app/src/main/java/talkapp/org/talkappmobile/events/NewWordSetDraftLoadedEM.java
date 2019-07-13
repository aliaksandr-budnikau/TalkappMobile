package talkapp.org.talkappmobile.events;

import android.support.annotation.NonNull;

import talkapp.org.talkappmobile.model.NewWordSetDraft;

public class NewWordSetDraftLoadedEM {
    private final NewWordSetDraft newWordSetDraft;

    public NewWordSetDraftLoadedEM(@NonNull NewWordSetDraft newWordSetDraft) {
        this.newWordSetDraft = newWordSetDraft;
    }

    @NonNull
    public NewWordSetDraft getNewWordSetDraft() {
        return newWordSetDraft;
    }
}
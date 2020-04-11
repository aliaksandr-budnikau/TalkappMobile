package talkapp.org.talkappmobile.activity.view;

import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

public interface AddingNewWordSetView {
    void onNewWordSetDraftLoaded(WordTranslation[] words);

    void onNewWordSuccessfullySubmitted(WordSet wordSet);

    void onSomeWordIsEmpty();
}
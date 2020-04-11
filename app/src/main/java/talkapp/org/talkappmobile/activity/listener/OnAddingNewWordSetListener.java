package talkapp.org.talkappmobile.activity.listener;

import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

public interface OnAddingNewWordSetListener {
    void onNewWordSetDraftLoaded(WordTranslation[] words);

    void onNewWordSuccessfullySubmitted(WordSet wordSet);

    void onSomeWordIsEmpty();

    void onNewWordTranslationWasNotFound();

    void onPhraseTranslationInputWasValidatedSuccessfully(String newPhrase, String newTranslation);
}
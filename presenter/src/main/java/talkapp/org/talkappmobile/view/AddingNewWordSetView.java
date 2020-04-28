package talkapp.org.talkappmobile.view;

import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

public interface AddingNewWordSetView {
    void onNewWordSetDraftLoaded(WordTranslation[] words);

    void onNewWordSuccessfullySubmitted(WordSet wordSet);

    void onSomeWordIsEmpty();

    void onNewWordTranslationWasNotFound();

    void onPhraseTranslationInputWasValidatedSuccessfully(String newPhrase, String newTranslation);
}
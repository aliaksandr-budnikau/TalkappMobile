package talkapp.org.talkappmobile.presenter;

import java.util.List;

import talkapp.org.talkappmobile.model.WordTranslation;

public interface AddingNewWordSetPresenter {
    void initialize();

    void submitNewWordSet(List<WordTranslation> translations);

    void saveChangedDraft(List<WordTranslation> vocabulary);

    void savePhraseTranslationInputOnPopup(String newPhrase, String newTranslation);
}

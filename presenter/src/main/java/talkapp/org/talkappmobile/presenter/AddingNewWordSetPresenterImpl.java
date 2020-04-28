package talkapp.org.talkappmobile.presenter;

import java.util.List;

import talkapp.org.talkappmobile.interactor.AddingNewWordSetInteractor;
import talkapp.org.talkappmobile.listener.OnAddingNewWordSetListener;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.view.AddingNewWordSetView;

public class AddingNewWordSetPresenterImpl implements OnAddingNewWordSetListener, AddingNewWordSetPresenter {
    private final AddingNewWordSetInteractor interactor;
    private final AddingNewWordSetView view;

    public AddingNewWordSetPresenterImpl(AddingNewWordSetView view, AddingNewWordSetInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

    @Override
    public void initialize() {
        interactor.initialize(this);
    }

    @Override
    public void onNewWordSetDraftLoaded(WordTranslation[] words) {
        view.onNewWordSetDraftLoaded(words);
    }

    @Override
    public void onNewWordSuccessfullySubmitted(WordSet wordSet) {
        view.onNewWordSuccessfullySubmitted(wordSet);
    }

    @Override
    public void onSomeWordIsEmpty() {
        view.onSomeWordIsEmpty();
    }

    @Override
    public void onNewWordTranslationWasNotFound() {
        view.onNewWordTranslationWasNotFound();
    }

    @Override
    public void onPhraseTranslationInputWasValidatedSuccessfully(String newPhrase, String newTranslation) {
        view.onPhraseTranslationInputWasValidatedSuccessfully(newPhrase, newTranslation);
    }

    @Override
    public void submitNewWordSet(List<WordTranslation> translations) {
        interactor.submitNewWordSet(translations, this);
    }

    @Override
    public void saveChangedDraft(List<WordTranslation> vocabulary) {
        interactor.saveChangedDraft(vocabulary);
    }

    @Override
    public void savePhraseTranslationInputOnPopup(String newPhrase, String newTranslation) {
        interactor.savePhraseTranslationInputOnPopup(newPhrase, newTranslation, this);
    }
}
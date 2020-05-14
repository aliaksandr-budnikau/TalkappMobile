package talkapp.org.talkappmobile.presenter.decorator;

import java.util.List;

import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.presenter.AddingNewWordSetPresenter;

public class AddingNewWordSetPresenterDecorator implements AddingNewWordSetPresenter {

    private final AddingNewWordSetPresenter presenter;

    public AddingNewWordSetPresenterDecorator(AddingNewWordSetPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void initialize() {
        presenter.initialize();
    }

    @Override
    public void submitNewWordSet(List<WordTranslation> translations) {
        presenter.submitNewWordSet(translations);
    }

    @Override
    public void saveChangedDraft(List<WordTranslation> vocabulary) {
        presenter.saveChangedDraft(vocabulary);
    }

    @Override
    public void savePhraseTranslationInputOnPopup(String newPhrase, String newTranslation) {
        presenter.savePhraseTranslationInputOnPopup(newPhrase, newTranslation);
    }
}

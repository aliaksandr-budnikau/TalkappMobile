package talkapp.org.talkappmobile.activity;

import androidx.test.espresso.idling.CountingIdlingResource;

import java.util.List;

import lombok.RequiredArgsConstructor;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.presenter.AddingNewWordSetPresenter;

@RequiredArgsConstructor
public class IdleResourceAddingNewWordSetPresenterDecorator implements AddingNewWordSetPresenter {
    private final AddingNewWordSetPresenter presenter;
    private final CountingIdlingResource resource;

    @Override
    public void initialize() {
        try {
            resource.increment();
            presenter.initialize();
        } finally {
            resource.decrement();
        }
    }

    @Override
    public void submitNewWordSet(List<WordTranslation> translations) {
        try {
            resource.increment();
            presenter.submitNewWordSet(translations);
        } finally {
            resource.decrement();
        }
    }

    @Override
    public void saveChangedDraft(List<WordTranslation> vocabulary) {
        try {
            resource.increment();
            presenter.saveChangedDraft(vocabulary);
        } finally {
            resource.decrement();
        }
    }

    @Override
    public void savePhraseTranslationInputOnPopup(String newPhrase, String newTranslation) {
        try {
            resource.increment();
            presenter.savePhraseTranslationInputOnPopup(newPhrase, newTranslation);
        } finally {
            resource.decrement();
        }
    }
}

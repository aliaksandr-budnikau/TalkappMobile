package talkapp.org.talkappmobile.activity;

import androidx.test.espresso.idling.CountingIdlingResource;

import lombok.RequiredArgsConstructor;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.presenter.PracticeWordSetVocabularyPresenter;

@RequiredArgsConstructor
public class IdleResourcePracticeWordSetVocabularyPresenterDecorator implements PracticeWordSetVocabularyPresenter {
    private final PracticeWordSetVocabularyPresenter presenter;
    private final CountingIdlingResource resource;

    @Override
    public void initialise(WordSet wordSet) {
        try {
            resource.increment();
            presenter.initialise(wordSet);
        } finally {
            resource.decrement();
        }
    }

    @Override
    public void updateCustomWordSet(int editedItemPosition, WordTranslation wordTranslation) {
        try {
            resource.increment();
            presenter.updateCustomWordSet(editedItemPosition, wordTranslation);
        } finally {
            resource.decrement();
        }
    }
}
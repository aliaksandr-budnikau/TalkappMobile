package talkapp.org.talkappmobile.activity;

import androidx.test.espresso.idling.CountingIdlingResource;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.presenter.IPracticeWordSetPresenter;

@RequiredArgsConstructor
public class IdleResourceIPracticeWordSetPresenterDecorator implements IPracticeWordSetPresenter {
    @Delegate(excludes = ExcludedModule.class)
    private final IPracticeWordSetPresenter presenter;
    private final CountingIdlingResource resource;

    @Override
    public void nextButtonClick() {
        try {
            resource.increment();
            presenter.nextButtonClick();
        } finally {
            resource.decrement();
        }
    }

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
    public void checkAnswerButtonClick(String answer) {
        try {
            resource.increment();
            presenter.checkAnswerButtonClick(answer);
        } finally {
            resource.decrement();
        }
    }

    private interface ExcludedModule {
        void nextButtonClick();

        void initialise(WordSet wordSet);

        void checkAnswerButtonClick(String answer);
    }
}

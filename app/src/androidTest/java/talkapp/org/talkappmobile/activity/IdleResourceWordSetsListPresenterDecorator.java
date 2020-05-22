package talkapp.org.talkappmobile.activity;

import androidx.test.espresso.idling.CountingIdlingResource;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.presenter.WordSetsListPresenter;

@RequiredArgsConstructor
public class IdleResourceWordSetsListPresenterDecorator implements WordSetsListPresenter {
    @Delegate(excludes = ExcludedMethods.class)
    private final WordSetsListPresenter presenter;
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
    public void itemClick(WordSet wordSet, int clickedItemNumber) {
        try {
            resource.increment();
            presenter.itemClick(wordSet, clickedItemNumber);
        } finally {
            resource.decrement();
        }
    }

    @Override
    public void resetExperienceClick(WordSet wordSet, int clickedItemNumber) {
        try {
            resource.increment();
            presenter.resetExperienceClick(wordSet, clickedItemNumber);
        } finally {
            resource.decrement();
        }
    }

    @Override
    public void refresh() {
        try {
            resource.increment();
            presenter.refresh();
        } finally {
            resource.decrement();
        }
    }

    private interface ExcludedMethods {
        void initialize();

        void itemClick(WordSet wordSet, int clickedItemNumber);

        void resetExperienceClick(WordSet wordSet, int clickedItemNumber);

        void refresh();
    }
}

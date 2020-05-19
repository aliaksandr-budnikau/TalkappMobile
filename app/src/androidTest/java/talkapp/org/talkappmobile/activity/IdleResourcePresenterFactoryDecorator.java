package talkapp.org.talkappmobile.activity;

import androidx.test.espresso.idling.CountingIdlingResource;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import talkapp.org.talkappmobile.presenter.AddingNewWordSetPresenter;
import talkapp.org.talkappmobile.presenter.IPracticeWordSetPresenter;
import talkapp.org.talkappmobile.presenter.PracticeWordSetVocabularyPresenter;
import talkapp.org.talkappmobile.presenter.PresenterFactory;
import talkapp.org.talkappmobile.view.AddingNewWordSetView;
import talkapp.org.talkappmobile.view.PracticeWordSetView;
import talkapp.org.talkappmobile.view.PracticeWordSetVocabularyView;

@RequiredArgsConstructor
public class IdleResourcePresenterFactoryDecorator implements PresenterFactory {
    @Delegate(excludes = ExcludedMethods.class)
    private final PresenterFactory presenterFactory;
    private final CountingIdlingResource resource;

    @Override
    public IPracticeWordSetPresenter create(PracticeWordSetView view, boolean repetitionMode) {
        IPracticeWordSetPresenter iPracticeWordSetPresenter = presenterFactory.create(view, repetitionMode);
        return new IdleResourceIPracticeWordSetPresenterDecorator(iPracticeWordSetPresenter, resource);
    }

    @Override
    public PracticeWordSetVocabularyPresenter create(PracticeWordSetVocabularyView view) {
        PracticeWordSetVocabularyPresenter presenter = presenterFactory.create(view);
        return new IdleResourcePracticeWordSetVocabularyPresenterDecorator(presenter, resource);
    }

    @Override
    public AddingNewWordSetPresenter create(AddingNewWordSetView view) {
        AddingNewWordSetPresenter presenter = presenterFactory.create(view);
        return new IdleResourceAddingNewWordSetPresenterDecorator(presenter, resource);
    }


    private interface ExcludedMethods {

        IPracticeWordSetPresenter create(PracticeWordSetView view, boolean repetitionMode);

        PracticeWordSetVocabularyPresenter create(PracticeWordSetVocabularyView view);

        AddingNewWordSetPresenter create(AddingNewWordSetView view);
    }
}

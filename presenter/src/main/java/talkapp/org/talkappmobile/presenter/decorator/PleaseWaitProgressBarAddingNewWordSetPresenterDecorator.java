package talkapp.org.talkappmobile.presenter.decorator;

import java.util.List;

import lombok.experimental.Delegate;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.presenter.AddingNewWordSetPresenter;
import talkapp.org.talkappmobile.view.AddingNewWordSetView;

public class PleaseWaitProgressBarAddingNewWordSetPresenterDecorator implements AddingNewWordSetPresenter {
    private final AddingNewWordSetView view;
    @Delegate(excludes = ExcludedMethods.class)
    private final AddingNewWordSetPresenter presenter;

    public PleaseWaitProgressBarAddingNewWordSetPresenterDecorator(AddingNewWordSetPresenter presenter, AddingNewWordSetView view) {
        this.presenter = presenter;
        this.view = view;
    }

    @Override
    public void submitNewWordSet(final List<WordTranslation> translations) {
        try {
            view.showPleaseWaitProgressBar();
            presenter.submitNewWordSet(translations);
        } finally {
            view.hidePleaseWaitProgressBar();
        }
    }

    private interface ExcludedMethods {
        void submitNewWordSet(List<WordTranslation> translations);
    }
}
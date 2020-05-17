package talkapp.org.talkappmobile.presenter.decorator;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.presenter.AddingNewWordSetPresenter;
import talkapp.org.talkappmobile.view.AddingNewWordSetView;

@RequiredArgsConstructor
public class PleaseWaitProgressBarAddingNewWordSetPresenterDecorator implements AddingNewWordSetPresenter {
    @Delegate(excludes = ExcludedMethods.class)
    private final AddingNewWordSetPresenter presenter;
    private final AddingNewWordSetView view;

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
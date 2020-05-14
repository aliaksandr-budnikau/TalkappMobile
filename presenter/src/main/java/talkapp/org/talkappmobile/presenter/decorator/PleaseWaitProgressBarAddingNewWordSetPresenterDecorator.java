package talkapp.org.talkappmobile.presenter.decorator;

import java.util.List;

import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.presenter.AddingNewWordSetPresenter;
import talkapp.org.talkappmobile.view.AddingNewWordSetView;

public class PleaseWaitProgressBarAddingNewWordSetPresenterDecorator extends AddingNewWordSetPresenterDecorator {
    private final AddingNewWordSetView view;

    public PleaseWaitProgressBarAddingNewWordSetPresenterDecorator(AddingNewWordSetPresenter presenter, AddingNewWordSetView view) {
        super(presenter);
        this.view = view;
    }

    @Override
    public void submitNewWordSet(final List<WordTranslation> translations) {
        new PleaseWaitProgressBarAddingNewWordSetPresenterDecorator.SuperClassWrapper(this.view) {
            @Override
            void doSuperMethod() {
                PleaseWaitProgressBarAddingNewWordSetPresenterDecorator.super.submitNewWordSet(translations);
            }
        }.execute();
    }

    private abstract static class SuperClassWrapper {
        private final AddingNewWordSetView view;

        SuperClassWrapper(AddingNewWordSetView view) {
            this.view = view;
        }

        void execute() {
            try {
                view.showPleaseWaitProgressBar();
                doSuperMethod();
            } finally {
                view.hidePleaseWaitProgressBar();
            }
        }

        abstract void doSuperMethod();
    }
}

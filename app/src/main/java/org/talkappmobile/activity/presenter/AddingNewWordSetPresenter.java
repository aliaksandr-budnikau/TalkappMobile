package org.talkappmobile.activity.presenter;

import java.util.List;

import org.talkappmobile.activity.interactor.AddingNewWordSetInteractor;
import org.talkappmobile.activity.listener.OnAddingNewWordSetPresenterListener;
import org.talkappmobile.activity.view.AddingNewWordSetFragmentView;
import org.talkappmobile.model.WordSet;

public class AddingNewWordSetPresenter implements OnAddingNewWordSetPresenterListener {
    private final AddingNewWordSetFragmentView view;
    private final AddingNewWordSetInteractor interactor;

    public AddingNewWordSetPresenter(AddingNewWordSetFragmentView view, AddingNewWordSetInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

    public void submit(List<String> words) {
        try {
            view.showPleaseWaitProgressBar();
            interactor.submit(words, this);
        } finally {
            view.hidePleaseWaitProgressBar();
        }
    }

    @Override
    public void onSentencesWereNotFound(int wordIndex) {
        view.markSentencesWereNotFound(wordIndex);
    }

    @Override
    public void onSentencesWereFound(int wordIndex) {
        view.markSentencesWereFound(wordIndex);
    }

    @Override
    public void onSubmitSuccessfully(WordSet wordSet) {
        view.submitSuccessfully(wordSet);
    }

    @Override
    public void onWordIsEmpty(int wordIndex) {
        view.markWordIsEmpty(wordIndex);
    }

    @Override
    public void onWordIsDuplicate(int wordIndex) {
        view.markWordIsDuplicate(wordIndex);
    }

    @Override
    public void onTranslationWasNotFound(int wordIndex) {
        view.markTranslationWasNotFound(wordIndex);
    }
}
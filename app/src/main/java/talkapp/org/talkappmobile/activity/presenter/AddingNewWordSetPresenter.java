package talkapp.org.talkappmobile.activity.presenter;

import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.AddingNewWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnAddingNewWordSetPresenterListener;
import talkapp.org.talkappmobile.activity.view.AddingNewWordSetFragmentView;
import talkapp.org.talkappmobile.model.WordSet;

public class AddingNewWordSetPresenter implements OnAddingNewWordSetPresenterListener {
    private final AddingNewWordSetFragmentView view;
    private final AddingNewWordSetInteractor interactor;

    public AddingNewWordSetPresenter(AddingNewWordSetFragmentView view, AddingNewWordSetInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

    public void submit(List<String> words) {
        interactor.submit(words, this);
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
}
package talkapp.org.talkappmobile.activity.presenter;

import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.AddingNewWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnAddingNewWordSetListener;
import talkapp.org.talkappmobile.activity.view.AddingNewWordSetView;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

public class AddingNewWordSetPresenter implements OnAddingNewWordSetListener {
    private final AddingNewWordSetInteractor interactor;
    private final AddingNewWordSetView view;

    public AddingNewWordSetPresenter(AddingNewWordSetView view, AddingNewWordSetInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

    public void initialize() {
        interactor.initialize(this);
    }

    @Override
    public void onNewWordSetDraftLoaded(WordTranslation[] words) {
        view.onNewWordSetDraftLoaded(words);
    }

    @Override
    public void onNewWordSuccessfullySubmitted(WordSet wordSet) {
        view.onNewWordSuccessfullySubmitted(wordSet);
    }

    @Override
    public void onSomeWordIsEmpty() {
        view.onSomeWordIsEmpty();
    }

    public void submitNewWordSet(List<WordTranslation> translations) {
        interactor.submitNewWordSet(translations, this);
    }

    public void saveChangedDraft(List<WordTranslation> vocabulary) {
        interactor.saveChangedDraft(vocabulary);
    }
}
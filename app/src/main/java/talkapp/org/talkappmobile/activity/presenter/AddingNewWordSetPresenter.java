package talkapp.org.talkappmobile.activity.presenter;

import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.AddingNewWordSetInteractor;
import talkapp.org.talkappmobile.activity.view.AddingNewWordSetFragmentView;

public class AddingNewWordSetPresenter {
    private final AddingNewWordSetFragmentView view;
    private final AddingNewWordSetInteractor interactor;

    public AddingNewWordSetPresenter(AddingNewWordSetFragmentView view, AddingNewWordSetInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

    public void submit(List<String> words) {
        try {
            view.showPleaseWaitProgressBar();
            interactor.submit(words);
        } finally {
            view.hidePleaseWaitProgressBar();
        }
    }
}
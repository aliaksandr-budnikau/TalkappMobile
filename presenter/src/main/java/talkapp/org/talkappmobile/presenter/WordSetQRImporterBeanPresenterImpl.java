package talkapp.org.talkappmobile.presenter;

import talkapp.org.talkappmobile.interactor.WordSetQRImporterBeanInteractor;
import talkapp.org.talkappmobile.model.NewWordSetDraft;
import talkapp.org.talkappmobile.view.WordSetQRImporterView;

public class WordSetQRImporterBeanPresenterImpl implements WordSetQRImporterBeanPresenter {
    private final WordSetQRImporterBeanInteractor interactor;
    private final WordSetQRImporterView view;

    public WordSetQRImporterBeanPresenterImpl(WordSetQRImporterView view, WordSetQRImporterBeanInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

    @Override
    public void saveWordSetDraft(NewWordSetDraft wordSetDraft) {
        interactor.saveWordSetDraft(wordSetDraft);
    }
}

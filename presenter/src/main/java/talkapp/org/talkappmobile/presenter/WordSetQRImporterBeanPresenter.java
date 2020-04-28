package talkapp.org.talkappmobile.presenter;

import talkapp.org.talkappmobile.interactor.WordSetQRImporterBeanInteractor;
import talkapp.org.talkappmobile.model.NewWordSetDraft;
import talkapp.org.talkappmobile.view.WordSetQRImporterView;

public class WordSetQRImporterBeanPresenter {
    private final WordSetQRImporterBeanInteractor interactor;
    private final WordSetQRImporterView view;

    public WordSetQRImporterBeanPresenter(WordSetQRImporterView view, WordSetQRImporterBeanInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

    public void saveWordSetDraft(NewWordSetDraft wordSetDraft) {
        interactor.saveWordSetDraft(wordSetDraft);
    }
}

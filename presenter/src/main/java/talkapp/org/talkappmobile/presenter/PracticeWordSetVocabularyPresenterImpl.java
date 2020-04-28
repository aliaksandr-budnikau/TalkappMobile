package talkapp.org.talkappmobile.presenter;

import java.util.List;

import talkapp.org.talkappmobile.interactor.PracticeWordSetVocabularyInteractor;
import talkapp.org.talkappmobile.listener.OnPracticeWordSetVocabularyListener;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.LocalCacheIsEmptyException;
import talkapp.org.talkappmobile.view.PracticeWordSetVocabularyView;

public class PracticeWordSetVocabularyPresenterImpl implements OnPracticeWordSetVocabularyListener, PracticeWordSetVocabularyPresenter {

    private final PracticeWordSetVocabularyView view;
    private final PracticeWordSetVocabularyInteractor interactor;

    public PracticeWordSetVocabularyPresenterImpl(PracticeWordSetVocabularyView view, PracticeWordSetVocabularyInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

    @Override
    public void initialise(WordSet wordSet) {
        try {
            view.onInitializeBeginning();
            interactor.initialiseVocabulary(wordSet, this);
        } catch (LocalCacheIsEmptyException e) {
            view.onLocalCacheIsEmptyException(e);
        } finally {
            view.onInitializeEnd();
        }
    }

    @Override
    public void onWordSetVocabularyFound(List<WordTranslation> wordTranslations) {
        view.setWordSetVocabularyList(wordTranslations);
    }

    @Override
    public void onUpdateNotCustomWordSet() {
        view.onUpdateNotCustomWordSet();
    }

    @Override
    public void onUpdateCustomWordSetFinished() {
        view.onUpdateCustomWordSetFinished();
    }

    @Override
    public void updateCustomWordSet(int editedItemPosition, WordTranslation wordTranslation) {
        try {
            view.onInitializeBeginning();
            interactor.updateCustomWordSet(editedItemPosition, wordTranslation, this);
        } finally {
            view.onInitializeEnd();
        }
    }
}

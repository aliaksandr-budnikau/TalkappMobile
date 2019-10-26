package talkapp.org.talkappmobile.activity.presenter;

import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetVocabularyInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetVocabularyListener;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetVocabularyView;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.impl.LocalCacheIsEmptyException;

public class PracticeWordSetVocabularyPresenter implements OnPracticeWordSetVocabularyListener {

    private final PracticeWordSetVocabularyView view;
    private final PracticeWordSetVocabularyInteractor interactor;

    public PracticeWordSetVocabularyPresenter(PracticeWordSetVocabularyView view, PracticeWordSetVocabularyInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

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
}

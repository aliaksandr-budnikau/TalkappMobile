package org.talkappmobile.activity.presenter;

import org.talkappmobile.model.WordSet;
import org.talkappmobile.model.WordTranslation;
import org.talkappmobile.service.impl.LocalCacheIsEmptyException;

import java.util.List;

import org.talkappmobile.activity.interactor.PracticeWordSetVocabularyInteractor;
import org.talkappmobile.activity.listener.OnPracticeWordSetVocabularyListener;
import org.talkappmobile.activity.view.PracticeWordSetVocabularyView;

public class PracticeWordSetVocabularyPresenter implements OnPracticeWordSetVocabularyListener {

    private final WordSet wordSet;
    private final PracticeWordSetVocabularyView view;
    private final PracticeWordSetVocabularyInteractor interactor;

    public PracticeWordSetVocabularyPresenter(WordSet wordSet, PracticeWordSetVocabularyView view, PracticeWordSetVocabularyInteractor interactor) {
        this.wordSet = wordSet;
        this.view = view;
        this.interactor = interactor;
    }

    public void initialise() {
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

package talkapp.org.talkappmobile.activity.presenter;

import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetVocabularyInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetVocabularyListener;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetVocabularyView;
import talkapp.org.talkappmobile.component.backend.impl.LocalCacheIsEmptyException;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

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

    public void onPronounceWordButtonClick(WordTranslation translation) {
        interactor.pronounceWordButtonClick(translation, this);
    }
}

package talkapp.org.talkappmobile.activity;

import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

public class PracticeWordSetVocabularyPresenter implements PracticeWordSetVocabularyInteractor.OnWordTranslationListener {

    private final WordSet wordSet;
    private final PracticeWordSetVocabularyView view;
    @Inject
    PracticeWordSetVocabularyInteractor interactor;

    public PracticeWordSetVocabularyPresenter(WordSet wordSet, PracticeWordSetVocabularyView view) {
        this.wordSet = wordSet;
        this.view = view;
        DIContext.get().inject(this);
    }

    public void onResume() {
        interactor.initialiseVocabulary(wordSet, this);
    }

    @Override
    public void onWordTranslationsFound(List<WordTranslation> wordTranslations) {
        view.setWordTranslationList(wordTranslations);
    }
}

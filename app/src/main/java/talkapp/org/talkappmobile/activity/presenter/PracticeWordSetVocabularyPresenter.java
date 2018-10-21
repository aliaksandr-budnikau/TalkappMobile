package talkapp.org.talkappmobile.activity.presenter;

import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.config.DIContextUtils;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

public class PracticeWordSetVocabularyPresenter implements OnPracticeWordSetVocabularyListener {

    private final WordSet wordSet;
    private final PracticeWordSetVocabularyView view;
    @Inject
    PracticeWordSetVocabularyInteractor interactor;

    public PracticeWordSetVocabularyPresenter(WordSet wordSet, PracticeWordSetVocabularyView view) {
        this.wordSet = wordSet;
        this.view = view;
        DIContextUtils.get().inject(this);
    }

    public void onResume() {
        interactor.initialiseVocabulary(wordSet, this);
    }

    @Override
    public void onWordSetVocabularyFound(List<WordTranslation> wordTranslations) {
        view.setWordSetVocabularyList(wordTranslations);
    }

    public void onPronounceWordButtonClick(WordTranslation translation) {
        interactor.pronounceWordButtonClick(translation, this);
    }
}

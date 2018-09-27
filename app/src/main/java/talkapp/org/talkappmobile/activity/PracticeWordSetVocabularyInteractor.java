package talkapp.org.talkappmobile.activity;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.backend.WordTranslationService;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

public class PracticeWordSetVocabularyInteractor {
    @Inject
    WordTranslationService wordTranslationService;
    @Inject
    AuthSign authSign;

    public PracticeWordSetVocabularyInteractor() {
        DIContext.get().inject(this);
    }

    public void initialiseVocabulary(WordSet wordSet, OnWordTranslationListener listener) {
        listener.onWordTranslationsFound(getWordTranslations(wordSet));
    }

    private List<WordTranslation> getWordTranslations(WordSet wordSet) {
        try {
            return wordTranslationService.findByWordSetIdAndByLanguage(wordSet.getId(), "russian", authSign).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    interface OnWordTranslationListener {
        void onWordTranslationsFound(List<WordTranslation> wordTranslations);
    }
}
package talkapp.org.talkappmobile.activity.presenter;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.Speaker;
import talkapp.org.talkappmobile.component.backend.WordTranslationService;
import talkapp.org.talkappmobile.config.DIContextUtils;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

public class PracticeWordSetVocabularyInteractor {
    @Inject
    WordTranslationService wordTranslationService;
    @Inject
    AuthSign authSign;
    @Inject
    Speaker speaker;

    public PracticeWordSetVocabularyInteractor() {
        DIContextUtils.get().inject(this);
    }

    public void initialiseVocabulary(WordSet wordSet, OnPracticeWordSetVocabularyListener listener) {
        listener.onWordSetVocabularyFound(getWordTranslations(wordSet));
    }

    private List<WordTranslation> getWordTranslations(WordSet wordSet) {
        try {
            return wordTranslationService.findByWordSetIdAndByLanguage(wordSet.getId(), "russian", authSign).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void pronounceWordButtonClick(WordTranslation translation, OnPracticeWordSetVocabularyListener listener) {
        if (translation == null) {
            return;
        }
        try {
            speaker.speak(translation.getWord()).get();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
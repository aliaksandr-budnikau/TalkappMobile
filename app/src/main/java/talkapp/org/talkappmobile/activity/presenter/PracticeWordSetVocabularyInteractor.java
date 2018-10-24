package talkapp.org.talkappmobile.activity.presenter;

import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.component.Speaker;
import talkapp.org.talkappmobile.component.backend.BackendServer;
import talkapp.org.talkappmobile.config.DIContextUtils;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

public class PracticeWordSetVocabularyInteractor {
    @Inject
    BackendServer server;
    @Inject
    Speaker speaker;

    public PracticeWordSetVocabularyInteractor() {
        DIContextUtils.get().inject(this);
    }

    public void initialiseVocabulary(WordSet wordSet, OnPracticeWordSetVocabularyListener listener) {
        listener.onWordSetVocabularyFound(getWordTranslations(wordSet));
    }

    private List<WordTranslation> getWordTranslations(WordSet wordSet) {
        return server.findWordTranslationsByWordSetIdAndByLanguage(wordSet.getId(), "russian");
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
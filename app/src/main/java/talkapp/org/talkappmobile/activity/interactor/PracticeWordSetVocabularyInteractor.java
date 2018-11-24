package talkapp.org.talkappmobile.activity.interactor;

import java.util.List;

import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetVocabularyListener;
import talkapp.org.talkappmobile.component.Speaker;
import talkapp.org.talkappmobile.component.backend.BackendServer;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

public class PracticeWordSetVocabularyInteractor {
    private final BackendServer server;
    private final Speaker speaker;

    public PracticeWordSetVocabularyInteractor(BackendServer server, Speaker speaker) {
        this.server = server;
        this.speaker = speaker;
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
            speaker.speak(translation.getWord());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
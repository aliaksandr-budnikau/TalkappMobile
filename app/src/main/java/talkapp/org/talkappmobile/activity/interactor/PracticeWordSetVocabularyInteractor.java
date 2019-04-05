package talkapp.org.talkappmobile.activity.interactor;

import android.support.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetVocabularyListener;
import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

public class PracticeWordSetVocabularyInteractor {
    private final DataServer server;

    public PracticeWordSetVocabularyInteractor(DataServer server) {
        this.server = server;
    }

    public void initialiseVocabulary(WordSet wordSet, OnPracticeWordSetVocabularyListener listener) {
        listener.onWordSetVocabularyFound(getWordTranslations(wordSet));
    }

    private List<WordTranslation> getWordTranslations(WordSet wordSet) {
        if (wordSet.getId() == 0) {
            return server.findWordTranslationsByWordsAndByLanguage(getWords(wordSet), "russian");
        } else {
            return server.findWordTranslationsByWordSetIdAndByLanguage(wordSet.getId(), "russian");
        }
    }

    @NonNull
    private LinkedList<String> getWords(WordSet wordSet) {
        List<Word2Tokens> word2Tokens = wordSet.getWords();
        LinkedList<String> words = new LinkedList<>();
        for (Word2Tokens word2Token : word2Tokens) {
            words.add(word2Token.getWord());
        }
        return words;
    }
}
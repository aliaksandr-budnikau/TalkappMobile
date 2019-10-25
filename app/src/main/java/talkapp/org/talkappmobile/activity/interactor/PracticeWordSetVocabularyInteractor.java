package talkapp.org.talkappmobile.activity.interactor;

import android.support.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetVocabularyListener;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.WordSetService;

public class PracticeWordSetVocabularyInteractor {
    private final DataServer server;
    private final WordSetService wordSetService;

    public PracticeWordSetVocabularyInteractor(DataServer server, WordSetService wordSetService) {
        this.server = server;
        this.wordSetService = wordSetService;
    }

    public void initialiseVocabulary(int wordSetId, OnPracticeWordSetVocabularyListener listener) {
        listener.onWordSetVocabularyFound(getWordTranslations(wordSetId));
    }

    private List<WordTranslation> getWordTranslations(int wordSetId) {
        if (wordSetId == 0) {
            return server.findWordTranslationsByWordsAndByLanguage(getWords(wordSetId), "russian");
        } else {
            return server.findWordTranslationsByWordSetIdAndByLanguage(wordSetId, "russian");
        }
    }

    @NonNull
    private LinkedList<String> getWords(int wordSetId) {
        WordSet wordSet = wordSetService.findById(wordSetId);
        List<Word2Tokens> word2Tokens = wordSet.getWords();
        LinkedList<String> words = new LinkedList<>();
        for (Word2Tokens word2Token : word2Tokens) {
            words.add(word2Token.getWord());
        }
        return words;
    }
}
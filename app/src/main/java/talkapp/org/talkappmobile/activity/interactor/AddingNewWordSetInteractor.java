package talkapp.org.talkappmobile.activity.interactor;

import java.util.List;

import talkapp.org.talkappmobile.activity.listener.OnAddingNewWordSetPresenterListener;
import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.component.backend.impl.LocalCacheIsEmptyException;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class AddingNewWordSetInteractor {
    private static final int WORDS_NUMBER = 6;
    private final DataServer server;

    public AddingNewWordSetInteractor(DataServer server) {
        this.server = server;
    }

    public void submit(List<String> words, OnAddingNewWordSetPresenterListener listener) {
        boolean valid = true;
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i).trim().toLowerCase();
            if (isEmpty(word)) {
                valid = false;
                listener.onWordIsEmpty(i);
                continue;
            }
            words.set(i, word);
            Word2Tokens tokens = new Word2Tokens(word, word);
            List<Sentence> sentences;
            try {
                sentences = server.findSentencesByWords(tokens, WORDS_NUMBER, 0);
            } catch (LocalCacheIsEmptyException e) {
                server.initLocalCacheOfAllSentencesForThisWord(word, WORDS_NUMBER);
                try {
                    sentences = server.findSentencesByWords(tokens, WORDS_NUMBER, 0);
                } catch (LocalCacheIsEmptyException ne) {
                    sentences = emptyList();
                }
            }
            if (sentences.isEmpty()) {
                valid = false;
                listener.onSentencesWereNotFound(i);
            } else {
                listener.onSentencesWereFound(i);
            }
        }

        if (valid) {
            WordSet set = new WordSet(words);
            set.setTopicId("43");
            WordSet wordSet = server.saveNewCustomWordSet(set);
            listener.onSubmitSuccessfully(wordSet);
        }
    }
}
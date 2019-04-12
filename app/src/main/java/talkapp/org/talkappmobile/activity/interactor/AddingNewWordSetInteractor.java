package talkapp.org.talkappmobile.activity.interactor;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.activity.listener.OnAddingNewWordSetPresenterListener;
import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.component.backend.impl.LocalCacheIsEmptyException;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class AddingNewWordSetInteractor {
    private static final int DEFAULT_TOP_SUM = 20000;
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
            WordSet set = new WordSet();
            LinkedList<Word2Tokens> word2Tokens = new LinkedList<>();
            int topSum = 0;
            for (String word : words) {
                WordTranslation translation = server.findWordTranslationsByWordAndByLanguage("russian", word);
                if (translation.getTop() == null) {
                    topSum += DEFAULT_TOP_SUM;
                } else {
                    topSum += translation.getTop();
                }
                word2Tokens.add(new Word2Tokens(translation.getWord(), translation.getTokens()));
            }
            set.setTop(topSum / words.size());
            set.setWords(word2Tokens);
            set.setTopicId("43");
            WordSet wordSet = server.saveNewCustomWordSet(set);
            listener.onSubmitSuccessfully(wordSet);
        }
    }
}
package talkapp.org.talkappmobile.activity.interactor;

import android.support.annotation.NonNull;

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
        normalizeAll(words);
        if (isAnyEmpty(words, listener)) {
            return;
        }
        if (hasDuplicates(words, listener)) {
            return;
        }

        if (anyHasNoSentences(words, listener)) {
            return;
        }

        List<WordTranslation> translations = findAllTranslations(words, listener);
        if (words.size() != translations.size()) {
            return;
        }

        int totalTop = countTotalTop(translations);
        LinkedList<Word2Tokens> word2Tokens = getWord2Tokens(translations);
        WordSet wordSet = server.saveNewCustomWordSet(makeWordSetDto(totalTop, word2Tokens));
        listener.onSubmitSuccessfully(wordSet);
    }

    @NonNull
    private WordSet makeWordSetDto(int totalTop, LinkedList<Word2Tokens> word2Tokens) {
        WordSet set = new WordSet();
        set.setTop(totalTop);
        set.setWords(word2Tokens);
        set.setTopicId("43");
        return set;
    }

    @NonNull
    private LinkedList<Word2Tokens> getWord2Tokens(List<WordTranslation> translations) {
        LinkedList<Word2Tokens> word2Tokens = new LinkedList<>();
        for (WordTranslation translation : translations) {
            word2Tokens.add(new Word2Tokens(translation.getWord(), translation.getTokens(), null));
        }
        return word2Tokens;
    }

    private int countTotalTop(List<WordTranslation> translations) {
        int totalTop = 0;
        for (WordTranslation translation : translations) {
            if (translation.getTop() == null) {
                totalTop += DEFAULT_TOP_SUM;
            } else {
                totalTop += translation.getTop();
            }
        }
        totalTop /= translations.size();
        return totalTop;
    }

    private List<WordTranslation> findAllTranslations(List<String> words, OnAddingNewWordSetPresenterListener listener) {
        LinkedList<WordTranslation> translations = new LinkedList<>();
        for (int i = 0; i < words.size(); i++) {
            WordTranslation translation = server.findWordTranslationsByWordAndByLanguage("russian", words.get(i));
            if (translation == null) {
                listener.onTranslationWasNotFound(i);
                continue;
            }
            translations.add(translation);

        }
        return translations;
    }

    private boolean hasDuplicates(List<String> words, OnAddingNewWordSetPresenterListener listener) {
        boolean hasDuplicates = false;
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            if (words.subList(0, i).contains(word)) {
                hasDuplicates = true;
                listener.onWordIsDuplicate(i);
            }
        }
        return hasDuplicates;
    }

    private boolean anyHasNoSentences(List<String> words, OnAddingNewWordSetPresenterListener listener) {
        boolean anyHasNoSentences = false;
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            Word2Tokens tokens = new Word2Tokens(word, word, null);
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
                anyHasNoSentences = true;
                listener.onSentencesWereNotFound(i);
            } else {
                listener.onSentencesWereFound(i);
            }
        }
        return anyHasNoSentences;
    }

    private boolean isAnyEmpty(List<String> words, OnAddingNewWordSetPresenterListener listener) {
        boolean anyEmpty = false;
        for (int i = 0; i < words.size(); i++) {
            if (isEmpty(words.get(i))) {
                anyEmpty = true;
                listener.onWordIsEmpty(i);
            }
        }
        return anyEmpty;
    }

    private void normalizeAll(List<String> words) {
        for (int i = 0; i < words.size(); i++) {
            words.set(i, words.get(i).trim().toLowerCase());
        }
    }
}
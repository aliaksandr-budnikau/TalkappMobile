package org.talkappmobile.activity.interactor;

import android.support.annotation.NonNull;

import org.talkappmobile.model.Sentence;
import org.talkappmobile.model.Word2Tokens;
import org.talkappmobile.model.WordSet;
import org.talkappmobile.model.WordTranslation;
import org.talkappmobile.service.DataServer;
import org.talkappmobile.service.WordSetService;
import org.talkappmobile.service.impl.LocalCacheIsEmptyException;

import java.util.LinkedList;
import java.util.List;

import org.talkappmobile.activity.listener.OnAddingNewWordSetPresenterListener;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class AddingNewWordSetInteractor {
    private static final int WORDS_NUMBER = 6;
    @NonNull
    private final DataServer server;
    @NonNull
    private final WordSetService wordSetService;

    public AddingNewWordSetInteractor(@NonNull DataServer server, @NonNull WordSetService wordSetService) {
        this.server = server;
        this.wordSetService = wordSetService;
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

        WordSet wordSet = wordSetService.createNewCustomWordSet(translations);
        listener.onSubmitSuccessfully(wordSet);
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
            Word2Tokens tokens = new Word2Tokens(word, word, 0);
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
package talkapp.org.talkappmobile.activity.interactor;

import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.events.NewWordIsDuplicateEM;
import talkapp.org.talkappmobile.events.NewWordIsEmptyEM;
import talkapp.org.talkappmobile.events.NewWordSentencesWereFoundEM;
import talkapp.org.talkappmobile.events.NewWordSentencesWereNotFoundEM;
import talkapp.org.talkappmobile.events.NewWordSuccessfullySubmittedEM;
import talkapp.org.talkappmobile.events.NewWordTranslationWasNotFoundEM;
import talkapp.org.talkappmobile.model.NewWordWithTranslation;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.TextToken;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.WordTranslationService;
import talkapp.org.talkappmobile.service.impl.LocalCacheIsEmptyException;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class AddingNewWordSetInteractor {
    public static final String RUSSIAN_LANGUAGE = "russian";
    private static final int WORDS_NUMBER = 6;
    @NonNull
    private final DataServer server;
    @NonNull
    private final WordSetService wordSetService;
    @NonNull
    private final WordTranslationService wordTranslationService;
    @NonNull
    private final EventBus eventBus;

    public AddingNewWordSetInteractor(@NonNull DataServer server, @NonNull WordSetService wordSetService, @NonNull WordTranslationService wordTranslationService, @NonNull EventBus eventBus) {
        this.server = server;
        this.wordSetService = wordSetService;
        this.wordTranslationService = wordTranslationService;
        this.eventBus = eventBus;
    }

    public void submit(List<String> words) {
        List<NewWordWithTranslation> normalizedWords = normalizeAll(words);
        if (isAnyEmpty(normalizedWords)) {
            return;
        }
        if (hasDuplicates(normalizedWords)) {
            return;
        }

        if (anyHasNoSentences(normalizedWords)) {
            return;
        }

        List<WordTranslation> translations = new LinkedList<>();
        for (NewWordWithTranslation normalizedWord : normalizedWords) {
            WordTranslation result;
            if (isEmpty(normalizedWord.getTranslation())) {
                result = server.findWordTranslationsByWordAndByLanguage(RUSSIAN_LANGUAGE, normalizedWord.getWord());
                if (result == null) {
                    eventBus.post(new NewWordTranslationWasNotFoundEM(normalizedWords.indexOf(normalizedWord)));
                    continue;
                }
            } else {
                WordTranslation translation = new WordTranslation();
                translation.setLanguage(RUSSIAN_LANGUAGE);
                translation.setTranslation(normalizedWord.getTranslation());
                translation.setWord(normalizedWord.getWord());
                translation.setTokens(normalizedWord.getWord());
                wordTranslationService.saveWordTranslations(asList(translation));
                result = translation;
            }
            translations.add(result);
        }
        if (words.size() != translations.size()) {
            return;
        }

        WordSet wordSet = wordSetService.createNewCustomWordSet(translations);
        eventBus.post(new NewWordSuccessfullySubmittedEM(wordSet));
    }

    private boolean hasDuplicates(List<NewWordWithTranslation> words) {
        boolean hasDuplicates = false;
        for (int i = 0; i < words.size(); i++) {
            NewWordWithTranslation word = words.get(i);
            if (words.subList(0, i).contains(word)) {
                hasDuplicates = true;
                eventBus.post(new NewWordIsDuplicateEM(i));
            }
        }
        return hasDuplicates;
    }

    private boolean anyHasNoSentences(List<NewWordWithTranslation> words) {
        boolean anyHasNoSentences = false;
        for (int i = 0; i < words.size(); i++) {
            NewWordWithTranslation wordWithTranslation = words.get(i);
            String word = wordWithTranslation.getWord();
            Word2Tokens tokens = new Word2Tokens(word, word, 0);
            List<Sentence> sentences;
            if (isEmpty(wordWithTranslation.getTranslation())) {
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
            } else {
                Sentence sentence = new Sentence();
                sentence.setId(valueOf(System.currentTimeMillis()));
                sentence.setTokens(getTextTokens(wordWithTranslation));
                HashMap<String, String> translations = new HashMap<>();
                translations.put("russian", wordWithTranslation.getTranslation());
                sentence.setTranslations(translations);
                sentence.setText(wordWithTranslation.getWord());
                sentences = singletonList(sentence);
                server.initLocalCacheOfAllSentencesForThisWord(word, sentences, WORDS_NUMBER);
            }
            if (sentences.isEmpty()) {
                anyHasNoSentences = true;
                eventBus.post(new NewWordSentencesWereNotFoundEM(i));
            } else {
                eventBus.post(new NewWordSentencesWereFoundEM(i));
            }
        }
        return anyHasNoSentences;
    }

    @NonNull
    private LinkedList<TextToken> getTextTokens(NewWordWithTranslation wordWithTranslation) {
        LinkedList<TextToken> textTokens = new LinkedList<>();
        TextToken textToken = new TextToken();
        textToken.setToken(wordWithTranslation.getWord());
        textToken.setStartOffset(0);
        textToken.setEndOffset(wordWithTranslation.getWord().length());
        textToken.setPosition(0);
        textTokens.add(textToken);
        return textTokens;
    }

    private boolean isAnyEmpty(List<NewWordWithTranslation> words) {
        boolean anyEmpty = false;
        for (int i = 0; i < words.size(); i++) {
            if (isEmpty(words.get(i).getWord())) {
                anyEmpty = true;
                eventBus.post(new NewWordIsEmptyEM(i));
            }
        }
        return anyEmpty;
    }

    private List<NewWordWithTranslation> normalizeAll(List<String> inputs) {
        LinkedList<NewWordWithTranslation> words = new LinkedList<>();
        for (String input : inputs) {
            String[] wordAndTranslation = input.split("\\|");
            String word, translation;
            if (wordAndTranslation.length == 2) {
                word = wordAndTranslation[0].trim().toLowerCase();
                translation = wordAndTranslation[1].trim().toLowerCase();
            } else if (wordAndTranslation.length == 1) {
                word = wordAndTranslation[0].trim().toLowerCase();
                translation = null;
            } else {
                word = input.trim().toLowerCase();
                translation = null;
            }
            words.add(new NewWordWithTranslation(word, translation));
        }
        return words;
    }
}
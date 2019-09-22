package talkapp.org.talkappmobile.activity.custom.controller;

import android.support.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.events.NewWordIsEmptyEM;
import talkapp.org.talkappmobile.events.NewWordSentencesWereFoundEM;
import talkapp.org.talkappmobile.events.NewWordSentencesWereNotFoundEM;
import talkapp.org.talkappmobile.events.NewWordTranslationWasNotFoundEM;
import talkapp.org.talkappmobile.events.PhraseTranslationInputPopupOkClickedEM;
import talkapp.org.talkappmobile.events.PhraseTranslationInputWasValidatedSuccessfullyEM;
import talkapp.org.talkappmobile.model.NewWordWithTranslation;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.TextToken;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.WordTranslationService;
import talkapp.org.talkappmobile.service.impl.LocalCacheIsEmptyException;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class PhraseTranslationInputTextViewController {
    private static final int WORDS_NUMBER = 6;
    private static final String RUSSIAN_LANGUAGE = "russian";
    private final EventBus eventBus;
    private final DataServer server;
    private final WordTranslationService wordTranslationService;

    public PhraseTranslationInputTextViewController(@NonNull EventBus eventBus, @NonNull DataServer server, @NonNull ServiceFactory factory) {
        this.eventBus = eventBus;
        this.server = server;
        this.wordTranslationService = factory.getWordTranslationService();
    }

    public void handle(PhraseTranslationInputPopupOkClickedEM event) {
        String translation = event.getTranslation();
        String phrase = event.getPhrase();

        NewWordWithTranslation normalizedPhrase = normalizeAll(phrase, translation);
        if (isEmpty(normalizedPhrase)) {
            return;
        }

        if (hasNoSentences(normalizedPhrase)) {
            return;
        }

        WordTranslation result;
        if (StringUtils.isEmpty(normalizedPhrase.getTranslation())) {
            result = server.findWordTranslationsByWordAndByLanguage(RUSSIAN_LANGUAGE, normalizedPhrase.getWord());
            if (result == null) {
                eventBus.post(new NewWordTranslationWasNotFoundEM());
                return;
            }
        } else {
            WordTranslation wordTranslation = new WordTranslation();
            wordTranslation.setLanguage(RUSSIAN_LANGUAGE);
            wordTranslation.setTranslation(normalizedPhrase.getTranslation());
            wordTranslation.setWord(normalizedPhrase.getWord());
            wordTranslation.setTokens(normalizedPhrase.getWord());
            wordTranslationService.saveWordTranslations(asList(wordTranslation));
        }
        eventBus.post(new PhraseTranslationInputWasValidatedSuccessfullyEM());
    }

    private NewWordWithTranslation normalizeAll(String phrase, String translation) {
        if (StringUtils.isEmpty(translation)) {
            return new NewWordWithTranslation(phrase.trim().toLowerCase(), null);
        }
        return new NewWordWithTranslation(phrase.trim(), translation.trim());
    }

    private boolean isEmpty(NewWordWithTranslation phrase) {
        if (StringUtils.isEmpty(phrase.getWord())) {
            eventBus.post(new NewWordIsEmptyEM());
            return true;
        }
        return false;
    }

    private boolean hasNoSentences(NewWordWithTranslation phrase) {
        boolean anyHasNoSentences = false;
        String word = phrase.getWord();
        Word2Tokens tokens = new Word2Tokens(word, word, 0);
        List<Sentence> sentences;
        if (StringUtils.isEmpty(phrase.getTranslation())) {
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
            sentence.setTokens(getTextTokens(phrase));
            HashMap<String, String> translations = new HashMap<>();
            translations.put("russian", phrase.getTranslation());
            sentence.setTranslations(translations);
            sentence.setText(phrase.getWord());
            sentences = singletonList(sentence);
            server.initLocalCacheOfAllSentencesForThisWord(word, sentences, WORDS_NUMBER);
        }
        if (sentences.isEmpty()) {
            anyHasNoSentences = true;
            eventBus.post(new NewWordSentencesWereNotFoundEM());
        } else {
            eventBus.post(new NewWordSentencesWereFoundEM());
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
}
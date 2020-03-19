package talkapp.org.talkappmobile.service.impl;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.TextToken;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.repository.WordTranslationRepository;
import talkapp.org.talkappmobile.service.SentenceProvider;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

class WordTranslationSentenceProviderDecorator extends SentenceProviderDecorator {
    private final WordTranslationRepository wordTranslationRepository;

    public WordTranslationSentenceProviderDecorator(SentenceProvider provider, WordTranslationRepository wordTranslationRepository) {
        super(provider);
        this.wordTranslationRepository = wordTranslationRepository;
    }

    @Override
    public List<Sentence> find(Word2Tokens word) {
        List<Sentence> sentences = super.find(word);
        if (!sentences.isEmpty()) {
            return sentences;
        }
        WordTranslation wordTranslation = findByWordAndLanguage(word.getWord());
        if (wordTranslation == null) {
            return emptyList();
        }
        return new ArrayList<>(asList(convertToSentence(wordTranslation)));
    }

    private WordTranslation findByWordAndLanguage(String word) {
        return wordTranslationRepository.findByWordAndByLanguage(word, "russian");
    }

    public Sentence convertToSentence(WordTranslation wordTranslation) {
        Sentence sentence = new Sentence();
        sentence.setId(valueOf(System.currentTimeMillis()));
        sentence.setTokens(getTextTokens(wordTranslation));
        HashMap<String, String> translations = new HashMap<>();
        translations.put(wordTranslation.getLanguage(), wordTranslation.getTranslation());
        sentence.setTranslations(translations);
        sentence.setText(wordTranslation.getWord());
        return sentence;
    }

    @NonNull
    private LinkedList<TextToken> getTextTokens(WordTranslation wordTranslation) {
        LinkedList<TextToken> textTokens = new LinkedList<>();
        TextToken textToken = new TextToken();
        textToken.setToken(wordTranslation.getWord());
        textToken.setStartOffset(0);
        textToken.setEndOffset(wordTranslation.getWord().length());
        textToken.setPosition(0);
        textTokens.add(textToken);
        return textTokens;
    }
}
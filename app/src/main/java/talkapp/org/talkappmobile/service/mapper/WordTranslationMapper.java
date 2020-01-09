package talkapp.org.talkappmobile.service.mapper;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.LinkedList;

import talkapp.org.talkappmobile.mappings.SentenceIdMapping;
import talkapp.org.talkappmobile.mappings.WordTranslationMapping;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.TextToken;
import talkapp.org.talkappmobile.model.WordTranslation;

import static java.lang.String.valueOf;

public class WordTranslationMapper {

    public static final String DELIMITER = "_";
    private final ObjectMapper mapper;

    public WordTranslationMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public WordTranslation toDto(WordTranslationMapping mapping) {
        WordTranslation dto = new WordTranslation();
        String word = mapping.getWord().split(DELIMITER)[0];
        dto.setId(mapping.getId());
        dto.setWord(word);
        dto.setTranslation(mapping.getTranslation());
        dto.setLanguage(mapping.getLanguage());
        dto.setTop(mapping.getTop());
        dto.setTokens(word);
        return dto;
    }

    public WordTranslationMapping toMapping(WordTranslation translation) {
        WordTranslationMapping mapping = new WordTranslationMapping();
        mapping.setId(translation.getId());
        mapping.setWord(translation.getWord() + DELIMITER + translation.getLanguage());
        mapping.setTranslation(translation.getTranslation());
        mapping.setLanguage(translation.getLanguage());
        mapping.setTop(translation.getTop());
        return mapping;
    }

    public Sentence convertToSentence(WordTranslation wordTranslation) {
        Sentence sentence = new Sentence();
        SentenceIdMapping sentenceIdMapping = new SentenceIdMapping(valueOf(System.currentTimeMillis()), 6);
        try {
            sentence.setId(mapper.writeValueAsString(sentenceIdMapping));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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
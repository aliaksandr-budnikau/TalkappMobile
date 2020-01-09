package talkapp.org.talkappmobile.service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.mappings.SentenceIdMapping;
import talkapp.org.talkappmobile.mappings.SentenceMapping;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.SentenceContentScore;
import talkapp.org.talkappmobile.model.TextToken;

public class SentenceMapper {
    private final MapType HASH_MAP_OF_STRING_2_STRING_JAVA_TYPE;
    private final CollectionType LINKED_LIST_OF_TOKENS_JAVA_TYPE;
    private final CollectionType LINKED_LIST_OF_SENTENCE_ID_JAVA_TYPE;
    private final ObjectMapper mapper;

    public SentenceMapper(ObjectMapper mapper) {
        this.mapper = mapper;
        HASH_MAP_OF_STRING_2_STRING_JAVA_TYPE = mapper.getTypeFactory().constructMapType(HashMap.class, String.class, String.class);
        LINKED_LIST_OF_TOKENS_JAVA_TYPE = mapper.getTypeFactory().constructCollectionType(LinkedList.class, TextToken.class);
        LINKED_LIST_OF_SENTENCE_ID_JAVA_TYPE = mapper.getTypeFactory().constructCollectionType(LinkedList.class, SentenceIdMapping.class);
    }

    public SentenceMapping toMapping(Sentence sentence, String word, int wordsNumber) {
        SentenceMapping mapping = new SentenceMapping();
        try {
            mapping.setId(mapper.writeValueAsString(new SentenceIdMapping(sentence.getId(), word, wordsNumber)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        mapping.setText(sentence.getText());
        mapping.setContentScore(sentence.getContentScore() == null ? null : sentence.getContentScore().name());
        try {
            mapping.setTranslations(mapper.writeValueAsString(sentence.getTranslations()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        try {
            mapping.setTokens(mapper.writeValueAsString(sentence.getTokens()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return mapping;
    }

    public Sentence toDto(SentenceMapping mapping) {
        Sentence sentence = new Sentence();
        sentence.setId(mapping.getId());
        sentence.setText(mapping.getText());
        sentence.setContentScore(mapping.getContentScore() == null ? null : SentenceContentScore.valueOf(mapping.getContentScore()));
        Map<String, String> translation = null;
        try {
            translation = mapper.readValue(mapping.getTranslations(), HASH_MAP_OF_STRING_2_STRING_JAVA_TYPE);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        sentence.setTranslations(translation);

        List<TextToken> tokens = null;
        try {
            tokens = mapper.readValue(mapping.getTokens(), LINKED_LIST_OF_TOKENS_JAVA_TYPE);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        sentence.setTokens(tokens);
        return sentence;
    }

    public List<SentenceIdMapping> toSentenceIdMapping(String sentenceIdsAsString) {
        List<SentenceIdMapping> ids;
        try {
            ids = mapper.readValue(sentenceIdsAsString, LINKED_LIST_OF_SENTENCE_ID_JAVA_TYPE);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return ids;
    }

    public String toSentenceIdMapping(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
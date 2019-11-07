package talkapp.org.talkappmobile.service.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import talkapp.org.talkappmobile.mappings.WordTranslationMapping;
import talkapp.org.talkappmobile.model.WordTranslation;

public class WordTranslationMapper {

    public static final String DELIMITER = "_";
    private final ObjectMapper mapper;

    public WordTranslationMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public WordTranslation toDto(WordTranslationMapping mapping) {
        WordTranslation dto = new WordTranslation();
        String word = mapping.getWord().split(DELIMITER)[0];
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
}
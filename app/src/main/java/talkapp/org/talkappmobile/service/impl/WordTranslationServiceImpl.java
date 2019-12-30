package talkapp.org.talkappmobile.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.mappings.WordTranslationMapping;
import talkapp.org.talkappmobile.model.NewWordSetDraft;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.WordTranslationService;
import talkapp.org.talkappmobile.service.mapper.WordTranslationMapper;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;

public class WordTranslationServiceImpl implements WordTranslationService {
    public static final String RUSSIAN_LANGUAGE = "russian";
    private final WordTranslationDao wordTranslationDao;
    private final ObjectMapper mapper;
    private final WordTranslationMapper wordTranslationMapper;

    public WordTranslationServiceImpl(WordTranslationDao wordTranslationDao, ObjectMapper mapper) {
        this.wordTranslationDao = wordTranslationDao;
        this.mapper = mapper;
        this.wordTranslationMapper = new WordTranslationMapper(mapper);
    }

    @Override
    public void saveWordTranslations(List<WordTranslation> wordTranslations) {
        List<WordTranslationMapping> mappings = new LinkedList<>();
        for (WordTranslation wordTranslation : wordTranslations) {
            WordTranslationMapping mapping = wordTranslationMapper.toMapping(wordTranslation);
            if (StringUtils.isEmpty(mapping.getId())) {
                mapping.setId(valueOf(System.currentTimeMillis()));
            }
            mappings.add(mapping);
        }
        wordTranslationDao.save(mappings);
    }

    @Override
    public void saveWordTranslations(String phrase, String translation) {
        WordTranslation wordTranslation = new WordTranslation();
        wordTranslation.setLanguage(RUSSIAN_LANGUAGE);
        wordTranslation.setTranslation(translation);
        wordTranslation.setWord(phrase);
        wordTranslation.setTokens(phrase);
        saveWordTranslations(asList(wordTranslation));
    }

    @Override
    public void saveWordTranslations(NewWordSetDraft wordSetDraft) {
        List<WordTranslation> wordTranslations = wordSetDraft.getWordTranslations();
        for (WordTranslation wordTranslation : wordTranslations) {
            saveWordTranslations(wordTranslation.getWord(), wordTranslation.getTranslation());
        }
    }

    @Override
    public WordTranslation findByWordAndLanguage(String word, String language) {
        WordTranslationMapping translationMapping = wordTranslationDao.findByWordAndByLanguage(word, language);
        if (translationMapping == null) {
            return null;
        }
        return wordTranslationMapper.toDto(translationMapping);
    }
}

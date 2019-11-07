package talkapp.org.talkappmobile.service.impl;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.mappings.WordTranslationMapping;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.WordTranslationService;
import talkapp.org.talkappmobile.service.mapper.WordTranslationMapper;

import static java.lang.String.valueOf;

public class WordTranslationServiceImpl implements WordTranslationService {
    private final WordTranslationDao wordTranslationDao;
    private final WordTranslationMapper mapper;

    public WordTranslationServiceImpl(WordTranslationDao wordTranslationDao, WordTranslationMapper mapper) {
        this.wordTranslationDao = wordTranslationDao;
        this.mapper = mapper;
    }

    @Override
    public void saveWordTranslations(List<WordTranslation> wordTranslations) {
        List<WordTranslationMapping> mappings = new LinkedList<>();
        for (WordTranslation wordTranslation : wordTranslations) {
            WordTranslationMapping e = mapper.toMapping(wordTranslation);
            e.setId(valueOf(System.currentTimeMillis()));
            mappings.add(e);
        }
        wordTranslationDao.save(mappings);
    }

    @Override
    public WordTranslation findByWordAndLanguage(String word, String language) {
        WordTranslationMapping translationMapping = wordTranslationDao.findByWordAndByLanguage(word, language);
        if (translationMapping == null) {
            return null;
        }
        return mapper.toDto(translationMapping);
    }
}

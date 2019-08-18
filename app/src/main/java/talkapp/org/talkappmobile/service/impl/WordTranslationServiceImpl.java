package talkapp.org.talkappmobile.service.impl;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.mappings.WordTranslationMapping;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.WordTranslationService;

public class WordTranslationServiceImpl implements WordTranslationService {
    private final WordTranslationDao wordTranslationDao;

    public WordTranslationServiceImpl(WordTranslationDao wordTranslationDao) {
        this.wordTranslationDao = wordTranslationDao;
    }

    @Override
    public void saveWordTranslations(List<WordTranslation> wordTranslations) {
        List<WordTranslationMapping> mappings = new LinkedList<>();
        for (WordTranslation wordTranslation : wordTranslations) {
            mappings.add(toMapping(wordTranslation));
        }
        wordTranslationDao.save(mappings);
    }

    private WordTranslationMapping toMapping(WordTranslation translation) {
        WordTranslationMapping mapping = new WordTranslationMapping();
        mapping.setWord(translation.getWord() + "_" + translation.getLanguage());
        mapping.setTranslation(translation.getTranslation());
        mapping.setLanguage(translation.getLanguage());
        mapping.setTop(translation.getTop());
        return mapping;
    }
}

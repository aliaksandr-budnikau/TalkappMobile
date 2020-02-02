package talkapp.org.talkappmobile.service.impl;

import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.mappings.WordTranslationMapping;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.WordTranslationRepository;
import talkapp.org.talkappmobile.service.mapper.WordTranslationMapper;

public class WordTranslationRepositoryImpl implements WordTranslationRepository {
    private final WordTranslationDao translationDao;
    private final WordTranslationMapper wordTranslationMapper;

    public WordTranslationRepositoryImpl(WordTranslationDao translationDao) {
        this.translationDao = translationDao;
        this.wordTranslationMapper = new WordTranslationMapper();
    }

    @Override
    public WordTranslation findByWordAndByLanguage(String word, String russian) {
        WordTranslationMapping mapping = translationDao.findByWordAndByLanguage(word, russian);
        if (mapping == null) {
            return null;
        }
        return wordTranslationMapper.toDto(mapping);
    }
}

package talkapp.org.talkappmobile.repository;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.mappings.WordTranslationMapping;
import talkapp.org.talkappmobile.model.WordTranslation;

import static java.lang.String.valueOf;

public class WordTranslationRepositoryImpl implements WordTranslationRepository {
    private final WordTranslationDao translationDao;
    private final WordTranslationMapper wordTranslationMapper;

    @Inject
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

    @Override
    public void createNewOrUpdate(List<WordTranslation> wordTranslations) {
        List<WordTranslationMapping> mappings = new LinkedList<>();
        for (WordTranslation wordTranslation : wordTranslations) {
            WordTranslationMapping mapping = wordTranslationMapper.toMapping(wordTranslation);
            if (StringUtils.isEmpty(mapping.getId())) {
                mapping.setId(valueOf(System.nanoTime()));
            }
            mappings.add(mapping);
        }
        translationDao.save(mappings);
    }
}

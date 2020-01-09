package talkapp.org.talkappmobile.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.mappings.WordTranslationMapping;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.SentenceProvider;
import talkapp.org.talkappmobile.service.mapper.WordTranslationMapper;

import static java.util.Collections.emptyList;

class WordTranslationSentenceProviderDecorator extends SentenceProviderDecorator {
    private final WordTranslationDao wordTranslationDao;
    private final WordTranslationMapper wordTranslationMapper;

    public WordTranslationSentenceProviderDecorator(SentenceProvider provider, WordTranslationDao wordTranslationDao, ObjectMapper mapper) {
        super(provider);
        this.wordTranslationDao = wordTranslationDao;
        this.wordTranslationMapper = new WordTranslationMapper(mapper);
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
        return Collections.singletonList(wordTranslationMapper.convertToSentence(wordTranslation));
    }

    private WordTranslation findByWordAndLanguage(String word) {
        WordTranslationMapping translationMapping = wordTranslationDao.findByWordAndByLanguage(word, "russian");
        if (translationMapping == null) {
            return null;
        }
        return wordTranslationMapper.toDto(translationMapping);
    }
}
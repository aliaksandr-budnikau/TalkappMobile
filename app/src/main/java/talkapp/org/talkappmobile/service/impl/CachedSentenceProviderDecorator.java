package talkapp.org.talkappmobile.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.mappings.SentenceMapping;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.service.SentenceProvider;
import talkapp.org.talkappmobile.service.mapper.SentenceMapper;

import static java.util.Collections.singletonList;

class CachedSentenceProviderDecorator extends SentenceProviderDecorator {
    public static final int WORDS_NUMBER = 6;
    private final SentenceDao sentenceDao;
    private final SentenceMapper sentenceMapper;

    public CachedSentenceProviderDecorator(SentenceProvider provider, SentenceDao sentenceDao, ObjectMapper mapper) {
        super(provider);
        this.sentenceDao = sentenceDao;
        this.sentenceMapper = new SentenceMapper(mapper);
    }

    @Override
    public List<Sentence> find(Word2Tokens word) {
        List<Sentence> sentences;
        try {
            sentences = super.find(word);
        } catch (InternetConnectionLostException e) {
            return super.getFromDB(word);
        }
        if (sentences == null || sentences.isEmpty()) {
            return super.getFromDB(word);
        }
        for (Sentence sentence : sentences) {
            if (wasAlreadySaved(sentence)) {
                continue;
            }
            SentenceMapping mapping = sentenceMapper.toMapping(sentence, word.getWord(), WORDS_NUMBER);
            sentence.setId(mapping.getId());
            sentenceDao.save(singletonList(mapping));
        }
        return sentences;
    }

    private boolean wasAlreadySaved(Sentence sentence) {
        return sentenceDao.findById(sentence.getId()) != null;
    }
}
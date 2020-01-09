package talkapp.org.talkappmobile.service.impl;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
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
            return getSentencesFromDB(word);
        }
        if (sentences.isEmpty()) {
            return getSentences(word);
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

    @NonNull
    private List<Sentence> getSentences(Word2Tokens word) {
        ArrayList<Sentence> result = new ArrayList<>();
        List<SentenceMapping> mappings = sentenceDao.findAllByWord(word.getWord(), WORDS_NUMBER);
        for (SentenceMapping mapping : mappings) {
            result.add(sentenceMapper.toDto(mapping));
        }
        return result;
    }

    private List<Sentence> getSentencesFromDB(Word2Tokens word) {
        List<SentenceMapping> mappings = sentenceDao.findAllByWord(word.getWord(), WORDS_NUMBER);
        ArrayList<Sentence> result = new ArrayList<>();
        for (SentenceMapping mapping : mappings) {
            result.add(sentenceMapper.toDto(mapping));
        }
        return result;
    }
}
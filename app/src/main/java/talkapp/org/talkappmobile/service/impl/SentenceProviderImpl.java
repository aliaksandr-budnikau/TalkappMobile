package talkapp.org.talkappmobile.service.impl;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.mappings.SentenceIdMapping;
import talkapp.org.talkappmobile.mappings.SentenceMapping;
import talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.SentenceProvider;
import talkapp.org.talkappmobile.service.mapper.SentenceMapper;
import talkapp.org.talkappmobile.service.mapper.WordSetMapper;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class SentenceProviderImpl implements SentenceProvider {

    public static final int WORDS_NUMBER = 6;
    private final WordSetDao wordSetDao;
    private final WordRepetitionProgressDao progressDao;
    private final WordSetMapper wordSetMapper;
    private final SentenceMapper sentenceMapper;
    private final SentenceDao sentenceDao;

    public SentenceProviderImpl(WordSetDao wordSetDao, WordRepetitionProgressDao progressDao, SentenceDao sentenceDao, ObjectMapper mapper) {
        this.wordSetDao = wordSetDao;
        this.progressDao = progressDao;
        this.sentenceDao = sentenceDao;
        this.wordSetMapper = new WordSetMapper(mapper);
        this.sentenceMapper = new SentenceMapper(mapper);
    }

    @Override
    public List<Sentence> find(Word2Tokens word) {
        WordSetMapping mapping = wordSetDao.findById(word.getSourceWordSetId());
        WordSet wordSet = wordSetMapper.toDto(mapping);
        List<WordRepetitionProgressMapping> exercises = progressDao.findByWordIndexAndWordSetId(wordSet.getWords().indexOf(word), word.getSourceWordSetId());
        if (exercises.isEmpty()) {
            return emptyList();
        }
        WordRepetitionProgressMapping exercise = exercises.get(0);
        if (isEmpty(exercise.getSentenceIds())) {
            return emptyList();
        }
        List<SentenceIdMapping> ids = sentenceMapper.toSentenceIdMapping(exercise.getSentenceIds());
        if (ids.isEmpty()) {
            return emptyList();
        }
        return getSentence(ids, word.getWord());
    }

    @Override
    public List<Sentence> getFromDB(Word2Tokens word) {
        List<SentenceMapping> mappings = sentenceDao.findAllByWord(word.getWord(), WORDS_NUMBER);
        List<Sentence> result = new ArrayList<>();
        for (SentenceMapping mapping : mappings) {
            result.add(sentenceMapper.toDto(mapping));
        }
        result = getRidOfDuplicates(result);
        return result;
    }

    private List<Sentence> getSentence(List<SentenceIdMapping> ids, String word) {
        String[] sentenceIds = new String[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            SentenceIdMapping idMapping = ids.get(i);
            sentenceIds[i] = sentenceMapper.toSentenceIdMapping(idMapping);
        }
        List<SentenceMapping> sentences = sentenceDao.findAllByIds(sentenceIds);
        if (sentences.isEmpty()) {
            return emptyList();
        }
        Map<String, SentenceMapping> hashMap = new HashMap<>();
        for (SentenceMapping sentence : sentences) {
            hashMap.put(sentence.getId(), sentence);
        }
        LinkedList<Sentence> result = new LinkedList<>();
        for (String id : sentenceIds) {
            SentenceMapping mapping = hashMap.get(id);
            if (mapping != null) {
                result.add(sentenceMapper.toDto(mapping));
            }
        }
        return result;
    }

    @NonNull
    private LinkedList<Sentence> getRidOfDuplicates(List<Sentence> sentences) {
        Set<String> texts = new HashSet<>();
        LinkedList<Sentence> result = new LinkedList<>();
        for (Sentence sentence : sentences) {
            if (texts.add(sentence.getTranslations().get("russian"))) {
                result.add(sentence);
            }
        }
        return result;
    }
}
package talkapp.org.talkappmobile.service.impl;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import talkapp.org.talkappmobile.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.SentenceProvider;
import talkapp.org.talkappmobile.service.SentenceRepository;
import talkapp.org.talkappmobile.service.WordSetRepository;
import talkapp.org.talkappmobile.service.mapper.SentenceMapper;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class SentenceProviderImpl implements SentenceProvider {

    public static final int WORDS_NUMBER = 6;
    private final WordSetRepository wordSetRepository;
    private final WordRepetitionProgressDao progressDao;
    private final SentenceMapper sentenceMapper;
    private final SentenceRepository sentenceRepository;

    public SentenceProviderImpl(WordSetRepository wordSetRepository, WordRepetitionProgressDao progressDao, SentenceRepository sentenceRepository, ObjectMapper mapper) {
        this.wordSetRepository = wordSetRepository;
        this.progressDao = progressDao;
        this.sentenceRepository = sentenceRepository;
        this.sentenceMapper = new SentenceMapper(mapper);
    }

    @Override
    public List<Sentence> find(Word2Tokens word) {
        WordSet wordSet = wordSetRepository.findById(word.getSourceWordSetId());
        List<WordRepetitionProgressMapping> exercises = progressDao.findByWordIndexAndWordSetId(wordSet.getWords().indexOf(word), word.getSourceWordSetId());
        if (exercises.isEmpty()) {
            return emptyList();
        }
        WordRepetitionProgressMapping exercise = exercises.get(0);
        if (isEmpty(exercise.getSentenceIds())) {
            return emptyList();
        }
        List<String> ids = sentenceMapper.toSentenceId(exercise.getSentenceIds());
        if (ids.isEmpty()) {
            return emptyList();
        }
        return getSentence(ids);
    }

    @Override
    public List<Sentence> getFromDB(Word2Tokens word) {
        List<Sentence> sentences = sentenceRepository.findAllByWord(word.getWord(), WORDS_NUMBER);
        return getRidOfDuplicates(sentences);
    }

    private List<Sentence> getSentence(List<String> ids) {
        List<Sentence> sentences = sentenceRepository.findAllByIds(ids.toArray(new String[0]));
        if (sentences.isEmpty()) {
            return emptyList();
        }
        Map<String, Sentence> hashMap = new HashMap<>();
        for (Sentence sentence : sentences) {
            hashMap.put(sentence.getId(), sentence);
        }
        LinkedList<Sentence> result = new LinkedList<>();
        for (String id : ids) {
            Sentence mapping = hashMap.get(id);
            if (mapping != null) {
                result.add(mapping);
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
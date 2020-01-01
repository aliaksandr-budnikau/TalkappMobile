package talkapp.org.talkappmobile.service.impl;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.mapper.WordSetMapper;

public class CurrentPracticeStateServiceImpl implements CurrentPracticeStateService {
    private final WordSetDao wordSetDao;
    private final WordSetMapper wordSetMapper;

    private WordSet wordSet;
    private WordSource currentWord;
    private Sentence currentSentence;
    private List<WordSource> finishedWords = new LinkedList<>();
    private List<WordSource> wordsSources = new LinkedList<>();

    public CurrentPracticeStateServiceImpl(WordSetDao wordSetDao, ObjectMapper mapper) {
        this.wordSetDao = wordSetDao;
        this.wordSetMapper = new WordSetMapper(mapper);
    }

    @Override
    public List<Word2Tokens> getFinishedWords() {
        ArrayList<Word2Tokens> result = new ArrayList<>();
        for (WordSource source : finishedWords) {
            result.add(getWord2Tokens(source));
        }
        return result;
    }

    @Override
    public List<Word2Tokens> getAllWords() {
        ArrayList<Word2Tokens> result = new ArrayList<>();
        for (WordSource source : wordsSources) {
            result.add(getWord2Tokens(source));
        }
        return result;
    }

    @Override
    public Word2Tokens getCurrentWord() {
        if (currentWord == null) {
            return null;
        }
        return getWord2Tokens(currentWord);
    }

    @Override
    public void setCurrentWord(Word2Tokens word) {
        this.currentWord = getWordSource(word);
    }

    @NonNull
    private WordSource getWordSource(Word2Tokens word) {
        WordSetMapping mapping = wordSetDao.findById(word.getSourceWordSetId());
        WordSet wordSet = wordSetMapper.toDto(mapping);
        return new WordSource(word.getSourceWordSetId(), wordSet.getWords().indexOf(word));
    }

    @Override
    public Sentence getCurrentSentence() {
        return currentSentence;
    }

    @Override
    public void setCurrentSentence(Sentence sentence) {
        currentSentence = sentence;
    }

    @Override
    public WordSet getWordSet() {
        return wordSet;
    }

    @Override
    public void persistWordSet() {
        if (wordSet.getId() == 0) {
            throw new UnsupportedOperationException();
        }
        wordSetDao.createNewOrUpdate(wordSetMapper.toMapping(wordSet));
    }

    @Override
    public void set(WordSet wordSet) {
        for (Word2Tokens word : wordSet.getWords()) {
            wordsSources.add(getWordSource(word));
        }
        this.wordSet = wordSet;
    }

    @Override
    public void changeWordSetStatus(WordSetProgressStatus status) {
        wordSet.setStatus(status);
        wordSetDao.createNewOrUpdate(wordSetMapper.toMapping(wordSet));
    }

    @Override
    public void addFinishedWord(Word2Tokens word) {
        finishedWords.add(getWordSource(word));
    }

    private Word2Tokens getWord2Tokens(WordSource source) {
        WordSetMapping mapping = wordSetDao.findById(source.getWordSetId());
        WordSet wordSet = wordSetMapper.toDto(mapping);
        return wordSet.getWords().get(source.getWordIndex());
    }

    public static class WordSource implements Serializable {
        private final int wordSetId;
        private final int wordIndex;

        public WordSource() {
            wordSetId = 0;
            wordIndex = 0;
        }

        public WordSource(int wordSetId, int wordIndex) {
            this.wordSetId = wordSetId;
            this.wordIndex = wordIndex;
        }

        public int getWordSetId() {
            return wordSetId;
        }

        public int getWordIndex() {
            return wordIndex;
        }
    }
}
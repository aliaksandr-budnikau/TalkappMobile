package talkapp.org.talkappmobile.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.model.CurrentPracticeState;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.mapper.WordSetMapper;

public class CurrentPracticeStateServiceImpl implements CurrentPracticeStateService {
    private final WordSetDao wordSetDao;
    private final WordSetMapper wordSetMapper;
    private CurrentPracticeState currentPracticeState;

    public CurrentPracticeStateServiceImpl(WordSetDao wordSetDao, ObjectMapper mapper) {
        this.wordSetDao = wordSetDao;
        this.wordSetMapper = new WordSetMapper(mapper);
    }

    @Override
    public CurrentPracticeState get() {
        return currentPracticeState;
    }

    @Override
    public void save(CurrentPracticeState currentPracticeState) {
        this.currentPracticeState = currentPracticeState;
    }

    @Override
    public List<Word2Tokens> getFinishedWords() {
        List<CurrentPracticeState.WordSource> finishedWords = currentPracticeState.getFinishedWords();
        ArrayList<Word2Tokens> result = new ArrayList<>();
        for (CurrentPracticeState.WordSource source : finishedWords) {
            result.add(getWord2Tokens(source));
        }
        return result;
    }

    @Override
    public List<Word2Tokens> getAllWords() {
        List<CurrentPracticeState.WordSource> wordsSources = currentPracticeState.getWordsSources();
        ArrayList<Word2Tokens> result = new ArrayList<>();
        for (CurrentPracticeState.WordSource source : wordsSources) {
            result.add(getWord2Tokens(source));
        }
        return result;
    }

    @Override
    public Word2Tokens getCurrentWord() {
        CurrentPracticeState.WordSource source = currentPracticeState.getCurrentWord();
        if (source == null) {
            return null;
        }
        return getWord2Tokens(source);
    }

    @Override
    public void setCurrentWord(Word2Tokens word) {
        WordSetMapping mapping = wordSetDao.findById(word.getSourceWordSetId());
        WordSet wordSet = wordSetMapper.toDto(mapping);
        currentPracticeState.setCurrentWord(new CurrentPracticeState.WordSource(word.getSourceWordSetId(), wordSet.getWords().indexOf(word)));
    }

    @Override
    public Sentence getCurrentSentence() {
        return currentPracticeState.getCurrentSentence();
    }

    @Override
    public void setCurrentSentence(Sentence sentence) {
        currentPracticeState.setCurrentSentence(sentence);
    }

    @Override
    public WordSet getWordSet() {
        return currentPracticeState.getWordSet();
    }

    @Override
    public void persistWordSet() {
        WordSet wordSet = currentPracticeState.getWordSet();
        if (wordSet.getId() == 0) {
            throw new UnsupportedOperationException();
        }
        wordSetDao.createNewOrUpdate(wordSetMapper.toMapping(wordSet));
    }

    private Word2Tokens getWord2Tokens(CurrentPracticeState.WordSource source) {
        WordSetMapping mapping = wordSetDao.findById(source.getWordSetId());
        WordSet wordSet = wordSetMapper.toDto(mapping);
        return wordSet.getWords().get(source.getWordIndex());
    }
}
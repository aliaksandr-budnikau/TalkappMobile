package talkapp.org.talkappmobile.service.impl;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.WordSetRepository;

public class CurrentPracticeStateServiceImpl implements CurrentPracticeStateService {
    private final WordSetRepository wordSetRepository;

    private WordSet wordSet;
    private WordSource currentWord;
    private Sentence currentSentence;
    private List<WordSource> finishedWords = new LinkedList<>();
    private List<WordSource> wordsSources = new LinkedList<>();

    public CurrentPracticeStateServiceImpl(WordSetRepository wordSetRepository) {
        this.wordSetRepository = wordSetRepository;
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
        WordSet wordSet = wordSetRepository.findById(word.getSourceWordSetId());
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
        wordSetRepository.createNewOrUpdate(wordSet);
    }

    @Override
    public void set(WordSet wordSet) {
        finishedWords.clear();
        wordsSources.clear();
        currentWord = null;
        currentSentence = null;
        for (Word2Tokens word : wordSet.getWords()) {
            wordsSources.add(getWordSource(word));
        }
        this.wordSet = wordSet;
    }

    @Override
    public void setStatus(WordSetProgressStatus status) {
        wordSet.setStatus(status);
    }

    @Override
    public void addFinishedWord(Word2Tokens word) {
        finishedWords.add(getWordSource(word));
    }

    @Override
    public void setTrainingExperience(int trainingExperience) {
        wordSet.setTrainingExperience(trainingExperience);
    }

    private Word2Tokens getWord2Tokens(WordSource source) {
        WordSet wordSet = wordSetRepository.findById(source.getWordSetId());
        return wordSet.getWords().get(source.getWordIndex());
    }

    public static class WordSource implements Serializable {
        private final int wordSetId;
        private final int wordIndex;

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
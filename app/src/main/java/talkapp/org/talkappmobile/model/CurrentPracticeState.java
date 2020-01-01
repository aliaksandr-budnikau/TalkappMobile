package talkapp.org.talkappmobile.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CurrentPracticeState {

    private final WordSet wordSet;
    private WordSource currentWord;
    private Sentence currentSentence;
    private List<WordSource> finishedWords = new LinkedList<>();

    public CurrentPracticeState(WordSet wordSet) {
        this.wordSet = wordSet;
    }

    public WordSet getWordSet() {
        return wordSet;
    }

    public WordSource getCurrentWord() {
        return currentWord;
    }

    public void setCurrentWord(WordSource currentWord) {
        this.currentWord = currentWord;
    }

    public Sentence getCurrentSentence() {
        return currentSentence;
    }

    public void setCurrentSentence(Sentence currentSentence) {
        this.currentSentence = currentSentence;
    }

    public void addFinishedWords(WordSource word) {
        finishedWords.add(word);
    }

    public List<WordSource> getFinishedWords() {
        return new ArrayList<>(finishedWords);
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

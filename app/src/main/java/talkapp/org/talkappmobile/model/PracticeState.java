package talkapp.org.talkappmobile.model;

public class PracticeState {

    private final WordSet wordSet;

    public PracticeState(WordSet wordSet) {
        this.wordSet = wordSet;
    }

    public WordSet getWordSet() {
        return wordSet;
    }
}

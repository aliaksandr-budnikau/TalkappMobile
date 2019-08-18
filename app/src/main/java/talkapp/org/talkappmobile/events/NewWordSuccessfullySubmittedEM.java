package talkapp.org.talkappmobile.events;

import java.util.Objects;

import talkapp.org.talkappmobile.model.WordSet;

public class NewWordSuccessfullySubmittedEM {
    private final WordSet wordSet;

    public NewWordSuccessfullySubmittedEM(WordSet wordSet) {
        this.wordSet = wordSet;
    }

    public WordSet getWordSet() {
        return wordSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewWordSuccessfullySubmittedEM that = (NewWordSuccessfullySubmittedEM) o;
        return Objects.equals(wordSet, that.wordSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wordSet);
    }
}
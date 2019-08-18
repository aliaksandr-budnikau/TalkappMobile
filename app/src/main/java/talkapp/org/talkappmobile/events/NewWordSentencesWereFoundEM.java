package talkapp.org.talkappmobile.events;

import java.util.Objects;

public class NewWordSentencesWereFoundEM {
    private final int wordIndex;

    public NewWordSentencesWereFoundEM(int wordIndex) {
        this.wordIndex = wordIndex;
    }

    public int getWordIndex() {
        return wordIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewWordSentencesWereFoundEM that = (NewWordSentencesWereFoundEM) o;
        return wordIndex == that.wordIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(wordIndex);
    }
}

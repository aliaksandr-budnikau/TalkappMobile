package talkapp.org.talkappmobile.events;

import java.util.Objects;

public class NewWordIsEmptyEM {
    private final int wordIndex;

    public NewWordIsEmptyEM(int wordIndex) {
        this.wordIndex = wordIndex;
    }

    public int getWordIndex() {
        return wordIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewWordIsEmptyEM that = (NewWordIsEmptyEM) o;
        return wordIndex == that.wordIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(wordIndex);
    }
}

package talkapp.org.talkappmobile.model;

import java.util.Objects;

/**
 * @author Budnikau Aliaksandr
 */
public class UncheckedAnswer {
    private String wordSetId;
    private String actualAnswer;
    private String expectedAnswer;

    public String getActualAnswer() {
        return actualAnswer;
    }

    public void setActualAnswer(String actualAnswer) {
        this.actualAnswer = actualAnswer;
    }

    public String getExpectedAnswer() {
        return expectedAnswer;
    }

    public void setExpectedAnswer(String expectedAnswer) {
        this.expectedAnswer = expectedAnswer;
    }

    public String getWordSetId() {
        return wordSetId;
    }

    public void setWordSetId(String wordSetId) {
        this.wordSetId = wordSetId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UncheckedAnswer that = (UncheckedAnswer) o;
        return Objects.equals(actualAnswer, that.actualAnswer) &&
                Objects.equals(expectedAnswer, that.expectedAnswer) &&
                Objects.equals(wordSetId, that.wordSetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actualAnswer, expectedAnswer, wordSetId);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UncheckedAnswer{");
        sb.append("actualAnswer='").append(actualAnswer).append('\'');
        sb.append("expectedAnswer='").append(expectedAnswer).append('\'');
        sb.append(", wordSetId='").append(wordSetId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
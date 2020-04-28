package talkapp.org.talkappmobile.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author Budnikau Aliaksandr
 */
public class UncheckedAnswer {
    private String actualAnswer;
    private String expectedAnswer;
    private Word2Tokens currentWord;

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

    public Word2Tokens getCurrentWord() {
        return currentWord;
    }

    public void setCurrentWord(Word2Tokens currentWord) {
        this.currentWord = currentWord;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        UncheckedAnswer that = (UncheckedAnswer) o;

        return new EqualsBuilder()
                .append(actualAnswer, that.actualAnswer)
                .append(expectedAnswer, that.expectedAnswer)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(actualAnswer)
                .append(expectedAnswer)
                .toHashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UncheckedAnswer{");
        sb.append("actualAnswer='").append(actualAnswer).append('\'');
        sb.append("expectedAnswer='").append(expectedAnswer).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
package talkapp.org.talkappmobile.model;

import java.util.Objects;

/**
 * @author Budnikau Aliaksandr
 */
public class UncheckedAnswer {
    private String text;
    private String wordSetId;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
        return Objects.equals(text, that.text) &&
                Objects.equals(wordSetId, that.wordSetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, wordSetId);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UncheckedAnswer{");
        sb.append("text='").append(text).append('\'');
        sb.append(", wordSetId='").append(wordSetId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
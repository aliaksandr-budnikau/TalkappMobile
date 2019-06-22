package talkapp.org.talkappmobile.model;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class Word2Tokens implements Serializable {
    private final String word;
    private final String tokens;
    private final Integer sourceWordSetId;

    public Word2Tokens() {
        word = null;
        tokens = null;
        sourceWordSetId = null;
    }

    public Word2Tokens(@NonNull String word, @NonNull Integer sourceWordSetId) {
        this.word = word;
        this.tokens = word;
        this.sourceWordSetId = sourceWordSetId;
    }

    public Word2Tokens(@NonNull String word, @NonNull String tokens, @NonNull Integer sourceWordSetId) {
        this.word = word;
        this.tokens = tokens;
        this.sourceWordSetId = sourceWordSetId;
    }

    @NonNull
    public String getWord() {
        return word;
    }

    @NonNull
    public String getTokens() {
        return tokens;
    }

    @NonNull
    public Integer getSourceWordSetId() {
        return sourceWordSetId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word2Tokens that = (Word2Tokens) o;
        return Objects.equals(word, that.word) &&
                Objects.equals(sourceWordSetId, that.sourceWordSetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, sourceWordSetId);
    }

    @Override
    public String toString() {
        return "Word2Tokens{" +
                "word='" + word + '\'' +
                ", sourceWordSetId='" + sourceWordSetId + '\'' +
                '}';
    }
}
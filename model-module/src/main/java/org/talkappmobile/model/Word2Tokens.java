package org.talkappmobile.model;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class Word2Tokens implements Serializable {
    private final String word;
    private final String tokens;
    private final Integer sourceWordSetId;

    public Word2Tokens() {
        this.word = null;
        this.tokens = null;
        this.sourceWordSetId = null;
    }

    public Word2Tokens(@NonNull String word, @NonNull String tokens, @NonNull Integer sourceWordSetId) {
        this.word = word;
        this.tokens = tokens;
        this.sourceWordSetId = sourceWordSetId;
    }

    public String getWord() {
        return word;
    }

    public String getTokens() {
        return tokens;
    }

    public Integer getSourceWordSetId() {
        return sourceWordSetId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word2Tokens that = (Word2Tokens) o;
        return Objects.equals(word, that.word) &&
                Objects.equals(tokens, that.tokens) &&
                Objects.equals(sourceWordSetId, that.sourceWordSetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), word, tokens, sourceWordSetId);
    }

    @Override
    public String toString() {
        return "Word2Tokens{" +
                "word='" + word + '\'' +
                ", tokens='" + tokens + '\'' +
                ", sourceWordSetId=" + sourceWordSetId +
                '}';
    }
}
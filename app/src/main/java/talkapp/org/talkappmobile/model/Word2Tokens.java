package talkapp.org.talkappmobile.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Objects;

public class Word2Tokens implements Serializable {
    private String word;
    private String tokens;
    @JsonIgnore
    private Integer sourceWordSetId;

    public Word2Tokens() {
    }

    public Word2Tokens(String word, Integer sourceWordSetId) {
        this.word = word;
        this.tokens = word;
        this.sourceWordSetId = sourceWordSetId;
    }

    public Word2Tokens(String word, String tokens, Integer sourceWordSetId) {
        this.word = word;
        this.tokens = tokens;
        this.sourceWordSetId = sourceWordSetId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTokens() {
        return tokens;
    }

    public void setTokens(String tokens) {
        this.tokens = tokens;
    }

    public Integer getSourceWordSetId() {
        return sourceWordSetId;
    }

    public void setSourceWordSetId(Integer sourceWordSetId) {
        this.sourceWordSetId = sourceWordSetId;
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
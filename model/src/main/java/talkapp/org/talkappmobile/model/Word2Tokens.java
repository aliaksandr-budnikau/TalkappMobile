package talkapp.org.talkappmobile.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class Word2Tokens implements Serializable {
    private final String word;
    private final String tokens;
    private final Integer sourceWordSetId;

    public Word2Tokens() {
        this.word = null;
        this.tokens = null;
        this.sourceWordSetId = null;
    }

    public Word2Tokens(String word, String tokens, Integer sourceWordSetId) {
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

        return new EqualsBuilder()
                .append(word, that.word)
                .append(tokens, that.tokens)
                .append(sourceWordSetId, that.sourceWordSetId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(word)
                .append(tokens)
                .append(sourceWordSetId)
                .toHashCode();
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
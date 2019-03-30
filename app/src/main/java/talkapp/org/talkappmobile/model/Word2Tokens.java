package talkapp.org.talkappmobile.model;

import java.io.Serializable;
import java.util.Objects;

public class Word2Tokens implements Serializable {
    private String word;
    private String tokens;

    public Word2Tokens() {
    }

    public Word2Tokens(String word) {
        this.word = word;
    }

    public Word2Tokens(String word, String tokens) {
        this.word = word;
        this.tokens = tokens;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word2Tokens that = (Word2Tokens) o;
        return Objects.equals(word, that.word) &&
                Objects.equals(tokens, that.tokens);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, tokens);
    }

    @Override
    public String toString() {
        return "Word2Tokens{" +
                "word='" + word + '\'' +
                ", tokens='" + tokens + '\'' +
                '}';
    }
}
package talkapp.org.talkappmobile.model;

import java.util.Objects;

/**
 * @author Budnikau Aliaksandr
 */
public class WordTranslation {
    private String id;

    private String word;

    private String language;

    private String translation;

    private Integer top;

    private String tokens;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
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
        WordTranslation that = (WordTranslation) o;
        return Objects.equals(id, that.id) &&
         Objects.equals(word, that.word) &&
         Objects.equals(language, that.language) &&
         Objects.equals(translation, that.translation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, word, language, translation);
    }

    @Override
    public String toString() {
        return "WordTranslation{" +
         "id='" + id + '\'' +
         ", word='" + word + '\'' +
         ", language='" + language + '\'' +
         ", translation='" + translation + '\'' +
         '}';
    }
}

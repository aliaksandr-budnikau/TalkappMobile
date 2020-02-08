package talkapp.org.talkappmobile.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Budnikau Aliaksandr
 */
public class Sentence {
    private String id;
    private String text;
    private Map<String, String> translations = new HashMap<>();
    private List<TextToken> tokens = new LinkedList<>();
    private SentenceContentScore contentScore;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, String> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, String> translations) {
        this.translations = translations;
    }

    public List<TextToken> getTokens() {
        return tokens;
    }

    public void setTokens(List<TextToken> tokens) {
        this.tokens = tokens;
    }

    public SentenceContentScore getContentScore() {
        return contentScore;
    }

    public void setContentScore(SentenceContentScore contentScore) {
        this.contentScore = contentScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Sentence sentence = (Sentence) o;

        return new EqualsBuilder()
                .append(id, sentence.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Sentence{");
        sb.append("id='").append(id).append('\'');
        sb.append(", text='").append(text).append('\'');
        sb.append(", translations=").append(translations);
        sb.append(", tokens=").append(tokens);
        sb.append('}');
        return sb.toString();
    }
}

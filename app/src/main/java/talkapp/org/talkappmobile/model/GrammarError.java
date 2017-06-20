package talkapp.org.talkappmobile.model;

import java.util.List;
import java.util.Objects;

/**
 * @author Budnikau Aliaksandr
 */
public class GrammarError {
    private String message;
    private int offset;
    private int length;
    private String bad;
    private List<String> suggestions;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getBad() {
        return bad;
    }

    public void setBad(String bad) {
        this.bad = bad;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GrammarError that = (GrammarError) o;
        return offset == that.offset &&
                length == that.length &&
                Objects.equals(message, that.message) &&
                Objects.equals(bad, that.bad) &&
                Objects.equals(suggestions, that.suggestions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, offset, length, bad, suggestions);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GrammarError{");
        sb.append("message='").append(message).append('\'');
        sb.append(", offset=").append(offset);
        sb.append(", length=").append(length);
        sb.append(", bad='").append(bad).append('\'');
        sb.append(", suggestions=").append(suggestions);
        sb.append('}');
        return sb.toString();
    }
}

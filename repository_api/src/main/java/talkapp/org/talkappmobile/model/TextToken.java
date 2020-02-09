package talkapp.org.talkappmobile.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Objects;

public class TextToken {
    private String token;
    private int startOffset;
    private int endOffset;
    private int position;

    public TextToken() {
    }

    public TextToken(String token) {
        this.token = token;
        this.endOffset = token.length();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    public int getEndOffset() {
        return endOffset;
    }

    public void setEndOffset(int endOffset) {
        this.endOffset = endOffset;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TextToken textToken = (TextToken) o;

        return new EqualsBuilder()
                .append(startOffset, textToken.startOffset)
                .append(endOffset, textToken.endOffset)
                .append(position, textToken.position)
                .append(token, textToken.token)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(token)
                .append(startOffset)
                .append(endOffset)
                .append(position)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "TextToken{" +
         "token='" + token + '\'' +
         ", startOffset=" + startOffset +
         ", endOffset=" + endOffset +
         ", position=" + position +
         '}';
    }
}
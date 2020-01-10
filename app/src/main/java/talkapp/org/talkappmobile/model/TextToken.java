package talkapp.org.talkappmobile.model;

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
        TextToken that = (TextToken) o;
        return startOffset == that.startOffset &&
         endOffset == that.endOffset &&
         position == that.position &&
         Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, startOffset, endOffset, position);
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
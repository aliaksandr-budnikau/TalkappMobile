package talkapp.org.talkappmobile.model;


import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @author Budnikau Aliaksandr
 */
public class WordSet implements Serializable {
    private int id;

    private String topicId;

    private List<String> words;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordSet wordSet = (WordSet) o;
        return Objects.equals(id, wordSet.id) &&
                Objects.equals(words, wordSet.words) &&
                Objects.equals(topicId, wordSet.topicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, topicId, words);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WordSet{");
        sb.append("id='").append(id).append('\'');
        sb.append(", words=").append(words);
        sb.append(", topicId=").append(topicId);
        sb.append('}');
        return sb.toString();
    }
}
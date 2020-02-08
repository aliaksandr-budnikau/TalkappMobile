package talkapp.org.talkappmobile.mappings;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Budnikau Aliaksandr
 */
@DatabaseTable(tableName = WordSetMapping.WORD_SET_TABLE)
public class WordSetMapping implements Serializable {
    public static final String WORD_SET_TABLE = "WordSet";
    public static final String ID_FN = "id";
    public static final String TOPIC_ID_FN = "topicId";
    public static final String WORDS_FN = "word";
    public static final String TOP_FN = "top";
    public static final String TRAINING_EXPERIENCE_FN = "trainingExperience";
    public static final String STATUS_FN = "status";

    @DatabaseField(id = true, unique = true, canBeNull = false, columnName = ID_FN)
    private String id;

    @DatabaseField(canBeNull = false, columnName = TOPIC_ID_FN)
    private String topicId;

    @DatabaseField(canBeNull = false, columnName = WORDS_FN)
    private String words;

    @DatabaseField(columnName = TOP_FN)
    private Integer top;

    @DatabaseField(canBeNull = false, columnName = TRAINING_EXPERIENCE_FN)
    private int trainingExperience;

    @DatabaseField(canBeNull = false, columnName = STATUS_FN)
    private String status = "FIRST_CYCLE";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    public int getTrainingExperience() {
        return trainingExperience;
    }

    public void setTrainingExperience(int trainingExperience) {
        this.trainingExperience = trainingExperience;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordSetMapping wordSet = (WordSetMapping) o;
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
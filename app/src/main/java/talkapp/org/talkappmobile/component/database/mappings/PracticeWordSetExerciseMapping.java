package talkapp.org.talkappmobile.component.database.mappings;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Objects;

import talkapp.org.talkappmobile.model.WordSetExperienceStatus;

import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.STUDYING;

@DatabaseTable(tableName = "PracticeWordSetExercise")
public class PracticeWordSetExerciseMapping {
    public static final String WORD_FN = "word";
    public static final String WORD_SET_ID_FN = "wordSetId";
    public static final String STATUS_FN = "status";
    public static final String CURRENT_FN = "current";
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, columnName = WORD_SET_ID_FN)
    private int wordSetId;

    @DatabaseField(canBeNull = false, columnName = WORD_FN)
    private String wordJSON;

    @DatabaseField
    private String sentenceJSON;

    @DatabaseField(canBeNull = false, columnName = STATUS_FN)
    private WordSetExperienceStatus status = STUDYING;

    @DatabaseField(canBeNull = false, columnName = CURRENT_FN)
    private boolean current;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWordJSON() {
        return wordJSON;
    }

    public void setWordJSON(String wordJSON) {
        this.wordJSON = wordJSON;
    }

    public int getWordSetId() {
        return wordSetId;
    }

    public void setWordSetId(int wordSetId) {
        this.wordSetId = wordSetId;
    }

    public String getSentenceJSON() {
        return sentenceJSON;
    }

    public void setSentenceJSON(String sentenceJSON) {
        this.sentenceJSON = sentenceJSON;
    }

    public WordSetExperienceStatus getStatus() {
        return status;
    }

    public void setStatus(WordSetExperienceStatus status) {
        this.status = status;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PracticeWordSetExerciseMapping that = (PracticeWordSetExerciseMapping) o;
        return id == that.id &&
                Objects.equals(wordJSON, that.wordJSON) &&
                Objects.equals(sentenceJSON, that.sentenceJSON);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, wordJSON, sentenceJSON);
    }

    @Override
    public String toString() {
        return "PracticeWordSetExerciseMapping{" +
                "id=" + id +
                ", word='" + wordJSON + '\'' +
                ", wordSetId='" + wordSetId + '\'' +
                ", sentenceJSON='" + sentenceJSON + '\'' +
                '}';
    }
}
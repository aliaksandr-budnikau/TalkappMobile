package talkapp.org.talkappmobile.component.database.mappings;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.Objects;

import talkapp.org.talkappmobile.model.WordSetExperienceStatus;

import static java.util.Calendar.getInstance;
import static okhttp3.internal.Util.UTC;
import static talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping.PRACTICE_WORD_SET_EXERCISE_TABLE;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.STUDYING;

@DatabaseTable(tableName = PRACTICE_WORD_SET_EXERCISE_TABLE)
public class PracticeWordSetExerciseMapping {
    public static final String ID_FN = "id";
    public static final String WORD_FN = "word";
    public static final String SENTENCE_FN = "sentence";
    public static final String WORD_SET_ID_FN = "wordSetId";
    public static final String STATUS_FN = "status";
    public static final String CURRENT_FN = "current";
    public static final String PRACTICE_WORD_SET_EXERCISE_TABLE = "PracticeWordSetExercise";
    public static final String UPDATED_DATE_FN = "updatedDate";
    @DatabaseField(generatedId = true, columnName = ID_FN)
    private int id;

    @DatabaseField(canBeNull = false, columnName = WORD_SET_ID_FN)
    private int wordSetId;

    @DatabaseField(canBeNull = false, columnName = WORD_FN)
    private String wordJSON;

    @DatabaseField(columnName = SENTENCE_FN)
    private String sentenceJSON;

    @DatabaseField(canBeNull = false, columnName = STATUS_FN)
    private WordSetExperienceStatus status = STUDYING;

    @DatabaseField(canBeNull = false, columnName = CURRENT_FN)
    private boolean current;

    @DatabaseField(canBeNull = false, columnName = UPDATED_DATE_FN, dataType = DataType.DATE_STRING, format = "EEE MMM d HH:mm:ss Z yyyy")
    private Date updatedDate;

    public PracticeWordSetExerciseMapping() {
    }

    public PracticeWordSetExerciseMapping(int id, int wordSetId, String wordJSON, String sentenceJSON, WordSetExperienceStatus status, boolean current) {
        this(id, wordSetId, wordJSON, sentenceJSON, status, current, getInstance(UTC).getTime());
    }

    public PracticeWordSetExerciseMapping(int id, int wordSetId, String wordJSON, String sentenceJSON, WordSetExperienceStatus status, boolean current, Date updatedDate) {
        this.id = id;
        this.wordSetId = wordSetId;
        this.wordJSON = wordJSON;
        this.sentenceJSON = sentenceJSON;
        this.status = status;
        this.current = current;
        this.updatedDate = updatedDate;
    }

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

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
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
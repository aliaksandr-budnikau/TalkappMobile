package talkapp.org.talkappmobile.component.database.mappings;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.Objects;

import talkapp.org.talkappmobile.model.WordSetProgressStatus;

import static java.util.Calendar.getInstance;
import static okhttp3.internal.Util.UTC;
import static talkapp.org.talkappmobile.component.database.mappings.WordRepetitionProgressMapping.WORD_REPETITION_PROGRESS_TABLE;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;

@DatabaseTable(tableName = WORD_REPETITION_PROGRESS_TABLE)
public class WordRepetitionProgressMapping {
    public static final String ID_FN = "id";
    public static final String WORD_FN = "word";
    public static final String SENTENCE_ID_FN = "sentenceId";
    public static final String WORD_SET_ID_FN = "wordSetId";
    public static final String REPETITION_COUNTER_FN = "repetitionCounter";
    public static final String STATUS_FN = "status";
    public static final String CURRENT_FN = "current";
    public static final String WORD_REPETITION_PROGRESS_TABLE = "WordRepetitionProgress";
    public static final String UPDATED_DATE_FN = "updatedDate";
    @DatabaseField(generatedId = true, columnName = ID_FN)
    private int id;

    @DatabaseField(canBeNull = false, columnName = WORD_SET_ID_FN)
    private int wordSetId;

    @DatabaseField(canBeNull = false, columnName = WORD_FN)
    private String wordJSON;

    @DatabaseField(columnName = SENTENCE_ID_FN)
    private String sentenceId;

    @DatabaseField(canBeNull = false, columnName = STATUS_FN)
    private WordSetProgressStatus status = FIRST_CYCLE;

    @DatabaseField(canBeNull = false, columnName = CURRENT_FN)
    private boolean current;

    @DatabaseField(canBeNull = false, columnName = UPDATED_DATE_FN, dataType = DataType.DATE_LONG)
    private Date updatedDate;

    @DatabaseField(canBeNull = false, columnName = REPETITION_COUNTER_FN)
    private int repetitionCounter;

    public WordRepetitionProgressMapping() {
    }

    public WordRepetitionProgressMapping(int id, int wordSetId, String wordJSON, String sentenceId, WordSetProgressStatus status, boolean current) {
        this(id, wordSetId, wordJSON, sentenceId, status, current, getInstance(UTC).getTime());
    }

    public WordRepetitionProgressMapping(int id, int wordSetId, String wordJSON, String sentenceId, WordSetProgressStatus status, boolean current, Date updatedDate) {
        this.id = id;
        this.wordSetId = wordSetId;
        this.wordJSON = wordJSON;
        this.sentenceId = sentenceId;
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

    public String getSentenceId() {
        return sentenceId;
    }

    public void setSentenceId(String sentenceJSON) {
        this.sentenceId = sentenceJSON;
    }

    public WordSetProgressStatus getStatus() {
        return status;
    }

    public void setStatus(WordSetProgressStatus status) {
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

    public int getRepetitionCounter() {
        return repetitionCounter;
    }

    public void setRepetitionCounter(int repetitionCounter) {
        this.repetitionCounter = repetitionCounter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordRepetitionProgressMapping that = (WordRepetitionProgressMapping) o;
        return id == that.id &&
                Objects.equals(wordJSON, that.wordJSON) &&
                Objects.equals(sentenceId, that.sentenceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, wordJSON, sentenceId);
    }

    @Override
    public String toString() {
        return "WordRepetitionProgressMapping{" +
                "id=" + id +
                ", word='" + wordJSON + '\'' +
                ", wordSetId='" + wordSetId + '\'' +
                ", sentenceId='" + sentenceId + '\'' +
                '}';
    }
}
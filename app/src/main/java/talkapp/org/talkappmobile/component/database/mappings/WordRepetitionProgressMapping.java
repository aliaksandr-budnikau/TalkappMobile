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
    public static final String SENTENCE_IDS_FN = "sentenceId";
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

    @DatabaseField(columnName = SENTENCE_IDS_FN)
    private String sentenceIds;

    @DatabaseField(canBeNull = false, columnName = STATUS_FN)
    private WordSetProgressStatus status = FIRST_CYCLE;

    @DatabaseField(canBeNull = false, columnName = CURRENT_FN)
    private boolean current;

    @DatabaseField(canBeNull = false, columnName = UPDATED_DATE_FN, dataType = DataType.DATE_LONG)
    private Date updatedDate;

    @DatabaseField(canBeNull = false, columnName = REPETITION_COUNTER_FN)
    private int repetitionCounter;

    @DatabaseField(canBeNull = false, columnName = "forgettingCounter")
    private int forgettingCounter;

    public WordRepetitionProgressMapping() {
    }

    public WordRepetitionProgressMapping(int id, int wordSetId, String wordJSON, String sentenceIds, WordSetProgressStatus status, boolean current) {
        this(id, wordSetId, wordJSON, sentenceIds, status, current, getInstance(UTC).getTime());
    }

    public WordRepetitionProgressMapping(int id, int wordSetId, String wordJSON, String sentenceIds, WordSetProgressStatus status, boolean current, Date updatedDate) {
        this.id = id;
        this.wordSetId = wordSetId;
        this.wordJSON = wordJSON;
        this.sentenceIds = sentenceIds;
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

    public String getSentenceIds() {
        return sentenceIds;
    }

    public void setSentenceIds(String sentenceJSON) {
        this.sentenceIds = sentenceJSON;
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

    public int getForgettingCounter() {
        return forgettingCounter;
    }

    public void setForgettingCounter(int forgettingCounter) {
        this.forgettingCounter = forgettingCounter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordRepetitionProgressMapping that = (WordRepetitionProgressMapping) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "WordRepetitionProgressMapping{" +
                "id=" + id +
                ", wordSetId=" + wordSetId +
                ", sentenceIds='" + sentenceIds + '\'' +
                ", status=" + status +
                ", current=" + current +
                ", updatedDate=" + updatedDate +
                ", repetitionCounter=" + repetitionCounter +
                ", forgettingCounter=" + forgettingCounter +
                '}';
    }
}
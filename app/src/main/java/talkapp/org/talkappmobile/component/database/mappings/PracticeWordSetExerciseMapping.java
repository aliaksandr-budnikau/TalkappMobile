package talkapp.org.talkappmobile.component.database.mappings;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Objects;

@DatabaseTable(tableName = "PracticeWordSetExercise")
public class PracticeWordSetExerciseMapping {
    public static final String WORD_FN = "word";
    public static final String WORD_SET_ID_FN = "wordSetId";
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, columnName = WORD_SET_ID_FN)
    private String wordSetId;

    @DatabaseField(canBeNull = false, columnName = WORD_FN)
    private String word;

    @DatabaseField(canBeNull = false)
    private String sentenceJSON;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWordSetId() {
        return wordSetId;
    }

    public void setWordSetId(String wordSetId) {
        this.wordSetId = wordSetId;
    }

    public String getSentenceJSON() {
        return sentenceJSON;
    }

    public void setSentenceJSON(String sentenceJSON) {
        this.sentenceJSON = sentenceJSON;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PracticeWordSetExerciseMapping that = (PracticeWordSetExerciseMapping) o;
        return id == that.id &&
                Objects.equals(word, that.word) &&
                Objects.equals(sentenceJSON, that.sentenceJSON);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, word, sentenceJSON);
    }

    @Override
    public String toString() {
        return "PracticeWordSetExerciseMapping{" +
                "id=" + id +
                ", word='" + word + '\'' +
                ", wordSetId='" + wordSetId + '\'' +
                ", sentenceJSON='" + sentenceJSON + '\'' +
                '}';
    }
}
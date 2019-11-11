package talkapp.org.talkappmobile.mappings;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = CurrentWordSetMapping.TOPIC_TABLE)
public class CurrentWordSetMapping {
    public static final String TOPIC_TABLE = "CurrentWordSet";
    public static final String ID_FN = "id";
    public static final String NAME_FN = "words";

    @DatabaseField(id = true, unique = true, canBeNull = false, columnName = ID_FN)
    private String id;

    @DatabaseField(canBeNull = false, columnName = NAME_FN)
    private String words;

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

    @Override
    public String toString() {
        return "TopicMapping{" +
                "id='" + id + '\'' +
                ", words='" + words + '\'' +
                '}';
    }

    public static class WordSource {
        private final int wordSetId;
        private final int wordIndex;

        public WordSource(int wordSetId, int wordIndex) {
            this.wordSetId = wordSetId;
            this.wordIndex = wordIndex;
        }

        public int getWordSetId() {
            return wordSetId;
        }

        public int getWordIndex() {
            return wordIndex;
        }
    }
}
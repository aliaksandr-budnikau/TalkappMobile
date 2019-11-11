package talkapp.org.talkappmobile.mappings;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = CurrentWordSetMapping.CURRENT_WORD_SET_TABLE)
public class CurrentWordSetMapping {
    public static final String CURRENT_WORD_SET_TABLE = "CurrentWordSet";
    public static final String ID_FN = "id";
    public static final String WORDS_FN = "words";

    @DatabaseField(id = true, unique = true, canBeNull = false, columnName = ID_FN)
    private String id;

    @DatabaseField(canBeNull = false, columnName = WORDS_FN)
    private String words;

    public CurrentWordSetMapping() {
    }

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
        return "CurrentWordSetMapping{" +
                "id='" + id + '\'' +
                ", words='" + words + '\'' +
                '}';
    }

    public static class WordSource {
        private int wordSetId;
        private int wordIndex;

        public WordSource() {
        }

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
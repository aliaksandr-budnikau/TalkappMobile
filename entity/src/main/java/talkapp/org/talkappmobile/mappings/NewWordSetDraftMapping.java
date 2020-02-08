package talkapp.org.talkappmobile.mappings;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = NewWordSetDraftMapping.NEW_WORD_SET_DRAFT_MAPPING_TABLE)
public class NewWordSetDraftMapping {
    public static final String NEW_WORD_SET_DRAFT_MAPPING_TABLE = "NewWordSetDraft";

    public static final String ID_FN = "id";
    public static final String WORDS_FN = "words";

    @DatabaseField(generatedId = true, columnName = ID_FN)
    private int id;

    @DatabaseField(canBeNull = false, columnName = WORDS_FN)
    private String words;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }
}
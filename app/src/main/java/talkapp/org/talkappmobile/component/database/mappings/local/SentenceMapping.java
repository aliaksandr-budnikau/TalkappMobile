package talkapp.org.talkappmobile.component.database.mappings.local;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Objects;

import talkapp.org.talkappmobile.model.SentenceContentScore;

@DatabaseTable(tableName = SentenceMapping.SENTENCE_TABLE)
public class SentenceMapping {
    public static final String SENTENCE_TABLE = "Sentence";
    public static final String ID_FN = "id";
    public static final String TEXT_FN = "text";
    public static final String TRANSLATIONS_FN = "translations";
    public static final String TOKENS_FN = "tokens";
    public static final String CONTENT_SCORE_FN = "contentScore";

    @DatabaseField(id = true, unique = true, canBeNull = false, columnName = ID_FN)
    private String id;
    @DatabaseField(canBeNull = false, columnName = TEXT_FN)
    private String text;
    @DatabaseField(canBeNull = false, columnName = TRANSLATIONS_FN)
    private String translations;
    @DatabaseField(canBeNull = false, columnName = TOKENS_FN)
    private String tokens;
    @DatabaseField(columnName = CONTENT_SCORE_FN)
    private SentenceContentScore contentScore;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTranslations() {
        return translations;
    }

    public void setTranslations(String translations) {
        this.translations = translations;
    }

    public String getTokens() {
        return tokens;
    }

    public void setTokens(String tokens) {
        this.tokens = tokens;
    }

    public SentenceContentScore getContentScore() {
        return contentScore;
    }

    public void setContentScore(SentenceContentScore contentScore) {
        this.contentScore = contentScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SentenceMapping sentence = (SentenceMapping) o;
        return Objects.equals(id, sentence.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SentenceMapping{");
        sb.append("id='").append(id).append('\'');
        sb.append(", text='").append(text).append('\'');
        sb.append(", translations=").append(translations);
        sb.append(", tokens=").append(tokens);
        sb.append('}');
        return sb.toString();
    }
}
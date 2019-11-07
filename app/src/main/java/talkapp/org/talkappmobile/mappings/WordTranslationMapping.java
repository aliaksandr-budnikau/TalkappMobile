package talkapp.org.talkappmobile.mappings;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Objects;

/**
 * @author Budnikau Aliaksandr
 */
@DatabaseTable(tableName = WordTranslationMapping.WORD_TRANSLATION)
public class WordTranslationMapping {

    public static final String WORD_TRANSLATION = "WordTranslation";
    public static final String ID_FN = "id";
    public static final String WORD_FN = "word";
    public static final String LANGUAGE_FN = "language";
    public static final String TRANSLATION_FN = "translation";
    public static final String TOP_FN = "top";


    @DatabaseField(id = true, unique = true, canBeNull = false, columnName = ID_FN)
    private String id;

    @DatabaseField(canBeNull = false, columnName = WORD_FN)
    private String word;

    @DatabaseField(canBeNull = false, columnName = LANGUAGE_FN)
    private String language;

    @DatabaseField(canBeNull = false, columnName = TRANSLATION_FN)
    private String translation;

    @DatabaseField(columnName = TOP_FN)
    private Integer top;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordTranslationMapping that = (WordTranslationMapping) o;
        return Objects.equals(word, that.word) &&
                Objects.equals(language, that.language) &&
                Objects.equals(translation, that.translation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, word, language, translation);
    }

    @Override
    public String toString() {
        return "WordTranslationMapping{" +
                "id='" + id + '\'' +
                "word='" + word + '\'' +
                ", language='" + language + '\'' +
                ", translation='" + translation + '\'' +
                '}';
    }
}
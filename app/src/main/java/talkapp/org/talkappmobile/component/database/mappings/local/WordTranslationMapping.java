package talkapp.org.talkappmobile.component.database.mappings.local;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Budnikau Aliaksandr
 */
@DatabaseTable(tableName = WordTranslationMapping.WORD_TRANSLATION)
public class WordTranslationMapping {

    public static final String WORD_TRANSLATION = "WordTranslation";
    public static final String ID_FN = "word";
    public static final String LANGUAGE_FN = "language";
    public static final String TRANSLATION_FN = "translation";

    @DatabaseField(id = true, unique = true, canBeNull = false, columnName = ID_FN)
    private String word;

    @DatabaseField(canBeNull = false, columnName = LANGUAGE_FN)
    private String language;

    @DatabaseField(canBeNull = false, columnName = TRANSLATION_FN)
    private String translation;

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
        return Objects.hash(word, language, translation);
    }

    @Override
    public String toString() {
        return "WordTranslationMapping{" +
                "word='" + word + '\'' +
                ", language='" + language + '\'' +
                ", translation='" + translation + '\'' +
                '}';
    }

    public static class WordTranslationMappingKey implements Serializable {
        private String word;
        private String language;

        public WordTranslationMappingKey() {
        }

        public WordTranslationMappingKey(String word, String language) {
            this.word = word;
            this.language = language;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WordTranslationMappingKey that = (WordTranslationMappingKey) o;
            return Objects.equals(word, that.word) &&
                    Objects.equals(language, that.language);
        }

        @Override
        public int hashCode() {
            return Objects.hash(word, language);
        }

        public String getWord() {
            return word;
        }

        public String getLanguage() {
            return language;
        }
    }
}
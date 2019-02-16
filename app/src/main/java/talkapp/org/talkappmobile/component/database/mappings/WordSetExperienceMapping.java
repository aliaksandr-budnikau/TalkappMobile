package talkapp.org.talkappmobile.component.database.mappings;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Objects;

import static talkapp.org.talkappmobile.component.database.mappings.WordSetExperienceMapping.WORD_SET_EXPERIENCE_TABLE;

@DatabaseTable(tableName = WORD_SET_EXPERIENCE_TABLE)
public class WordSetExperienceMapping {
    public static final String ID_FN = "id";
    public static final String WORD_SET_EXPERIENCE_TABLE = "WordSetExperience";

    @DatabaseField(id = true, unique = true, canBeNull = false, columnName = ID_FN)
    private int id;

    public WordSetExperienceMapping() {
    }

    public WordSetExperienceMapping(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordSetExperienceMapping that = (WordSetExperienceMapping) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WordSetExperienceMapping{");
        sb.append("id='").append(id).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
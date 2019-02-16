package talkapp.org.talkappmobile.component.database.mappings;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Objects;

import talkapp.org.talkappmobile.model.WordSetExperienceStatus;

import static talkapp.org.talkappmobile.component.database.mappings.WordSetExperienceMapping.WORD_SET_EXPERIENCE_TABLE;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.FIRST_CYCLE;

@DatabaseTable(tableName = WORD_SET_EXPERIENCE_TABLE)
public class WordSetExperienceMapping {
    public static final String ID_FN = "id";
    public static final String STATUS_FN = "status";
    public static final String WORD_SET_EXPERIENCE_TABLE = "WordSetExperience";

    @DatabaseField(id = true, unique = true, canBeNull = false, columnName = ID_FN)
    private int id;

    @DatabaseField(canBeNull = false, columnName = STATUS_FN)
    private WordSetExperienceStatus status = FIRST_CYCLE;

    public WordSetExperienceMapping() {
    }

    public WordSetExperienceMapping(int id, WordSetExperienceStatus status) {
        this.id = id;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public WordSetExperienceStatus getStatus() {
        return status;
    }

    public void setStatus(WordSetExperienceStatus status) {
        this.status = status;
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
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }
}
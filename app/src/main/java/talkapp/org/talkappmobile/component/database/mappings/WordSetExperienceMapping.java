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
    public static final String TRAINING_EXPERIENCE_FN = "trainingExperience";
    public static final String MAX_TRAINING_EXPERIENCE_FN = "maxTrainingExperience";
    public static final String STATUS_FN = "status";
    public static final String WORD_SET_EXPERIENCE_TABLE = "WordSetExperience";

    @DatabaseField(id = true, unique = true, canBeNull = false, columnName = ID_FN)
    private int id;

    @DatabaseField(canBeNull = false, columnName = TRAINING_EXPERIENCE_FN)
    private int trainingExperience;

    @DatabaseField(canBeNull = false, columnName = MAX_TRAINING_EXPERIENCE_FN)
    private int maxTrainingExperience;

    @DatabaseField(canBeNull = false, columnName = STATUS_FN)
    private WordSetExperienceStatus status = FIRST_CYCLE;

    public WordSetExperienceMapping() {
    }

    public WordSetExperienceMapping(int id, int trainingExperience, int maxTrainingExperience, WordSetExperienceStatus status) {
        this.id = id;
        this.trainingExperience = trainingExperience;
        this.maxTrainingExperience = maxTrainingExperience;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTrainingExperience() {
        return trainingExperience;
    }

    public void setTrainingExperience(int trainingExperience) {
        this.trainingExperience = trainingExperience;
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
        return trainingExperience == that.trainingExperience &&
                Objects.equals(id, that.id) &&
                Objects.equals(maxTrainingExperience, that.maxTrainingExperience);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, trainingExperience, maxTrainingExperience);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WordSetExperienceMapping{");
        sb.append("id='").append(id).append('\'');
        sb.append(", maxTrainingExperience='").append(maxTrainingExperience).append('\'');
        sb.append(", trainingExperience=").append(trainingExperience);
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }

    public int getMaxTrainingExperience() {
        return maxTrainingExperience;
    }

    public void setMaxTrainingExperience(int maxTrainingExperience) {
        this.maxTrainingExperience = maxTrainingExperience;
    }
}
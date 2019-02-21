package talkapp.org.talkappmobile.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Budnikau Aliaksandr
 */
public class WordSetExperience implements Serializable {
    private int id;

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
        WordSetExperience that = (WordSetExperience) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WordSetExperienceMapping{");
        sb.append(", id='").append(id).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
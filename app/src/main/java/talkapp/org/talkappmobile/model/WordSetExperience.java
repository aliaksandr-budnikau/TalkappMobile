package talkapp.org.talkappmobile.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Budnikau Aliaksandr
 */
public class WordSetExperience implements Serializable {
    private int id;
    private String wordSetId;
    private int trainingExperience;
    private int maxTrainingExperience;
    private WordSetExperienceStatus status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWordSetId() {
        return wordSetId;
    }

    public void setWordSetId(String wordSetId) {
        this.wordSetId = wordSetId;
    }

    public int getTrainingExperience() {
        return trainingExperience;
    }

    public void setTrainingExperience(int trainingExperience) {
        this.trainingExperience = trainingExperience;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordSetExperience that = (WordSetExperience) o;
        return trainingExperience == that.trainingExperience &&
                Objects.equals(wordSetId, that.wordSetId) &&
                Objects.equals(id, that.id) &&
                Objects.equals(maxTrainingExperience, that.maxTrainingExperience);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, wordSetId, trainingExperience, maxTrainingExperience);
    }

    public WordSetExperienceStatus getStatus() {
        return status;
    }

    public void setStatus(WordSetExperienceStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WordSetExperienceMapping{");
        sb.append("wordSetId='").append(wordSetId).append('\'');
        sb.append(", status='").append(status).append('\'');
        sb.append(", id='").append(id).append('\'');
        sb.append(", maxTrainingExperience='").append(maxTrainingExperience).append('\'');
        sb.append(", trainingExperience=").append(trainingExperience);
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
package talkapp.org.talkappmobile.model;


import java.io.Serializable;
import java.util.List;

/**
 * @author Budnikau Aliaksandr
 */
public class WordSet implements Serializable {
    private String id;

    private List<String> words;

    private int trainingExperience;

    private int maxTrainingExperience;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public int getTrainingExperience() {
        return trainingExperience;
    }

    public void setTrainingExperience(int trainingExperience) {
        this.trainingExperience = trainingExperience;
    }

    public int getMaxTrainingExperience() {
        return maxTrainingExperience;
    }

    public void setMaxTrainingExperience(int maxTrainingExperience) {
        this.maxTrainingExperience = maxTrainingExperience;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WordSet)) return false;

        WordSet wordSet = (WordSet) o;

        if (getTrainingExperience() != wordSet.getTrainingExperience()) return false;
        if (getMaxTrainingExperience() != wordSet.getMaxTrainingExperience()) return false;
        if (!getId().equals(wordSet.getId())) return false;
        return getWords().equals(wordSet.getWords());

    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getWords().hashCode();
        result = 31 * result + getTrainingExperience();
        result = 31 * result + getMaxTrainingExperience();
        return result;
    }

    @Override
    public String toString() {
        return "WordSet{" +
                "id='" + id + '\'' +
                ", words=" + words +
                ", trainingExperience=" + trainingExperience +
                ", maxTrainingExperience=" + maxTrainingExperience +
                '}';
    }
}
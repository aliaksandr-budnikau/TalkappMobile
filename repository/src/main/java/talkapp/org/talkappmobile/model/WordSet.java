package talkapp.org.talkappmobile.model;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FINISHED;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;

/**
 * @author Budnikau Aliaksandr
 */
public class WordSet implements Serializable {
    private int id;

    private String topicId;

    private List<Word2Tokens> words;

    private Integer top;

    private int trainingExperience;

    private WordSetProgressStatus status = FIRST_CYCLE;

    private RepetitionClass repetitionClass;

    private int availableInHours;

    public WordSet() {
    }

    public WordSet(WordSet wordSet) {
        this.id = wordSet.getId();
        this.topicId = wordSet.getTopicId();
        this.words = new ArrayList<>(wordSet.getWords());
        this.top = wordSet.getTop();
        this.trainingExperience = wordSet.getTrainingExperience();
        this.status = wordSet.getStatus();
        this.repetitionClass = wordSet.getRepetitionClass();
        this.availableInHours = wordSet.getAvailableInHours();
    }

    public int getMaxTrainingExperience() {
        if (status != FINISHED) {
            return words.size() * 2;
        }
        return words.size();
    }

    public int getTrainingExperienceInPercentages() {
        return (trainingExperience * 100) / getMaxTrainingExperience();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Word2Tokens> getWords() {
        return words;
    }

    public void setWords(List<Word2Tokens> words) {
        this.words = words;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    public int getTrainingExperience() {
        return trainingExperience;
    }

    public void setTrainingExperience(int trainingExperience) {
        this.trainingExperience = trainingExperience;
    }

    public WordSetProgressStatus getStatus() {
        return status;
    }

    public void setStatus(WordSetProgressStatus status) {
        this.status = status;
    }

    public RepetitionClass getRepetitionClass() {
        return repetitionClass;
    }

    public void setRepetitionClass(RepetitionClass repetitionClass) {
        this.repetitionClass = repetitionClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        WordSet wordSet = (WordSet) o;

        return new EqualsBuilder()
                .append(id, wordSet.id)
                .append(words, wordSet.words)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(words)
                .toHashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WordSet{");
        sb.append("id='").append(id).append('\'');
        sb.append(", words=").append(words);
        sb.append(", topicId=").append(topicId);
        sb.append('}');
        return sb.toString();
    }

    public int getAvailableInHours() {
        return availableInHours;
    }

    public void setAvailableInHours(int availableInHours) {
        this.availableInHours = availableInHours;
    }
}
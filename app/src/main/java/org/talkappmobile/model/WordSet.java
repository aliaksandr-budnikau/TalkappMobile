package org.talkappmobile.model;


import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import static org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;

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

    public WordSet() {
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
        return Objects.equals(id, wordSet.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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
}
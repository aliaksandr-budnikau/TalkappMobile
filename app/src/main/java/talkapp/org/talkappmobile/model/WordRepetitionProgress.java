package talkapp.org.talkappmobile.model;

import java.util.Date;
import java.util.List;

public class WordRepetitionProgress {
    private List<String> sentenceIds;
    private Date updatedDate;
    private int repetitionCounter;
    private int wordSetId;
    private int wordIndex;
    private int forgettingCounter;
    private String status;
    private int id;

    public List<String> getSentenceIds() {
        return sentenceIds;
    }

    public void setSentenceIds(List<String> sentenceIds) {
        this.sentenceIds = sentenceIds;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public int getRepetitionCounter() {
        return repetitionCounter;
    }

    public void setRepetitionCounter(int repetitionCounter) {
        this.repetitionCounter = repetitionCounter;
    }

    public int getWordSetId() {
        return wordSetId;
    }

    public void setWordSetId(int wordSetId) {
        this.wordSetId = wordSetId;
    }

    public int getWordIndex() {
        return wordIndex;
    }

    public void setWordIndex(int wordIndex) {
        this.wordIndex = wordIndex;
    }

    public int getForgettingCounter() {
        return forgettingCounter;
    }

    public void setForgettingCounter(int forgettingCounter) {
        this.forgettingCounter = forgettingCounter;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
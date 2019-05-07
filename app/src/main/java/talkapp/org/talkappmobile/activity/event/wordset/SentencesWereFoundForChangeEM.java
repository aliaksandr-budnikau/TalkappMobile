package talkapp.org.talkappmobile.activity.event.wordset;

import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;

public class SentencesWereFoundForChangeEM {
    private final List<Sentence> sentences;

    public SentencesWereFoundForChangeEM(List<Sentence> sentences) {
        this.sentences = sentences;
    }

    public List<Sentence> getSentences() {
        return sentences;
    }
}
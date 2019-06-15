package talkapp.org.talkappmobile.activity.event.wordset;

import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;

public class SentencesWereFoundForChangeEM {
    private final List<Sentence> sentences;
    private final List<Sentence> alreadyPickedSentences;

    public SentencesWereFoundForChangeEM(List<Sentence> sentences, List<Sentence> alreadyPickedSentences) {
        this.sentences = sentences;
        this.alreadyPickedSentences = alreadyPickedSentences;
    }

    public List<Sentence> getSentences() {
        return sentences;
    }

    public List<Sentence> getAlreadyPickedSentences() {
        return alreadyPickedSentences;
    }
}
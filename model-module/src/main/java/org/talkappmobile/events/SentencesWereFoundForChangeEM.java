package org.talkappmobile.events;

import java.util.List;

import org.talkappmobile.model.Sentence;
import org.talkappmobile.model.Word2Tokens;

public class SentencesWereFoundForChangeEM {
    private final List<Sentence> sentences;
    private final List<Sentence> alreadyPickedSentences;
    private final Word2Tokens word;

    public SentencesWereFoundForChangeEM(List<Sentence> sentences, List<Sentence> alreadyPickedSentences, Word2Tokens word) {
        this.sentences = sentences;
        this.alreadyPickedSentences = alreadyPickedSentences;
        this.word = word;
    }

    public List<Sentence> getSentences() {
        return sentences;
    }

    public List<Sentence> getAlreadyPickedSentences() {
        return alreadyPickedSentences;
    }

    public Word2Tokens getWord() {
        return word;
    }
}
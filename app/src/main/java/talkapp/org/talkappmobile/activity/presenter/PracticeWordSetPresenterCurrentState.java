package talkapp.org.talkappmobile.activity.presenter;

import android.net.Uri;

import java.util.ListIterator;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.WordSet;

public class PracticeWordSetPresenterCurrentState {
    private final WordSet wordSet;
    private Sentence sentence;
    private ListIterator<String> wordSequenceIterator;
    private String word;
    private Uri voiceRecordUri;

    public PracticeWordSetPresenterCurrentState(WordSet wordSet) {
        this.wordSet = wordSet;
    }

    public WordSet getWordSet() {
        return wordSet;
    }

    public Sentence getSentence() {
        return sentence;
    }

    public void setSentence(Sentence sentence) {
        this.sentence = sentence;
    }

    public void setWordSequenceIterator() {
        this.wordSequenceIterator = wordSet.getWords().listIterator();
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Uri getVoiceRecordUri() {
        return voiceRecordUri;
    }

    public void setVoiceRecordUri(Uri voiceRecordUri) {
        this.voiceRecordUri = voiceRecordUri;
    }

    public void nextWord() {
        word = wordSequenceIterator.next();
    }

    public String getWordSetId() {
        return wordSet.getId();
    }
}

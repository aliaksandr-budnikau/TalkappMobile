package talkapp.org.talkappmobile.activity.presenter;

import android.net.Uri;

import talkapp.org.talkappmobile.model.WordSet;

public class PracticeWordSetPresenterCurrentState {
    private final WordSet wordSet;
    private Uri voiceRecordUri;

    public PracticeWordSetPresenterCurrentState(WordSet wordSet) {
        this.wordSet = wordSet;
    }

    public WordSet getWordSet() {
        return wordSet;
    }

    public Uri getVoiceRecordUri() {
        return voiceRecordUri;
    }

    public void setVoiceRecordUri(Uri voiceRecordUri) {
        this.voiceRecordUri = voiceRecordUri;
    }

    public int getWordSetId() {
        return wordSet.getId();
    }
}

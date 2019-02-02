package talkapp.org.talkappmobile.activity.custom.presenter;

import talkapp.org.talkappmobile.model.Sentence;

public class PronounceRightAnswerButtonPresenter {
    private Sentence sentence;
    private boolean locked;

    public void setModel(Sentence sentence) {
        this.sentence = sentence;
    }

    public void lock() {
        locked = true;
    }

    public void unlock() {
        locked = false;
    }
}
package talkapp.org.talkappmobile.activity.custom.presenter;

import talkapp.org.talkappmobile.activity.custom.interactor.RightAnswerTextViewInteractor;
import talkapp.org.talkappmobile.activity.custom.listener.OnRightAnswerTextViewListener;
import talkapp.org.talkappmobile.activity.custom.view.RightAnswerTextViewView;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;

public class RightAnswerTextViewPresenter implements OnRightAnswerTextViewListener {
    private final RightAnswerTextViewView view;
    private final RightAnswerTextViewInteractor interactor;
    private Sentence sentence;
    private Word2Tokens word;
    private boolean locked;

    public RightAnswerTextViewPresenter(RightAnswerTextViewInteractor interactor, RightAnswerTextViewView view) {
        this.interactor = interactor;
        this.view = view;
    }

    public void setModel(Sentence sentence, Word2Tokens word) {
        this.sentence = sentence;
        this.word = word;
    }

    public void maskEntirely() {
        interactor.maskEntirely(sentence, locked, this);
    }

    public void maskOnlyWord() {
        interactor.maskOnlyWord(sentence, word, locked, this);
    }

    public void unmask() {
        interactor.unmask(sentence, this);
    }

    public void lock() {
        locked = true;
    }

    public void unlock() {
        locked = false;
    }

    @Override
    public void onNewValue(String newValue) {
        view.onNewValue(newValue);
    }
}
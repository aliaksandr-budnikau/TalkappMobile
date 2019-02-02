package talkapp.org.talkappmobile.activity.custom.presenter;

import talkapp.org.talkappmobile.activity.custom.interactor.PronounceRightAnswerButtonInteractor;
import talkapp.org.talkappmobile.activity.custom.listener.PronounceRightAnswerButtonListener;
import talkapp.org.talkappmobile.activity.custom.view.PronounceRightAnswerButtonView;
import talkapp.org.talkappmobile.model.Sentence;

public class PronounceRightAnswerButtonPresenter implements PronounceRightAnswerButtonListener {
    private final PronounceRightAnswerButtonView view;
    private final PronounceRightAnswerButtonInteractor interactor;
    private Sentence sentence;
    private boolean locked;

    public PronounceRightAnswerButtonPresenter(PronounceRightAnswerButtonInteractor interactor, PronounceRightAnswerButtonView view) {
        this.interactor = interactor;
        this.view = view;
    }

    public void setModel(Sentence sentence) {
        this.sentence = sentence;
    }

    public void lock() {
        locked = true;
    }

    public void unlock() {
        locked = false;
    }

    public void pronounceRightAnswerButtonClick() {
        try {
            view.onStartSpeaking();
            if (sentence != null) {
                interactor.pronounceRightAnswer(sentence, locked, this);
            }
        } finally {
            view.onStopSpeaking();
        }
    }

    @Override
    public void onAnswerHasBeenRevealed() {
        view.onAnswerHasBeenRevealed();
    }
}
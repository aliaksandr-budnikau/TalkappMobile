package talkapp.org.talkappmobile.presenter;

import talkapp.org.talkappmobile.interactor.PronounceRightAnswerButtonInteractor;
import talkapp.org.talkappmobile.listener.PronounceRightAnswerButtonListener;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.view.PronounceRightAnswerButtonView;

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

    @Override
    public void onPronounceRightAnswer(String text) {
        view.onPronounceRightAnswer(text);
    }
}
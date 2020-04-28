package talkapp.org.talkappmobile.presenter;

import talkapp.org.talkappmobile.interactor.PronounceRightAnswerButtonInteractor;
import talkapp.org.talkappmobile.listener.PronounceRightAnswerButtonListener;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.view.PronounceRightAnswerButtonView;

public class PronounceRightAnswerButtonPresenterImpl implements PronounceRightAnswerButtonListener, PronounceRightAnswerButtonPresenter {
    private final PronounceRightAnswerButtonView view;
    private final PronounceRightAnswerButtonInteractor interactor;
    private Sentence sentence;
    private boolean locked;

    public PronounceRightAnswerButtonPresenterImpl(PronounceRightAnswerButtonInteractor interactor, PronounceRightAnswerButtonView view) {
        this.interactor = interactor;
        this.view = view;
    }

    @Override
    public void setModel(Sentence sentence) {
        this.sentence = sentence;
    }

    @Override
    public void lock() {
        locked = true;
    }

    @Override
    public void unlock() {
        locked = false;
    }

    @Override
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
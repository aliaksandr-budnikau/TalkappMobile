package talkapp.org.talkappmobile.interactor;

import javax.inject.Inject;

import talkapp.org.talkappmobile.listener.PronounceRightAnswerButtonListener;
import talkapp.org.talkappmobile.model.Sentence;

public class PronounceRightAnswerButtonInteractor {

    @Inject
    public PronounceRightAnswerButtonInteractor() {
    }

    public void pronounceRightAnswer(Sentence sentence, boolean locked, PronounceRightAnswerButtonListener listener) {
        try {
            listener.onPronounceRightAnswer(sentence.getText());
            if (!locked) {
                listener.onAnswerHasBeenRevealed();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
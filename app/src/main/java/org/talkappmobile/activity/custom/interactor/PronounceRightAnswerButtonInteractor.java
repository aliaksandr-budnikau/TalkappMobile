package org.talkappmobile.activity.custom.interactor;

import org.talkappmobile.model.Sentence;

import org.talkappmobile.activity.custom.listener.PronounceRightAnswerButtonListener;
import org.talkappmobile.component.Speaker;

public class PronounceRightAnswerButtonInteractor {
    private final Speaker speaker;

    public PronounceRightAnswerButtonInteractor(Speaker speaker) {
        this.speaker = speaker;
    }

    public void pronounceRightAnswer(Sentence sentence, boolean locked, PronounceRightAnswerButtonListener listener) {
        try {
            speaker.speak(sentence.getText());
            if (!locked) {
                listener.onAnswerHasBeenRevealed();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
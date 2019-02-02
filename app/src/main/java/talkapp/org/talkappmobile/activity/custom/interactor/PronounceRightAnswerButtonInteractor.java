package talkapp.org.talkappmobile.activity.custom.interactor;

import talkapp.org.talkappmobile.activity.custom.listener.PronounceRightAnswerButtonListener;
import talkapp.org.talkappmobile.component.Speaker;
import talkapp.org.talkappmobile.model.Sentence;

public class PronounceRightAnswerButtonInteractor {
    private final Speaker speaker;

    public PronounceRightAnswerButtonInteractor(Speaker speaker) {
        this.speaker = speaker;
    }

    public void pronounceRightAnswer(Sentence sentence, PronounceRightAnswerButtonListener listener) {
        try {
            speaker.speak(sentence.getText());
            listener.onAnswerPronounced();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
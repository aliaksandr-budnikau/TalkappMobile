package talkapp.org.talkappmobile.presenter;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;

public interface RightAnswerTextViewPresenter {
    void setModel(Sentence sentence, Word2Tokens word);

    void mask();

    void rightAnswerTouched();

    void lock();

    void unlock();

    void enableHideAllMode();

    void turnAnswerToLink();
}

package talkapp.org.talkappmobile.presenter;

import talkapp.org.talkappmobile.model.Sentence;

public interface PronounceRightAnswerButtonPresenter {
    void unlock();

    void setModel(Sentence sentence);

    void lock();

    void pronounceRightAnswerButtonClick();
}

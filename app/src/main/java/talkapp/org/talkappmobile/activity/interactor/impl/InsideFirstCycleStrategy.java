package talkapp.org.talkappmobile.activity.interactor.impl;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;

class InsideFirstCycleStrategy extends PracticeWordSetInteractorStrategy {
    InsideFirstCycleStrategy(PracticeWordSetInteractor interactor) {
        super(interactor);
    }

    @Override
    void finishWord(OnPracticeWordSetListener listener) {
        listener.onRightAnswer(getInteractor().getCurrentSentence());
    }
}
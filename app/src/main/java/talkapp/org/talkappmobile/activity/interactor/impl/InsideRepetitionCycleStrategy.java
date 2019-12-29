package talkapp.org.talkappmobile.activity.interactor.impl;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;

class InsideRepetitionCycleStrategy extends PracticeWordSetInteractorStrategy {
    public InsideRepetitionCycleStrategy(PracticeWordSetInteractor interactor) {
        super(interactor);
    }

    @Override
    void finishWord(OnPracticeWordSetListener listener) {
        listener.onRightAnswer(getInteractor().getCurrentSentence());
    }
}
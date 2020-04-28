package talkapp.org.talkappmobile.interactor.impl;

import talkapp.org.talkappmobile.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.listener.OnPracticeWordSetListener;

class RepetitionFinishedStrategy extends PracticeWordSetInteractorStrategy {
    RepetitionFinishedStrategy(PracticeWordSetInteractor interactor) {
        super(interactor);
    }

    @Override
    void finishWord(OnPracticeWordSetListener listener) {
        listener.onTrainingFinished();
    }
}
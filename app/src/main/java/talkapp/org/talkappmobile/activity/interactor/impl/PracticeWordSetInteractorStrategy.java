package talkapp.org.talkappmobile.activity.interactor.impl;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;

public abstract class PracticeWordSetInteractorStrategy {

    private final PracticeWordSetInteractor interactor;

    PracticeWordSetInteractorStrategy(PracticeWordSetInteractor interactor) {
        this.interactor = interactor;
    }

    public PracticeWordSetInteractor getInteractor() {
        return interactor;
    }

    void finishWord(OnPracticeWordSetListener listener) {

    }

    void initialiseExperience(OnPracticeWordSetListener listener) {

    }
}
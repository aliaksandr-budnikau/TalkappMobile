package talkapp.org.talkappmobile.interactor.impl;

import lombok.experimental.Delegate;
import talkapp.org.talkappmobile.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;

public class StrategySwitcherDecorator implements PracticeWordSetInteractor {
    private final WordRepetitionProgressService progressService;
    private final CurrentPracticeStateService currentPracticeStateService;
    @Delegate(excludes = ExcludedMethods.class)
    private final PracticeWordSetInteractor interactor;

    public StrategySwitcherDecorator(PracticeWordSetInteractor interactor, WordRepetitionProgressService progressService, CurrentPracticeStateService currentPracticeStateService) {
        this.interactor = interactor;
        this.progressService = progressService;
        this.currentPracticeStateService = currentPracticeStateService;
    }

    @Override
    public void initialiseExperience(OnPracticeWordSetListener listener) {
        WordSet wordSet = currentPracticeStateService.getWordSet();
        interactor.changeStrategy(new UnknownState(this));
        if (WordSetProgressStatus.SECOND_CYCLE.equals(wordSet.getStatus())) {
            interactor.changeStrategy(new InsideSecondCycleStrategy(this, progressService, currentPracticeStateService));
        } else if (WordSetProgressStatus.FINISHED.equals(wordSet.getStatus())) {
            interactor.changeStrategy(new InsideRepetitionCycleStrategy(this, currentPracticeStateService));
        }
        interactor.initialiseExperience(listener);
    }

    @Override
    public void finishWord(OnPracticeWordSetListener listener) {
        WordSet wordSet = currentPracticeStateService.getWordSet();
        if (wordSet.getId() == 0 && wordSet.getStatus() == WordSetProgressStatus.FINISHED) {
            if (wordSet.getTrainingExperience() == currentPracticeStateService.getAllWords().size()) {
                interactor.changeStrategy(new RepetitionFinishedStrategy(this));
            } else {
                interactor.changeStrategy(new InsideRepetitionCycleStrategy(this, currentPracticeStateService));
            }
        } else if (wordSet.getStatus() != WordSetProgressStatus.FINISHED) {
            if (wordSet.getTrainingExperience() == wordSet.getMaxTrainingExperience() / 2) {
                interactor.changeStrategy(new FirstCycleFinishedStrategy(this, currentPracticeStateService));
            } else if (wordSet.getTrainingExperience() == wordSet.getMaxTrainingExperience()) {
                interactor.changeStrategy(new SecondCycleFinishedStrategy(this, progressService, currentPracticeStateService));
            } else {
                if (wordSet.getStatus() == WordSetProgressStatus.SECOND_CYCLE) {
                    interactor.changeStrategy(new InsideSecondCycleStrategy(this, progressService, currentPracticeStateService));
                } else {
                    interactor.changeStrategy(new InsideFirstCycleStrategy(this, currentPracticeStateService));
                }
            }
        }
        interactor.finishWord(listener);
    }

    private interface ExcludedMethods {

        void initialiseExperience(OnPracticeWordSetListener listener);

        void finishWord(OnPracticeWordSetListener listener);
    }
}
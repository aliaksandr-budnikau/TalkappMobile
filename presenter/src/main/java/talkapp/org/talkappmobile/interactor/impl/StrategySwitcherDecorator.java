package talkapp.org.talkappmobile.interactor.impl;

import talkapp.org.talkappmobile.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;

public class StrategySwitcherDecorator extends PracticeWordSetInteractorDecorator {
    private final WordRepetitionProgressService progressService;
    private final CurrentPracticeStateService currentPracticeStateService;

    public StrategySwitcherDecorator(PracticeWordSetInteractor interactor, WordRepetitionProgressService progressService, CurrentPracticeStateService currentPracticeStateService) {
        super(interactor);
        this.progressService = progressService;
        this.currentPracticeStateService = currentPracticeStateService;
    }

    @Override
    public void initialiseExperience(OnPracticeWordSetListener listener) {
        WordSet wordSet = currentPracticeStateService.getWordSet();
        super.changeStrategy(new UnknownState(this));
        if (WordSetProgressStatus.SECOND_CYCLE.equals(wordSet.getStatus())) {
            super.changeStrategy(new InsideSecondCycleStrategy(this, progressService, currentPracticeStateService));
        } else if (WordSetProgressStatus.FINISHED.equals(wordSet.getStatus())) {
            super.changeStrategy(new InsideRepetitionCycleStrategy(this, currentPracticeStateService));
        }
        super.initialiseExperience(listener);
    }

    @Override
    public void finishWord(OnPracticeWordSetListener listener) {
        WordSet wordSet = currentPracticeStateService.getWordSet();
        if (wordSet.getId() == 0 && wordSet.getStatus() == WordSetProgressStatus.FINISHED) {
            if (wordSet.getTrainingExperience() == currentPracticeStateService.getAllWords().size()) {
                super.changeStrategy(new RepetitionFinishedStrategy(this));
            } else {
                super.changeStrategy(new InsideRepetitionCycleStrategy(this, currentPracticeStateService));
            }
        } else if (wordSet.getStatus() != WordSetProgressStatus.FINISHED) {
            if (wordSet.getTrainingExperience() == wordSet.getMaxTrainingExperience() / 2) {
                super.changeStrategy(new FirstCycleFinishedStrategy(this, currentPracticeStateService));
            } else if (wordSet.getTrainingExperience() == wordSet.getMaxTrainingExperience()) {
                super.changeStrategy(new SecondCycleFinishedStrategy(this, progressService, currentPracticeStateService));
            } else {
                if (wordSet.getStatus() == WordSetProgressStatus.SECOND_CYCLE) {
                    super.changeStrategy(new InsideSecondCycleStrategy(this, progressService, currentPracticeStateService));
                } else {
                    super.changeStrategy(new InsideFirstCycleStrategy(this, currentPracticeStateService));
                }
            }
        }
        super.finishWord(listener);
    }
}
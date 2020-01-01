package talkapp.org.talkappmobile.activity.interactor.impl;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetExperienceUtils;

import static talkapp.org.talkappmobile.model.WordSetProgressStatus.SECOND_CYCLE;

public class StrategySwitcherDecorator extends PracticeWordSetInteractorDecorator {
    private final WordSetExperienceUtils experienceUtils;
    private final WordRepetitionProgressService progressService;
    private final CurrentPracticeStateService currentPracticeStateService;

    public StrategySwitcherDecorator(PracticeWordSetInteractor interactor,
                                     WordSetExperienceUtils experienceUtils, WordRepetitionProgressService progressService, CurrentPracticeStateService currentPracticeStateService) {
        super(interactor);
        this.experienceUtils = experienceUtils;
        this.progressService = progressService;
        this.currentPracticeStateService = currentPracticeStateService;
    }

    @Override
    public void initialiseExperience(OnPracticeWordSetListener listener) {
        WordSet wordSet = currentPracticeStateService.getWordSet();
        super.changeStrategy(new UnknownState(this));
        if (SECOND_CYCLE.equals(wordSet.getStatus())) {
            super.changeStrategy(new InsideSecondCycleStrategy(this, progressService, currentPracticeStateService));
        }
        super.initialiseExperience(listener);
    }

    @Override
    public void finishWord(OnPracticeWordSetListener listener) {
        WordSet wordSet = currentPracticeStateService.getWordSet();
        if (wordSet.getId() == 0) {
            if (wordSet.getTrainingExperience() == wordSet.getWords().size()) {
                super.changeStrategy(new RepetitionFinishedStrategy(this));
            } else {
                super.changeStrategy(new InsideRepetitionCycleStrategy(this, currentPracticeStateService));
            }
        } else if (wordSet.getTrainingExperience() == experienceUtils.getMaxTrainingProgress(wordSet) / 2) {
            super.changeStrategy(new FirstCycleFinishedStrategy(this, currentPracticeStateService));
        } else if (wordSet.getTrainingExperience() == experienceUtils.getMaxTrainingProgress(wordSet)) {
            super.changeStrategy(new SecondCycleFinishedStrategy(this, progressService, currentPracticeStateService));
        } else {
            if (wordSet.getStatus() == SECOND_CYCLE) {
                super.changeStrategy(new InsideSecondCycleStrategy(this, progressService, currentPracticeStateService));
            } else {
                super.changeStrategy(new InsideFirstCycleStrategy(this, currentPracticeStateService));
            }
        }
        super.finishWord(listener);
    }
}
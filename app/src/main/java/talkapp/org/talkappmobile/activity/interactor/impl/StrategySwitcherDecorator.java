package talkapp.org.talkappmobile.activity.interactor.impl;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.CurrentPracticeState;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetExperienceUtils;
import talkapp.org.talkappmobile.service.WordSetService;

import static talkapp.org.talkappmobile.model.WordSetProgressStatus.SECOND_CYCLE;

public class StrategySwitcherDecorator extends PracticeWordSetInteractorDecorator {
    private final WordSetService wordSetService;
    private final WordSetExperienceUtils experienceUtils;
    private final WordRepetitionProgressService progressService;
    private final CurrentPracticeStateService currentPracticeStateService;

    public StrategySwitcherDecorator(PracticeWordSetInteractor interactor, WordSetService wordSetService,
                                     WordSetExperienceUtils experienceUtils, WordRepetitionProgressService progressService, CurrentPracticeStateService currentPracticeStateService) {
        super(interactor);
        this.wordSetService = wordSetService;
        this.experienceUtils = experienceUtils;
        this.progressService = progressService;
        this.currentPracticeStateService = currentPracticeStateService;
    }

    @Override
    public void initialiseExperience(OnPracticeWordSetListener listener) {
        WordSet wordSet = currentPracticeStateService.getWordSet();
        super.changeStrategy(new UnknownState(this));
        if (SECOND_CYCLE.equals(wordSet.getStatus())) {
            super.changeStrategy(new InsideSecondCycleStrategy(this, wordSetService, progressService, currentPracticeStateService));
        }
        super.initialiseExperience(listener);
    }

    @Override
    public void finishWord(OnPracticeWordSetListener listener) {
        CurrentPracticeState currentPracticeState = currentPracticeStateService.get();
        if (currentPracticeState.getWordSet().getId() == 0) {
            if (currentPracticeState.getWordSet().getTrainingExperience() == currentPracticeState.getWordSet().getWords().size()) {
                super.changeStrategy(new RepetitionFinishedStrategy(this));
            } else {
                super.changeStrategy(new InsideRepetitionCycleStrategy(this, currentPracticeStateService));
            }
        } else if (currentPracticeState.getWordSet().getTrainingExperience() == experienceUtils.getMaxTrainingProgress(currentPracticeState.getWordSet()) / 2) {
            super.changeStrategy(new FirstCycleFinishedStrategy(this, wordSetService, currentPracticeStateService));
        } else if (currentPracticeState.getWordSet().getTrainingExperience() == experienceUtils.getMaxTrainingProgress(currentPracticeState.getWordSet())) {
            super.changeStrategy(new SecondCycleFinishedStrategy(this, wordSetService, progressService, currentPracticeStateService));
        } else {
            if (currentPracticeState.getWordSet().getStatus() == SECOND_CYCLE) {
                super.changeStrategy(new InsideSecondCycleStrategy(this, wordSetService, progressService, currentPracticeStateService));
            } else {
                super.changeStrategy(new InsideFirstCycleStrategy(this, currentPracticeStateService));
            }
        }
        super.finishWord(listener);
    }
}
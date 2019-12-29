package talkapp.org.talkappmobile.activity.interactor.impl;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.PracticeState;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetExperienceUtils;
import talkapp.org.talkappmobile.service.WordSetService;

import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FINISHED;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.SECOND_CYCLE;

public class StrategySwitcherDecorator extends PracticeWordSetInteractorDecorator {
    private final WordSetService wordSetService;
    private final WordSetExperienceUtils experienceUtils;
    private final WordRepetitionProgressService progressService;

    public StrategySwitcherDecorator(PracticeWordSetInteractor interactor, WordSetService wordSetService,
                                     WordSetExperienceUtils experienceUtils, WordRepetitionProgressService progressService) {
        super(interactor);
        this.wordSetService = wordSetService;
        this.experienceUtils = experienceUtils;
        this.progressService = progressService;
    }

    @Override
    public void initialiseExperience(OnPracticeWordSetListener listener) {
        PracticeState practiceState = wordSetService.getCurrent();
        super.changeStrategy(new UnknownState(this));
        if (SECOND_CYCLE.equals(practiceState.getWordSet().getStatus())) {
            super.changeStrategy(new InsideSecondCycleStrategy(this, progressService));
        }
        super.initialiseExperience(listener);
    }

    @Override
    public void finishWord(OnPracticeWordSetListener listener) {
        PracticeState practiceState = wordSetService.getCurrent();
        if (practiceState.getWordSet().getId() == 0) {
            if (practiceState.getWordSet().getTrainingExperience() == practiceState.getWordSet().getWords().size()) {
                super.changeStrategy(new RepetitionFinishedStrategy(this));
            } else {
                super.changeStrategy(new InsideRepetitionCycleStrategy(this));
            }
        } else if (practiceState.getWordSet().getTrainingExperience() == experienceUtils.getMaxTrainingProgress(practiceState.getWordSet()) / 2) {
            super.changeStrategy(new FirstCycleFinishedStrategy(this, wordSetService));
        } else if (practiceState.getWordSet().getTrainingExperience() == experienceUtils.getMaxTrainingProgress(practiceState.getWordSet())) {
            super.changeStrategy(new SecondCycleFinishedStrategy(this, wordSetService, progressService));
        } else {
            if (practiceState.getWordSet().getStatus() == SECOND_CYCLE) {
                super.changeStrategy(new InsideSecondCycleStrategy(this, progressService));
            } else {
                super.changeStrategy(new InsideFirstCycleStrategy(this));
            }
        }
        super.finishWord(listener);
    }
}
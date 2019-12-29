package talkapp.org.talkappmobile.activity.interactor.impl;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
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
        WordSet wordSet = wordSetService.getCurrent();
        super.changeStrategy(new UnknownState(this));
        if (SECOND_CYCLE.equals(wordSet.getStatus())) {
            super.changeStrategy(new InsideSecondCycleStrategy(this, progressService));
        }
        super.initialiseExperience(listener);
    }

    @Override
    public void finishWord(OnPracticeWordSetListener listener) {
        WordSet wordSet = wordSetService.getCurrent();
        if (wordSet.getId() == 0) {
            if (wordSet.getTrainingExperience() == wordSet.getWords().size()) {
                super.changeStrategy(new RepetitionFinishedStrategy(this));
            } else {
                super.changeStrategy(new InsideRepetitionCycleStrategy(this));
            }
        } else if (wordSet.getTrainingExperience() == experienceUtils.getMaxTrainingProgress(wordSet) / 2) {
            super.changeStrategy(new FirstCycleFinishedStrategy(this, wordSetService));
        } else if (wordSet.getTrainingExperience() == experienceUtils.getMaxTrainingProgress(wordSet)) {
            super.changeStrategy(new SecondCycleFinishedStrategy(this, wordSetService, progressService));
        } else {
            if (wordSet.getStatus() == SECOND_CYCLE) {
                super.changeStrategy(new InsideSecondCycleStrategy(this, progressService));
            } else {
                super.changeStrategy(new InsideFirstCycleStrategy(this));
            }
        }
        super.finishWord(listener);
    }
}
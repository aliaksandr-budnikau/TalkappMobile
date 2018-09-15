package talkapp.org.talkappmobile.activity;

import javax.inject.Inject;

import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.WordSet;

public class PracticeWordSetPresenter implements PracticeWordSetInteractor.OnPracticeWordSetListener {
    private final WordSet wordSet;
    @Inject
    PracticeWordSetInteractor interactor;
    private PracticeWordSetView view;
    private Sentence sentence;

    public PracticeWordSetPresenter(WordSet wordSet, PracticeWordSetView view) {
        this.wordSet = wordSet;
        this.view = view;
        DIContext.get().inject(this);
    }

    @Override
    public void onInitialiseExperience() {
        view.setProgress(wordSet.getExperience());
    }

    @Override
    public void onSentencesFound(final Sentence sentence) {
        this.sentence = sentence;
        view.hideNextButton();
        view.showCheckButton();
        view.setOriginalText(sentence);
        view.setHiddenRightAnswer(sentence);
        view.setAnswerText(sentence);
    }

    @Override
    public void onAnswerEmpty() {
        view.showMessageAnswerEmpty();
    }

    @Override
    public void onSpellingOrGrammarError() {
        view.showMessageSpellingOrGrammarError();
    }

    @Override
    public void onAccuracyTooLowError() {
        view.showMessageAccuracyTooLow();
    }

    @Override
    public void onUpdateProgress(int currentTrainingExperience) {
        view.updateProgress(wordSet.getExperience(), currentTrainingExperience);
    }

    @Override
    public void onTrainingFinished() {
        view.showCongratulationMessage();
        view.closeActivity();
        view.openAnotherActivity();
    }

    @Override
    public void onRightAnswer() {
        view.setRightAnswer(sentence);
        view.showNextButton();
        view.hideCheckButton();
    }

    public void onResume() {
        interactor.initialiseExperience(wordSet, this);
        interactor.initialiseWordsSequence(wordSet, this);
    }

    public void onDestroy() {
        view = null;
    }

    public void onNextButtonClick() {
        interactor.initialiseSentence(wordSet, this);
    }

    public void onCheckAnswerButtonClick(final String answer) {
        interactor.checkAnswer(answer, wordSet, sentence, this);
    }

    @Deprecated
    public Sentence getSentence() {
        return sentence;
    }
}
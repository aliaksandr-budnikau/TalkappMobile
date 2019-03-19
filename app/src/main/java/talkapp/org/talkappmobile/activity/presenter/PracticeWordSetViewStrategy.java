package talkapp.org.talkappmobile.activity.presenter;

import java.util.List;

import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.component.WordSetExperienceUtils;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

public class PracticeWordSetViewStrategy {
    private final PracticeWordSetView view;
    private final WordSetExperienceUtils experienceUtils;
    private final TextUtils textUtils;

    public PracticeWordSetViewStrategy(PracticeWordSetView view, TextUtils textUtils, WordSetExperienceUtils experienceUtils) {
        this.view = view;
        this.textUtils = textUtils;
        this.experienceUtils = experienceUtils;
    }

    public void onInitialiseExperience(WordSet wordSet) {
        int progress = experienceUtils.getProgress(wordSet.getTrainingExperience(), experienceUtils.getMaxTrainingProgress(wordSet));
        view.setProgress(progress);
    }

    public void onSentencesFound() {
        view.hideNextButton();
        view.showCheckButton();
        view.setAnswerText("");
    }

    public void onAnswerEmpty() {
        view.showMessageAnswerEmpty();
    }

    public void onAccuracyTooLowError() {
        view.showMessageAccuracyTooLow();
    }

    public void onUpdateProgress(WordSet wordSet) {
        int progress = experienceUtils.getProgress(wordSet.getTrainingExperience(), experienceUtils.getMaxTrainingProgress(wordSet));
        view.setProgress(progress);
    }

    public void onUpdateProgress(WordSet wordSet, int maxTrainingProgress) {
        int progress = experienceUtils.getProgress(wordSet.getTrainingExperience(), maxTrainingProgress);
        view.setProgress(progress);
    }

    public void onTrainingFinished() {
        view.showCongratulationMessage();
        view.closeActivity();
        view.openAnotherActivity();
    }

    public void onRightAnswer(Sentence sentence) {
        view.setRightAnswer(sentence.getText());
        view.showNextButton();
        view.hideCheckButton();
        view.onExerciseGotAnswered();
    }

    public void onStartPlaying() {
        view.setEnableVoiceRecButton(false);
        view.setEnableCheckButton(false);
        view.setEnableNextButton(false);
    }

    public void onStartSpeaking() {
        view.setEnablePronounceRightAnswerButton(false);
        view.setEnableVoiceRecButton(false);
        view.setEnableCheckButton(false);
        view.setEnableNextButton(false);
    }

    public void onStopSpeaking() {
        view.setEnablePronounceRightAnswerButton(true);
        view.setEnableVoiceRecButton(true);
        view.setEnableCheckButton(true);
        view.setEnableNextButton(true);
    }

    public void onStopPlaying() {
        view.setEnableVoiceRecButton(true);
        view.setEnableCheckButton(true);
        view.setEnableNextButton(true);
    }

    public void onGotRecognitionResult(Sentence sentence, List<String> result) {
        String textWithUpper = textUtils.toUpperCaseFirstLetter(result.get(0));
        String textWithLastSymbol = textUtils.appendLastSymbol(textWithUpper, sentence.getText());
        view.setAnswerText(textWithLastSymbol);
    }

    public void onTrainingHalfFinished() {

    }

    public void onNextButtonStart() {
        view.showPleaseWaitProgressBar();
        view.setEnableRightAnswerTextView(false);
        view.setEnablePronounceRightAnswerButton(false);
        view.setEnableNextButton(false);
    }

    public void onNextButtonFinish() {
        view.hidePleaseWaitProgressBar();
        view.setEnableRightAnswerTextView(true);
        view.setEnablePronounceRightAnswerButton(true);
        view.setEnableNextButton(true);
    }

    public void onCheckAnswerStart() {
        view.showPleaseWaitProgressBar();
        view.setEnableRightAnswerTextView(false);
        view.setEnablePronounceRightAnswerButton(false);
        view.setEnableCheckButton(false);
    }

    public void onCheckAnswerFinish() {
        view.hidePleaseWaitProgressBar();
        view.setEnableRightAnswerTextView(true);
        view.setEnablePronounceRightAnswerButton(true);
        view.setEnableCheckButton(true);
    }

    public void onScoringUnsuccessful() {
        view.showScoringUnsuccessfulMessage();
    }

    public void onScoringSuccessful() {
        view.showScoringSuccessfulMessage();
    }

    public void onScoreSentenceStart() {
        view.showPleaseWaitProgressBar();
        view.setEnableCheckButton(false);
        view.setEnableNextButton(false);
    }

    public void onScoreSentenceFinish() {
        view.hidePleaseWaitProgressBar();
        view.setEnableCheckButton(true);
        view.setEnableNextButton(true);
    }

    public void onChangeSentenceStart() {
        view.showPleaseWaitProgressBar();
        view.setEnableCheckButton(false);
        view.setEnableNextButton(false);
    }

    public void onChangeSentenceFinish() {
        view.hidePleaseWaitProgressBar();
        view.setEnableCheckButton(true);
        view.setEnableNextButton(true);
    }

    public void onSentenceChanged() {
        view.showSentenceChangedSuccessfullyMessage();
    }

    public void onEnableRepetitionMode() {
        view.onEnableRepetitionMode();
    }

    public void onSentencesFound(Sentence sentence, Word2Tokens word) {
        view.onSentencesFound(sentence, word);
    }

    public void onUpdateUserExp(double expScore) {
        view.onUpdateUserExp(expScore);
    }
}
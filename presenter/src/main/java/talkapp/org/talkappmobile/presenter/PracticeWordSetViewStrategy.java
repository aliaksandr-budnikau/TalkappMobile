package talkapp.org.talkappmobile.presenter;

import java.util.List;

import talkapp.org.talkappmobile.view.PracticeWordSetView;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

public class PracticeWordSetViewStrategy {
    private final PracticeWordSetView view;

    public PracticeWordSetViewStrategy(PracticeWordSetView view) {
        this.view = view;
    }

    public void onInitialiseExperience(WordSet wordSet) {
        view.setProgress(wordSet.getTrainingExperienceInPercentages());
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
        view.setProgress(wordSet.getTrainingExperienceInPercentages());
    }

    public void onTrainingFinished() {
        view.showCongratulationMessage();
        view.showCloseButton();
        view.hideNextButton();
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
        view.setAnswerText(result.get(0));
    }

    public void onTrainingHalfFinished() {

    }

    public void onNextButtonStart() {
        view.setEnableRightAnswerTextView(false);
        view.setEnablePronounceRightAnswerButton(false);
        view.setEnableNextButton(false);
    }

    public void onNextButtonFinish() {
        view.setEnableRightAnswerTextView(true);
        view.setEnablePronounceRightAnswerButton(true);
        view.setEnableNextButton(true);
    }

    public void onCheckAnswerStart() {
        view.setEnableRightAnswerTextView(false);
        view.setEnablePronounceRightAnswerButton(false);
        view.setEnableCheckButton(false);
    }

    public void onCheckAnswerFinish() {
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
        view.setEnableCheckButton(false);
        view.setEnableNextButton(false);
    }

    public void onScoreSentenceFinish() {
        view.setEnableCheckButton(true);
        view.setEnableNextButton(true);
    }

    public void onChangeSentenceStart() {
        view.setEnableCheckButton(false);
        view.setEnableNextButton(false);
    }

    public void onChangeSentenceFinish() {
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

    public void onNoSentencesToChange() {
        view.onNoSentencesToChange();
    }

    public void onGotSentencesToChange(List<Sentence> sentences, List<Sentence> alreadyPickedSentences, Word2Tokens word) {
        view.onGotSentencesToChange(sentences, alreadyPickedSentences, word);
    }

    public void onForgottenAgain(int counter) {
        view.onForgottenAgain(counter);
    }

    public void onOriginalTextClickEMPrepared(Word2Tokens word) {
        view.onOriginalTextClickEMPrepared(word);
    }
}
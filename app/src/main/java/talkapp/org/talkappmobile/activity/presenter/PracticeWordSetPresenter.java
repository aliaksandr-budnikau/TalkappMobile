package talkapp.org.talkappmobile.activity.presenter;

import android.net.Uri;

import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.activity.presenter.decorator.IPracticeWordSetPresenter;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.SentenceContentScore;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

public class PracticeWordSetPresenter implements OnPracticeWordSetListener, IPracticeWordSetPresenter {
    private final PracticeWordSetInteractor interactor;
    private final PracticeWordSetViewStrategy viewStrategy;
    private boolean answerHasBeenSeen;
    private Uri voiceRecordUri;

    public PracticeWordSetPresenter(PracticeWordSetInteractor interactor,
                                    PracticeWordSetViewStrategy viewStrategy) {
        this.interactor = interactor;
        this.viewStrategy = viewStrategy;
    }

    @Override
    public void onInitialiseExperience(WordSet wordSet) {
        viewStrategy.onInitialiseExperience(wordSet);
    }

    @Override
    public void onSentencesFound(final Sentence sentence, Word2Tokens word) {
        viewStrategy.onSentencesFound(sentence, word);
    }

    public void refreshSentence() {
        answerHasBeenSeen = false;
        viewStrategy.onSentencesFound();
    }

    @Override
    public void onAnswerEmpty() {
        viewStrategy.onAnswerEmpty();
    }

    @Override
    public void onAccuracyTooLowError() {
        viewStrategy.onAccuracyTooLowError();
    }

    @Override
    public void onUpdateProgress(WordSet wordSet) {
        viewStrategy.onUpdateProgress(wordSet);
    }

    @Override
    public void onUpdateProgress(int trainingExperience, int maxTrainingProgress) {
        viewStrategy.onUpdateProgress(trainingExperience, maxTrainingProgress);
    }

    @Override
    public void onTrainingHalfFinished(Sentence currentSentence) {
        viewStrategy.onTrainingHalfFinished();
        viewStrategy.onRightAnswer(currentSentence);
    }

    @Override
    public void onTrainingFinished() {
        viewStrategy.onTrainingFinished();
    }

    @Override
    public void onRightAnswer(Sentence sentence) {
        viewStrategy.onRightAnswer(sentence);
    }

    @Override
    public void onStartPlaying() {
        viewStrategy.onStartPlaying();
    }

    @Override
    public void onStopPlaying() {
        viewStrategy.onStopPlaying();
    }

    @Override
    public void onEnableRepetitionMode() {
        viewStrategy.onEnableRepetitionMode();
    }

    @Override
    public void onScoringUnsuccessful() {
        viewStrategy.onScoringUnsuccessful();
    }

    @Override
    public void onScoringSuccessful() {
        viewStrategy.onScoringSuccessful();
    }

    @Override
    public void onSentenceChanged() {
        viewStrategy.onSentenceChanged();
    }

    @Override
    public void onUpdateUserExp(double expScore) {
        viewStrategy.onUpdateUserExp(expScore);
    }

    @Override
    public void onNoSentencesToChange() {
        viewStrategy.onNoSentencesToChange();
    }

    @Override
    public void onGotSentencesToChange(List<Sentence> sentences, List<Sentence> alreadyPickedSentences, Word2Tokens word) {
        viewStrategy.onGotSentencesToChange(sentences, alreadyPickedSentences, word);
    }

    @Override
    public void onForgottenAgain(int counter) {
        viewStrategy.onForgottenAgain(counter);
    }

    @Override
    public void onOriginalTextClickEMPrepared(Word2Tokens word) {
        viewStrategy.onOriginalTextClickEMPrepared(word);
    }

    public void gotRecognitionResult(List<String> result) {
        Sentence currentSentence = interactor.getCurrentSentence();
        viewStrategy.onGotRecognitionResult(currentSentence, result);
    }

    public void initialise(WordSet wordSet) {
        interactor.initialiseExperience(this);
        interactor.initialiseWordsSequence(wordSet, this);
    }

    public void nextButtonClick() {
        Word2Tokens word = interactor.peekAnyNewWordByWordSetId();
        if (word == null) {
            return;
        }
        interactor.initialiseSentence(word, this);
    }

    public Sentence getCurrentSentence() {
        return interactor.getCurrentSentence();
    }

    public void checkAnswerButtonClick(final String answer) {
        Sentence currentSentence = interactor.getCurrentSentence();
        interactor.checkAnswer(answer, currentSentence, answerHasBeenSeen, this);
    }

    public void playVoiceButtonClick() {
        interactor.playVoice(this.voiceRecordUri, this);
    }

    public void voiceRecorded(Uri voiceRecordUri) {
        this.voiceRecordUri = voiceRecordUri;
    }

    public void checkRightAnswerCommandRecognized() {
        Sentence currentSentence = interactor.getCurrentSentence();
        checkAnswerButtonClick(currentSentence.getText());
    }

    public void changeSentence() {
        interactor.changeSentence(this);
    }

    public void changeSentence(List<Sentence> sentences, Word2Tokens currentWord) {
        interactor.changeSentence(currentWord, sentences, this);
    }

    public void scoreSentence(SentenceContentScore score, Sentence sentence) {
        interactor.scoreSentence(sentence, score, this);
    }

    public void markAnswerHasBeenSeen() {
        this.answerHasBeenSeen = true;
    }

    public void disableButtonsDuringPronunciation() {
    }

    public void enableButtonsAfterPronunciation() {
    }

    public void findSentencesForChange(Word2Tokens currentWord) {
        interactor.findSentencesForChange(currentWord, this);
    }

    public void prepareOriginalTextClickEM() {
        interactor.prepareOriginalTextClickEM(this);
    }

    public void refreshCurrentWord() {
        interactor.refreshSentence(this);
    }
}
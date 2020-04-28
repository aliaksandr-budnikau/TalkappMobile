package talkapp.org.talkappmobile.presenter;

import java.util.List;

import talkapp.org.talkappmobile.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.SentenceContentScore;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

public class PracticeWordSetPresenterImpl implements OnPracticeWordSetListener, IPracticeWordSetPresenter {
    private final PracticeWordSetInteractor interactor;
    private final PracticeWordSetViewStrategy viewStrategy;

    public PracticeWordSetPresenterImpl(PracticeWordSetInteractor interactor,
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

    @Override
    public void refreshSentence() {
        interactor.resetSentenceState(this);
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

    @Override
    public void onSentencesFound() {
        viewStrategy.onSentencesFound();
    }

    @Override
    public void gotRecognitionResult(List<String> result) {
        Sentence currentSentence = interactor.getCurrentSentence();
        viewStrategy.onGotRecognitionResult(currentSentence, result);
    }

    @Override
    public void initialise(WordSet wordSet) {
        interactor.saveCurrentWordSet(wordSet);
        interactor.initialiseExperience(this);
        interactor.initialiseWordsSequence(this);
    }

    @Override
    public void nextButtonClick() {
        Word2Tokens word = interactor.peekAnyNewWordByWordSetId();
        if (word == null) {
            return;
        }
        interactor.initialiseSentence(word, this);
    }

    @Override
    public Sentence getCurrentSentence() {
        return interactor.getCurrentSentence();
    }

    @Override
    public void checkAnswerButtonClick(final String answer) {
        boolean result = interactor.checkAnswer(answer, this);
        if (result) {
            interactor.finishWord(this);
        }
    }

    @Override
    public void checkRightAnswerCommandRecognized() {
        Sentence currentSentence = interactor.getCurrentSentence();
        checkAnswerButtonClick(currentSentence.getText());
    }

    @Override
    public void changeSentence() {
        interactor.changeSentence(this);
    }

    @Override
    public void changeSentence(List<Sentence> sentences, Word2Tokens currentWord) {
        interactor.changeSentence(currentWord, sentences, this);
    }

    @Override
    public void scoreSentence(SentenceContentScore score, Sentence sentence) {
        interactor.scoreSentence(sentence, score, this);
    }

    @Override
    public void markAnswerHasBeenSeen() {
        interactor.markAnswerHasBeenSeen();
    }

    @Override
    public void disableButtonsDuringPronunciation() {
    }

    @Override
    public void enableButtonsAfterPronunciation() {
    }

    @Override
    public void findSentencesForChange(Word2Tokens currentWord) {
        interactor.findSentencesForChange(currentWord, this);
    }

    @Override
    public void prepareOriginalTextClickEM() {
        interactor.prepareOriginalTextClickEM(this);
    }

    @Override
    public void refreshCurrentWord() {
        interactor.refreshSentence(this);
    }
}
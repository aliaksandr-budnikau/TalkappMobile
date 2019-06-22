package talkapp.org.talkappmobile.activity.presenter;

import android.net.Uri;

import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.SentenceContentScore;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

public class PracticeWordSetPresenter implements OnPracticeWordSetListener {
    private final PracticeWordSetPresenterCurrentState state;
    private final PracticeWordSetInteractor interactor;
    private final PracticeWordSetViewStrategy viewStrategy;
    private boolean answerHasBeenSeen;

    public PracticeWordSetPresenter(WordSet wordSet,
                                    PracticeWordSetInteractor interactor,
                                    PracticeWordSetViewStrategy viewStrategy) {
        this.interactor = interactor;
        state = new PracticeWordSetPresenterCurrentState(wordSet);
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
    public void onUpdateProgress(WordSet wordSet, int maxTrainingProgress) {
        viewStrategy.onUpdateProgress(wordSet, maxTrainingProgress);
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

    public void initialise() {
        interactor.initialiseExperience(state.getWordSet(), this);
        interactor.initialiseWordsSequence(state.getWordSet(), this);
    }

    public void nextButtonClick() {
        try {
            viewStrategy.onNextButtonStart();
            Word2Tokens word = interactor.peekAnyNewWordByWordSetId(state.getWordSetId());
            if (word == null) {
                return;
            }
            interactor.initialiseSentence(word, state.getWordSetId(), this);
        } finally {
            viewStrategy.onNextButtonFinish();
        }
    }

    public void finishActivity() {
        viewStrategy.onFinishActivity();
    }

    public Sentence getCurrentSentence() {
        return interactor.getCurrentSentence();
    }

    public void checkAnswerButtonClick(final String answer) {
        try {
            viewStrategy.onCheckAnswerStart();
            Sentence currentSentence = interactor.getCurrentSentence();
            interactor.checkAnswer(answer, state.getWordSet(), currentSentence, answerHasBeenSeen, this);
        } finally {
            viewStrategy.onCheckAnswerFinish();
        }
    }

    public void playVoiceButtonClick() {
        interactor.playVoice(state.getVoiceRecordUri(), this);
    }

    public void voiceRecorded(Uri voiceRecordUri) {
        state.setVoiceRecordUri(voiceRecordUri);
    }

    public void checkRightAnswerCommandRecognized() {
        Sentence currentSentence = interactor.getCurrentSentence();
        checkAnswerButtonClick(currentSentence.getText());
    }

    public void changeSentence() {
        try {
            viewStrategy.onChangeSentenceStart();
            interactor.changeSentence(state.getWordSetId(), this);
        } finally {
            viewStrategy.onChangeSentenceFinish();
        }
    }

    public void changeSentence(List<Sentence> sentences, Word2Tokens currentWord) {
        try {
            viewStrategy.onChangeSentenceStart();
            interactor.changeSentence(currentWord, sentences, this);
        } finally {
            viewStrategy.onChangeSentenceFinish();
        }
    }

    public void scoreSentence(SentenceContentScore score, Sentence sentence) {
        try {
            viewStrategy.onScoreSentenceStart();
            interactor.scoreSentence(sentence, score, this);
        } finally {
            viewStrategy.onScoreSentenceFinish();
        }
    }

    public void markAnswerHasBeenSeen() {
        this.answerHasBeenSeen = true;
    }

    public void disableButtonsDuringPronunciation() {
        viewStrategy.onStartSpeaking();
    }

    public void enableButtonsAfterPronunciation() {
        viewStrategy.onStopSpeaking();
    }

    public void findSentencesForChange(Word2Tokens currentWord) {
        interactor.findSentencesForChange(currentWord, this);
    }

    public void prepareOriginalTextClickEM() {
        interactor.prepareOriginalTextClickEM(this);
    }
}
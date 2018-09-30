package talkapp.org.talkappmobile.activity.presenter;

import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.VoiceRecognitionResult;
import talkapp.org.talkappmobile.model.WordSet;

public class PracticeWordSetPresenter implements OnPracticeWordSetListener {
    private static final int SPEECH_TIMEOUT_MILLIS = 1000;
    private final WordSet wordSet;
    @Inject
    PracticeWordSetInteractor interactor;
    private PracticeWordSetViewStrategy viewStrategy;
    private Sentence sentence;

    public PracticeWordSetPresenter(WordSet wordSet, PracticeWordSetView view) {
        this.wordSet = wordSet;
        this.viewStrategy = new PracticeWordSetViewStrategy(view);
        DIContext.get().inject(this);
    }

    @Override
    public void onInitialiseExperience() {
        viewStrategy.onInitialiseExperience(wordSet.getExperience());
    }

    @Override
    public void onSentencesFound(final Sentence sentence) {
        this.sentence = sentence;
        viewStrategy.onSentencesFound(sentence);
    }

    @Override
    public void onAnswerEmpty() {
        viewStrategy.onAnswerEmpty();
    }

    @Override
    public void onSpellingOrGrammarError(List<GrammarError> errors) {
        viewStrategy.onSpellingOrGrammarError(errors);
    }

    @Override
    public void onAccuracyTooLowError() {
        viewStrategy.onAccuracyTooLowError();
    }

    @Override
    public void onUpdateProgress(int currentTrainingExperience) {
        viewStrategy.onUpdateProgress(wordSet.getExperience(), currentTrainingExperience);
    }

    @Override
    public void onTrainingFinished() {
        viewStrategy.onTrainingFinished();
    }

    @Override
    public void onRightAnswer() {
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
    public void onSnippetRecorded(long speechLength, int maxSpeechLengthMillis) {
        viewStrategy.onSnippetRecorded(speechLength, maxSpeechLengthMillis);
    }

    @Override
    public void onStartRecording() {
        viewStrategy.onStartRecording(sentence);
    }

    @Override
    public void onStopRecording() {
        viewStrategy.onStopRecording();
    }

    @Override
    public void onStopRecognition() {
        viewStrategy.onStopRecognition();
    }

    @Override
    public void onGotRecognitionResult(VoiceRecognitionResult result) {
        viewStrategy.onGotRecognitionResult(sentence, result);
    }

    public void onResume() {
        interactor.initialiseExperience(wordSet, this);
        interactor.initialiseWordsSequence(wordSet, this);
    }

    public void onDestroy() {
        viewStrategy = null;
    }

    public void onNextButtonClick() {
        interactor.initialiseSentence(wordSet, this);
    }

    public void onCheckAnswerButtonClick(final String answer) {
        interactor.checkAnswer(answer, wordSet, sentence, this);
    }

    public void onPlayVoiceButtonClick() {
        interactor.playVoice(this);
    }

    public void onRecogniseVoiceButtonClick() {
        interactor.recVoice(SPEECH_TIMEOUT_MILLIS, this);
        interactor.recognizeVoice(this);
    }

    public void onStopRecognitionVoiceButtonClick() {
        interactor.stopRecording();
    }

    public void rightAnswerTouched() {
        viewStrategy.rightAnswerTouched(sentence);
    }

    public void rightAnswerUntouched() {
        viewStrategy.rightAnswerUntouched(sentence);
    }
}
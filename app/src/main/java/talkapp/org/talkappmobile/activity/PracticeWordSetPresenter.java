package talkapp.org.talkappmobile.activity;

import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.VoiceRecognitionResult;
import talkapp.org.talkappmobile.model.WordSet;

public class PracticeWordSetPresenter implements PracticeWordSetInteractor.OnPracticeWordSetListener {
    private static final int SPEECH_TIMEOUT_MILLIS = 1000;
    private final WordSet wordSet;
    @Inject
    PracticeWordSetInteractor interactor;
    @Inject
    TextUtils textUtils;
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
    public void onSpellingOrGrammarError(List<GrammarError> errors) {
        view.showMessageSpellingOrGrammarError();
        view.hideSpellingOrGrammarErrorPanel();
        for (GrammarError error : errors) {
            String errorMessage = textUtils.buildSpellingGrammarErrorMessage(error);
            view.showSpellingOrGrammarErrorPanel(errorMessage);
        }
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

    @Override
    public void onStartPlaying() {
        view.setEnableVoiceRecButton(false);
        view.setEnableCheckButton(false);
        view.setEnableNextButton(false);
    }

    @Override
    public void onStopPlaying() {
        view.setEnableVoiceRecButton(true);
        view.setEnableCheckButton(true);
        view.setEnableNextButton(true);
    }

    @Override
    public void onSnippetRecorded(long speechLength, int maxSpeechLengthMillis) {
        view.setRecProgress((int) (((double) speechLength / maxSpeechLengthMillis) * 100));
    }

    @Override
    public void onStartRecording() {
        view.setRecProgress(0);
        view.showRecProgress();
        view.setEnablePlayButton(false);
        view.setEnableCheckButton(false);
        view.setEnableNextButton(false);
        view.setHiddenRightAnswer(sentence);
        view.setEnableRightAnswer(false);
    }

    @Override
    public void onStopRecording() {
        view.setEnablePlayButton(true);
        view.setEnableCheckButton(true);
        view.setEnableNextButton(true);
        view.setEnableRightAnswer(true);
    }

    @Override
    public void onStopRecognition() {
        view.hideRecProgress();
        view.setRecProgress(0);
    }

    @Override
    public void onGotRecognitionResult(VoiceRecognitionResult result) {
        String textWithUpper = textUtils.toUpperCaseFirstLetter(result.getVariant().get(0));
        String textWithLastSymbol = textUtils.appendLastSymbol(textWithUpper, sentence.getText());
        view.setAnswerText(textWithLastSymbol);
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
        view.setRightAnswer(sentence);
    }

    public void rightAnswerUntouched() {
        view.setHiddenRightAnswer(sentence);
    }
}
package talkapp.org.talkappmobile.activity.presenter;

import android.net.Uri;

import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

public class PracticeWordSetPresenter implements OnPracticeWordSetListener {
    private final PracticeWordSetPresenterCurrentState state;
    private final PracticeWordSetInteractor interactor;
    private final PracticeWordSetViewHideAllStrategy hideAllStrategy;
    private final PracticeWordSetViewHideNewWordOnlyStrategy newWordOnlyStrategy;
    private PracticeWordSetViewStrategy viewStrategy;

    public PracticeWordSetPresenter(WordSet wordSet,
                                    PracticeWordSetInteractor interactor,
                                    PracticeWordSetViewHideNewWordOnlyStrategy newWordOnlyStrategy,
                                    PracticeWordSetViewHideAllStrategy hideAllStrategy) {
        this.interactor = interactor;
        this.hideAllStrategy = hideAllStrategy;
        state = new PracticeWordSetPresenterCurrentState(wordSet);
        this.newWordOnlyStrategy = newWordOnlyStrategy;
        this.viewStrategy = newWordOnlyStrategy;
    }

    @Override
    public void onInitialiseExperience(WordSetExperience exp) {
        viewStrategy.onInitialiseExperience(exp);
    }

    @Override
    public void onSentencesFound(final Sentence sentence, Word2Tokens word) {
        viewStrategy.onSentencesFound(sentence, word);
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
    public void onUpdateProgress(WordSetExperience exp) {
        viewStrategy.onUpdateProgress(exp);
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
        this.viewStrategy = hideAllStrategy;
    }

    @Override
    public void onDisableRepetitionMode() {
        this.viewStrategy = newWordOnlyStrategy;
    }

    @Override
    public void onHideRightAnswer(Sentence sentence, Word2Tokens word) {
        viewStrategy.rightAnswerUntouched(sentence, word);
    }

    public void gotRecognitionResult(List<String> result) {
        Sentence currentSentence = interactor.getCurrentSentence(state.getWordSetId());
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
            interactor.initialiseSentence(word, state.getWordSetId(), this);
        } finally {
            viewStrategy.onNextButtonFinish();
        }
    }

    public void checkAnswerButtonClick(final String answer) {
        try {
            viewStrategy.onCheckAnswerStart();
            Sentence currentSentence = interactor.getCurrentSentence(state.getWordSetId());
            interactor.checkAnswer(answer, state.getWordSet(), currentSentence, this);
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

    public void rightAnswerTouched() {
        Sentence currentSentence = interactor.getCurrentSentence(state.getWordSetId());
        if (currentSentence != null) {
            viewStrategy.rightAnswerTouched(currentSentence);
        }
    }

    public void rightAnswerUntouched() {
        interactor.rightAnswerUntouched(state.getWordSetId(), this);
    }

    public void pronounceRightAnswerButtonClick() {
        try {
            viewStrategy.onStartSpeaking();
            interactor.pronounceRightAnswer(state.getWordSetId(), this);
        } finally {
            viewStrategy.onStopSpeaking();
        }
    }

    public void checkRightAnswerCommandRecognized() {
        Sentence currentSentence = interactor.getCurrentSentence(state.getWordSetId());
        checkAnswerButtonClick(currentSentence.getText());
    }
}
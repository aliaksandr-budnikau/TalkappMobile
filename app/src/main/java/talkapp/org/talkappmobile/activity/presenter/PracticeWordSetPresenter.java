package talkapp.org.talkappmobile.activity.presenter;

import android.net.Uri;

import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.component.SentenceProvider;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.REPETITION;

public class PracticeWordSetPresenter implements OnPracticeWordSetListener {
    private final PracticeWordSetView view;
    private final PracticeWordSetPresenterCurrentState state;
    @Inject
    PracticeWordSetInteractor interactor;
    private PracticeWordSetViewStrategy viewStrategy;

    public PracticeWordSetPresenter(WordSet wordSet, PracticeWordSetView view) {
        this.view = view;
        state = new PracticeWordSetPresenterCurrentState(wordSet);
        this.viewStrategy = new PracticeWordSetViewHideNewWordOnlyStrategy(view);
        DIContext.get().inject(this);
    }

    @Override
    public void onInitialiseExperience(WordSetExperience exp) {
        viewStrategy.onInitialiseExperience(exp);
    }

    @Override
    public void onSentencesFound(final Sentence sentence, String word) {
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
        viewStrategy = new PracticeWordSetViewHideAllStrategy(view);
    }

    public void gotRecognitionResult(List<String> result) {
        Sentence currentSentence = interactor.getCurrentSentence(state.getWordSetId());
        viewStrategy.onGotRecognitionResult(currentSentence, result);
    }

    public void initialise() {
        interactor.initialiseExperience(state.getWordSet(), this);
        interactor.initialiseWordsSequence(state.getWordSet(), this);
    }

    public void destroy() {
        viewStrategy = null;
    }

    public void nextButtonClick() {
        try {
            viewStrategy.onNextButtonStart();
            String word = interactor.peekByWordSetIdAnyWord(state.getWordSetId());
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
        Sentence currentSentence = interactor.getCurrentSentence(state.getWordSetId());
        if (currentSentence != null) {
            String currentWord = interactor.getCurrentWord(state.getWordSetId());
            viewStrategy.rightAnswerUntouched(currentSentence, currentWord);
        }
    }
}
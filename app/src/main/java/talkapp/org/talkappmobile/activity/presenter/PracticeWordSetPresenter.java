package talkapp.org.talkappmobile.activity.presenter;

import android.net.Uri;

import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.component.SentenceProvider;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseRepository;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

public class PracticeWordSetPresenter implements OnPracticeWordSetListener {
    private final PracticeWordSetView view;
    private final PracticeWordSetPresenterCurrentState state;
    @Inject
    PracticeWordSetInteractor interactor;
    @Inject
    PracticeWordSetExerciseRepository practiceWordSetExerciseRepository;
    @Inject
    SentenceProvider sentenceProvider;
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
        practiceWordSetExerciseRepository.save(word, state.getWordSetId(), sentence);
        state.setSentence(sentence);
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
    public void onUpdateProgress(WordSetExperience exp, int currentTrainingExperience) {
        viewStrategy.onUpdateProgress(exp, currentTrainingExperience);
    }

    @Override
    public void onTrainingHalfFinished() {
        viewStrategy.onTrainingHalfFinished();
        viewStrategy.onRightAnswer(state.getSentence());
        viewStrategy = new PracticeWordSetViewHideAllStrategy(view);
        sentenceProvider.enableRepetitionMode();
        state.setWordSequenceIterator();
    }

    @Override
    public void onTrainingFinished() {
        viewStrategy.onTrainingFinished();
    }

    @Override
    public void onRightAnswer() {
        viewStrategy.onRightAnswer(state.getSentence());
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
    public void onGotRecognitionResult(List<String> result) {
        viewStrategy.onGotRecognitionResult(state.getSentence(), result);
    }

    public void onResume() {
        interactor.initialiseExperience(state.getWordSet(), this);
        interactor.initialiseWordsSequence(state.getWordSet(), this);
        state.setWordSequenceIterator();
    }

    public void onDestroy() {
        viewStrategy = null;
    }

    public void onNextButtonClick() {
        state.nextWord();
        interactor.initialiseSentence(state.getWord(), state.getWordSetId(), this);
    }

    public void onCheckAnswerButtonClick(final String answer) {
        interactor.checkAnswer(answer, state.getWordSet(), state.getSentence(), this);
    }

    public void onPlayVoiceButtonClick() {
        interactor.playVoice(state.getVoiceRecordUri(), this);
    }

    public void onVoiceRecognized(Uri voiceRecordUri) {
        state.setVoiceRecordUri(voiceRecordUri);
    }

    public void rightAnswerTouched() {
        if (state.getSentence() != null) {
            viewStrategy.rightAnswerTouched(state.getSentence());
        }
    }

    public void rightAnswerUntouched() {
        if (state.getSentence() != null) {
            viewStrategy.rightAnswerUntouched(state.getSentence(), state.getWord());
        }
    }
}
package talkapp.org.talkappmobile.activity.presenter;

import android.net.Uri;

import java.util.List;
import java.util.ListIterator;

import javax.inject.Inject;

import talkapp.org.talkappmobile.component.SentenceProvider;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseRepository;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.WordSet;

public class PracticeWordSetPresenter implements OnPracticeWordSetListener {
    private final WordSet wordSet;
    private final PracticeWordSetView view;
    @Inject
    PracticeWordSetInteractor interactor;
    @Inject
    PracticeWordSetExerciseRepository practiceWordSetExerciseRepository;
    @Inject
    SentenceProvider sentenceProvider;
    private PracticeWordSetViewStrategy viewStrategy;
    private Sentence currentSentence;
    private ListIterator<String> wordSequenceIterator;
    private String currentWord;
    private Uri voiceRecordUri;

    public PracticeWordSetPresenter(WordSet wordSet, PracticeWordSetView view) {
        this.wordSet = wordSet;
        this.view = view;
        this.viewStrategy = new PracticeWordSetViewHideNewWordOnlyStrategy(view);
        DIContext.get().inject(this);
    }

    @Override
    public void onInitialiseExperience() {
        viewStrategy.onInitialiseExperience(wordSet.getExperience());
    }

    @Override
    public void onSentencesFound(final Sentence sentence, String word) {
        practiceWordSetExerciseRepository.save(word, wordSet.getId(), sentence);
        this.currentSentence = sentence;
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
    public void onUpdateProgress(int currentTrainingExperience) {
        viewStrategy.onUpdateProgress(wordSet.getExperience(), currentTrainingExperience);
    }

    @Override
    public void onTrainingHalfFinished() {
        viewStrategy.onTrainingHalfFinished();
        viewStrategy.onRightAnswer(currentSentence);
        viewStrategy = new PracticeWordSetViewHideAllStrategy(view);
        sentenceProvider.enableRepetitionMode();
        wordSequenceIterator = wordSet.getWords().listIterator();
    }

    @Override
    public void onTrainingFinished() {
        viewStrategy.onTrainingFinished();
    }

    @Override
    public void onRightAnswer() {
        viewStrategy.onRightAnswer(currentSentence);
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
        viewStrategy.onGotRecognitionResult(currentSentence, result);
    }

    public void onResume() {
        interactor.initialiseExperience(wordSet, this);
        interactor.initialiseWordsSequence(wordSet, this);
        wordSequenceIterator = wordSet.getWords().listIterator();
    }

    public void onDestroy() {
        viewStrategy = null;
    }

    public void onNextButtonClick() {
        currentWord = wordSequenceIterator.next();
        interactor.initialiseSentence(currentWord, wordSet.getId(), this);
    }

    public void onCheckAnswerButtonClick(final String answer) {
        interactor.checkAnswer(answer, wordSet, currentSentence, this);
    }

    public void onPlayVoiceButtonClick() {
        interactor.playVoice(voiceRecordUri, this);
    }

    public void onVoiceRecognized(Uri voiceRecordUri) {
        this.voiceRecordUri = voiceRecordUri;
    }

    public void rightAnswerTouched() {
        if (currentSentence != null) {
            viewStrategy.rightAnswerTouched(currentSentence);
        }
    }

    public void rightAnswerUntouched() {
        if (currentSentence != null) {
            viewStrategy.rightAnswerUntouched(currentSentence, currentWord);
        }
    }
}
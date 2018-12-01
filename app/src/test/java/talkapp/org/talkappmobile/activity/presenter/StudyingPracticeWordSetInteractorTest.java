package talkapp.org.talkappmobile.activity.presenter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.impl.StudyingPracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.app.TalkappMobileApplication;
import talkapp.org.talkappmobile.component.AudioStuffFactory;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.RefereeService;
import talkapp.org.talkappmobile.component.SentenceProvider;
import talkapp.org.talkappmobile.component.SentenceSelector;
import talkapp.org.talkappmobile.component.WordsCombinator;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseService;
import talkapp.org.talkappmobile.component.database.WordSetExperienceService;
import talkapp.org.talkappmobile.config.DIContextUtils;
import talkapp.org.talkappmobile.model.AnswerCheckingResult;
import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.UncheckedAnswer;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.FINISHED;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.REPETITION;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.STUDYING;

@RunWith(MockitoJUnitRunner.class)
public class StudyingPracticeWordSetInteractorTest {

    @Mock
    AudioStuffFactory audioStuffFactory;
    @Mock
    PracticeWordSetExerciseService exerciseService;
    @Mock
    private WordsCombinator wordsCombinator;
    @Mock
    private SentenceProvider sentenceProvider;
    @Mock
    private SentenceSelector sentenceSelector;
    @Mock
    private RefereeService refereeService;
    @Mock
    private OnPracticeWordSetListener listener;
    @Mock
    private WordSetExperienceService wordSetExperienceService;
    @Mock
    private Context context;
    @Mock
    private Logger logger;
    @InjectMocks
    private StudyingPracticeWordSetInteractor interactor;

    @BeforeClass
    public static void setUp() {
        DIContextUtils.init(new TalkappMobileApplication());
    }

    @Test
    public void initialiseExperience_experienceIsNull() {
        // setup
        WordSet wordSet = new WordSet();
        wordSet.setId(1);

        WordSetExperience experience = new WordSetExperience();
        experience.setId(3);
        experience.setStatus(STUDYING);

        // when
        when(wordSetExperienceService.findById(wordSet.getId())).thenReturn(null);
        when(wordSetExperienceService.createNew(wordSet)).thenReturn(experience);
        interactor.initialiseExperience(wordSet, listener);

        // then
        verify(listener).onInitialiseExperience(experience);
        verify(listener, times(0)).onEnableRepetitionMode();
        verify(sentenceProvider, times(0)).enableRepetitionMode();
    }

    @Test
    public void initialiseExperience_experienceIsNotNullAndStatusRepetition() {
        // setup
        int id = 2;

        WordSetExperience experience = new WordSetExperience();
        experience.setId(id);
        experience.setStatus(REPETITION);

        WordSet wordSet = new WordSet();
        wordSet.setId(id);

        // when
        when(wordSetExperienceService.findById(wordSet.getId())).thenReturn(experience);
        interactor.initialiseExperience(wordSet, listener);

        // then
        verify(wordSetExperienceService, times(0)).createNew(wordSet);
        verify(sentenceProvider).enableRepetitionMode();
        verify(listener).onEnableRepetitionMode();
        verify(listener).onInitialiseExperience(experience);
    }

    @Test
    public void initialiseExperience_experienceIsNotNullAndStatusNotRepetition() {
        // setup
        int id = 3;

        WordSetExperience experience = new WordSetExperience();
        experience.setId(id);
        experience.setStatus(STUDYING);

        WordSet wordSet = new WordSet();
        wordSet.setId(id);

        // when
        when(wordSetExperienceService.findById(wordSet.getId())).thenReturn(experience);
        interactor.initialiseExperience(wordSet, listener);

        // then
        verify(wordSetExperienceService, times(0)).createNew(wordSet);
        verify(sentenceProvider, times(0)).enableRepetitionMode();
        verify(listener, times(0)).onEnableRepetitionMode();
        verify(listener).onInitialiseExperience(experience);
    }

    @Test
    public void initialiseWordsSequence() {
        // setup
        WordSet wordSet = new WordSet();
        wordSet.setWords(asList(new Word2Tokens("fdsfs"), new Word2Tokens("sdfs")));
        wordSet.setId(4);

        HashSet<Word2Tokens> words = new HashSet<>(wordSet.getWords());

        // when
        when(wordsCombinator.combineWords(wordSet.getWords())).thenReturn(words);
        interactor.initialiseWordsSequence(wordSet, listener);

        // then
        verify(exerciseService).createSomeIfNecessary(words, wordSet.getId());
    }

    @Test
    public void initialiseSentence_sentenceFound() {
        // setup
        Sentence sentence1 = new Sentence();
        sentence1.setId("fds32ddd");
        Sentence sentence2 = new Sentence();
        sentence2.setId("fds32ddddsas");
        List<Sentence> sentences = asList(sentence1, sentence2);

        Sentence selectedSentence = new Sentence();
        selectedSentence.setId("fds32");
        Word2Tokens word = new Word2Tokens("sdfs");
        int wordSetId = 3;

        // when
        when(sentenceProvider.findByWordAndWordSetId(word, wordSetId)).thenReturn(sentences);
        when(sentenceSelector.getSentence(sentences)).thenReturn(selectedSentence);
        interactor.initialiseSentence(word, wordSetId, listener);

        // then
        verify(exerciseService).save(word, wordSetId, selectedSentence);
        verify(listener).onSentencesFound(selectedSentence, word);
    }

    @Test
    public void initialiseSentence_sentenceNotFound() {
        // setup
        Sentence selectedSentence = new Sentence();
        selectedSentence.setId("fds32");

        Word2Tokens word = new Word2Tokens("SDFDS");
        int wordSetId = 3;

        // when
        when(sentenceProvider.findByWordAndWordSetId(word, wordSetId)).thenReturn(Collections.<Sentence>emptyList());
        interactor.initialiseSentence(word, wordSetId, listener);

        // then
        verify(listener, times(0)).onSentencesFound(selectedSentence, word);
        verify(exerciseService, times(0)).save(word, wordSetId, selectedSentence);
        verify(sentenceSelector, times(0)).getSentence(any(List.class));
    }

    @Test
    public void checkAnswer_answerIsOk() {
        // setup
        int id = 3;

        WordSet wordSet = new WordSet();
        wordSet.setId(id);

        WordSetExperience experience = new WordSetExperience();
        experience.setId(id);
        experience.setMaxTrainingExperience(12);
        experience.setTrainingExperience(4);

        Sentence sentence = new Sentence();
        sentence.setId("dsfds3");
        sentence.setText("dsdsdsfds3");

        UncheckedAnswer uncheckedAnswer = new UncheckedAnswer();
        uncheckedAnswer.setExpectedAnswer(sentence.getText());
        uncheckedAnswer.setActualAnswer("fsdf");
        uncheckedAnswer.setWordSetExperienceId(experience.getId());

        AnswerCheckingResult checkingResult = new AnswerCheckingResult();
        checkingResult.setErrors(new ArrayList<GrammarError>());

        // when
        when(refereeService.checkAnswer(uncheckedAnswer)).thenReturn(checkingResult);
        when(wordSetExperienceService.increaseExperience(wordSet.getId(), 1)).thenReturn(experience);
        interactor.checkAnswer(uncheckedAnswer.getActualAnswer(), wordSet, sentence, listener);

        // then
        verify(listener).onUpdateProgress(experience);
        verify(listener).onRightAnswer(sentence);
        verify(listener, times(0)).onTrainingFinished();
        verify(listener, times(0)).onTrainingHalfFinished(sentence);
        verify(listener, times(0)).onAccuracyTooLowError();
        verify(listener, times(0)).onSpellingOrGrammarError(any(List.class));
        verify(listener, times(0)).onAnswerEmpty();
        verify(wordSetExperienceService, times(0)).moveToAnotherState(wordSet.getId(), FINISHED);
        verify(wordSetExperienceService, times(0)).moveToAnotherState(wordSet.getId(), REPETITION);
        verify(sentenceProvider, times(0)).enableRepetitionMode();
        verify(listener, times(0)).onEnableRepetitionMode();
    }

    @Test
    public void checkAnswer_answerIsOkAndFinish() {
        // setup
        int id = 3;

        WordSet wordSet = new WordSet();
        wordSet.setId(id);

        WordSetExperience experience = new WordSetExperience();
        experience.setId(id);
        experience.setMaxTrainingExperience(12);
        experience.setTrainingExperience(12);

        Sentence sentence = new Sentence();
        sentence.setId("dsfds3");
        sentence.setText("dsdsdsfds3");

        UncheckedAnswer uncheckedAnswer = new UncheckedAnswer();
        uncheckedAnswer.setExpectedAnswer(sentence.getText());
        uncheckedAnswer.setActualAnswer("fsdf");
        uncheckedAnswer.setWordSetExperienceId(experience.getId());

        AnswerCheckingResult checkingResult = new AnswerCheckingResult();
        checkingResult.setErrors(new ArrayList<GrammarError>());

        // when
        when(refereeService.checkAnswer(uncheckedAnswer)).thenReturn(checkingResult);
        when(wordSetExperienceService.increaseExperience(wordSet.getId(), 1)).thenReturn(experience);
        interactor.checkAnswer(uncheckedAnswer.getActualAnswer(), wordSet, sentence, listener);

        // then
        verify(listener).onUpdateProgress(experience);
        verify(listener, times(0)).onRightAnswer(sentence);
        verify(listener).onTrainingFinished();
        verify(wordSetExperienceService).moveToAnotherState(wordSet.getId(), FINISHED);
        verify(listener, times(0)).onAccuracyTooLowError();
        verify(listener, times(0)).onSpellingOrGrammarError(any(List.class));
        verify(listener, times(0)).onAnswerEmpty();
        verify(listener, times(0)).onTrainingHalfFinished(sentence);
        verify(wordSetExperienceService, times(0)).moveToAnotherState(wordSet.getId(), REPETITION);
        verify(sentenceProvider, times(0)).enableRepetitionMode();
        verify(listener, times(0)).onEnableRepetitionMode();
        verify(exerciseService).moveCurrentWordToNextState(wordSet.getId());
    }

    @Test
    public void checkAnswer_accuracyTooLowError() {
        // setup
        int id = 3;

        WordSet wordSet = new WordSet();
        wordSet.setId(id);

        WordSetExperience experience = new WordSetExperience();
        experience.setId(id);
        experience.setMaxTrainingExperience(12);
        experience.setTrainingExperience(0);

        Sentence sentence = new Sentence();
        sentence.setId("dsfds3");
        sentence.setText("dsdsdsfds3");

        UncheckedAnswer uncheckedAnswer = new UncheckedAnswer();
        uncheckedAnswer.setExpectedAnswer(sentence.getText());
        uncheckedAnswer.setActualAnswer("fsdf");
        uncheckedAnswer.setWordSetExperienceId(experience.getId());

        AnswerCheckingResult checkingResult = new AnswerCheckingResult();
        checkingResult.setErrors(new ArrayList<GrammarError>());
        checkingResult.setAccuracyTooLow(true);

        // when
        when(refereeService.checkAnswer(uncheckedAnswer)).thenReturn(checkingResult);
        interactor.checkAnswer(uncheckedAnswer.getActualAnswer(), wordSet, sentence, listener);

        // then
        verify(listener).onAccuracyTooLowError();
        verify(listener, times(0)).onUpdateProgress(experience);
        verify(listener, times(0)).onRightAnswer(sentence);
        verify(listener, times(0)).onTrainingFinished();
        verify(listener, times(0)).onSpellingOrGrammarError(any(List.class));
        verify(listener, times(0)).onAnswerEmpty();
        verify(listener, times(0)).onTrainingHalfFinished(sentence);
        verify(wordSetExperienceService, times(0)).moveToAnotherState(wordSet.getId(), REPETITION);
        verify(sentenceProvider, times(0)).enableRepetitionMode();
        verify(listener, times(0)).onEnableRepetitionMode();
        verify(exerciseService, times(0)).putOffCurrentWord(wordSet.getId());
    }

    @Test
    public void checkAnswer_spellingOrGrammarError() {
        // setup
        int id = 3;

        WordSet wordSet = new WordSet();
        wordSet.setId(id);

        WordSetExperience experience = new WordSetExperience();
        experience.setId(id);
        experience.setMaxTrainingExperience(12);
        experience.setTrainingExperience(0);

        Sentence sentence = new Sentence();
        sentence.setId("dsfds3");
        sentence.setText("dsdsdsfds3");

        UncheckedAnswer uncheckedAnswer = new UncheckedAnswer();
        uncheckedAnswer.setExpectedAnswer(sentence.getText());
        uncheckedAnswer.setActualAnswer("fsdf");
        uncheckedAnswer.setWordSetExperienceId(experience.getId());

        AnswerCheckingResult checkingResult = new AnswerCheckingResult();
        List<GrammarError> errors = asList(new GrammarError());
        checkingResult.setErrors(errors);

        // when
        when(refereeService.checkAnswer(uncheckedAnswer)).thenReturn(checkingResult);
        interactor.checkAnswer(uncheckedAnswer.getActualAnswer(), wordSet, sentence, listener);

        // then
        verify(listener).onSpellingOrGrammarError(errors);
        verify(listener, times(0)).onAccuracyTooLowError();
        verify(listener, times(0)).onUpdateProgress(experience);
        verify(listener, times(0)).onRightAnswer(sentence);
        verify(listener, times(0)).onTrainingFinished();
        verify(listener, times(0)).onTrainingHalfFinished(sentence);
        verify(listener, times(0)).onAnswerEmpty();
        verify(listener, times(0)).onTrainingHalfFinished(sentence);
        verify(wordSetExperienceService, times(0)).moveToAnotherState(wordSet.getId(), REPETITION);
        verify(sentenceProvider, times(0)).enableRepetitionMode();
        verify(listener, times(0)).onEnableRepetitionMode();
        verify(exerciseService, times(0)).putOffCurrentWord(wordSet.getId());
    }

    @Test
    public void checkAnswer_emptyAnswer() {
        // setup
        WordSet wordSet = new WordSet();
        WordSetExperience experience = new WordSetExperience();
        experience.setId(3);
        experience.setMaxTrainingExperience(12);
        experience.setTrainingExperience(0);
        wordSet.setId(4);

        Sentence sentence = new Sentence();
        sentence.setId("dsfds3");
        sentence.setText("dsdsdsfds3");

        UncheckedAnswer uncheckedAnswer = new UncheckedAnswer();
        uncheckedAnswer.setExpectedAnswer(sentence.getText());
        uncheckedAnswer.setActualAnswer("");
        uncheckedAnswer.setWordSetExperienceId(experience.getId());

        AnswerCheckingResult checkingResult = new AnswerCheckingResult();
        checkingResult.setErrors(asList(new GrammarError()));

        // when
        interactor.checkAnswer(uncheckedAnswer.getActualAnswer(), wordSet, sentence, listener);

        // then
        verify(refereeService, times(0)).checkAnswer(uncheckedAnswer);
        verify(listener).onAnswerEmpty();
        verify(listener, times(0)).onSpellingOrGrammarError(any(List.class));
        verify(listener, times(0)).onAccuracyTooLowError();
        verify(listener, times(0)).onUpdateProgress(experience);
        verify(listener, times(0)).onRightAnswer(sentence);
        verify(listener, times(0)).onTrainingFinished();
        verify(listener, times(0)).onTrainingHalfFinished(sentence);
        verify(wordSetExperienceService, times(0)).moveToAnotherState(wordSet.getId(), REPETITION);
        verify(sentenceProvider, times(0)).enableRepetitionMode();
        verify(listener, times(0)).onEnableRepetitionMode();
        verify(exerciseService, times(0)).putOffCurrentWord(wordSet.getId());
    }

    @Test
    public void playVoice_bufferIsEmpty() {
        // when
        interactor.playVoice(null, listener);

        // then
        verify(listener, times(0)).onStartPlaying();
        verify(listener, times(0)).onStopPlaying();
        verify(audioStuffFactory, times(0)).createMediaPlayer();
    }

    @Test
    public void playVoice_bufferIsNotEmpty() throws IOException {
        // setup
        MediaPlayer mediaPlayer = mock(MediaPlayer.class);
        Uri empty = mock(Uri.class);

        // when
        when(audioStuffFactory.createMediaPlayer()).thenReturn(mediaPlayer);
        when(mediaPlayer.isPlaying()).thenReturn(true).thenReturn(false);
        interactor.playVoice(empty, listener);

        // then
        verify(listener).onStartPlaying();
        verify(listener).onStopPlaying();
        verify(mediaPlayer).setDataSource(context, empty);
        verify(mediaPlayer).prepare();
        verify(mediaPlayer).start();
    }

    @Test
    public void playVoice_bufferIsNotEmptyButExceptionOnStartPlaying() throws IOException {
        // setup
        MediaPlayer mediaPlayer = mock(MediaPlayer.class);
        Uri empty = mock(Uri.class);

        // when
        doThrow(RuntimeException.class).when(listener).onStartPlaying();
        try {
            interactor.playVoice(empty, listener);
        } catch (Exception e) {
        }

        // then
        verify(listener).onStopPlaying();
        verify(mediaPlayer, times(0)).setDataSource(context, empty);
        verify(mediaPlayer, times(0)).prepare();
        verify(mediaPlayer, times(0)).start();
        verify(mediaPlayer, times(0)).isPlaying();
    }


    @Test
    public void playVoice_bufferIsNotEmptyButExceptionOnPlay() throws IOException {
        // setup
        MediaPlayer mediaPlayer = mock(MediaPlayer.class);
        Uri empty = mock(Uri.class);

        // when
        doThrow(RuntimeException.class).when(mediaPlayer).start();
        when(audioStuffFactory.createMediaPlayer()).thenReturn(mediaPlayer);
        try {
            interactor.playVoice(empty, listener);
        } catch (Exception e) {
        }

        // then
        verify(listener).onStartPlaying();
        verify(listener).onStopPlaying();
        verify(mediaPlayer).setDataSource(context, empty);
        verify(mediaPlayer).prepare();
        verify(mediaPlayer, times(0)).isPlaying();
    }

    @Test
    public void peekAnyNewWordByWordSetId() {
        // setup
        int wordSetId = 5;
        Word2Tokens value = new Word2Tokens("dsfs");

        // when
        when(exerciseService.peekByWordSetIdAnyWord(wordSetId)).thenReturn(value);
        Word2Tokens actual = interactor.peekAnyNewWordByWordSetId(wordSetId);

        // then
        assertEquals(value, actual);
        verify(exerciseService).putOffCurrentWord(wordSetId);
    }

    @Test
    public void rightAnswerUntouched_answered() {
        // setup
        int wordSetId = 5;

        // when
        when(exerciseService.isCurrentExerciseAnswered(wordSetId)).thenReturn(true);
        interactor.rightAnswerUntouched(wordSetId, listener);

        // then
        verify(listener, times(0)).onHideRightAnswer(any(Sentence.class), any(Word2Tokens.class));
    }

    @Test
    public void rightAnswerUntouched_notAnswered() {
        // setup
        int wordSetId = 5;
        Sentence sentence = new Sentence();
        Word2Tokens word = new Word2Tokens();

        // when
        when(exerciseService.isCurrentExerciseAnswered(wordSetId)).thenReturn(false);
        when(exerciseService.getCurrentSentence(wordSetId)).thenReturn(sentence);
        when(exerciseService.getCurrentWord(wordSetId)).thenReturn(word);
        interactor.rightAnswerUntouched(wordSetId, listener);

        // then
        verify(listener).onHideRightAnswer(sentence, word);
    }
}
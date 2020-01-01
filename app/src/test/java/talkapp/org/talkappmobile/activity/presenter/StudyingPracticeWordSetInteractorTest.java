package talkapp.org.talkappmobile.activity.presenter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.StrategySwitcherDecorator;
import talkapp.org.talkappmobile.activity.interactor.impl.StudyingPracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.UncheckedAnswer;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.AudioStuffFactory;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.Logger;
import talkapp.org.talkappmobile.service.RefereeService;
import talkapp.org.talkappmobile.service.SentenceService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetExperienceUtils;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.WordTranslationService;
import talkapp.org.talkappmobile.service.impl.LocalCacheIsEmptyException;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FINISHED;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.SECOND_CYCLE;

@RunWith(MockitoJUnitRunner.class)
public class StudyingPracticeWordSetInteractorTest {

    @Mock
    AudioStuffFactory audioStuffFactory;
    @Mock
    WordRepetitionProgressService exerciseService;
    @Mock
    private SentenceService sentenceService;
    @Mock
    private WordTranslationService wordTranslationService;
    @Mock
    private RefereeService refereeService;
    @Mock
    private OnPracticeWordSetListener listener;
    @Mock
    private WordSetService wordSetService;
    @Mock
    private WordSetExperienceUtils experienceUtils;
    @Mock
    private CurrentPracticeStateService currentPracticeStateService;
    @Mock
    private Context context;
    @Mock
    private Logger logger;
    @InjectMocks
    private StudyingPracticeWordSetInteractor origInteractor;
    private PracticeWordSetInteractor interactor;


    @Before
    public void setUp() throws Exception {
        interactor = new StrategySwitcherDecorator(origInteractor, experienceUtils, exerciseService, currentPracticeStateService);
    }

    @Test
    public void initialiseExperience_experienceIsNull() {
        // setup
        WordSet wordSet = new WordSet();
        wordSet.setId(1);

        // when
        when(currentPracticeStateService.getWordSet()).thenReturn(wordSet);
        interactor.initialiseExperience(listener);

        // then
        verify(listener).onInitialiseExperience(wordSet);
        verify(listener, times(0)).onEnableRepetitionMode();
    }

    @Test
    public void initialiseExperience_experienceIsNotNullAndStatusRepetition() {
        // setup
        int id = 2;

        WordSet wordSet = new WordSet();
        wordSet.setId(id);
        wordSet.setTrainingExperience(2);
        wordSet.setStatus(SECOND_CYCLE);

        // when
        when(currentPracticeStateService.getWordSet()).thenReturn(wordSet);
        interactor.initialiseExperience(listener);

        // then
        verify(wordSetService, times(0)).resetProgress(wordSet);
        verify(listener).onEnableRepetitionMode();
        verify(listener).onInitialiseExperience(wordSet);
    }

    @Test
    public void initialiseExperience_experienceIsNotNullAndStatusNotRepetition() {
        // setup
        int id = 3;

        WordSet wordSet = new WordSet();
        wordSet.setStatus(FIRST_CYCLE);
        wordSet.setId(id);
        wordSet.setTrainingExperience(2);

        // when
        when(currentPracticeStateService.getWordSet()).thenReturn(wordSet);
        interactor.initialiseExperience(listener);

        // then
        verify(wordSetService, times(0)).resetProgress(wordSet);
        verify(listener, times(0)).onEnableRepetitionMode();
        verify(listener).onInitialiseExperience(wordSet);
    }

    @Test
    public void initialiseWordsSequence() {
        // setup
        WordSet wordSet = new WordSet();
        wordSet.setId(4);
        wordSet.setWords(asList(new Word2Tokens("fdsfs", "fdsfs", wordSet.getId()), new Word2Tokens("sdfs", "sdfs", wordSet.getId())));

        List<Word2Tokens> words = new ArrayList<>(wordSet.getWords());

        // when
        when(currentPracticeStateService.getWordSet()).thenReturn(wordSet);
        interactor.initialiseWordsSequence(listener);

        // then
        verify(exerciseService).createSomeIfNecessary(words);
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
        int wordSetId = 3;
        Word2Tokens word = new Word2Tokens("sdfs", "sdfs", wordSetId);

        WordSet wordSet = new WordSet();
        wordSet.setWords(asList(word));
        wordSet.setId(wordSetId);

        // when
        when(sentenceService.fetchSentencesFromServerByWordAndWordSetId(word)).thenReturn(sentences);
        when(sentenceService.selectSentences(sentences)).thenReturn(singletonList(selectedSentence));
        when(currentPracticeStateService.getCurrentSentence()).thenReturn(selectedSentence);
        interactor.initialiseSentence(word, listener);

        // then
        verify(exerciseService).save(word, singletonList(selectedSentence));
        verify(listener).onSentencesFound(selectedSentence, word);
    }

    @Test
    public void initialiseSentence_sentenceNotFound() {
        // setup
        Sentence selectedSentence = new Sentence();
        selectedSentence.setId("fds32");

        int wordSetId = 3;
        Word2Tokens word = new Word2Tokens("SDFDS", "SDFDS", wordSetId);
        WordSet wordSet = new WordSet();
        wordSet.setWords(asList(word));
        wordSet.setId(wordSetId);

        WordTranslation translation = new WordTranslation();
        translation.setId("russian");
        translation.setLanguage("russian");
        translation.setWord(word.getWord());
        translation.setTokens(word.getTokens());
        translation.setTranslation("fdsf");

        // when
        when(sentenceService.fetchSentencesFromServerByWordAndWordSetId(word)).thenThrow(new LocalCacheIsEmptyException(""));
        when(wordTranslationService.findByWordAndLanguage(word.getWord(), "russian")).thenReturn(translation);
        when(sentenceService.convertToSentence(translation)).thenReturn(selectedSentence);
        when(currentPracticeStateService.getCurrentSentence()).thenReturn(selectedSentence);
        when(sentenceService.selectSentences(asList(selectedSentence))).thenReturn(asList(selectedSentence));
        interactor.initialiseSentence(word, listener);

        // then
        verify(listener, times(1)).onSentencesFound(selectedSentence, word);
        verify(exerciseService, times(1)).save(word, singletonList(selectedSentence));
        verify(sentenceService, times(1)).selectSentences(any(List.class));
    }

    @Test
    public void checkAnswer_answerIsOk() {
        // setup
        int id = 3;

        WordSet wordSet = new WordSet();
        wordSet.setWords(asList(new Word2Tokens()));
        wordSet.setId(id);
        wordSet.setTrainingExperience(4);

        Sentence sentence = new Sentence();
        sentence.setId("dsfds3");
        sentence.setText("dsdsdsfds3");

        UncheckedAnswer uncheckedAnswer = new UncheckedAnswer();
        uncheckedAnswer.setExpectedAnswer(sentence.getText());
        uncheckedAnswer.setActualAnswer("fsdf");

        // when
        when(currentPracticeStateService.getCurrentSentence()).thenReturn(sentence);
        when(refereeService.checkAnswer(uncheckedAnswer)).thenReturn(true);
        when(currentPracticeStateService.getWordSet()).thenReturn(wordSet);
        interactor.checkAnswer(uncheckedAnswer.getActualAnswer(), listener);

        // then
        verify(listener).onUpdateProgress(wordSet.getTrainingExperience(), wordSet.getWords().size() * 2);
        verify(listener, times(0)).onTrainingFinished();
        verify(listener, times(0)).onTrainingHalfFinished(sentence);
        verify(listener, times(0)).onAnswerEmpty();
        verify(wordSetService, times(0)).moveToAnotherState(wordSet.getId(), FINISHED);
        verify(wordSetService, times(0)).moveToAnotherState(wordSet.getId(), SECOND_CYCLE);
        verify(listener, times(0)).onEnableRepetitionMode();
    }

    @Test
    public void checkAnswer_answerIsOkAndFinish() {
        // setup
        int id = 3;

        WordSet wordSet = new WordSet();
        wordSet.setId(id);
        wordSet.setTrainingExperience(12);
        wordSet.setWords(asList(new Word2Tokens("sds", "sds", wordSet.getId()), new Word2Tokens("sds", "sds", wordSet.getId()),
                new Word2Tokens("sds", "sds", wordSet.getId()), new Word2Tokens("sds", "sds", wordSet.getId()), new Word2Tokens("sds", "sds", wordSet.getId()), new Word2Tokens("sds", "sds", wordSet.getId())));

        Sentence sentence = new Sentence();
        sentence.setId("dsfds3");
        sentence.setText("dsdsdsfds3");

        UncheckedAnswer uncheckedAnswer = new UncheckedAnswer();
        uncheckedAnswer.setExpectedAnswer(sentence.getText());
        uncheckedAnswer.setActualAnswer("fsdf");

        // when
        when(currentPracticeStateService.getCurrentSentence()).thenReturn(sentence);
        when(currentPracticeStateService.getWordSet()).thenReturn(wordSet);
        when(refereeService.checkAnswer(uncheckedAnswer)).thenReturn(true);
        interactor.checkAnswer(uncheckedAnswer.getActualAnswer(), listener);

        // then
        verify(listener).onUpdateProgress(wordSet.getTrainingExperience(), wordSet.getWords().size() * 2);
        verify(listener, times(0)).onRightAnswer(sentence);
        verify(listener, times(0)).onAnswerEmpty();
        verify(listener, times(0)).onTrainingHalfFinished(sentence);
        verify(wordSetService, times(0)).moveToAnotherState(wordSet.getId(), SECOND_CYCLE);
        verify(listener, times(0)).onEnableRepetitionMode();
        verify(exerciseService).moveCurrentWordToNextState(wordSet.getId());
    }

    @Test
    public void checkAnswer_accuracyTooLowError() {
        // setup
        int id = 3;

        WordSet wordSet = new WordSet();
        wordSet.setWords(asList(new Word2Tokens()));
        wordSet.setId(id);
        wordSet.setTrainingExperience(0);

        Sentence sentence = new Sentence();
        sentence.setId("dsfds3");
        sentence.setText("dsdsdsfds3");

        UncheckedAnswer uncheckedAnswer = new UncheckedAnswer();
        uncheckedAnswer.setExpectedAnswer(sentence.getText());
        uncheckedAnswer.setActualAnswer("fsdf");

        // when
        when(refereeService.checkAnswer(uncheckedAnswer)).thenReturn(false);
        when(currentPracticeStateService.getCurrentSentence()).thenReturn(sentence);
        interactor.checkAnswer(uncheckedAnswer.getActualAnswer(), listener);

        // then
        verify(listener, times(0)).onUpdateProgress(wordSet);
        verify(listener, times(0)).onRightAnswer(sentence);
        verify(listener, times(0)).onTrainingFinished();
        verify(listener, times(0)).onAnswerEmpty();
        verify(listener, times(0)).onTrainingHalfFinished(sentence);
        verify(wordSetService, times(0)).moveToAnotherState(wordSet.getId(), SECOND_CYCLE);
        verify(listener, times(0)).onEnableRepetitionMode();
        verify(exerciseService, times(0)).putOffCurrentWord(wordSet.getId());
    }

    @Test
    public void checkAnswer_spellingOrGrammarError() {
        // setup
        int id = 3;

        WordSet wordSet = new WordSet();
        wordSet.setWords(asList(new Word2Tokens()));
        wordSet.setId(id);
        wordSet.setTrainingExperience(0);

        Sentence sentence = new Sentence();
        sentence.setId("dsfds3");
        sentence.setText("dsdsdsfds3");

        UncheckedAnswer uncheckedAnswer = new UncheckedAnswer();
        uncheckedAnswer.setExpectedAnswer(sentence.getText());
        uncheckedAnswer.setActualAnswer("fsdf");

        // when
        when(currentPracticeStateService.getCurrentSentence()).thenReturn(sentence);
        when(refereeService.checkAnswer(uncheckedAnswer)).thenReturn(false);
        interactor.checkAnswer(uncheckedAnswer.getActualAnswer(), listener);

        // then
        verify(listener, times(0)).onUpdateProgress(wordSet);
        verify(listener, times(0)).onRightAnswer(sentence);
        verify(listener, times(0)).onTrainingFinished();
        verify(listener, times(0)).onTrainingHalfFinished(sentence);
        verify(listener, times(0)).onAnswerEmpty();
        verify(listener, times(0)).onTrainingHalfFinished(sentence);
        verify(wordSetService, times(0)).moveToAnotherState(wordSet.getId(), SECOND_CYCLE);
        verify(listener, times(0)).onEnableRepetitionMode();
        verify(exerciseService, times(0)).putOffCurrentWord(wordSet.getId());
    }

    @Test
    public void checkAnswer_emptyAnswer() {
        // setup
        WordSet wordSet = new WordSet();
        wordSet.setWords(asList(new Word2Tokens()));
        wordSet.setId(4);
        wordSet.setTrainingExperience(0);

        Sentence sentence = new Sentence();
        sentence.setId("dsfds3");
        sentence.setText("dsdsdsfds3");

        UncheckedAnswer uncheckedAnswer = new UncheckedAnswer();
        uncheckedAnswer.setExpectedAnswer(sentence.getText());
        uncheckedAnswer.setActualAnswer("");

        // when
        interactor.checkAnswer(uncheckedAnswer.getActualAnswer(), listener);

        // then
        verify(refereeService, times(0)).checkAnswer(uncheckedAnswer);
        verify(listener).onAnswerEmpty();
        verify(listener, times(0)).onUpdateProgress(wordSet);
        verify(listener, times(0)).onRightAnswer(sentence);
        verify(listener, times(0)).onTrainingFinished();
        verify(listener, times(0)).onTrainingHalfFinished(sentence);
        verify(wordSetService, times(0)).moveToAnotherState(wordSet.getId(), SECOND_CYCLE);
        verify(listener, times(0)).onEnableRepetitionMode();
        verify(exerciseService, times(0)).putOffCurrentWord(wordSet.getId());
    }

    @Test
    public void playVoice_bufferIsEmpty() {
        // when
        interactor.playVoice(listener);

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
        interactor.saveVoice(empty, listener);

        // when
        when(audioStuffFactory.createMediaPlayer()).thenReturn(mediaPlayer);
        when(mediaPlayer.isPlaying()).thenReturn(true).thenReturn(false);
        interactor.playVoice(listener);

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
        interactor.saveVoice(empty, listener);

        // when
        doThrow(RuntimeException.class).when(listener).onStartPlaying();
        try {
            interactor.playVoice(listener);
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
        interactor.saveVoice(empty, listener);

        // when
        doThrow(RuntimeException.class).when(mediaPlayer).start();
        when(audioStuffFactory.createMediaPlayer()).thenReturn(mediaPlayer);
        try {
            interactor.playVoice(listener);
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
        Word2Tokens newCurrentWord = new Word2Tokens("newCurrentWord", "newCurrentWord", wordSetId);
        Word2Tokens currentWord = new Word2Tokens("currentWord", "currentWord", wordSetId);
        ArrayList<Word2Tokens> words = new ArrayList<>();
        words.add(currentWord);

        // when
        WordSet wordSet = new WordSet();
        wordSet.setId(wordSetId);
        wordSet.setWords(words);
        when(currentPracticeStateService.getCurrentWord()).thenReturn(currentWord);
        when(currentPracticeStateService.getWordSet()).thenReturn(wordSet);
        when(exerciseService.getLeftOverOfWordSetByWordSetId(wordSetId)).thenReturn(asList(currentWord, newCurrentWord));
        Word2Tokens actual = interactor.peekAnyNewWordByWordSetId();

        // then
        assertEquals(newCurrentWord, actual);
        assertNotEquals(actual, currentWord);
        verify(exerciseService).putOffCurrentWord(wordSetId);
        verify(exerciseService).markNewCurrentWordByWordSetIdAndWord(wordSetId, newCurrentWord);
    }
}
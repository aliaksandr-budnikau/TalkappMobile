package talkapp.org.talkappmobile.activity.presenter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.impl.StudyingPracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.UncheckedAnswer;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.AudioStuffFactory;
import talkapp.org.talkappmobile.service.Logger;
import talkapp.org.talkappmobile.service.RefereeService;
import talkapp.org.talkappmobile.service.SentenceService;
import talkapp.org.talkappmobile.service.UserExpService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetExperienceUtils;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.WordTranslationService;
import talkapp.org.talkappmobile.service.WordsCombinator;
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
    private WordsCombinator wordsCombinator;
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
    private UserExpService userExpService;
    @Mock
    private Context context;
    @Mock
    private Logger logger;
    @InjectMocks
    private StudyingPracticeWordSetInteractor interactor;

    @Test
    public void initialiseExperience_experienceIsNull() {
        // setup
        WordSet wordSet = new WordSet();
        wordSet.setId(1);

        // when
        when(wordSetService.getCurrent()).thenReturn(wordSet);
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
        when(wordSetService.getCurrent()).thenReturn(wordSet);
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
        when(wordSetService.getCurrent()).thenReturn(wordSet);
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
        interactor.initialiseWordsSequence(wordSet, listener);

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
        when(wordSetService.findById(wordSetId)).thenReturn(wordSet);
        Whitebox.setInternalState(interactor, "wordSetId", wordSet.getId());
        Whitebox.setInternalState(interactor, "currentWordIndex", 0);
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
        Whitebox.setInternalState(interactor, "wordSetId", wordSet.getId());
        when(wordSetService.findById(wordSetId)).thenReturn(wordSet);
        when(sentenceService.fetchSentencesFromServerByWordAndWordSetId(word)).thenThrow(new LocalCacheIsEmptyException(""));
        when(wordTranslationService.findByWordAndLanguage(word.getWord(), "russian")).thenReturn(translation);
        when(sentenceService.convertToSentence(translation)).thenReturn(selectedSentence);
        when(sentenceService.selectSentences(asList(selectedSentence))).thenReturn(asList(selectedSentence));
        Whitebox.setInternalState(interactor, "currentWordIndex", 0);
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
        when(wordSetService.getCurrent()).thenReturn(wordSet);
        when(refereeService.checkAnswer(uncheckedAnswer)).thenReturn(true);
        when(wordSetService.increaseExperience(wordSet.getId(), 1)).thenReturn(wordSet.getTrainingExperience() + 1);
        when(wordSetService.findById(wordSet.getId())).thenReturn(wordSet);
        Whitebox.setInternalState(interactor, "wordSetId", wordSet.getId());
        Whitebox.setInternalState(interactor, "currentWordIndex", 0);
        interactor.checkAnswer(uncheckedAnswer.getActualAnswer(), sentence, false, listener);

        // then
        verify(listener).onUpdateProgress(wordSet.getTrainingExperience(), wordSet.getWords().size() * 2);
        verify(listener).onRightAnswer(sentence);
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
        when(wordSetService.getCurrent()).thenReturn(wordSet);
        when(wordSetService.findById(wordSet.getId())).thenReturn(wordSet);
        Whitebox.setInternalState(interactor, "wordSetId", wordSet.getId());
        when(refereeService.checkAnswer(uncheckedAnswer)).thenReturn(true);
        when(wordSetService.increaseExperience(wordSet.getId(), 1)).thenReturn(wordSet.getTrainingExperience());
        when(experienceUtils.getMaxTrainingProgress(wordSet)).thenReturn(wordSet.getWords().size() * 2);
        interactor.checkAnswer(uncheckedAnswer.getActualAnswer(), sentence, false, listener);

        // then
        verify(listener).onUpdateProgress(wordSet.getTrainingExperience(), wordSet.getWords().size() * 2);
        verify(listener, times(0)).onRightAnswer(sentence);
        verify(listener).onTrainingFinished();
        verify(wordSetService).moveToAnotherState(wordSet.getId(), FINISHED);
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
        when(wordSetService.findById(wordSet.getId())).thenReturn(wordSet);
        Whitebox.setInternalState(interactor, "wordSetId", wordSet.getId());
        Whitebox.setInternalState(interactor, "currentWordIndex", 0);
        interactor.checkAnswer(uncheckedAnswer.getActualAnswer(), sentence, false, listener);

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
        when(wordSetService.findById(wordSet.getId())).thenReturn(wordSet);
        when(refereeService.checkAnswer(uncheckedAnswer)).thenReturn(false);
        Whitebox.setInternalState(interactor, "wordSetId", wordSet.getId());
        Whitebox.setInternalState(interactor, "currentWordIndex", 0);
        interactor.checkAnswer(uncheckedAnswer.getActualAnswer(), sentence, false, listener);

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
        when(wordSetService.findById(wordSet.getId())).thenReturn(wordSet);
        Whitebox.setInternalState(interactor, "currentWordIndex", 0);
        Whitebox.setInternalState(interactor, "wordSetId", wordSet.getId());
        interactor.checkAnswer(uncheckedAnswer.getActualAnswer(), sentence, false, listener);

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
        Word2Tokens newCurrentWord = new Word2Tokens("newCurrentWord", "newCurrentWord", wordSetId);
        Word2Tokens currentWord = new Word2Tokens("currentWord", "currentWord", wordSetId);

        // when
        WordSet wordSet = new WordSet();
        wordSet.setId(wordSetId);
        when(wordSetService.getCurrent()).thenReturn(wordSet);
        when(exerciseService.getCurrentWord(wordSetId)).thenReturn(currentWord);
        when(exerciseService.getLeftOverOfWordSetByWordSetId(wordSetId)).thenReturn(asList(currentWord, newCurrentWord));
        Word2Tokens actual = interactor.peekAnyNewWordByWordSetId();

        // then
        assertEquals(newCurrentWord, actual);
        assertNotEquals(actual, currentWord);
        verify(exerciseService).putOffCurrentWord(wordSetId);
        verify(exerciseService).markNewCurrentWordByWordSetIdAndWord(wordSetId, newCurrentWord);
    }
}
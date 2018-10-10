package talkapp.org.talkappmobile.activity.presenter;

import android.media.AudioTrack;

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

import talkapp.org.talkappmobile.app.TalkappMobileApplication;
import talkapp.org.talkappmobile.component.AudioStuffFactory;
import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.RecordedTrack;
import talkapp.org.talkappmobile.component.RefereeService;
import talkapp.org.talkappmobile.component.SentenceProvider;
import talkapp.org.talkappmobile.component.SentenceSelector;
import talkapp.org.talkappmobile.component.WordsCombinator;
import talkapp.org.talkappmobile.component.backend.WordSetExperienceService;
import talkapp.org.talkappmobile.component.database.WordSetExperienceRepository;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.AnswerCheckingResult;
import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.UncheckedAnswer;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PracticeWordSetInteractorTest {

    @Mock
    private WordsCombinator wordsCombinator;
    @Mock
    private AuthSign authSign;
    @Mock
    private Logger logger;
    @Mock
    private WordSetExperienceService wordSetExperienceService;
    @Mock
    private SentenceProvider sentenceProvider;
    @Mock
    private SentenceSelector sentenceSelector;
    @Mock
    private RefereeService refereeService;
    @Mock
    private RecordedTrack recordedTrackBuffer;
    @Mock
    private OnPracticeWordSetListener listener;
    @Mock
    private AudioStuffFactory audioStuffFactory;
    @Mock
    private WordSetExperienceRepository wordSetExperienceRepository;
    @InjectMocks
    private PracticeWordSetInteractor interactor;

    @BeforeClass
    public static void setUp() {
        DIContext.init(new TalkappMobileApplication());
    }

    @Test
    public void initialiseExperience_experienceIsNull() throws IOException {
        // setup
        WordSet wordSet = new WordSet();
        wordSet.setExperience(null);
        wordSet.setId("3243");

        WordSetExperience experience = new WordSetExperience();
        experience.setId(323423);

        // when
        when(wordSetExperienceRepository.createNew(wordSet)).thenReturn(experience);
        interactor.initialiseExperience(wordSet, listener);

        // then
        verify(listener).onInitialiseExperience();
        assertNotNull(wordSet.getExperience());
        assertEquals(experience.getId(), wordSet.getExperience().getId());
    }

    @Test
    public void initialiseExperience_experienceIsNotNull() throws IOException {
        // setup
        WordSetExperience experience = new WordSetExperience();
        experience.setId(323423);

        WordSet wordSet = new WordSet();
        wordSet.setExperience(experience);
        wordSet.setId("3243");

        // when
        interactor.initialiseExperience(wordSet, listener);

        // then
        verify(wordSetExperienceRepository, times(0)).createNew(wordSet);
        verify(listener).onInitialiseExperience();
        assertNotNull(wordSet.getExperience());
        assertEquals(experience.getId(), wordSet.getExperience().getId());
    }

    @Test
    public void initialiseWordsSequence() {
        // setup
        WordSet wordSet = new WordSet();
        wordSet.setWords(asList("fdsfs", "sdfs"));
        wordSet.setId("3243");

        // when
        when(wordsCombinator.combineWords(wordSet.getWords())).thenReturn(new HashSet<String>());
        interactor.initialiseWordsSequence(wordSet, listener);

        // then
        assertTrue(wordSet.getWords().isEmpty());
    }

    @Test
    public void initialiseSentence_sentenceFound() {
        // setup
        Sentence sentence = new Sentence();
        sentence.setId("fds32ddd");
        sentence = new Sentence();
        sentence.setId("fds32ddddsas");
        List<Sentence> sentences = asList(sentence, sentence);

        Sentence selectedSentence = new Sentence();
        selectedSentence.setId("fds32");
        String word = "sdfs";
        String wordSetId = "sdfsId";

        // when
        when(sentenceProvider.findByWordAndWordSetId(word, wordSetId)).thenReturn(sentences);
        when(sentenceSelector.getSentence(sentences)).thenReturn(selectedSentence);
        interactor.initialiseSentence(word, wordSetId, listener);

        // then
        verify(listener).onSentencesFound(selectedSentence, word);
    }

    @Test
    public void initialiseSentence_sentenceNotFound() {
        // setup
        Sentence selectedSentence = new Sentence();
        selectedSentence.setId("fds32");

        String word = "SDFDS";
        String wordSetId = "SDFDSId";

        // when
        when(sentenceProvider.findByWordAndWordSetId(word, wordSetId)).thenReturn(Collections.<Sentence>emptyList());
        interactor.initialiseSentence(word, wordSetId, listener);

        // then
        verify(listener, times(0)).onSentencesFound(selectedSentence, word);
        verify(sentenceSelector, times(0)).getSentence(any(List.class));
    }

    @Test
    public void checkAnswer_answerIsOk() throws IOException {
        // setup
        WordSet wordSet = new WordSet();
        wordSet.setExperience(new WordSetExperience());
        wordSet.getExperience().setId(323);
        wordSet.getExperience().setMaxTrainingExperience(12);
        wordSet.setId("3243");

        Sentence sentence = new Sentence();
        sentence.setId("dsfds3");
        sentence.setText("dsdsdsfds3");

        UncheckedAnswer uncheckedAnswer = new UncheckedAnswer();
        uncheckedAnswer.setExpectedAnswer(sentence.getText());
        uncheckedAnswer.setActualAnswer("fsdf");
        uncheckedAnswer.setWordSetExperienceId(wordSet.getExperience().getId());

        AnswerCheckingResult checkingResult = new AnswerCheckingResult();
        int currentTrainingExperience = 4;
        checkingResult.setCurrentTrainingExperience(currentTrainingExperience);
        checkingResult.setErrors(new ArrayList<GrammarError>());

        // when
        when(refereeService.checkAnswer(uncheckedAnswer)).thenReturn(checkingResult);
        interactor.checkAnswer(uncheckedAnswer.getActualAnswer(), wordSet, sentence, listener);

        // then
        verify(listener).onUpdateProgress(currentTrainingExperience);
        verify(listener).onRightAnswer();
        verify(listener, times(0)).onTrainingFinished();
        verify(listener, times(0)).onAccuracyTooLowError();
        verify(listener, times(0)).onSpellingOrGrammarError(any(List.class));
        verify(listener, times(0)).onAnswerEmpty();
    }

    @Test
    public void checkAnswer_answerIsOkAndFinish() throws IOException {
        // setup
        WordSet wordSet = new WordSet();
        wordSet.setExperience(new WordSetExperience());
        wordSet.getExperience().setId(23534);
        wordSet.getExperience().setMaxTrainingExperience(12);
        wordSet.setId("3243");

        Sentence sentence = new Sentence();
        sentence.setId("dsfds3");
        sentence.setText("dsdsdsfds3");

        UncheckedAnswer uncheckedAnswer = new UncheckedAnswer();
        uncheckedAnswer.setExpectedAnswer(sentence.getText());
        uncheckedAnswer.setActualAnswer("fsdf");
        uncheckedAnswer.setWordSetExperienceId(wordSet.getExperience().getId());

        AnswerCheckingResult checkingResult = new AnswerCheckingResult();
        int currentTrainingExperience = 12;
        checkingResult.setCurrentTrainingExperience(currentTrainingExperience);
        checkingResult.setErrors(new ArrayList<GrammarError>());

        // when
        when(refereeService.checkAnswer(uncheckedAnswer)).thenReturn(checkingResult);
        interactor.checkAnswer(uncheckedAnswer.getActualAnswer(), wordSet, sentence, listener);

        // then
        verify(listener).onUpdateProgress(currentTrainingExperience);
        verify(listener, times(0)).onRightAnswer();
        verify(listener).onTrainingFinished();
        verify(listener, times(0)).onAccuracyTooLowError();
        verify(listener, times(0)).onSpellingOrGrammarError(any(List.class));
        verify(listener, times(0)).onAnswerEmpty();
    }

    @Test
    public void checkAnswer_accuracyTooLowError() throws IOException {
        // setup
        WordSet wordSet = new WordSet();
        wordSet.setExperience(new WordSetExperience());
        wordSet.getExperience().setId(3232);
        wordSet.getExperience().setMaxTrainingExperience(12);
        wordSet.setId("3243");

        Sentence sentence = new Sentence();
        sentence.setId("dsfds3");
        sentence.setText("dsdsdsfds3");

        UncheckedAnswer uncheckedAnswer = new UncheckedAnswer();
        uncheckedAnswer.setExpectedAnswer(sentence.getText());
        uncheckedAnswer.setActualAnswer("fsdf");
        uncheckedAnswer.setWordSetExperienceId(wordSet.getExperience().getId());

        AnswerCheckingResult checkingResult = new AnswerCheckingResult();
        int currentTrainingExperience = 0;
        checkingResult.setCurrentTrainingExperience(currentTrainingExperience);
        checkingResult.setErrors(new ArrayList<GrammarError>());

        // when
        when(refereeService.checkAnswer(uncheckedAnswer)).thenReturn(checkingResult);
        interactor.checkAnswer(uncheckedAnswer.getActualAnswer(), wordSet, sentence, listener);

        // then
        verify(listener).onAccuracyTooLowError();
        verify(listener, times(0)).onUpdateProgress(currentTrainingExperience);
        verify(listener, times(0)).onRightAnswer();
        verify(listener, times(0)).onTrainingFinished();
        verify(listener, times(0)).onSpellingOrGrammarError(any(List.class));
        verify(listener, times(0)).onAnswerEmpty();
    }

    @Test
    public void checkAnswer_spellingOrGrammarError() throws IOException {
        // setup
        WordSet wordSet = new WordSet();
        wordSet.setExperience(new WordSetExperience());
        wordSet.getExperience().setId(32534);
        wordSet.getExperience().setMaxTrainingExperience(12);
        wordSet.setId("3243");

        Sentence sentence = new Sentence();
        sentence.setId("dsfds3");
        sentence.setText("dsdsdsfds3");

        UncheckedAnswer uncheckedAnswer = new UncheckedAnswer();
        uncheckedAnswer.setExpectedAnswer(sentence.getText());
        uncheckedAnswer.setActualAnswer("fsdf");
        uncheckedAnswer.setWordSetExperienceId(wordSet.getExperience().getId());

        AnswerCheckingResult checkingResult = new AnswerCheckingResult();
        int currentTrainingExperience = 0;
        checkingResult.setCurrentTrainingExperience(currentTrainingExperience);
        List<GrammarError> errors = asList(new GrammarError());
        checkingResult.setErrors(errors);

        // when
        when(refereeService.checkAnswer(uncheckedAnswer)).thenReturn(checkingResult);
        interactor.checkAnswer(uncheckedAnswer.getActualAnswer(), wordSet, sentence, listener);

        // then
        verify(listener).onSpellingOrGrammarError(errors);
        verify(listener, times(0)).onAccuracyTooLowError();
        verify(listener, times(0)).onUpdateProgress(currentTrainingExperience);
        verify(listener, times(0)).onRightAnswer();
        verify(listener, times(0)).onTrainingFinished();
        verify(listener, times(0)).onAnswerEmpty();
    }


    @Test
    public void checkAnswer_emptyAnswer() {
        // setup
        WordSet wordSet = new WordSet();
        wordSet.setExperience(new WordSetExperience());
        wordSet.getExperience().setId(23234);
        wordSet.getExperience().setMaxTrainingExperience(12);
        wordSet.setId("3243");

        Sentence sentence = new Sentence();
        sentence.setId("dsfds3");
        sentence.setText("dsdsdsfds3");

        UncheckedAnswer uncheckedAnswer = new UncheckedAnswer();
        uncheckedAnswer.setExpectedAnswer(sentence.getText());
        uncheckedAnswer.setActualAnswer("");
        uncheckedAnswer.setWordSetExperienceId(wordSet.getExperience().getId());

        AnswerCheckingResult checkingResult = new AnswerCheckingResult();
        int currentTrainingExperience = 0;
        checkingResult.setCurrentTrainingExperience(currentTrainingExperience);
        checkingResult.setErrors(asList(new GrammarError()));

        // when
        interactor.checkAnswer(uncheckedAnswer.getActualAnswer(), wordSet, sentence, listener);

        // then
        verify(refereeService, times(0)).checkAnswer(uncheckedAnswer);
        verify(listener).onAnswerEmpty();
        verify(listener, times(0)).onSpellingOrGrammarError(any(List.class));
        verify(listener, times(0)).onAccuracyTooLowError();
        verify(listener, times(0)).onUpdateProgress(currentTrainingExperience);
        verify(listener, times(0)).onRightAnswer();
        verify(listener, times(0)).onTrainingFinished();
    }

    @Test
    public void playVoice_bufferIsEmpty() {
        // setup
        // when
        when(recordedTrackBuffer.isEmpty()).thenReturn(true);
        interactor.playVoice(listener);

        // then
        verify(listener, times(0)).onStartPlaying();
        verify(listener, times(0)).onStopPlaying();
        verify(audioStuffFactory, times(0)).createAudioTrack();
        verify(recordedTrackBuffer, times(0)).getAsOneArray();
        verify(recordedTrackBuffer, times(0)).getPosition();
    }

    @Test
    public void playVoice_bufferIsNotEmpty() {
        // setup
        AudioTrack audioTrack = mock(AudioTrack.class);
        byte[] array = new byte[10];
        int position = 5;

        // when
        when(audioStuffFactory.createAudioTrack()).thenReturn(audioTrack);
        when(recordedTrackBuffer.isEmpty()).thenReturn(false);
        when(recordedTrackBuffer.getAsOneArray()).thenReturn(array);
        when(recordedTrackBuffer.getPosition()).thenReturn(position);
        interactor.playVoice(listener);

        // then
        verify(listener).onStartPlaying();
        verify(listener).onStopPlaying();
        verify(audioTrack).play();
        verify(audioTrack).write(array, 0, position);
        verify(audioTrack).release();
    }

    @Test
    public void playVoice_bufferIsNotEmptyButExceptionOnStartPlaying() {
        // setup
        // when
        when(recordedTrackBuffer.isEmpty()).thenReturn(false);
        doThrow(RuntimeException.class).when(listener).onStartPlaying();
        try {
            interactor.playVoice(listener);
        } catch (Exception e) {
        }

        // then
        verify(listener).onStopPlaying();
        verify(audioStuffFactory, times(0)).createAudioTrack();
        verify(recordedTrackBuffer, times(0)).getAsOneArray();
        verify(recordedTrackBuffer, times(0)).getPosition();
    }


    @Test
    public void playVoice_bufferIsNotEmptyButExceptionOnPlay() {
        // setup
        AudioTrack audioTrack = mock(AudioTrack.class);

        // when
        when(recordedTrackBuffer.isEmpty()).thenReturn(false);
        when(audioStuffFactory.createAudioTrack()).thenReturn(audioTrack);
        doThrow(RuntimeException.class).when(audioTrack).play();
        try {
            interactor.playVoice(listener);
        } catch (Exception e) {
        }

        // then
        verify(listener).onStartPlaying();
        verify(listener).onStopPlaying();
        verify(recordedTrackBuffer, times(0)).getAsOneArray();
        verify(recordedTrackBuffer, times(0)).getPosition();
        verify(audioTrack).release();
    }


    @Test
    public void playVoice_bufferIsNotEmptyButExceptionOnCreateAudioTrack() {
        // setup
        // when
        when(recordedTrackBuffer.isEmpty()).thenReturn(false);
        doThrow(RuntimeException.class).when(audioStuffFactory).createAudioTrack();
        try {
            interactor.playVoice(listener);
        } catch (Exception e) {
        }

        // then
        verify(listener).onStartPlaying();
        verify(listener).onStopPlaying();
        verify(recordedTrackBuffer, times(0)).getAsOneArray();
        verify(recordedTrackBuffer, times(0)).getPosition();
    }
}
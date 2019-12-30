package talkapp.org.talkappmobile.activity.presenter;

import android.content.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.DaoHelper;
import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.RepetitionPracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.StrategySwitcherDecorator;
import talkapp.org.talkappmobile.activity.interactor.impl.UserExperienceDecorator;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.dao.TopicDao;
import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.mappings.SentenceIdMapping;
import talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.SentenceService;
import talkapp.org.talkappmobile.service.UserExpService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.WordTranslationService;
import talkapp.org.talkappmobile.service.impl.AudioStuffFactoryBean;
import talkapp.org.talkappmobile.service.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.service.impl.EqualityScorerBean;
import talkapp.org.talkappmobile.service.impl.LocalDataServiceImpl;
import talkapp.org.talkappmobile.service.impl.LoggerBean;
import talkapp.org.talkappmobile.service.impl.RefereeServiceImpl;
import talkapp.org.talkappmobile.service.impl.RequestExecutor;
import talkapp.org.talkappmobile.service.impl.SentenceServiceImpl;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;
import talkapp.org.talkappmobile.service.impl.TextUtilsImpl;
import talkapp.org.talkappmobile.service.impl.UserExpServiceImpl;
import talkapp.org.talkappmobile.service.impl.WordRepetitionProgressServiceImpl;
import talkapp.org.talkappmobile.service.impl.WordSetExperienceUtilsImpl;
import talkapp.org.talkappmobile.service.impl.WordSetServiceImpl;
import talkapp.org.talkappmobile.service.impl.WordTranslationServiceImpl;
import talkapp.org.talkappmobile.service.mapper.ExpAuditMapper;
import talkapp.org.talkappmobile.service.mapper.WordSetMapper;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static java.util.Arrays.asList;
import static java.util.Collections.shuffle;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class PracticeWordSetRepetitionPresenterAndInteractorIntegTest extends PresenterAndInteractorIntegTest {
    private PracticeWordSetView view;
    private PracticeWordSetPresenter presenter;
    private WordRepetitionProgressService exerciseService;
    private UserExpService userExpService;
    private WordSet wordSet;
    private PracticeWordSetInteractor interactor;
    private Context context;
    private WordSetExperienceUtilsImpl experienceUtils;
    private WordSetService wordSetService;
    private DaoHelper daoHelper;
    private WordTranslationService wordTranslationService;
    private RepetitionPracticeWordSetInteractor repetitionPracticeWordSetInteractor;

    @Before
    public void setup() throws SQLException {
        view = mock(PracticeWordSetView.class);
        context = mock(Context.class);

        ObjectMapper mapper = new ObjectMapper();
        LoggerBean logger = new LoggerBean();
        daoHelper = new DaoHelper();
        LocalDataServiceImpl localDataService = new LocalDataServiceImpl(daoHelper.getWordSetDao(), mock(TopicDao.class), daoHelper.getSentenceDao(), mock(WordTranslationDao.class), mapper, logger);

        BackendServerFactoryBean factory = new BackendServerFactoryBean();
        Whitebox.setInternalState(factory, "logger", new LoggerBean());
        ServiceFactoryBean mockServiceFactoryBean = mock(ServiceFactoryBean.class);
        when(mockServiceFactoryBean.getLocalDataService()).thenReturn(localDataService);
        Whitebox.setInternalState(factory, "serviceFactory", mockServiceFactoryBean);
        Whitebox.setInternalState(factory, "requestExecutor", new RequestExecutor());
        DataServer server = factory.get();

        userExpService = new UserExpServiceImpl(daoHelper.getExpAuditDao(), mock(ExpAuditMapper.class));
        exerciseService = new WordRepetitionProgressServiceImpl(daoHelper.getWordRepetitionProgressDao(), daoHelper.getWordSetDao(), daoHelper.getSentenceDao(), mapper);
        experienceUtils = new WordSetExperienceUtilsImpl();
        wordSetService = new WordSetServiceImpl(daoHelper.getWordSetDao(), daoHelper.getCurrentWordSetDao(), daoHelper.getNewWordSetDraftDao(), mapper);
        wordTranslationService = new WordTranslationServiceImpl(daoHelper.getWordTranslationDao(), mapper);
        SentenceService sentenceService = new SentenceServiceImpl(server, exerciseService, mapper);
        repetitionPracticeWordSetInteractor = new RepetitionPracticeWordSetInteractor(sentenceService, new RefereeServiceImpl(new EqualityScorerBean()),
                logger, exerciseService, experienceUtils, wordSetService, wordTranslationService, context, new AudioStuffFactoryBean());
        this.interactor = new UserExperienceDecorator(repetitionPracticeWordSetInteractor, wordSetService, userExpService, exerciseService);
        server.initLocalCacheOfAllSentencesForThisWordset(-1, 6);
    }

    @After
    public void tearDown() {
        daoHelper.releaseHelper();
    }

    private void createPresenter(PracticeWordSetInteractor interactor) throws JsonProcessingException, SQLException {
        int id = 0;
        int trainingExperience = 0;
        WordSetProgressStatus status = WordSetProgressStatus.FINISHED;
        if (wordSet != null) {
            trainingExperience = wordSet.getTrainingExperience();
            status = wordSet.getStatus();
        }
        wordSet = new WordSet();
        wordSet.setId(id);
        wordSet.setStatus(WordSetProgressStatus.FINISHED);

        ObjectMapper mapper = new ObjectMapper();

        int ageWordSetId = id + 1;
        Word2Tokens age = new Word2Tokens("age", "age", ageWordSetId);
        List<Word2Tokens> ageWordSetWords = asList(age, new Word2Tokens(), new Word2Tokens());
        WordRepetitionProgressMapping exercise = new WordRepetitionProgressMapping();
        exercise.setSentenceIds(getSentenceJSON(mapper, "AWbgboVdNEXFMlzHK5SR", age.getWord(), 6));
        exercise.setStatus(WordSetProgressStatus.FINISHED.name());
        exercise.setUpdatedDate(new Date());
        exercise.setWordSetId(ageWordSetId);
        exercise.setWordIndex(ageWordSetWords.indexOf(age));
        daoHelper.getWordRepetitionProgressDao().createNewOrUpdate(exercise);

        WordSet ageWordSet = new WordSet();
        ageWordSet.setId(ageWordSetId);
        ageWordSet.setStatus(WordSetProgressStatus.FINISHED);
        ageWordSet.setWords(new LinkedList<>(ageWordSetWords));
        ageWordSet.setTopicId("topicId");
        ageWordSet.setTrainingExperience(0);
        WordSetMapping ageWordSetMapping = new WordSetMapper(mapper).toMapping(ageWordSet);
        daoHelper.getWordSetDao().createNewOrUpdate(ageWordSetMapping);

        int anniversaryWordSetId = id + 2;
        Word2Tokens anniversary = new Word2Tokens("anniversary", "anniversary", anniversaryWordSetId);
        List<Word2Tokens> anniversaryWordSetWords = asList(new Word2Tokens(), anniversary, new Word2Tokens());
        exercise = new WordRepetitionProgressMapping();
        exercise.setSentenceIds(getSentenceJSON(mapper, "AWbgbq6hNEXFMlzHK5Ul", anniversary.getWord(), 6));
        exercise.setStatus(WordSetProgressStatus.FINISHED.name());
        exercise.setUpdatedDate(new Date());
        exercise.setWordSetId(anniversaryWordSetId);
        exercise.setWordIndex(anniversaryWordSetWords.indexOf(anniversary));
        daoHelper.getWordRepetitionProgressDao().createNewOrUpdate(exercise);

        WordSet anniversaryWordSet = new WordSet();
        anniversaryWordSet.setId(anniversaryWordSetId);
        anniversaryWordSet.setStatus(WordSetProgressStatus.FINISHED);
        anniversaryWordSet.setWords(new LinkedList<>(anniversaryWordSetWords));
        anniversaryWordSet.setTopicId("topicId");
        anniversaryWordSet.setTrainingExperience(0);
        WordSetMapping anniversaryWordSetMapping = new WordSetMapper(mapper).toMapping(anniversaryWordSet);
        daoHelper.getWordSetDao().createNewOrUpdate(anniversaryWordSetMapping);

        int birthWordSetId = id + 3;
        Word2Tokens birth = new Word2Tokens("birth", "birth", birthWordSetId);
        List<Word2Tokens> birthWordSetWords = asList(new Word2Tokens(), new Word2Tokens(), birth);
        exercise = new WordRepetitionProgressMapping();
        exercise.setSentenceIds(getSentenceJSON(mapper, "AWbgbsUXNEXFMlzHK5V2", birth.getWord(), 6));
        exercise.setStatus(WordSetProgressStatus.FINISHED.name());
        exercise.setUpdatedDate(new Date());
        exercise.setWordSetId(birthWordSetId);
        exercise.setWordIndex(birthWordSetWords.indexOf(birth));
        daoHelper.getWordRepetitionProgressDao().createNewOrUpdate(exercise);

        WordSet birthWordSet = new WordSet();
        birthWordSet.setId(birthWordSetId);
        birthWordSet.setStatus(WordSetProgressStatus.FINISHED);
        birthWordSet.setWords(new LinkedList<>(birthWordSetWords));
        birthWordSet.setTopicId("topicId");
        birthWordSet.setTrainingExperience(0);
        WordSetMapping birthWordSetMapping = new WordSetMapper(mapper).toMapping(birthWordSet);
        daoHelper.getWordSetDao().createNewOrUpdate(birthWordSetMapping);

        wordSet.setWords(new LinkedList<>(asList(age, anniversary, birth)));
        wordSet.setTopicId("topicId");
        wordSet.setTrainingExperience(trainingExperience);
        wordSet.setStatus(status);
        PracticeWordSetViewStrategy firstCycleViewStrategy = new PracticeWordSetViewStrategy(view, new TextUtilsImpl(), new WordSetExperienceUtilsImpl());
        presenter = new PracticeWordSetPresenter(new StrategySwitcherDecorator(interactor, wordSetService, experienceUtils, exerciseService), firstCycleViewStrategy);
        Whitebox.setInternalState(repetitionPracticeWordSetInteractor, "finishedWords", new LinkedList<>());
    }

    private String getSentenceJSON(ObjectMapper mapper, String sentenceId, String word, int lengthInWords) throws JsonProcessingException {
        return mapper.writeValueAsString(singletonList(new SentenceIdMapping(sentenceId, lengthInWords)));
    }

    @Test
    public void testPracticeWordSet_completeOneSet() throws JsonProcessingException, SQLException {
        createPresenter(interactor);

        presenter.initialise(wordSet);
        verify(view).setProgress(0);
        reset(view);

        // sentence 1
        presenter.nextButtonClick();
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        String wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        Sentence sentence = interactor.getCurrentSentence();
        shuffle(wordSet.getWords());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(33);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).onUpdateUserExp(1);
        reset(view);

        // sentence 2
        presenter.nextButtonClick();
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        sentence = interactor.getCurrentSentence();
        shuffle(wordSet.getWords());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(66);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).onUpdateUserExp(1);
        reset(view);

        // sentence 3
        presenter.nextButtonClick();
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        sentence = interactor.getCurrentSentence();
        shuffle(wordSet.getWords());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(100);
        verify(view, times(0)).setRightAnswer(sentence.getText());
        verify(view, times(0)).showNextButton();
        verify(view, times(0)).hideCheckButton();
        verify(view).showCongratulationMessage();
        verify(view).hideNextButton();
        verify(view).showCloseButton();
        verify(view).onUpdateUserExp(1);
        reset(view);

        for (WordRepetitionProgressMapping progressMapping : daoHelper.getWordRepetitionProgressDao().findAll()) {
            assertEquals(1, progressMapping.getRepetitionCounter());
        }
        assertEquals(3, daoHelper.getWordRepetitionProgressDao().findAll().size());
    }

    @Test
    public void testPracticeWordSet_completeOneSetAndRestartAfterEacheStep() throws JsonProcessingException, SQLException {
        Map<String, Integer> sentencesCounter = new HashMap<>();
        createPresenter(interactor);

        presenter.initialise(wordSet);
        verify(view).setProgress(0);
        reset(view);

        // sentence 1
        presenter.nextButtonClick();
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        String wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        Sentence sentence = interactor.getCurrentSentence();
        increaseCounter(sentencesCounter, sentence);
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(33);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).onUpdateUserExp(sentencesCounter.get(sentence.getText()));
        reset(view);

        // sentence 2
        presenter.nextButtonClick();
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        reset(view);

        createPresenter(interactor);

        presenter.initialise(wordSet);
        verify(view).setProgress(0);
        reset(view);

        presenter.nextButtonClick();
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        sentence = interactor.getCurrentSentence();
        increaseCounter(sentencesCounter, sentence);
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(33);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).onUpdateUserExp(sentencesCounter.get(sentence.getText()));
        reset(view);

        // sentence 3
        presenter.nextButtonClick();
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        reset(view);

        createPresenter(interactor);

        presenter.initialise(wordSet);
        verify(view).setProgress(0);
        reset(view);

        presenter.nextButtonClick();
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        sentence = interactor.getCurrentSentence();
        increaseCounter(sentencesCounter, sentence);
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(33);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).onUpdateUserExp(sentencesCounter.get(sentence.getText()));
        reset(view);

        // sentence 4
        presenter.nextButtonClick();
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        reset(view);

        createPresenter(interactor);

        presenter.initialise(wordSet);
        verify(view).setProgress(0);
        reset(view);

        presenter.nextButtonClick();
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        sentence = interactor.getCurrentSentence();
        increaseCounter(sentencesCounter, sentence);
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(33);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).onUpdateUserExp(sentencesCounter.get(sentence.getText()));
        reset(view);

        // sentence 5
        presenter.nextButtonClick();
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        reset(view);

        createPresenter(interactor);

        presenter.initialise(wordSet);
        verify(view).setProgress(0);
        reset(view);

        presenter.nextButtonClick();
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        sentence = interactor.getCurrentSentence();
        increaseCounter(sentencesCounter, sentence);
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(33);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).onUpdateUserExp(sentencesCounter.get(sentence.getText()));
        reset(view);

        // sentence 6
        presenter.nextButtonClick();
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        reset(view);

        createPresenter(interactor);

        presenter.initialise(wordSet);
        verify(view).setProgress(0);
        reset(view);

        presenter.nextButtonClick();
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        sentence = interactor.getCurrentSentence();
        increaseCounter(sentencesCounter, sentence);
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(33);
        verify(view, times(0)).showCongratulationMessage();
        verify(view).onUpdateUserExp(sentencesCounter.get(sentence.getText()));
        reset(view);
    }

    private void increaseCounter(Map<String, Integer> map, Sentence sentence) {
        Integer counter = map.get(sentence.getText());
        if (counter == null) {
            map.put(sentence.getText(), 1);
        } else {
            map.put(sentence.getText(), counter + 1);
        }
    }

    @Test
    public void testPracticeWordSet_rightAnswerCheckedTouchRightAnswerUntouchBug() throws JsonProcessingException, SQLException {
        createPresenter(interactor);

        presenter.initialise(wordSet);
        presenter.nextButtonClick();

        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        reset(view);

        Sentence sentence = interactor.getCurrentSentence();
        presenter.checkAnswerButtonClick(sentence.getText());

        verify(view).onExerciseGotAnswered();
    }
}
package talkapp.org.talkappmobile.activity.presenter;

import android.content.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.RepetitionPracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.StrategySwitcherDecorator;
import talkapp.org.talkappmobile.activity.interactor.impl.UserExperienceDecorator;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.dao.DatabaseHelper;
import talkapp.org.talkappmobile.mappings.SentenceIdMapping;
import talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;
import talkapp.org.talkappmobile.service.impl.AudioStuffFactoryBean;
import talkapp.org.talkappmobile.service.impl.EqualityScorerBean;
import talkapp.org.talkappmobile.service.impl.LoggerBean;
import talkapp.org.talkappmobile.service.impl.RefereeServiceImpl;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;
import talkapp.org.talkappmobile.service.impl.TextUtilsImpl;
import talkapp.org.talkappmobile.service.impl.WordSetExperienceUtilsImpl;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static com.j256.ormlite.android.apptools.OpenHelperManager.getHelper;
import static java.util.Arrays.asList;
import static java.util.Collections.shuffle;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class PracticeWordSetRepetitionPresenterAndInteractorIntegTest extends PresenterAndInteractorIntegTest {
    private PracticeWordSetView view;
    private PracticeWordSetPresenter presenter;
    private WordSet wordSet;
    private PracticeWordSetInteractor interactor;
    private Context context;
    private WordSetExperienceUtilsImpl experienceUtils;
    private RepetitionPracticeWordSetInteractor repetitionPracticeWordSetInteractor;
    private ServiceFactoryBean serviceFactory;

    @Before
    public void setup() throws SQLException {
        view = mock(PracticeWordSetView.class);
        context = mock(Context.class);

        ObjectMapper mapper = new ObjectMapper();
        LoggerBean logger = new LoggerBean();

        serviceFactory = new ServiceFactoryBean() {
            private DatabaseHelper helper;

            @Override
            protected DatabaseHelper databaseHelper() {
                if (helper != null) {
                    return helper;
                }
                helper = getHelper(RuntimeEnvironment.application, DatabaseHelper.class);
                return helper;
            }
        };
        serviceFactory.setContext(mock(Context.class));

        experienceUtils = new WordSetExperienceUtilsImpl();
        repetitionPracticeWordSetInteractor = new RepetitionPracticeWordSetInteractor(serviceFactory.getSentenceService(null), new RefereeServiceImpl(new EqualityScorerBean()),
                logger, serviceFactory.getPracticeWordSetExerciseRepository(), experienceUtils, serviceFactory.getSentenceProvider(), context, serviceFactory.getCurrentPracticeStateService(), new AudioStuffFactoryBean());
        this.interactor = new UserExperienceDecorator(repetitionPracticeWordSetInteractor, serviceFactory.getUserExpService(), serviceFactory.getCurrentPracticeStateService(), serviceFactory.getPracticeWordSetExerciseRepository());
        serviceFactory.getSentenceService(null).findSentencesByWordSetId(-1, 6);
    }

    @After
    public void tearDown() {
        OpenHelperManager.releaseHelper();
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
        serviceFactory.getExerciseDao().createNewOrUpdate(exercise);

        WordSet ageWordSet = new WordSet();
        ageWordSet.setId(ageWordSetId);
        ageWordSet.setStatus(WordSetProgressStatus.FINISHED);
        ageWordSet.setWords(new LinkedList<>(ageWordSetWords));
        ageWordSet.setTopicId("topicId");
        ageWordSet.setTrainingExperience(0);
        serviceFactory.getWordSetExperienceRepository().save(ageWordSet);

        int anniversaryWordSetId = id + 2;
        Word2Tokens anniversary = new Word2Tokens("anniversary", "anniversary", anniversaryWordSetId);
        List<Word2Tokens> anniversaryWordSetWords = asList(new Word2Tokens(), anniversary, new Word2Tokens());
        exercise = new WordRepetitionProgressMapping();
        exercise.setSentenceIds(getSentenceJSON(mapper, "AWbgbq6hNEXFMlzHK5Ul", anniversary.getWord(), 6));
        exercise.setStatus(WordSetProgressStatus.FINISHED.name());
        exercise.setUpdatedDate(new Date());
        exercise.setWordSetId(anniversaryWordSetId);
        exercise.setWordIndex(anniversaryWordSetWords.indexOf(anniversary));
        serviceFactory.getExerciseDao().createNewOrUpdate(exercise);

        WordSet anniversaryWordSet = new WordSet();
        anniversaryWordSet.setId(anniversaryWordSetId);
        anniversaryWordSet.setStatus(WordSetProgressStatus.FINISHED);
        anniversaryWordSet.setWords(new LinkedList<>(anniversaryWordSetWords));
        anniversaryWordSet.setTopicId("topicId");
        anniversaryWordSet.setTrainingExperience(0);
        serviceFactory.getWordSetExperienceRepository().save(anniversaryWordSet);

        int birthWordSetId = id + 3;
        Word2Tokens birth = new Word2Tokens("birth", "birth", birthWordSetId);
        List<Word2Tokens> birthWordSetWords = asList(new Word2Tokens(), new Word2Tokens(), birth);
        exercise = new WordRepetitionProgressMapping();
        exercise.setSentenceIds(getSentenceJSON(mapper, "AWbgbsUXNEXFMlzHK5V2", birth.getWord(), 6));
        exercise.setStatus(WordSetProgressStatus.FINISHED.name());
        exercise.setUpdatedDate(new Date());
        exercise.setWordSetId(birthWordSetId);
        exercise.setWordIndex(birthWordSetWords.indexOf(birth));
        serviceFactory.getExerciseDao().createNewOrUpdate(exercise);

        WordSet birthWordSet = new WordSet();
        birthWordSet.setId(birthWordSetId);
        birthWordSet.setStatus(WordSetProgressStatus.FINISHED);
        birthWordSet.setWords(new LinkedList<>(birthWordSetWords));
        birthWordSet.setTopicId("topicId");
        birthWordSet.setTrainingExperience(0);
        serviceFactory.getWordSetExperienceRepository().save(birthWordSet);

        wordSet.setWords(new LinkedList<>(asList(age, anniversary, birth)));
        wordSet.setTopicId("topicId");
        wordSet.setTrainingExperience(trainingExperience);
        wordSet.setStatus(status);
        PracticeWordSetViewStrategy firstCycleViewStrategy = new PracticeWordSetViewStrategy(view, new TextUtilsImpl(), new WordSetExperienceUtilsImpl());
        presenter = new PracticeWordSetPresenter(new StrategySwitcherDecorator(interactor, experienceUtils, serviceFactory.getPracticeWordSetExerciseRepository(), serviceFactory.getCurrentPracticeStateService()), firstCycleViewStrategy);
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

        for (WordRepetitionProgressMapping progressMapping : serviceFactory.getExerciseDao().findAll()) {
            assertEquals(1, progressMapping.getRepetitionCounter());
        }
        assertEquals(3, serviceFactory.getExerciseDao().findAll().size());
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
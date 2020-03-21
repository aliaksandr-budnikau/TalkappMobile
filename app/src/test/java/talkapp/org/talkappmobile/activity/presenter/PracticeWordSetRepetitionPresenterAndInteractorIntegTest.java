package talkapp.org.talkappmobile.activity.presenter;

import android.content.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import talkapp.org.talkappmobile.repository.RepositoryFactory;
import talkapp.org.talkappmobile.repository.RepositoryFactoryImpl;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordRepetitionProgress;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.impl.AudioStuffFactoryBean;
import talkapp.org.talkappmobile.service.impl.EqualityScorerBean;
import talkapp.org.talkappmobile.service.impl.LoggerBean;
import talkapp.org.talkappmobile.service.impl.RefereeServiceImpl;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static com.j256.ormlite.android.apptools.OpenHelperManager.getHelper;
import static java.util.Arrays.asList;
import static java.util.Collections.shuffle;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
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
    private RepetitionPracticeWordSetInteractor repetitionPracticeWordSetInteractor;
    private ServiceFactory serviceFactory;
    private RepositoryFactory repositoryFactory;

    @Before
    public void setup() throws SQLException {
        view = mock(PracticeWordSetView.class);
        context = mock(Context.class);

        LoggerBean logger = new LoggerBean();

        repositoryFactory = new RepositoryFactoryImpl(context) {
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
        serviceFactory = ServiceFactoryBean.getInstance(repositoryFactory);

        repetitionPracticeWordSetInteractor = new RepetitionPracticeWordSetInteractor(serviceFactory.getSentenceService(null), new RefereeServiceImpl(new EqualityScorerBean()),
                logger, serviceFactory.getWordRepetitionProgressService(), serviceFactory.getSentenceProvider(), context, serviceFactory.getCurrentPracticeStateService(), new AudioStuffFactoryBean());
        this.interactor = new UserExperienceDecorator(repetitionPracticeWordSetInteractor, serviceFactory.getUserExpService(), serviceFactory.getCurrentPracticeStateService(), serviceFactory.getWordRepetitionProgressService());
    }

    @After
    public void tearDown() {
        OpenHelperManager.releaseHelper();
        ServiceFactoryBean.removeInstance();
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
        String name = WordSetProgressStatus.FINISHED.name();

        ObjectMapper mapper = new ObjectMapper();

        int ageWordSetId = id + 1;
        Word2Tokens age = new Word2Tokens("age", "age", ageWordSetId);
        List<Word2Tokens> ageWordSetWords = asList(age, new Word2Tokens(), new Word2Tokens());
        int wordIndexAge = ageWordSetWords.indexOf(age);
        List<WordRepetitionProgress> ageProgress = repositoryFactory.getWordRepetitionProgressRepository().findByWordIndexAndByWordSetIdAndByStatus(wordIndexAge, ageWordSetId, name);
        WordRepetitionProgress exercise;
        if (ageProgress.isEmpty()) {
            exercise = new WordRepetitionProgress();
        } else {
            exercise = ageProgress.get(0);
        }
        exercise.setSentenceIds(asList("AWbgbnj1NEXFMlzHK5Rk"));
        exercise.setStatus(name);
        exercise.setUpdatedDate(new Date());
        exercise.setWordSetId(ageWordSetId);
        exercise.setWordIndex(wordIndexAge);
        repositoryFactory.getWordRepetitionProgressRepository().createNewOrUpdate(exercise);

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
        int wordIndexAnniversary = anniversaryWordSetWords.indexOf(anniversary);
        List<WordRepetitionProgress> anniversaryProgress = repositoryFactory.getWordRepetitionProgressRepository().findByWordIndexAndByWordSetIdAndByStatus(wordIndexAnniversary, anniversaryWordSetId, name);
        if (anniversaryProgress.isEmpty()) {
            exercise = new WordRepetitionProgress();
        } else {
            exercise = anniversaryProgress.get(0);
        }
        exercise.setSentenceIds(asList("AWoFiKcqDTAu_IiLfhod"));
        exercise.setStatus(name);
        exercise.setUpdatedDate(new Date());
        exercise.setWordSetId(anniversaryWordSetId);
        exercise.setWordIndex(wordIndexAnniversary);
        repositoryFactory.getWordRepetitionProgressRepository().createNewOrUpdate(exercise);

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
        int wordIndexBirth = birthWordSetWords.indexOf(birth);
        List<WordRepetitionProgress> birthProgress = repositoryFactory.getWordRepetitionProgressRepository().findByWordIndexAndByWordSetIdAndByStatus(wordIndexBirth, birthWordSetId, name);
        if (birthProgress.isEmpty()) {
            exercise = new WordRepetitionProgress();
        } else {
            exercise = birthProgress.get(0);
        }
        exercise.setSentenceIds(asList("AWoEEi8tDTAu_IiLecuS"));
        exercise.setStatus(WordSetProgressStatus.FINISHED.name());
        exercise.setUpdatedDate(new Date());
        exercise.setWordSetId(birthWordSetId);
        exercise.setWordIndex(wordIndexBirth);
        repositoryFactory.getWordRepetitionProgressRepository().createNewOrUpdate(exercise);

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
        PracticeWordSetViewStrategy firstCycleViewStrategy = new PracticeWordSetViewStrategy(view);
        presenter = new PracticeWordSetPresenter(new StrategySwitcherDecorator(interactor, serviceFactory.getWordRepetitionProgressService(), serviceFactory.getCurrentPracticeStateService()), firstCycleViewStrategy);
    }

    @Test
    public void testPracticeWordSet_completeOneSet() throws JsonProcessingException, SQLException {
        createPresenter(interactor);
        HashSet<Word2Tokens> historyOfWords = new HashSet<>();

        presenter.initialise(wordSet);
        verify(view).setProgress(0);
        reset(view);

        // sentence 1
        ArgumentCaptor<Word2Tokens> captor = ArgumentCaptor.forClass(Word2Tokens.class);
        presenter.nextButtonClick();
        verify(view).onSentencesFound(any(Sentence.class), captor.capture());
        reset(view);

        Word2Tokens currentWord = captor.getValue();
        if (historyOfWords.contains(currentWord)) {
            fail();
        }
        historyOfWords.add(currentWord);

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
        verify(view).onSentencesFound(any(Sentence.class), captor.capture());
        reset(view);

        currentWord = captor.getValue();
        if (historyOfWords.contains(currentWord)) {
            fail();
        }
        historyOfWords.add(currentWord);

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
        verify(view).onSentencesFound(any(Sentence.class), captor.capture());
        reset(view);

        currentWord = captor.getValue();
        if (historyOfWords.contains(currentWord)) {
            fail();
        }
        historyOfWords.add(currentWord);

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

        for (WordRepetitionProgress progressMapping : repositoryFactory.getWordRepetitionProgressRepository().findAll()) {
            assertEquals(1, progressMapping.getRepetitionCounter());
        }
        assertEquals(3, repositoryFactory.getWordRepetitionProgressRepository().findAll().size());
    }

    @Test
    public void testPracticeWordSet_completeOneSetAndRestartAfterEacheStep() throws JsonProcessingException, SQLException {
        Map<String, Integer> sentencesCounter = new HashMap<>();
        createPresenter(interactor);
        HashSet<Word2Tokens> historyOfWords = new HashSet<>();

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
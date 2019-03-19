package talkapp.org.talkappmobile.activity.presenter;

import android.content.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.LinkedList;

import talkapp.org.talkappmobile.activity.interactor.impl.RepetitionPracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.component.database.UserExpService;
import talkapp.org.talkappmobile.component.database.WordRepetitionProgressService;
import talkapp.org.talkappmobile.component.database.WordSetService;
import talkapp.org.talkappmobile.component.database.dao.SentenceDao;
import talkapp.org.talkappmobile.component.database.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.component.database.dao.WordSetDao;
import talkapp.org.talkappmobile.component.database.impl.WordRepetitionProgressServiceImpl;
import talkapp.org.talkappmobile.component.database.impl.WordSetServiceImpl;
import talkapp.org.talkappmobile.component.database.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.component.impl.AudioStuffFactoryBean;
import talkapp.org.talkappmobile.component.impl.BackendSentenceProviderStrategy;
import talkapp.org.talkappmobile.component.impl.EqualityScorerBean;
import talkapp.org.talkappmobile.component.impl.GrammarCheckServiceImpl;
import talkapp.org.talkappmobile.component.impl.LoggerBean;
import talkapp.org.talkappmobile.component.impl.RandomSentenceSelectorBean;
import talkapp.org.talkappmobile.component.impl.RefereeServiceImpl;
import talkapp.org.talkappmobile.component.impl.SentenceProviderImpl;
import talkapp.org.talkappmobile.component.impl.SentenceProviderRepetitionStrategy;
import talkapp.org.talkappmobile.component.impl.TextUtilsImpl;
import talkapp.org.talkappmobile.component.impl.WordSetExperienceUtilsImpl;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PracticeWordSetRepetitionPresenterAndInteractorIntegTest extends PresenterAndInteractorIntegTest {
    @Mock
    private PracticeWordSetView view;
    private PracticeWordSetPresenter presenter;
    private WordRepetitionProgressService exerciseService;
    @Mock
    private UserExpService userExpService;
    private WordSet wordSet;
    private RepetitionPracticeWordSetInteractor interactor;
    @Mock
    private Context context;
    private WordSetDao wordSetDao;
    private SentenceDao sentenceDao;
    private WordSetService experienceService;
    private WordSetExperienceUtilsImpl experienceUtils;
    private WordRepetitionProgressDao exerciseDao;

    @Before
    public void setup() {
        exerciseDao = provideWordRepetitionProgressDao();
        wordSetDao = provideWordSetDao();
        sentenceDao = provideSentenceDao();
        exerciseService = new WordRepetitionProgressServiceImpl(exerciseDao, wordSetDao, sentenceDao, new ObjectMapper());
        LoggerBean logger = new LoggerBean();
        experienceUtils = new WordSetExperienceUtilsImpl();
        experienceService = new WordSetServiceImpl(wordSetDao, experienceUtils);
        interactor = new RepetitionPracticeWordSetInteractor(new SentenceProviderImpl(new BackendSentenceProviderStrategy(getServer()), new SentenceProviderRepetitionStrategy(getServer(), exerciseService)),
                new RandomSentenceSelectorBean(), new RefereeServiceImpl(new GrammarCheckServiceImpl(getServer()), new EqualityScorerBean()),
                logger, exerciseService, userExpService, experienceService, experienceUtils, context, new AudioStuffFactoryBean());
        getServer().initLocalCacheOfAllSentencesForThisWordset(-1, 6);
    }

    private void createPresenter(RepetitionPracticeWordSetInteractor interactor) throws JsonProcessingException {
        int id = 0;
        int trainingExperience = 0;
        WordSetProgressStatus status = null;
        if (wordSet != null) {
            trainingExperience = wordSet.getTrainingExperience();
            status = wordSet.getStatus();
        }
        wordSet = new WordSet();
        wordSet.setId(id);
        wordSet.setStatus(WordSetProgressStatus.FINISHED);

        ObjectMapper mapper = new ObjectMapper();

        Word2Tokens age = new Word2Tokens("age");
        age.setTokens("age");
        WordRepetitionProgressMapping exercise = new WordRepetitionProgressMapping();
        exercise.setSentenceId("AWbgboVdNEXFMlzHK5SR#" + age.getWord() + "#6");
        exercise.setStatus(WordSetProgressStatus.FINISHED);
        exercise.setUpdatedDate(new Date());
        exercise.setWordJSON(mapper.writeValueAsString(age));
        exerciseDao.createNewOrUpdate(exercise);


        Word2Tokens anniversary = new Word2Tokens("anniversary");
        anniversary.setTokens("anniversary");
        exercise = new WordRepetitionProgressMapping();
        exercise.setSentenceId("AWbgbq6hNEXFMlzHK5Ul#" + anniversary.getWord() + "#6");
        exercise.setStatus(WordSetProgressStatus.FINISHED);
        exercise.setUpdatedDate(new Date());
        exercise.setWordJSON(mapper.writeValueAsString(anniversary));
        exerciseDao.createNewOrUpdate(exercise);


        Word2Tokens birth = new Word2Tokens("birth");
        birth.setTokens("birth");
        exercise = new WordRepetitionProgressMapping();
        exercise.setSentenceId("AWbgbsUXNEXFMlzHK5V2#" + birth.getWord() + "#6");
        exercise.setStatus(WordSetProgressStatus.FINISHED);
        exercise.setUpdatedDate(new Date());
        exercise.setWordJSON(mapper.writeValueAsString(birth));
        exerciseDao.createNewOrUpdate(exercise);


        wordSet.setWords(new LinkedList<>(asList(age, anniversary, birth)));
        wordSet.setTopicId("topicId");
        wordSet.setTrainingExperience(trainingExperience);
        wordSet.setStatus(status);
        PracticeWordSetViewStrategy firstCycleViewStrategy = new PracticeWordSetViewStrategy(view, new TextUtilsImpl(), new WordSetExperienceUtilsImpl());
        presenter = new PracticeWordSetPresenter(wordSet, interactor, firstCycleViewStrategy);
    }

    @Test
    public void testPracticeWordSet_completeOneSet() throws JsonProcessingException {
        createPresenter(interactor);

        presenter.initialise();
        verify(view).setProgress(0);
        reset(view);

        // sentence 1
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageAnswerEmpty();
        verify(view).setEnableCheckButton(true);
        reset(view);

        String wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableCheckButton(true);
        reset(view);

        Sentence sentence = interactor.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(33);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 2
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageAnswerEmpty();
        verify(view).setEnableCheckButton(true);
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableCheckButton(true);
        reset(view);

        sentence = interactor.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(66);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 3
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageAnswerEmpty();
        verify(view).setEnableCheckButton(true);
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableCheckButton(true);
        reset(view);

        sentence = interactor.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(100);
        verify(view, times(0)).setRightAnswer(sentence.getText());
        verify(view, times(0)).showNextButton();
        verify(view, times(0)).hideCheckButton();
        verify(view).showCongratulationMessage();
        verify(view).closeActivity();
        verify(view).openAnotherActivity();
        verify(view).setEnableCheckButton(true);
        reset(view);
    }

    @Test
    public void testPracticeWordSet_completeOneSetAndRestartAfterEacheStep() throws JsonProcessingException {
        createPresenter(interactor);

        presenter.initialise();
        verify(view).setProgress(0);
        reset(view);

        // sentence 1
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageAnswerEmpty();
        verify(view).setEnableCheckButton(true);
        reset(view);

        String wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableCheckButton(true);
        reset(view);

        Sentence sentence = interactor.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(33);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 2
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        createPresenter(interactor);

        presenter.initialise();
        verify(view).setProgress(0);
        reset(view);

        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageAnswerEmpty();
        verify(view).setEnableCheckButton(true);
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableCheckButton(true);
        reset(view);

        sentence = interactor.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(33);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 3
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        createPresenter(interactor);

        presenter.initialise();
        verify(view).setProgress(0);
        reset(view);

        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageAnswerEmpty();
        verify(view).setEnableCheckButton(true);
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableCheckButton(true);
        reset(view);

        sentence = interactor.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(33);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 4
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        createPresenter(interactor);

        presenter.initialise();
        verify(view).setProgress(0);
        reset(view);

        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageAnswerEmpty();
        verify(view).setEnableCheckButton(true);
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableCheckButton(true);
        reset(view);

        sentence = interactor.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(33);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 5
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        createPresenter(interactor);

        presenter.initialise();
        verify(view).setProgress(0);
        reset(view);

        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageAnswerEmpty();
        verify(view).setEnableCheckButton(true);
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableCheckButton(true);
        reset(view);

        sentence = interactor.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(33);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).setEnableCheckButton(true);
        reset(view);

        // sentence 6
        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        createPresenter(interactor);

        presenter.initialise();
        verify(view).setProgress(0);
        reset(view);

        presenter.nextButtonClick();
        verify(view).showPleaseWaitProgressBar();
        verify(view).setEnableNextButton(false);
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableNextButton(true);
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).setEnableCheckButton(false);
        verify(view).showMessageAnswerEmpty();
        verify(view).setEnableCheckButton(true);
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableCheckButton(true);
        reset(view);

        sentence = interactor.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setEnableCheckButton(false);
        verify(view).setProgress(33);
        verify(view, times(0)).showCongratulationMessage();
        verify(view, times(0)).closeActivity();
        verify(view, times(0)).openAnotherActivity();
        verify(view).setEnableCheckButton(true);
        reset(view);
    }

    @Test
    public void testPracticeWordSet_rightAnswerCheckedTouchRightAnswerUntouchBug() throws JsonProcessingException {
        createPresenter(interactor);

        presenter.initialise();
        presenter.nextButtonClick();

        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        reset(view);

        Sentence sentence = interactor.getCurrentSentence(wordSet.getId());
        presenter.checkAnswerButtonClick(sentence.getText());

        verify(view).onExerciseGotAnswered();
    }
}
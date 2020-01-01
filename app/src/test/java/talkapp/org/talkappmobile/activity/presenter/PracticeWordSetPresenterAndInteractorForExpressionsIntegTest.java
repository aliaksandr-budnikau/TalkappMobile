package talkapp.org.talkappmobile.activity.presenter;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.DaoHelper;
import talkapp.org.talkappmobile.activity.PresenterFactory;
import talkapp.org.talkappmobile.activity.presenter.decorator.IPracticeWordSetPresenter;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.dao.TopicDao;
import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.model.CurrentPracticeState;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.TextToken;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.UserExpService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.impl.AudioStuffFactoryBean;
import talkapp.org.talkappmobile.service.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.service.impl.CurrentPracticeStateServiceImpl;
import talkapp.org.talkappmobile.service.impl.EqualityScorerBean;
import talkapp.org.talkappmobile.service.impl.LocalDataServiceImpl;
import talkapp.org.talkappmobile.service.impl.LoggerBean;
import talkapp.org.talkappmobile.service.impl.RequestExecutor;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;
import talkapp.org.talkappmobile.service.impl.TextUtilsImpl;
import talkapp.org.talkappmobile.service.impl.UserExpServiceImpl;
import talkapp.org.talkappmobile.service.impl.WordRepetitionProgressServiceImpl;
import talkapp.org.talkappmobile.service.impl.WordSetExperienceUtilsImpl;
import talkapp.org.talkappmobile.service.impl.WordSetServiceImpl;
import talkapp.org.talkappmobile.service.mapper.ExpAuditMapper;
import talkapp.org.talkappmobile.service.mapper.WordSetMapper;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class PracticeWordSetPresenterAndInteractorForExpressionsIntegTest extends PresenterAndInteractorIntegTest {
    public static final String LOOK_FOR = "look for";
    public static final String MAKE_OUT = "make out";
    public static final String IN_FACT = "in fact";
    private PracticeWordSetView view;
    private IPracticeWordSetPresenter presenter;
    private WordRepetitionProgressService exerciseService;
    private UserExpService userExpService;
    private WordSet wordSet;
    private Context context;
    private WordSetService experienceService;
    private WordSetExperienceUtilsImpl experienceUtils;
    private PresenterFactory presenterFactory;
    private DaoHelper daoHelper;
    private WordSetDao wordSetDao;
    private WordSetMapper wordSetMapper;
    private CurrentPracticeStateService currentPracticeStateService;

    @Before
    public void setup() throws SQLException {
        view = mock(PracticeWordSetView.class);
        context = mock(Context.class);
        LoggerBean logger = new LoggerBean();
        ObjectMapper mapper = new ObjectMapper();
        daoHelper = new DaoHelper();
        LocalDataServiceImpl localDataService = new LocalDataServiceImpl(daoHelper.getWordSetDao(), mock(TopicDao.class), daoHelper.getSentenceDao(), mock(WordTranslationDao.class), mapper, logger);

        BackendServerFactoryBean factory = new BackendServerFactoryBean();
        Whitebox.setInternalState(factory, "logger", new LoggerBean());
        ServiceFactoryBean mockServiceFactoryBean = mock(ServiceFactoryBean.class);
        when(mockServiceFactoryBean.getLocalDataService()).thenReturn(localDataService);

        userExpService = new UserExpServiceImpl(daoHelper.getExpAuditDao(), mock(ExpAuditMapper.class));
        when(mockServiceFactoryBean.getUserExpService()).thenReturn(userExpService);

        exerciseService = new WordRepetitionProgressServiceImpl(daoHelper.getWordRepetitionProgressDao(), daoHelper.getWordSetDao(), daoHelper.getSentenceDao(), mapper);
        when(mockServiceFactoryBean.getPracticeWordSetExerciseRepository()).thenReturn(exerciseService);

        experienceUtils = new WordSetExperienceUtilsImpl();
        wordSetDao = daoHelper.getWordSetDao();
        wordSetMapper = new WordSetMapper(mapper);
        currentPracticeStateService = new CurrentPracticeStateServiceImpl(wordSetDao, mapper);
        when(mockServiceFactoryBean.getCurrentPracticeStateService()).thenReturn(currentPracticeStateService);
        experienceService = new WordSetServiceImpl(wordSetDao, daoHelper.getNewWordSetDraftDao(), mapper);
        when(mockServiceFactoryBean.getWordSetExperienceRepository()).thenReturn(experienceService);

        Whitebox.setInternalState(factory, "serviceFactory", mockServiceFactoryBean);
        Whitebox.setInternalState(factory, "requestExecutor", new RequestExecutor());
        DataServer server = factory.get();
        presenterFactory = new PresenterFactory();
        Whitebox.setInternalState(presenterFactory, "backendServerFactory", factory);
        Whitebox.setInternalState(presenterFactory, "serviceFactory", mockServiceFactoryBean);
        Whitebox.setInternalState(presenterFactory, "equalityScorer", new EqualityScorerBean());
        Whitebox.setInternalState(presenterFactory, "textUtils", new TextUtilsImpl());
        Whitebox.setInternalState(presenterFactory, "experienceUtils", experienceUtils);
        Whitebox.setInternalState(presenterFactory, "logger", logger);
        Whitebox.setInternalState(presenterFactory, "audioStuffFactory", new AudioStuffFactoryBean());

        server.findAllWordSets();

        HashMap<String, List<Sentence>> words2Sentences = new HashMap<>();

        Sentence sentence1 = new Sentence();
        sentence1.setId(LOOK_FOR + "fsdfsfs");
        sentence1.setText(LOOK_FOR);
        HashMap<String, String> translations1 = new HashMap<>();
        translations1.put("russian", "искать");
        sentence1.setTranslations(translations1);
        sentence1.setTokens(new LinkedList<TextToken>());
        words2Sentences.put(LOOK_FOR, asList(sentence1));

        Sentence sentence2 = new Sentence();
        sentence2.setId(MAKE_OUT + "fsdfsfs");
        sentence2.setText(MAKE_OUT);
        HashMap<String, String> translations2 = new HashMap<>();
        translations2.put("russian", "разглядеть, различить, разбирать");
        sentence2.setTranslations(translations2);
        sentence2.setTokens(new LinkedList<TextToken>());
        words2Sentences.put(MAKE_OUT, asList(sentence2));

        Sentence sentence3 = new Sentence();
        sentence3.setId(IN_FACT + "fsdfsfs");
        sentence3.setText(IN_FACT);
        HashMap<String, String> translations3 = new HashMap<>();
        translations3.put("russian", "разглядеть, различить, разбирать");
        sentence3.setTranslations(translations3);
        sentence3.setTokens(new LinkedList<TextToken>());
        words2Sentences.put(IN_FACT, asList(sentence3));

        localDataService.saveSentences(words2Sentences, 6);
    }

    @After
    public void tearDown() {
        daoHelper.releaseHelper();
    }

    private void createPresenter() {
        int id = -2;
        int trainingExperience = 0;
        WordSetProgressStatus status = FIRST_CYCLE;
        if (wordSet != null) {
            CurrentPracticeState currentPracticeState = currentPracticeStateService.get();
            trainingExperience = currentPracticeState.getWordSet().getTrainingExperience();
            status = currentPracticeState.getWordSet().getStatus();
        }
        wordSet = new WordSet();
        wordSet.setId(id);

        Word2Tokens lookFor = new Word2Tokens(LOOK_FOR, LOOK_FOR, wordSet.getId());
        Word2Tokens makeOut = new Word2Tokens(MAKE_OUT, MAKE_OUT, wordSet.getId());
        Word2Tokens inFact = new Word2Tokens(IN_FACT, IN_FACT, wordSet.getId());

        wordSet.setWords(asList(lookFor, makeOut, inFact));
        wordSet.setTopicId("topicId");
        wordSet.setTrainingExperience(trainingExperience);
        wordSet.setStatus(status);
        WordSetMapping wordSetMapping = wordSetMapper.toMapping(wordSet);
        wordSetDao.createNewOrUpdate(wordSetMapping);
        presenter = presenterFactory.create(view, context, false);
    }

    @Test
    public void testPracticeWordSet_completeOneSet() {
        createPresenter();

        presenter.initialise(wordSet);
        verify(view).setProgress(0);
        reset(view);

        // sentence 1
        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.changeSentence();

        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).showSentenceChangedSuccessfullyMessage();
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        String wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        Sentence sentence = presenter.getCurrentSentence();
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(16);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).onUpdateUserExp(1);
        reset(view);

        // sentence 2
        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.changeSentence();

        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).showSentenceChangedSuccessfullyMessage();

        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        sentence = presenter.getCurrentSentence();
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(33);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).onUpdateUserExp(1);
        reset(view);

        // sentence 3
        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.changeSentence();

        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).showSentenceChangedSuccessfullyMessage();

        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        sentence = presenter.getCurrentSentence();
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(50);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).onUpdateUserExp(1);
        reset(view);

        // sentence 4
        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        sentence = presenter.getCurrentSentence();
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(66);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).onUpdateUserExp(1);
        reset(view);

        // sentence 5
        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        sentence = presenter.getCurrentSentence();
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(83);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).onUpdateUserExp(1);
        reset(view);

        // sentence 6
        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        sentence = presenter.getCurrentSentence();
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(100);
        verify(view).showCongratulationMessage();
        verify(view).hideNextButton();
        verify(view).showCloseButton();
        verify(view).onUpdateUserExp(1);
        reset(view);
    }

    @Test
    public void testPracticeWordSet_completeOneSetAndRestartAfterEacheStep() {
        createPresenter();

        presenter.initialise(wordSet);
        verify(view).setProgress(0);
        reset(view);

        // sentence 1
        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.changeSentence();

        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).showSentenceChangedSuccessfullyMessage();

        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        String wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        Sentence sentence = presenter.getCurrentSentence();
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(16);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).onUpdateUserExp(1);
        reset(view);

        // sentence 2
        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        createPresenter();

        presenter.initialise(wordSet);
        verify(view).setProgress(16);
        reset(view);

        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.changeSentence();
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).showSentenceChangedSuccessfullyMessage();
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        sentence = presenter.getCurrentSentence();
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(33);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).onUpdateUserExp(1);
        reset(view);

        // sentence 3
        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        createPresenter();

        presenter.initialise(wordSet);
        verify(view).setProgress(33);
        reset(view);

        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.changeSentence();

        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).showSentenceChangedSuccessfullyMessage();

        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        sentence = presenter.getCurrentSentence();
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(50);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).onUpdateUserExp(1);
        reset(view);

        // sentence 4
        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        createPresenter();

        presenter.initialise(wordSet);
        verify(view).setProgress(50);
        reset(view);

        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        sentence = presenter.getCurrentSentence();
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(66);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).onUpdateUserExp(1);
        reset(view);

        // sentence 5
        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        createPresenter();

        presenter.initialise(wordSet);
        verify(view).setProgress(66);
        reset(view);

        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        sentence = presenter.getCurrentSentence();
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(83);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).onExerciseGotAnswered();
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).onUpdateUserExp(1);
        reset(view);

        // sentence 6
        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        createPresenter();

        presenter.initialise(wordSet);
        verify(view).setProgress(83);
        reset(view);

        presenter.nextButtonClick();
        verify(view).setEnableRightAnswerTextView(false);
        verify(view).setEnablePronounceRightAnswerButton(false);
        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        verify(view).setEnableRightAnswerTextView(true);
        verify(view).setEnablePronounceRightAnswerButton(true);
        reset(view);

        presenter.checkAnswerButtonClick("");
        verify(view).showMessageAnswerEmpty();
        reset(view);

        wrongWord = "Housd";
        presenter.checkAnswerButtonClick(wrongWord);
        reset(view);

        sentence = presenter.getCurrentSentence();
        presenter.checkAnswerButtonClick(sentence.getText());
        verify(view).setProgress(100);
        verify(view).showCongratulationMessage();
        verify(view).hideNextButton();
        verify(view).showCloseButton();
        verify(view).onUpdateUserExp(1);
        reset(view);
    }

    @Test
    public void testPracticeWordSet_rightAnswerCheckedTouchRightAnswerUntouchBug() {
        createPresenter();

        presenter.initialise(wordSet);
        presenter.nextButtonClick();

        verify(view).onSentencesFound(any(Sentence.class), any(Word2Tokens.class));
        reset(view);

        Sentence sentence = presenter.getCurrentSentence();
        presenter.checkAnswerButtonClick(sentence.getText());

        verify(view).onExerciseGotAnswered();
    }
}
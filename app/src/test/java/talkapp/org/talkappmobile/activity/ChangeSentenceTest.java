package talkapp.org.talkappmobile.activity;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.greenrobot.eventbus.EventBus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.DaoHelper;
import talkapp.org.talkappmobile.TestHelper;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.activity.custom.presenter.OriginalTextTextViewPresenter;
import talkapp.org.talkappmobile.activity.custom.view.OriginalTextTextViewView;
import talkapp.org.talkappmobile.dao.ExpAuditDao;
import talkapp.org.talkappmobile.dao.NewWordSetDraftDao;
import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.dao.TopicDao;
import talkapp.org.talkappmobile.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.events.ChangeSentenceOptionPickedEM;
import talkapp.org.talkappmobile.events.NewSentenceEM;
import talkapp.org.talkappmobile.events.SentenceWasPickedForChangeEM;
import talkapp.org.talkappmobile.events.SentencesWereFoundForChangeEM;
import talkapp.org.talkappmobile.events.UserExpUpdatedEM;
import talkapp.org.talkappmobile.events.WordSetPracticeFinishedEM;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;
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
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class ChangeSentenceTest {
    private WordRepetitionProgressService exerciseService;
    private UserExpService userExpService;
    private WordSetService experienceService;
    private WordSetExperienceUtilsImpl experienceUtils;
    private WordSet wordSet;
    private PracticeWordSetFragment practiceWordSetFragment;
    private OriginalTextTextViewPresenter originalTextTextViewPresenter;
    private TextView answerTextMock;
    private WordSetDao wordSetDaoMock;
    private SentenceDao sentenceDaoMock;
    private WordRepetitionProgressDao wordRepetitionProgressDaoMock;
    private NewWordSetDraftDao newWordSetDraftDaoMock;
    private ExpAuditDao expAuditDaoMock;
    private DaoHelper daoHelper;
    private TestHelper testHelper;
    private WordSetMapper wordSetMapper;

    @Before
    public void setup() throws SQLException {
        LoggerBean logger = new LoggerBean();
        ObjectMapper mapper = new ObjectMapper();
        testHelper = new TestHelper();
        daoHelper = new DaoHelper();
        wordSetDaoMock = daoHelper.getWordSetDao();
        sentenceDaoMock = daoHelper.getSentenceDao();
        LocalDataServiceImpl localDataService = new LocalDataServiceImpl(mock(TopicDao.class), sentenceDaoMock, mock(WordTranslationDao.class), mapper);

        BackendServerFactoryBean factory = new BackendServerFactoryBean();
        Whitebox.setInternalState(factory, "logger", new LoggerBean());
        ServiceFactoryBean mockServiceFactoryBean = mock(ServiceFactoryBean.class);
        when(mockServiceFactoryBean.getLocalDataService()).thenReturn(localDataService);

        WordTranslationServiceImpl wordTranslationService = new WordTranslationServiceImpl(mockServiceFactoryBean.getDataServer(), daoHelper.getWordTranslationDao(), wordSetDaoMock, mapper);
        when(mockServiceFactoryBean.getWordTranslationService()).thenReturn(wordTranslationService);
        expAuditDaoMock = daoHelper.getExpAuditDao();
        userExpService = new UserExpServiceImpl(expAuditDaoMock, mock(ExpAuditMapper.class));
        when(mockServiceFactoryBean.getUserExpService()).thenReturn(userExpService);

        wordRepetitionProgressDaoMock = daoHelper.getWordRepetitionProgressDao();
        exerciseService = new WordRepetitionProgressServiceImpl(wordRepetitionProgressDaoMock, daoHelper.getWordSetDao(), sentenceDaoMock, mapper);
        when(mockServiceFactoryBean.getPracticeWordSetExerciseRepository()).thenReturn(exerciseService);

        experienceUtils = new WordSetExperienceUtilsImpl();
        newWordSetDraftDaoMock = daoHelper.getNewWordSetDraftDao();
        wordSetMapper = new WordSetMapper(mapper);
        when(mockServiceFactoryBean.getWordSetExperienceRepository()).thenReturn(experienceService);
        when(mockServiceFactoryBean.getCurrentPracticeStateService()).thenReturn(new CurrentPracticeStateServiceImpl(daoHelper.getWordSetDao(), mapper));

        Whitebox.setInternalState(factory, "serviceFactory", mockServiceFactoryBean);
        Whitebox.setInternalState(factory, "requestExecutor", new RequestExecutor());
        DataServer server = factory.get();
        experienceService = new WordSetServiceImpl(server, daoHelper.getWordSetDao(), newWordSetDraftDaoMock, mapper);
        when(mockServiceFactoryBean.getSentenceService(server)).thenReturn(new SentenceServiceImpl(server, daoHelper.getWordSetDao(), daoHelper.getSentenceDao(), daoHelper.getWordRepetitionProgressDao(), mapper));

        PresenterFactory presenterFactory = new PresenterFactory();
        Whitebox.setInternalState(presenterFactory, "backendServerFactory", factory);
        Whitebox.setInternalState(presenterFactory, "serviceFactory", mockServiceFactoryBean);
        Whitebox.setInternalState(presenterFactory, "equalityScorer", new EqualityScorerBean());
        Whitebox.setInternalState(presenterFactory, "textUtils", new TextUtilsImpl());
        Whitebox.setInternalState(presenterFactory, "experienceUtils", experienceUtils);
        Whitebox.setInternalState(presenterFactory, "logger", logger);
        Whitebox.setInternalState(presenterFactory, "audioStuffFactory", new AudioStuffFactoryBean());

        server.findAllWordSets();
        wordSet = createWordSet(-1, "age");
        practiceWordSetFragment = new PracticeWordSetFragment();
        WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory = mock(WaitingForProgressBarManagerFactory.class);
        when(waitingForProgressBarManagerFactory.get(any(View.class), any(View.class))).thenReturn(mock(WaitingForProgressBarManager.class));
        Whitebox.setInternalState(practiceWordSetFragment, "waitingForProgressBarManagerFactory", waitingForProgressBarManagerFactory);
        Whitebox.setInternalState(practiceWordSetFragment, "wordSet", wordSet);
        Whitebox.setInternalState(practiceWordSetFragment, "presenterFactory", presenterFactory);
        Whitebox.setInternalState(practiceWordSetFragment, "originalText", mock(TextView.class));
        Whitebox.setInternalState(practiceWordSetFragment, "rightAnswer", mock(TextView.class));
        answerTextMock = mock(TextView.class);
        Whitebox.setInternalState(practiceWordSetFragment, "answerText", answerTextMock);
        Whitebox.setInternalState(practiceWordSetFragment, "wordSetProgress", mock(ProgressBar.class));
        Whitebox.setInternalState(practiceWordSetFragment, "nextButton", mock(Button.class));
        Whitebox.setInternalState(practiceWordSetFragment, "checkButton", mock(Button.class));
        Whitebox.setInternalState(practiceWordSetFragment, "closeButton", mock(Button.class));
        Whitebox.setInternalState(practiceWordSetFragment, "speakButton", mock(Button.class));
        Whitebox.setInternalState(practiceWordSetFragment, "playButton", mock(Button.class));
        Whitebox.setInternalState(practiceWordSetFragment, "pronounceRightAnswerButton", mock(Button.class));
        Whitebox.setInternalState(practiceWordSetFragment, "pleaseWaitProgressBar", mock(View.class));
        Whitebox.setInternalState(practiceWordSetFragment, "wordSetPractiseForm", mock(View.class));
        Whitebox.setInternalState(practiceWordSetFragment, "spellingGrammarErrorsListView", mock(LinearLayout.class));
        EventBus eventBus = testHelper.getEventBusMock();
        Whitebox.setInternalState(practiceWordSetFragment, "eventBus", eventBus);

        originalTextTextViewPresenter = new OriginalTextTextViewPresenter(mock(OriginalTextTextViewView.class));
    }

    private WordSet createWordSet(int id, WordSetProgressStatus status, String... words) {
        int trainingExperience = 0;
        WordSet wordSet = new WordSet();
        wordSet.setId(id);

        LinkedList<Word2Tokens> word2Tokens = new LinkedList<>();
        for (String word : words) {
            Word2Tokens age = new Word2Tokens(word, word, id);
            word2Tokens.add(age);
        }

        wordSet.setWords(word2Tokens);
        wordSet.setTopicId("topicId");
        wordSet.setTrainingExperience(trainingExperience);
        wordSet.setStatus(status);
        WordSetMapping wordSetMapping = wordSetMapper.toMapping(wordSet);
        wordSetDaoMock.createNewOrUpdate(wordSetMapping);
        return wordSet;
    }

    private WordSet createWordSet(int id, String... words) {
        return createWordSet(id, FIRST_CYCLE, words);
    }

    @After
    public void tearDown() {
        daoHelper.releaseHelper();
    }

    @Test
    public void testChangeOtherSentences() throws SQLException {
        EventBus eventBus = practiceWordSetFragment.eventBus;

        practiceWordSetFragment.init();

        // Test of showing a dialog where all sentences are checked in the beginning
        NewSentenceEM newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        Sentence displayedSentence = newSentenceEM.getSentence();
        Word2Tokens displayedWord = newSentenceEM.getWord();

        displayNewSentence(displayedSentence);
        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWord));

        SentencesWereFoundForChangeEM sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);

        originalTextTextViewPresenter.prepareSentencesForPicking(sentencesWereFoundForChangeEM.getSentences(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences(), displayedWord);

        assertEquals(16, sentencesWereFoundForChangeEM.getSentences().size());
        assertEquals(16, sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());
        assertEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());

        reset(eventBus);

        // Test of changing of sentence the same sentence should not be picked twice sequentially
        List<Sentence> pickedSentences = getPickedSentences(displayedSentence, sentencesWereFoundForChangeEM);
        SentenceWasPickedForChangeEM wasPickedForChangeEM = new SentenceWasPickedForChangeEM(pickedSentences, displayedWord);
        for (int i = 0; i < 10; i++) {
            practiceWordSetFragment.onMessageEvent(wasPickedForChangeEM);
            newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
            assertTrue(pickedSentences.contains(newSentenceEM.getSentence()));
            assertNotEquals(displayedSentence, newSentenceEM.getSentence());
            displayedSentence = newSentenceEM.getSentence();
            displayedWord = newSentenceEM.getWord();
        }
        displayNewSentence(displayedSentence);

        // Test of showing the dialog where only previously changed sentences are checked
        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWord));

        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);
        originalTextTextViewPresenter.prepareSentencesForPicking(sentencesWereFoundForChangeEM.getSentences(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences(), displayedWord);

        assertNotEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());
        assertEquals(2, sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());

        for (Sentence alreadyPickedSentence : sentencesWereFoundForChangeEM.getAlreadyPickedSentences()) {
            assertTrue(pickedSentences.contains(alreadyPickedSentence));
        }

        practiceWordSetFragment.onNextButtonClick();
        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        displayedSentence = newSentenceEM.getSentence();
        displayedWord = newSentenceEM.getWord();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWord));

        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);

        assertNotEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());


        //
        // START OF MOVING TO THE NEXT CYCLE
        //
        when(answerTextMock.getText()).thenReturn(displayedSentence.getText());
        reset(eventBus);
        practiceWordSetFragment.onCheckAnswerButtonClick();
        UserExpUpdatedEM expUpdatedEM = testHelper.getEM(UserExpUpdatedEM.class);
        assertNotNull(expUpdatedEM);
        assertEquals(1, expUpdatedEM.getNewExpScore(), 0);
        reset(eventBus);
        practiceWordSetFragment.onNextButtonClick();
        //
        // SECOND CYCLE
        //

        // Test of showing a dialog on the second cycle. Only previously changed sentences are checked
        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        displayedSentence = newSentenceEM.getSentence();
        displayedWord = newSentenceEM.getWord();

        displayNewSentence(displayedSentence);
        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWord));

        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);

        originalTextTextViewPresenter.prepareSentencesForPicking(sentencesWereFoundForChangeEM.getSentences(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences(), displayedWord);

        assertEquals(16, sentencesWereFoundForChangeEM.getSentences().size());
        assertEquals(2, sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());
        assertNotEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());

        // Test of picking all sentences on the second cycle
        practiceWordSetFragment.onMessageEvent(new SentenceWasPickedForChangeEM(sentencesWereFoundForChangeEM.getSentences(), displayedWord));

        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        displayedSentence = newSentenceEM.getSentence();
        displayedWord = newSentenceEM.getWord();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWord));

        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);

        assertEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());
        reset(eventBus);

        // Test of changing of sentence the same sentence should not be picked twice sequentially
        pickedSentences = getPickedSentences(displayedSentence, sentencesWereFoundForChangeEM);
        wasPickedForChangeEM = new SentenceWasPickedForChangeEM(pickedSentences, displayedWord);
        for (int i = 0; i < 10; i++) {
            practiceWordSetFragment.onMessageEvent(wasPickedForChangeEM);
            newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
            assertTrue(pickedSentences.contains(newSentenceEM.getSentence()));
            assertNotEquals(displayedSentence, newSentenceEM.getSentence());
            displayedSentence = newSentenceEM.getSentence();
            displayedWord = newSentenceEM.getWord();
        }
        displayNewSentence(displayedSentence);

        // Test of showing the dialog where only previously changed sentences are checked
        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWord));

        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);
        originalTextTextViewPresenter.prepareSentencesForPicking(sentencesWereFoundForChangeEM.getSentences(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences(), displayedWord);

        assertNotEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());
        assertEquals(2, sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());

        for (Sentence alreadyPickedSentence : sentencesWereFoundForChangeEM.getAlreadyPickedSentences()) {
            assertTrue(pickedSentences.contains(alreadyPickedSentence));
        }

        practiceWordSetFragment.onNextButtonClick();
        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        displayedSentence = newSentenceEM.getSentence();
        displayedWord = newSentenceEM.getWord();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWord));

        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);

        assertNotEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());

        when(answerTextMock.getText()).thenReturn(displayedSentence.getText());
        reset(eventBus);
        practiceWordSetFragment.onCheckAnswerButtonClick();
        expUpdatedEM = testHelper.getEM(UserExpUpdatedEM.class);
        assertNotNull(expUpdatedEM);
        assertEquals(1, expUpdatedEM.getNewExpScore(), 0);
        reset(eventBus);
        practiceWordSetFragment.onNextButtonClick();


        //
        // REPETITION MODE
        //

        Whitebox.setInternalState(practiceWordSetFragment, "repetitionMode", true);
        reset(eventBus);
        practiceWordSetFragment.init();

        // Test of showing a dialog in the repetition mode. Only previously changed sentences (2) are checked
        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 2);
        displayedSentence = newSentenceEM.getSentence();
        displayedWord = newSentenceEM.getWord();

        displayNewSentence(displayedSentence);
        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWord));

        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);

        originalTextTextViewPresenter.prepareSentencesForPicking(sentencesWereFoundForChangeEM.getSentences(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences(), displayedWord);

        assertEquals(16, sentencesWereFoundForChangeEM.getSentences().size());
        assertEquals(2, sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());
        assertNotEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());

        // Test of picking all sentences in the repetition mode
        practiceWordSetFragment.onMessageEvent(new SentenceWasPickedForChangeEM(sentencesWereFoundForChangeEM.getSentences(), displayedWord));

        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        displayedSentence = newSentenceEM.getSentence();
        displayedWord = newSentenceEM.getWord();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWord));

        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);

        assertEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());
        assertEquals(16, sentencesWereFoundForChangeEM.getSentences().size());
        assertEquals(16, sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());
        reset(eventBus);


        //
        // TEST OF SENTENCES CYCLICAL MOVEMENT FIRST STAGE
        //
        daoHelper.getWordRepetitionProgressDao().cleanByWordSetId(-1);
        reset(eventBus);

        wordSet = createWordSet(-1, "birth", "anniversary");
        Whitebox.setInternalState(practiceWordSetFragment, "repetitionMode", false);
        Whitebox.setInternalState(practiceWordSetFragment, "wordSet", wordSet);
        practiceWordSetFragment.init();

        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        Sentence displayedSentenceForFirstWord = newSentenceEM.getSentence();
        Word2Tokens displayedWordForFirstWord = newSentenceEM.getWord();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWordForFirstWord));
        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);
        List<Sentence> allSentencesForFirstWord = sentencesWereFoundForChangeEM.getSentences();
        List<Sentence> alreadyPickedSentencesForFirstWord = sentencesWereFoundForChangeEM.getAlreadyPickedSentences();
        assertEquals(allSentencesForFirstWord.size(), alreadyPickedSentencesForFirstWord.size());

        alreadyPickedSentencesForFirstWord = newArrayList(allSentencesForFirstWord.get(0), allSentencesForFirstWord.get(2), allSentencesForFirstWord.get(6));
        practiceWordSetFragment.onMessageEvent(new SentenceWasPickedForChangeEM(alreadyPickedSentencesForFirstWord, displayedWordForFirstWord));

        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        displayedSentenceForFirstWord = newSentenceEM.getSentence();
        displayedWordForFirstWord = newSentenceEM.getWord();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWordForFirstWord));
        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);
        alreadyPickedSentencesForFirstWord = sentencesWereFoundForChangeEM.getAlreadyPickedSentences();

        when(answerTextMock.getText()).thenReturn(displayedSentenceForFirstWord.getText());
        reset(eventBus);
        practiceWordSetFragment.onCheckAnswerButtonClick();
        expUpdatedEM = testHelper.getEM(UserExpUpdatedEM.class);
        assertNotNull(expUpdatedEM);
        assertEquals(1, expUpdatedEM.getNewExpScore(), 0);
        reset(eventBus);
        practiceWordSetFragment.onNextButtonClick();

        // finished the first word for the first cycle

        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        Sentence displayedSentenceForSecondWord = newSentenceEM.getSentence();
        Word2Tokens displayedWordForSecondWord = newSentenceEM.getWord();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWordForSecondWord));
        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);
        List<Sentence> allSentencesForSecondWord = sentencesWereFoundForChangeEM.getSentences();
        List<Sentence> alreadyPickedSentencesForSecondWord = sentencesWereFoundForChangeEM.getAlreadyPickedSentences();
        assertEquals(allSentencesForSecondWord.size(), alreadyPickedSentencesForSecondWord.size());

        alreadyPickedSentencesForSecondWord = newArrayList(allSentencesForSecondWord.get(0), allSentencesForSecondWord.get(6));
        practiceWordSetFragment.onMessageEvent(new SentenceWasPickedForChangeEM(alreadyPickedSentencesForSecondWord, displayedWordForSecondWord));

        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        displayedSentenceForSecondWord = newSentenceEM.getSentence();
        displayedWordForSecondWord = newSentenceEM.getWord();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWordForSecondWord));
        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);
        alreadyPickedSentencesForSecondWord = sentencesWereFoundForChangeEM.getAlreadyPickedSentences();

        when(answerTextMock.getText()).thenReturn(displayedSentenceForSecondWord.getText());
        reset(eventBus);
        practiceWordSetFragment.onCheckAnswerButtonClick();
        expUpdatedEM = testHelper.getEM(UserExpUpdatedEM.class);
        assertNotNull(expUpdatedEM);
        assertEquals(1, expUpdatedEM.getNewExpScore(), 0);
        reset(eventBus);
        practiceWordSetFragment.onNextButtonClick();

        // finished the second word for the first cycle

        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        assertEquals(displayedSentenceForFirstWord, newSentenceEM.getSentence());
        assertEquals(displayedWordForFirstWord, newSentenceEM.getWord());

        //
        // TEST OF SENTENCES CYCLICAL MOVEMENT SECOND STAGE
        //

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWordForFirstWord));
        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);
        assertEquals(allSentencesForFirstWord, sentencesWereFoundForChangeEM.getSentences());

        assertEquals(alreadyPickedSentencesForFirstWord, sentencesWereFoundForChangeEM.getAlreadyPickedSentences());
        assertEquals(3, alreadyPickedSentencesForFirstWord.size());
        assertEquals(3, sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());

        when(answerTextMock.getText()).thenReturn(displayedSentenceForFirstWord.getText());
        reset(eventBus);
        practiceWordSetFragment.onCheckAnswerButtonClick();
        expUpdatedEM = testHelper.getEM(UserExpUpdatedEM.class);
        assertNotNull(expUpdatedEM);
        assertEquals(1, expUpdatedEM.getNewExpScore(), 0);
        reset(eventBus);
        practiceWordSetFragment.onNextButtonClick();

        // finished the first word for the second cycle

        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        assertEquals(displayedSentenceForSecondWord, newSentenceEM.getSentence());
        assertEquals(displayedWordForSecondWord, newSentenceEM.getWord());

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWordForSecondWord));
        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);
        assertEquals(allSentencesForSecondWord, sentencesWereFoundForChangeEM.getSentences());

        assertEquals(alreadyPickedSentencesForSecondWord, sentencesWereFoundForChangeEM.getAlreadyPickedSentences());
        assertEquals(2, alreadyPickedSentencesForSecondWord.size());
        assertEquals(2, sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWordForSecondWord));
        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);
        allSentencesForSecondWord = sentencesWereFoundForChangeEM.getSentences();
        alreadyPickedSentencesForSecondWord = sentencesWereFoundForChangeEM.getAlreadyPickedSentences();

        alreadyPickedSentencesForSecondWord = newArrayList(allSentencesForSecondWord.get(0), allSentencesForSecondWord.get(2), allSentencesForSecondWord.get(6));
        practiceWordSetFragment.onMessageEvent(new SentenceWasPickedForChangeEM(alreadyPickedSentencesForSecondWord, displayedWordForSecondWord));

        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        displayedSentenceForSecondWord = newSentenceEM.getSentence();
        displayedWordForSecondWord = newSentenceEM.getWord();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWordForSecondWord));
        sentencesWereFoundForChangeEM = testHelper.getEM(SentencesWereFoundForChangeEM.class, 1);
        allSentencesForSecondWord = sentencesWereFoundForChangeEM.getSentences();
        alreadyPickedSentencesForSecondWord = sentencesWereFoundForChangeEM.getAlreadyPickedSentences();

        when(answerTextMock.getText()).thenReturn(displayedSentenceForSecondWord.getText());
        reset(eventBus);
        practiceWordSetFragment.onCheckAnswerButtonClick();
        expUpdatedEM = testHelper.getEM(UserExpUpdatedEM.class);
        assertNotNull(expUpdatedEM);
        assertEquals(1, expUpdatedEM.getNewExpScore(), 0);
        reset(eventBus);
        practiceWordSetFragment.onNextButtonClick();

        // finished the second word for the second cycle

        //
        // REPETITION MODE
        //
        Whitebox.setInternalState(practiceWordSetFragment, "repetitionMode", true);
        reset(eventBus);
        practiceWordSetFragment.init();


        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 2);
        while (!displayedWordForFirstWord.equals(newSentenceEM.getWord())) {
            practiceWordSetFragment.onNextButtonClick();
            newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        }
        assertNotEquals(displayedSentenceForFirstWord, newSentenceEM.getSentence());
        assertEquals(alreadyPickedSentencesForFirstWord.get(1), newSentenceEM.getSentence());

        displayedSentenceForFirstWord = newSentenceEM.getSentence();
        when(answerTextMock.getText()).thenReturn(displayedSentenceForFirstWord.getText());
        reset(eventBus);
        practiceWordSetFragment.onCheckAnswerButtonClick();
        expUpdatedEM = testHelper.getEM(UserExpUpdatedEM.class);
        assertNotNull(expUpdatedEM);
        assertEquals(1, expUpdatedEM.getNewExpScore(), 0);
        reset(eventBus);
        practiceWordSetFragment.onNextButtonClick();

        newSentenceEM = testHelper.getEM(NewSentenceEM.class, 1);
        assertNotEquals(displayedSentenceForSecondWord, newSentenceEM.getSentence());
        assertEquals(alreadyPickedSentencesForSecondWord.get(1), newSentenceEM.getSentence());

        displayedSentenceForSecondWord = newSentenceEM.getSentence();
        when(answerTextMock.getText()).thenReturn(displayedSentenceForSecondWord.getText());
        reset(eventBus);
        practiceWordSetFragment.onCheckAnswerButtonClick();
        expUpdatedEM = testHelper.getEM(UserExpUpdatedEM.class);
        assertNotNull(expUpdatedEM);
        assertEquals(1, expUpdatedEM.getNewExpScore(), 0);
        reset(eventBus);
        practiceWordSetFragment.onNextButtonClick();
        reset(eventBus);

        practiceWordSetFragment.onCloseButtonClick();

        WordSetPracticeFinishedEM wordSetPracticeFinishedEM = testHelper.getEM(WordSetPracticeFinishedEM.class, 1);
        assertNotNull(wordSetPracticeFinishedEM);

        //
        // REPETITION MODE SECOND TIME
        //
        wordSet = createWordSet(-1, WordSetProgressStatus.FINISHED, "birth", "anniversary");
        Whitebox.setInternalState(practiceWordSetFragment, "wordSet", wordSet);
        Whitebox.setInternalState(practiceWordSetFragment, "repetitionMode", true);
        reset(eventBus);
        practiceWordSetFragment.init();

        newSentenceEM = testHelper.getEM(NewSentenceEM.class);
        displayedSentenceForSecondWord = newSentenceEM.getSentence();
        when(answerTextMock.getText()).thenReturn(displayedSentenceForSecondWord.getText());
        reset(eventBus);
        practiceWordSetFragment.onCheckAnswerButtonClick();
        expUpdatedEM = testHelper.getEM(UserExpUpdatedEM.class);
        assertNotNull(expUpdatedEM);
        assertEquals(2, expUpdatedEM.getNewExpScore(), 0);
        reset(eventBus);
        practiceWordSetFragment.onNextButtonClick();

        newSentenceEM = testHelper.getEM(NewSentenceEM.class);
        displayedSentenceForSecondWord = newSentenceEM.getSentence();
        when(answerTextMock.getText()).thenReturn(displayedSentenceForSecondWord.getText());
        reset(eventBus);
        practiceWordSetFragment.onCheckAnswerButtonClick();
        expUpdatedEM = testHelper.getEM(UserExpUpdatedEM.class);
        assertNotNull(expUpdatedEM);
        assertEquals(2, expUpdatedEM.getNewExpScore(), 0);
        reset(eventBus);
        practiceWordSetFragment.onNextButtonClick();

        practiceWordSetFragment.onCloseButtonClick();
    }

    @NonNull
    private List<Sentence> getPickedSentences(Sentence displayedSentence, SentencesWereFoundForChangeEM sentencesWereFoundForChangeEM) {
        List<Sentence> sentences = sentencesWereFoundForChangeEM.getSentences();
        return asList(sentences.indexOf(displayedSentence) == 5 ? sentences.get(6) : sentences.get(5), displayedSentence);
    }

    private void displayNewSentence(Sentence displayedSentence) {
        originalTextTextViewPresenter.setModel(displayedSentence);
        originalTextTextViewPresenter.unlock();
        originalTextTextViewPresenter.refresh();
    }
}
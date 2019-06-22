package talkapp.org.talkappmobile.activity;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.greenrobot.eventbus.EventBus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.activity.custom.presenter.OriginalTextTextViewPresenter;
import talkapp.org.talkappmobile.activity.custom.view.OriginalTextTextViewView;
import talkapp.org.talkappmobile.activity.event.wordset.ChangeSentenceOptionPickedEM;
import talkapp.org.talkappmobile.activity.event.wordset.NewSentenceEM;
import talkapp.org.talkappmobile.activity.event.wordset.SentenceWasPickedForChangeEM;
import talkapp.org.talkappmobile.activity.event.wordset.SentencesWereFoundForChangeEM;
import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.component.backend.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.component.backend.impl.RequestExecutor;
import talkapp.org.talkappmobile.component.database.DatabaseHelper;
import talkapp.org.talkappmobile.component.database.UserExpService;
import talkapp.org.talkappmobile.component.database.WordRepetitionProgressService;
import talkapp.org.talkappmobile.component.database.WordSetService;
import talkapp.org.talkappmobile.component.database.dao.ExpAuditDao;
import talkapp.org.talkappmobile.component.database.dao.SentenceDao;
import talkapp.org.talkappmobile.component.database.dao.TopicDao;
import talkapp.org.talkappmobile.component.database.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.component.database.dao.WordSetDao;
import talkapp.org.talkappmobile.component.database.dao.WordTranslationDao;
import talkapp.org.talkappmobile.component.database.dao.impl.ExpAuditDaoImpl;
import talkapp.org.talkappmobile.component.database.dao.impl.WordRepetitionProgressDaoImpl;
import talkapp.org.talkappmobile.component.database.dao.impl.local.SentenceDaoImpl;
import talkapp.org.talkappmobile.component.database.dao.impl.local.WordSetDaoImpl;
import talkapp.org.talkappmobile.component.database.impl.LocalDataServiceImpl;
import talkapp.org.talkappmobile.component.database.impl.ServiceFactoryBean;
import talkapp.org.talkappmobile.component.database.impl.UserExpServiceImpl;
import talkapp.org.talkappmobile.component.database.impl.WordRepetitionProgressServiceImpl;
import talkapp.org.talkappmobile.component.database.impl.WordSetServiceImpl;
import talkapp.org.talkappmobile.component.database.mappings.ExpAuditMapping;
import talkapp.org.talkappmobile.component.database.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.component.database.mappings.local.SentenceMapping;
import talkapp.org.talkappmobile.component.database.mappings.local.WordSetMapping;
import talkapp.org.talkappmobile.component.impl.AudioStuffFactoryBean;
import talkapp.org.talkappmobile.component.impl.EqualityScorerBean;
import talkapp.org.talkappmobile.component.impl.LoggerBean;
import talkapp.org.talkappmobile.component.impl.RandomSentenceSelectorBean;
import talkapp.org.talkappmobile.component.impl.RandomWordsCombinatorBean;
import talkapp.org.talkappmobile.component.impl.TextUtilsImpl;
import talkapp.org.talkappmobile.component.impl.WordSetExperienceUtilsImpl;
import talkapp.org.talkappmobile.component.view.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.component.view.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.component.database.dao.impl")
public class ChangeSentenceTest {
    private WordRepetitionProgressService exerciseService;
    private UserExpService userExpService;
    private WordSetService experienceService;
    private WordSetExperienceUtilsImpl experienceUtils;
    private PresenterFactory presenterFactory;
    private WordSet wordSet;
    private PracticeWordSetFragment practiceWordSetFragment;
    private OriginalTextTextViewPresenter originalTextTextViewPresenter;
    private TextView answerTextMock;
    private WordRepetitionProgressDao exerciseDao;


    @Before
    public void setup() throws SQLException {
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(RuntimeEnvironment.application, DatabaseHelper.class);
        SentenceDao sentenceDao = new SentenceDaoImpl(databaseHelper.getConnectionSource(), SentenceMapping.class);
        WordSetDao wordSetDao = new WordSetDaoImpl(databaseHelper.getConnectionSource(), WordSetMapping.class);
        ExpAuditDao expAuditDao = new ExpAuditDaoImpl(databaseHelper.getConnectionSource(), ExpAuditMapping.class);
        exerciseDao = new WordRepetitionProgressDaoImpl(databaseHelper.getConnectionSource(), WordRepetitionProgressMapping.class);

        LoggerBean logger = new LoggerBean();
        ObjectMapper mapper = new ObjectMapper();
        LocalDataServiceImpl localDataService = new LocalDataServiceImpl(wordSetDao, mock(TopicDao.class), sentenceDao, mock(WordTranslationDao.class), mapper, logger);

        BackendServerFactoryBean factory = new BackendServerFactoryBean();
        Whitebox.setInternalState(factory, "logger", new LoggerBean());
        ServiceFactoryBean mockServiceFactoryBean = mock(ServiceFactoryBean.class);
        when(mockServiceFactoryBean.getLocalDataService()).thenReturn(localDataService);

        userExpService = new UserExpServiceImpl(expAuditDao);
        when(mockServiceFactoryBean.getUserExpService()).thenReturn(userExpService);

        exerciseService = new WordRepetitionProgressServiceImpl(exerciseDao, wordSetDao, sentenceDao, mapper);
        when(mockServiceFactoryBean.getPracticeWordSetExerciseRepository()).thenReturn(exerciseService);

        experienceUtils = new WordSetExperienceUtilsImpl();
        experienceService = new WordSetServiceImpl(wordSetDao, experienceUtils);
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
        Whitebox.setInternalState(presenterFactory, "wordsCombinator", new RandomWordsCombinatorBean());
        Whitebox.setInternalState(presenterFactory, "sentenceSelector", new RandomSentenceSelectorBean());
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
        EventBus eventBus = mock(EventBus.class);
        Whitebox.setInternalState(practiceWordSetFragment, "eventBus", eventBus);

        originalTextTextViewPresenter = new OriginalTextTextViewPresenter(mock(OriginalTextTextViewView.class));
    }

    private WordSet createWordSet(int id, String... words) {
        int trainingExperience = 0;
        WordSet wordSet = new WordSet();
        wordSet.setId(id);

        LinkedList<Word2Tokens> word2Tokens = new LinkedList<>();
        for (String word : words) {
            Word2Tokens age = new Word2Tokens(word);
            age.setTokens(word);
            age.setSourceWordSetId(id);
            word2Tokens.add(age);
        }

        wordSet.setWords(word2Tokens);
        wordSet.setTopicId("topicId");
        wordSet.setTrainingExperience(trainingExperience);
        return wordSet;
    }

    @After
    public void tearDown() {
        OpenHelperManager.releaseHelper();
    }

    @Test
    public void testChangeOtherSentences() {
        EventBus eventBus = practiceWordSetFragment.eventBus;

        practiceWordSetFragment.init();

        // Test of showing a dialog where all sentences are checked in the beginning
        NewSentenceEM newSentenceEM = getEM(NewSentenceEM.class, eventBus);
        Sentence displayedSentence = newSentenceEM.getSentence();
        Word2Tokens displayedWord = newSentenceEM.getWord();

        displayNewSentence(displayedSentence);
        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWord));

        SentencesWereFoundForChangeEM sentencesWereFoundForChangeEM = getEM(SentencesWereFoundForChangeEM.class, eventBus);

        originalTextTextViewPresenter.prepareSentencesForPicking(sentencesWereFoundForChangeEM.getSentences(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences(), displayedWord);

        assertEquals(16, sentencesWereFoundForChangeEM.getSentences().size());
        assertEquals(16, sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());
        assertEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());

        reset(eventBus);

        // Test of changing of sentence the same sentence should not be picked twice sequentially
        List<Sentence> pickedSentences = asList(sentencesWereFoundForChangeEM.getSentences().get(5), displayedSentence);
        SentenceWasPickedForChangeEM wasPickedForChangeEM = new SentenceWasPickedForChangeEM(pickedSentences, displayedWord);
        for (int i = 0; i < 10; i++) {
            practiceWordSetFragment.onMessageEvent(wasPickedForChangeEM);
            newSentenceEM = getEM(NewSentenceEM.class, eventBus);
            assertTrue(pickedSentences.contains(newSentenceEM.getSentence()));
            assertNotEquals(displayedSentence, newSentenceEM.getSentence());
            displayedSentence = newSentenceEM.getSentence();
            displayedWord = newSentenceEM.getWord();
        }
        displayNewSentence(displayedSentence);

        // Test of showing the dialog where only previously changed sentences are checked
        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWord));

        sentencesWereFoundForChangeEM = getEM(SentencesWereFoundForChangeEM.class, eventBus);
        originalTextTextViewPresenter.prepareSentencesForPicking(sentencesWereFoundForChangeEM.getSentences(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences(), displayedWord);

        assertNotEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());
        assertEquals(2, sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());

        for (Sentence alreadyPickedSentence : sentencesWereFoundForChangeEM.getAlreadyPickedSentences()) {
            assertTrue(pickedSentences.contains(alreadyPickedSentence));
        }

        practiceWordSetFragment.onNextButtonClick();
        newSentenceEM = getEM(NewSentenceEM.class, eventBus);
        displayedSentence = newSentenceEM.getSentence();
        displayedWord = newSentenceEM.getWord();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWord));

        sentencesWereFoundForChangeEM = getEM(SentencesWereFoundForChangeEM.class, eventBus);

        assertNotEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());


        //
        // START OF MOVING TO THE NEXT CYCLE
        //
        when(answerTextMock.getText()).thenReturn(displayedSentence.getText());
        practiceWordSetFragment.onCheckAnswerButtonClick();
        reset(eventBus);
        practiceWordSetFragment.onNextButtonClick();
        //
        // SECOND CYCLE
        //

        // Test of showing a dialog on the second cycle. Only previously changed sentences are checked
        newSentenceEM = getEM(NewSentenceEM.class, eventBus);
        displayedSentence = newSentenceEM.getSentence();
        displayedWord = newSentenceEM.getWord();

        displayNewSentence(displayedSentence);
        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWord));

        sentencesWereFoundForChangeEM = getEM(SentencesWereFoundForChangeEM.class, eventBus);

        originalTextTextViewPresenter.prepareSentencesForPicking(sentencesWereFoundForChangeEM.getSentences(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences(), displayedWord);

        assertEquals(16, sentencesWereFoundForChangeEM.getSentences().size());
        assertEquals(2, sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());
        assertNotEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());

        // Test of picking all sentences on the second cycle
        practiceWordSetFragment.onMessageEvent(new SentenceWasPickedForChangeEM(sentencesWereFoundForChangeEM.getSentences(), displayedWord));

        newSentenceEM = getEM(NewSentenceEM.class, eventBus);
        displayedSentence = newSentenceEM.getSentence();
        displayedWord = newSentenceEM.getWord();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWord));

        sentencesWereFoundForChangeEM = getEM(SentencesWereFoundForChangeEM.class, eventBus);

        assertEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());
        reset(eventBus);

        // Test of changing of sentence the same sentence should not be picked twice sequentially
        pickedSentences = asList(sentencesWereFoundForChangeEM.getSentences().get(5), displayedSentence);
        wasPickedForChangeEM = new SentenceWasPickedForChangeEM(pickedSentences, displayedWord);
        for (int i = 0; i < 10; i++) {
            practiceWordSetFragment.onMessageEvent(wasPickedForChangeEM);
            newSentenceEM = getEM(NewSentenceEM.class, eventBus);
            assertTrue(pickedSentences.contains(newSentenceEM.getSentence()));
            assertNotEquals(displayedSentence, newSentenceEM.getSentence());
            displayedSentence = newSentenceEM.getSentence();
            displayedWord = newSentenceEM.getWord();
        }
        displayNewSentence(displayedSentence);

        // Test of showing the dialog where only previously changed sentences are checked
        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWord));

        sentencesWereFoundForChangeEM = getEM(SentencesWereFoundForChangeEM.class, eventBus);
        originalTextTextViewPresenter.prepareSentencesForPicking(sentencesWereFoundForChangeEM.getSentences(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences(), displayedWord);

        assertNotEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());
        assertEquals(2, sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());

        for (Sentence alreadyPickedSentence : sentencesWereFoundForChangeEM.getAlreadyPickedSentences()) {
            assertTrue(pickedSentences.contains(alreadyPickedSentence));
        }

        practiceWordSetFragment.onNextButtonClick();
        newSentenceEM = getEM(NewSentenceEM.class, eventBus);
        displayedSentence = newSentenceEM.getSentence();
        displayedWord = newSentenceEM.getWord();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWord));

        sentencesWereFoundForChangeEM = getEM(SentencesWereFoundForChangeEM.class, eventBus);

        assertNotEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());

        when(answerTextMock.getText()).thenReturn(displayedSentence.getText());
        practiceWordSetFragment.onCheckAnswerButtonClick();
        reset(eventBus);
        practiceWordSetFragment.onNextButtonClick();


        //
        // REPETITION MODE
        //

        Whitebox.setInternalState(practiceWordSetFragment, "repetitionMode", true);
        reset(eventBus);
        practiceWordSetFragment.init();

        // Test of showing a dialog in the repetition mode. Only previously changed sentences (2) are checked
        newSentenceEM = getEM(NewSentenceEM.class, eventBus, 2);
        displayedSentence = newSentenceEM.getSentence();
        displayedWord = newSentenceEM.getWord();

        displayNewSentence(displayedSentence);
        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWord));

        sentencesWereFoundForChangeEM = getEM(SentencesWereFoundForChangeEM.class, eventBus);

        originalTextTextViewPresenter.prepareSentencesForPicking(sentencesWereFoundForChangeEM.getSentences(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences(), displayedWord);

        assertEquals(16, sentencesWereFoundForChangeEM.getSentences().size());
        assertEquals(2, sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());
        assertNotEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());

        // Test of picking all sentences in the repetition mode
        practiceWordSetFragment.onMessageEvent(new SentenceWasPickedForChangeEM(sentencesWereFoundForChangeEM.getSentences(), displayedWord));

        newSentenceEM = getEM(NewSentenceEM.class, eventBus);
        displayedSentence = newSentenceEM.getSentence();
        displayedWord = newSentenceEM.getWord();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWord));

        sentencesWereFoundForChangeEM = getEM(SentencesWereFoundForChangeEM.class, eventBus);

        assertEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());
        assertEquals(16, sentencesWereFoundForChangeEM.getSentences().size());
        assertEquals(16, sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());
        reset(eventBus);


        //
        // TEST OF SENTENCES CYCLICAL MOVEMENT FIRST STAGE
        //
        exerciseDao.cleanByWordSetId(-1);
        reset(eventBus);

        wordSet = createWordSet(-1, "birth", "anniversary");
        Whitebox.setInternalState(practiceWordSetFragment, "repetitionMode", false);
        Whitebox.setInternalState(practiceWordSetFragment, "wordSet", wordSet);
        practiceWordSetFragment.init();

        newSentenceEM = getEM(NewSentenceEM.class, eventBus);
        Sentence displayedSentenceForFirstWord = newSentenceEM.getSentence();
        Word2Tokens displayedWordForFirstWord = newSentenceEM.getWord();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWordForFirstWord));
        sentencesWereFoundForChangeEM = getEM(SentencesWereFoundForChangeEM.class, eventBus);
        List<Sentence> allSentencesForFirstWord = sentencesWereFoundForChangeEM.getSentences();
        List<Sentence> alreadyPickedSentencesForFirstWord = sentencesWereFoundForChangeEM.getAlreadyPickedSentences();
        assertEquals(allSentencesForFirstWord.size(), alreadyPickedSentencesForFirstWord.size());

        alreadyPickedSentencesForFirstWord = newArrayList(allSentencesForFirstWord.get(0), allSentencesForFirstWord.get(2), allSentencesForFirstWord.get(6));
        practiceWordSetFragment.onMessageEvent(new SentenceWasPickedForChangeEM(alreadyPickedSentencesForFirstWord, displayedWordForFirstWord));

        newSentenceEM = getEM(NewSentenceEM.class, eventBus);
        displayedSentenceForFirstWord = newSentenceEM.getSentence();
        displayedWordForFirstWord = newSentenceEM.getWord();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWordForFirstWord));
        sentencesWereFoundForChangeEM = getEM(SentencesWereFoundForChangeEM.class, eventBus);
        alreadyPickedSentencesForFirstWord = sentencesWereFoundForChangeEM.getAlreadyPickedSentences();

        when(answerTextMock.getText()).thenReturn(displayedSentenceForFirstWord.getText());
        practiceWordSetFragment.onCheckAnswerButtonClick();
        reset(eventBus);
        practiceWordSetFragment.onNextButtonClick();

        // finished the first word for the first cycle

        newSentenceEM = getEM(NewSentenceEM.class, eventBus);
        Sentence displayedSentenceForSecondWord = newSentenceEM.getSentence();
        Word2Tokens displayedWordForSecondWord = newSentenceEM.getWord();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWordForSecondWord));
        sentencesWereFoundForChangeEM = getEM(SentencesWereFoundForChangeEM.class, eventBus);
        List<Sentence> allSentencesForSecondWord = sentencesWereFoundForChangeEM.getSentences();
        List<Sentence> alreadyPickedSentencesForSecondWord = sentencesWereFoundForChangeEM.getAlreadyPickedSentences();
        assertEquals(allSentencesForSecondWord.size(), alreadyPickedSentencesForSecondWord.size());

        alreadyPickedSentencesForSecondWord = newArrayList(allSentencesForSecondWord.get(0), allSentencesForSecondWord.get(6));
        practiceWordSetFragment.onMessageEvent(new SentenceWasPickedForChangeEM(alreadyPickedSentencesForSecondWord, displayedWordForSecondWord));

        newSentenceEM = getEM(NewSentenceEM.class, eventBus);
        displayedSentenceForSecondWord = newSentenceEM.getSentence();
        displayedWordForSecondWord = newSentenceEM.getWord();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWordForSecondWord));
        sentencesWereFoundForChangeEM = getEM(SentencesWereFoundForChangeEM.class, eventBus);
        alreadyPickedSentencesForSecondWord = sentencesWereFoundForChangeEM.getAlreadyPickedSentences();

        when(answerTextMock.getText()).thenReturn(displayedSentenceForSecondWord.getText());
        practiceWordSetFragment.onCheckAnswerButtonClick();
        reset(eventBus);
        practiceWordSetFragment.onNextButtonClick();

        // finished the second word for the first cycle

        newSentenceEM = getEM(NewSentenceEM.class, eventBus);
        assertEquals(displayedSentenceForFirstWord, newSentenceEM.getSentence());
        assertEquals(displayedWordForFirstWord, newSentenceEM.getWord());

        //
        // TEST OF SENTENCES CYCLICAL MOVEMENT SECOND STAGE
        //

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWordForFirstWord));
        sentencesWereFoundForChangeEM = getEM(SentencesWereFoundForChangeEM.class, eventBus);
        assertEquals(allSentencesForFirstWord, sentencesWereFoundForChangeEM.getSentences());

        assertEquals(alreadyPickedSentencesForFirstWord, sentencesWereFoundForChangeEM.getAlreadyPickedSentences());
        assertEquals(3, alreadyPickedSentencesForFirstWord.size());
        assertEquals(3, sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());

        when(answerTextMock.getText()).thenReturn(displayedSentenceForFirstWord.getText());
        practiceWordSetFragment.onCheckAnswerButtonClick();
        reset(eventBus);
        practiceWordSetFragment.onNextButtonClick();

        // finished the first word for the second cycle

        newSentenceEM = getEM(NewSentenceEM.class, eventBus);
        assertEquals(displayedSentenceForSecondWord, newSentenceEM.getSentence());
        assertEquals(displayedWordForSecondWord, newSentenceEM.getWord());

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWordForSecondWord));
        sentencesWereFoundForChangeEM = getEM(SentencesWereFoundForChangeEM.class, eventBus);
        assertEquals(allSentencesForSecondWord, sentencesWereFoundForChangeEM.getSentences());

        assertEquals(alreadyPickedSentencesForSecondWord, sentencesWereFoundForChangeEM.getAlreadyPickedSentences());
        assertEquals(2, alreadyPickedSentencesForSecondWord.size());
        assertEquals(2, sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWordForSecondWord));
        sentencesWereFoundForChangeEM = getEM(SentencesWereFoundForChangeEM.class, eventBus);
        allSentencesForSecondWord = sentencesWereFoundForChangeEM.getSentences();
        alreadyPickedSentencesForSecondWord = sentencesWereFoundForChangeEM.getAlreadyPickedSentences();

        alreadyPickedSentencesForSecondWord = newArrayList(allSentencesForSecondWord.get(0), allSentencesForSecondWord.get(2), allSentencesForSecondWord.get(6));
        practiceWordSetFragment.onMessageEvent(new SentenceWasPickedForChangeEM(alreadyPickedSentencesForSecondWord, displayedWordForSecondWord));

        newSentenceEM = getEM(NewSentenceEM.class, eventBus);
        displayedSentenceForSecondWord = newSentenceEM.getSentence();
        displayedWordForSecondWord = newSentenceEM.getWord();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(displayedWordForSecondWord));
        sentencesWereFoundForChangeEM = getEM(SentencesWereFoundForChangeEM.class, eventBus);
        allSentencesForSecondWord = sentencesWereFoundForChangeEM.getSentences();
        alreadyPickedSentencesForSecondWord = sentencesWereFoundForChangeEM.getAlreadyPickedSentences();

        when(answerTextMock.getText()).thenReturn(displayedSentenceForSecondWord.getText());
        practiceWordSetFragment.onCheckAnswerButtonClick();
        reset(eventBus);
        practiceWordSetFragment.onNextButtonClick();

        // finished the second word for the second cycle

        //
        // REPETITION MODE
        //
        Whitebox.setInternalState(practiceWordSetFragment, "repetitionMode", true);
        reset(eventBus);
        practiceWordSetFragment.init();


        newSentenceEM = getEM(NewSentenceEM.class, eventBus, 2);
        while (!displayedWordForFirstWord.equals(newSentenceEM.getWord())) {
            practiceWordSetFragment.onNextButtonClick();
            newSentenceEM = getEM(NewSentenceEM.class, eventBus);
        }
        assertNotEquals(displayedSentenceForFirstWord, newSentenceEM.getSentence());
        assertEquals(alreadyPickedSentencesForFirstWord.get(1), newSentenceEM.getSentence());

        displayedSentenceForFirstWord = newSentenceEM.getSentence();
        when(answerTextMock.getText()).thenReturn(displayedSentenceForFirstWord.getText());
        practiceWordSetFragment.onCheckAnswerButtonClick();
        reset(eventBus);
        practiceWordSetFragment.onNextButtonClick();

        newSentenceEM = getEM(NewSentenceEM.class, eventBus);
        assertNotEquals(displayedSentenceForSecondWord, newSentenceEM.getSentence());
        assertEquals(alreadyPickedSentencesForSecondWord.get(1), newSentenceEM.getSentence());

        displayedSentenceForSecondWord = newSentenceEM.getSentence();
        when(answerTextMock.getText()).thenReturn(displayedSentenceForSecondWord.getText());
        practiceWordSetFragment.onCheckAnswerButtonClick();
        reset(eventBus);
        practiceWordSetFragment.onNextButtonClick();
    }

    private void displayNewSentence(Sentence displayedSentence) {
        originalTextTextViewPresenter.setModel(displayedSentence);
        originalTextTextViewPresenter.unlock();
        originalTextTextViewPresenter.refresh();
    }

    private <T> T getEM(Class<T> clazz, EventBus eventBus, int times) {
        ArgumentCaptor<T> captor = ArgumentCaptor.forClass(clazz);
        verify(eventBus, times(times)).post(captor.capture());
        reset(eventBus);
        return captor.getValue();
    }

    private <T> T getEM(Class<T> clazz, EventBus eventBus) {
        return getEM(clazz, eventBus, 1);
    }
}
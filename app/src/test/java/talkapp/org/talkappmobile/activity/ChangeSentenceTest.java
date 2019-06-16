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
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
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


    @Before
    public void setup() throws SQLException {
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(RuntimeEnvironment.application, DatabaseHelper.class);
        SentenceDao sentenceDao = new SentenceDaoImpl(databaseHelper.getConnectionSource(), SentenceMapping.class);
        WordSetDao wordSetDao = new WordSetDaoImpl(databaseHelper.getConnectionSource(), WordSetMapping.class);
        ExpAuditDao expAuditDao = new ExpAuditDaoImpl(databaseHelper.getConnectionSource(), ExpAuditMapping.class);
        WordRepetitionProgressDao exerciseDao = new WordRepetitionProgressDaoImpl(databaseHelper.getConnectionSource(), WordRepetitionProgressMapping.class);

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
        Whitebox.setInternalState(practiceWordSetFragment, "answerText", mock(TextView.class));
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

        ArgumentCaptor<NewSentenceEM> captorNewSentenceEM = ArgumentCaptor.forClass(NewSentenceEM.class);
        verify(eventBus).post(captorNewSentenceEM.capture());
        reset(eventBus);
        NewSentenceEM newSentenceEM = captorNewSentenceEM.getValue();
        Sentence displayedSentence = newSentenceEM.getSentence();
        originalTextTextViewPresenter.setModel(displayedSentence);
        originalTextTextViewPresenter.unlock();
        originalTextTextViewPresenter.refresh();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(newSentenceEM.getWord()));

        ArgumentCaptor<SentencesWereFoundForChangeEM> captorSentencesWereFoundForChangeEM = ArgumentCaptor.forClass(SentencesWereFoundForChangeEM.class);
        verify(eventBus).post(captorSentencesWereFoundForChangeEM.capture());
        reset(eventBus);

        SentencesWereFoundForChangeEM sentencesWereFoundForChangeEM = captorSentencesWereFoundForChangeEM.getValue();
        originalTextTextViewPresenter.prepareSentencesForPicking(sentencesWereFoundForChangeEM.getSentences(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences(), newSentenceEM.getWord());

        assertEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());
        reset(eventBus);

        List<Sentence> pickedSentences = asList(sentencesWereFoundForChangeEM.getSentences().get(5), displayedSentence);
        SentenceWasPickedForChangeEM wasPickedForChangeEM = new SentenceWasPickedForChangeEM(pickedSentences, newSentenceEM.getWord());
        for (int i = 0; i < 10; i++) {
            practiceWordSetFragment.onMessageEvent(wasPickedForChangeEM);

            captorNewSentenceEM = ArgumentCaptor.forClass(NewSentenceEM.class);
            verify(eventBus).post(captorNewSentenceEM.capture());
            reset(eventBus);

            newSentenceEM = captorNewSentenceEM.getValue();
            assertTrue(pickedSentences.contains(newSentenceEM.getSentence()));
            assertNotEquals(displayedSentence, newSentenceEM.getSentence());
            displayedSentence = newSentenceEM.getSentence();
        }
        originalTextTextViewPresenter.setModel(newSentenceEM.getSentence());
        originalTextTextViewPresenter.unlock();
        originalTextTextViewPresenter.refresh();

        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(newSentenceEM.getWord()));

        captorSentencesWereFoundForChangeEM = ArgumentCaptor.forClass(SentencesWereFoundForChangeEM.class);
        verify(eventBus).post(captorSentencesWereFoundForChangeEM.capture());
        reset(eventBus);

        sentencesWereFoundForChangeEM = captorSentencesWereFoundForChangeEM.getValue();
        originalTextTextViewPresenter.prepareSentencesForPicking(sentencesWereFoundForChangeEM.getSentences(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences(), newSentenceEM.getWord());

        assertNotEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());
        assertEquals(2, sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());

        for (Sentence alreadyPickedSentence : sentencesWereFoundForChangeEM.getAlreadyPickedSentences()) {
            assertTrue(pickedSentences.contains(alreadyPickedSentence));
        }

        practiceWordSetFragment.onNextButtonClick();
        reset(eventBus);
        practiceWordSetFragment.onMessageEvent(new ChangeSentenceOptionPickedEM(newSentenceEM.getWord()));

        captorSentencesWereFoundForChangeEM = ArgumentCaptor.forClass(SentencesWereFoundForChangeEM.class);
        verify(eventBus).post(captorSentencesWereFoundForChangeEM.capture());
        reset(eventBus);

        sentencesWereFoundForChangeEM = captorSentencesWereFoundForChangeEM.getValue();
        assertNotEquals(sentencesWereFoundForChangeEM.getSentences().size(), sentencesWereFoundForChangeEM.getAlreadyPickedSentences().size());
    }
}
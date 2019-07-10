package talkapp.org.talkappmobile.activity;

import android.support.v7.widget.RecyclerView;
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
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetPresenter;
import talkapp.org.talkappmobile.dao.DatabaseHelper;
import talkapp.org.talkappmobile.dao.ExpAuditDao;
import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.dao.TopicDao;
import talkapp.org.talkappmobile.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.dao.impl.ExpAuditDaoImpl;
import talkapp.org.talkappmobile.dao.impl.SentenceDaoImpl;
import talkapp.org.talkappmobile.dao.impl.WordRepetitionProgressDaoImpl;
import talkapp.org.talkappmobile.dao.impl.WordSetDaoImpl;
import talkapp.org.talkappmobile.mappings.ExpAuditMapping;
import talkapp.org.talkappmobile.mappings.SentenceMapping;
import talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.UserExpService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.impl.AudioStuffFactoryBean;
import talkapp.org.talkappmobile.service.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.service.impl.EqualityScorerBean;
import talkapp.org.talkappmobile.service.impl.LocalDataServiceImpl;
import talkapp.org.talkappmobile.service.impl.LoggerBean;
import talkapp.org.talkappmobile.service.impl.RandomWordsCombinatorBean;
import talkapp.org.talkappmobile.service.impl.RequestExecutor;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;
import talkapp.org.talkappmobile.service.impl.TextUtilsImpl;
import talkapp.org.talkappmobile.service.impl.UserExpServiceImpl;
import talkapp.org.talkappmobile.service.impl.WordRepetitionProgressServiceImpl;
import talkapp.org.talkappmobile.service.impl.WordSetExperienceUtilsImpl;
import talkapp.org.talkappmobile.service.impl.WordSetServiceImpl;
import talkapp.org.talkappmobile.service.mapper.WordSetMapper;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class CapitalLetterInNewWordTest {
    private WordRepetitionProgressService exerciseService;
    private UserExpService userExpService;
    private WordSetService wordSetService;
    private WordSetExperienceUtilsImpl experienceUtils;
    private PresenterFactory presenterFactory;
    private WordSet wordSet;
    private PracticeWordSetFragment practiceWordSetFragment;
    private AddingNewWordSetFragment addingNewWordSetFragment;
    private PracticeWordSetVocabularyFragment practiceWordSetVocabularyFragment;
    private TextView word1;
    private TextView word2;
    private TextView word3;
    private TextView word4;
    private TextView word5;
    private TextView word6;
    private TextView word7;
    private TextView word8;
    private TextView word9;
    private TextView word10;
    private TextView word11;
    private TextView word12;
    private WordSetDao wordSetDao;
    private WordSetMapper wordSetMapper;

    @Before
    public void setup() throws SQLException {
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(RuntimeEnvironment.application, DatabaseHelper.class);
        SentenceDao sentenceDao = new SentenceDaoImpl(databaseHelper.getConnectionSource(), SentenceMapping.class);
        wordSetDao = new WordSetDaoImpl(databaseHelper.getConnectionSource(), WordSetMapping.class);
        ExpAuditDao expAuditDao = new ExpAuditDaoImpl(databaseHelper.getConnectionSource(), ExpAuditMapping.class);
        WordRepetitionProgressDao exerciseDao = new WordRepetitionProgressDaoImpl(databaseHelper.getConnectionSource(), WordRepetitionProgressMapping.class);

        LoggerBean logger = new LoggerBean();
        ObjectMapper mapper = new ObjectMapper();
        wordSetMapper = new WordSetMapper(mapper);
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
        wordSetService = new WordSetServiceImpl(wordSetDao, experienceUtils, new WordSetMapper(mapper));
        when(mockServiceFactoryBean.getWordSetExperienceRepository()).thenReturn(wordSetService);

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
        Whitebox.setInternalState(presenterFactory, "logger", logger);
        Whitebox.setInternalState(presenterFactory, "audioStuffFactory", new AudioStuffFactoryBean());

        server.findAllWordSets();
        practiceWordSetFragment = new PracticeWordSetFragment();
        WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory = mock(WaitingForProgressBarManagerFactory.class);
        when(waitingForProgressBarManagerFactory.get(any(View.class), any(View.class))).thenReturn(mock(WaitingForProgressBarManager.class));
        Whitebox.setInternalState(practiceWordSetFragment, "waitingForProgressBarManagerFactory", waitingForProgressBarManagerFactory);
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
        Whitebox.setInternalState(practiceWordSetFragment, "eventBus", mock(EventBus.class));

        addingNewWordSetFragment = new AddingNewWordSetFragment();
        Whitebox.setInternalState(addingNewWordSetFragment, "waitingForProgressBarManagerFactory", waitingForProgressBarManagerFactory);
        Whitebox.setInternalState(addingNewWordSetFragment, "presenterFactory", presenterFactory);
        Whitebox.setInternalState(addingNewWordSetFragment, "pleaseWaitProgressBar", mock(View.class));
        Whitebox.setInternalState(addingNewWordSetFragment, "mainForm", mock(View.class));
        word1 = mock(TextView.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "word1", word1);
        word2 = mock(TextView.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "word2", word2);
        word3 = mock(TextView.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "word3", word3);
        word4 = mock(TextView.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "word4", word4);
        word5 = mock(TextView.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "word5", word5);
        word6 = mock(TextView.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "word6", word6);
        word7 = mock(TextView.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "word7", word7);
        word8 = mock(TextView.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "word8", word8);
        word9 = mock(TextView.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "word9", word9);
        word10 = mock(TextView.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "word10", word10);
        word11 = mock(TextView.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "word11", word11);
        word12 = mock(TextView.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "word12", word12);


        practiceWordSetVocabularyFragment = new PracticeWordSetVocabularyFragment();
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "waitingForProgressBarManagerFactory", waitingForProgressBarManagerFactory);
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "presenterFactory", presenterFactory);
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "progressBarView", mock(View.class));
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "eventBus", mock(EventBus.class));
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "wordSetVocabularyView", mock(RecyclerView.class));
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "progressBarView", mock(View.class));
    }

    private WordSet createWordSet(int id, String... words) {
        int trainingExperience = 0;
        WordSet wordSet = new WordSet();
        wordSet.setId(id);

        LinkedList<Word2Tokens> word2Tokens = new LinkedList<>();
        for (String word : words) {
            Word2Tokens age = new Word2Tokens(word, word, wordSet.getId());
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
    public void testCapitalLetterInNewWord() {
        addingNewWordSetFragment.init();
        wordSet = createWordSet(1000000, "solemn", "grip", "wink", "adoption", "Voluntary", "consent", "preamble",
                "conquer", "adore", "deplete", "cease", "ratification");
        List<Word2Tokens> words = wordSet.getWords();
        when(word1.getText()).thenReturn(words.get(0).getWord());
        when(word2.getText()).thenReturn(words.get(1).getWord());
        when(word3.getText()).thenReturn(words.get(2).getWord());
        when(word4.getText()).thenReturn(words.get(3).getWord());
        when(word5.getText()).thenReturn(words.get(4).getWord());
        when(word6.getText()).thenReturn(words.get(5).getWord());
        when(word7.getText()).thenReturn(words.get(6).getWord());
        when(word8.getText()).thenReturn(words.get(7).getWord());
        when(word9.getText()).thenReturn(words.get(8).getWord());
        when(word10.getText()).thenReturn(words.get(9).getWord());
        when(word11.getText()).thenReturn(words.get(10).getWord());
        when(word12.getText()).thenReturn(words.get(11).getWord());

        try {
            addingNewWordSetFragment.onButtonSubmitClick();
        } catch (NullPointerException e) {
        }

        verify(word1).setText("");
        verify(word2).setText("");
        verify(word3).setText("");
        verify(word4).setText("");
        verify(word5).setText("");
        verify(word6).setText("");
        verify(word7).setText("");
        verify(word8).setText("");
        verify(word9).setText("");
        verify(word10).setText("");
        verify(word11).setText("");
        verify(word12).setText("");

        wordSet = wordSetMapper.toDto(wordSetDao.findById(wordSetService.getCustomWordSetsStartsSince()));

        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "wordSet", wordSet);
        practiceWordSetVocabularyFragment.init();

        Whitebox.setInternalState(practiceWordSetFragment, "wordSet", wordSet);
        practiceWordSetFragment.init();

        WordSet wordSet = Whitebox.getInternalState(practiceWordSetFragment, "wordSet");

        assertEquals(wordSetService.getCustomWordSetsStartsSince(), wordSet.getId());
        for (Word2Tokens word : wordSet.getWords()) {
            assertEquals(wordSet.getId(), word.getSourceWordSetId().intValue());
        }

        PracticeWordSetPresenter presenter = Whitebox.getInternalState(practiceWordSetFragment, "presenter");
        Sentence currentSentence = presenter.getCurrentSentence();

        assertNotNull(currentSentence);
        assertFalse(isEmpty(currentSentence.getText()));
        assertFalse(isEmpty(currentSentence.getId()));
    }
}
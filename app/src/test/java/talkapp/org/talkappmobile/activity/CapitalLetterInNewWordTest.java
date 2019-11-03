package talkapp.org.talkappmobile.activity;

import android.support.v7.widget.RecyclerView;
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
import org.mockito.ArgumentCaptor;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.DaoHelper;
import talkapp.org.talkappmobile.ServiceHelper;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.activity.custom.WordSetVocabularyView;
import talkapp.org.talkappmobile.activity.custom.controller.WordSetVocabularyViewController;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetPresenter;
import talkapp.org.talkappmobile.dao.ExpAuditDao;
import talkapp.org.talkappmobile.dao.NewWordSetDraftDao;
import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.dao.TopicDao;
import talkapp.org.talkappmobile.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.events.AddNewWordSetButtonSubmitClickedEM;
import talkapp.org.talkappmobile.events.PhraseTranslationInputPopupOkClickedEM;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
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
import talkapp.org.talkappmobile.service.impl.WordTranslationServiceImpl;
import talkapp.org.talkappmobile.service.mapper.ExpAuditMapper;
import talkapp.org.talkappmobile.service.mapper.WordSetMapper;
import talkapp.org.talkappmobile.service.mapper.WordTranslationMapper;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.text.TextUtils.isEmpty;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static talkapp.org.talkappmobile.activity.custom.controller.WordSetVocabularyViewController.RUSSIAN_LANGUAGE;

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
    private WordSetMapper wordSetMapper;
    private TextView answerTextMock;
    private EventBus eventBusMock = mock(EventBus.class);
    private WordSetVocabularyViewController.LocalEventBus localEventBusMock = mock(WordSetVocabularyViewController.LocalEventBus.class);
    private WordSetDao wordSetDaoMock;
    private SentenceDao sentenceDaoMock;
    private NewWordSetDraftDao newWordSetDraftDaoMock;
    private WordRepetitionProgressDao wordRepetitionProgressDaoMock;
    private ExpAuditDao expAuditDaoMock;
    private WordTranslationDao wordTranslationDaoMock;
    private ServiceFactoryBean serviceFactoryBeanMock;
    private DaoHelper daoHelper;
    private ServiceHelper serviceHelper;
    private WordTranslationMapper wordTranslationMapper;
    private WordSetVocabularyViewController dialogInputTextViewController;
    private WordSetVocabularyView wordSetVocabularyView;

    @Before
    public void setup() throws SQLException {
        LoggerBean logger = new LoggerBean();
        ObjectMapper mapper = new ObjectMapper();
        wordSetMapper = new WordSetMapper(mapper);
        wordTranslationMapper = new WordTranslationMapper(mapper);
        daoHelper = new DaoHelper();
        serviceHelper = new ServiceHelper(daoHelper);
        wordTranslationDaoMock = daoHelper.getWordTranslationDao();
        wordSetDaoMock = daoHelper.getWordSetDao();
        sentenceDaoMock = daoHelper.getSentenceDao();
        LocalDataServiceImpl localDataService = new LocalDataServiceImpl(wordSetDaoMock, mock(TopicDao.class), sentenceDaoMock, wordTranslationDaoMock, mapper, logger);

        BackendServerFactoryBean factory = new BackendServerFactoryBean();
        Whitebox.setInternalState(factory, "logger", new LoggerBean());
        ServiceFactoryBean mockServiceFactoryBean = mock(ServiceFactoryBean.class);
        when(mockServiceFactoryBean.getLocalDataService()).thenReturn(localDataService);

        expAuditDaoMock = daoHelper.getExpAuditDao();
        userExpService = new UserExpServiceImpl(expAuditDaoMock, mock(ExpAuditMapper.class));
        when(mockServiceFactoryBean.getUserExpService()).thenReturn(userExpService);

        wordRepetitionProgressDaoMock = daoHelper.getWordRepetitionProgressDao();
        exerciseService = new WordRepetitionProgressServiceImpl(wordRepetitionProgressDaoMock, wordSetDaoMock, sentenceDaoMock, mapper);
        when(mockServiceFactoryBean.getPracticeWordSetExerciseRepository()).thenReturn(exerciseService);


        experienceUtils = new WordSetExperienceUtilsImpl();
        newWordSetDraftDaoMock = daoHelper.getNewWordSetDraftDao();
        wordSetService = new WordSetServiceImpl(wordSetDaoMock, newWordSetDraftDaoMock, experienceUtils, new WordSetMapper(mapper));
        when(mockServiceFactoryBean.getWordSetExperienceRepository()).thenReturn(wordSetService);

        when(mockServiceFactoryBean.getWordTranslationService()).thenReturn(new WordTranslationServiceImpl(wordTranslationDaoMock, wordTranslationMapper));

        Whitebox.setInternalState(factory, "serviceFactory", mockServiceFactoryBean);
        Whitebox.setInternalState(factory, "requestExecutor", new RequestExecutor());
        DataServer server = factory.get();

        dialogInputTextViewController = new WordSetVocabularyViewController(localEventBusMock, server, mockServiceFactoryBean);

        presenterFactory = new PresenterFactory();
        Whitebox.setInternalState(presenterFactory, "backendServerFactory", factory);
        Whitebox.setInternalState(presenterFactory, "serviceFactory", mockServiceFactoryBean);
        Whitebox.setInternalState(presenterFactory, "equalityScorer", new EqualityScorerBean());
        Whitebox.setInternalState(presenterFactory, "textUtils", new TextUtilsImpl());
        Whitebox.setInternalState(presenterFactory, "experienceUtils", experienceUtils);
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
        Whitebox.setInternalState(practiceWordSetFragment, "eventBus", eventBusMock);

        addingNewWordSetFragment = new AddingNewWordSetFragment();
        BackendServerFactoryBean backendServerFactoryMock = mock(BackendServerFactoryBean.class);
        when(backendServerFactoryMock.get()).thenReturn(server);
        Whitebox.setInternalState(addingNewWordSetFragment, "eventBus", eventBusMock);
        serviceFactoryBeanMock = serviceHelper.getServiceFactoryBean();
        Whitebox.setInternalState(addingNewWordSetFragment, "serviceFactory", serviceFactoryBeanMock);
        Whitebox.setInternalState(addingNewWordSetFragment, "waitingForProgressBarManagerFactory", waitingForProgressBarManagerFactory);
        Whitebox.setInternalState(addingNewWordSetFragment, "backendServerFactory", backendServerFactoryMock);
        Whitebox.setInternalState(addingNewWordSetFragment, "pleaseWaitProgressBar", mock(View.class));
        Whitebox.setInternalState(addingNewWordSetFragment, "mainForm", mock(View.class));
        wordSetVocabularyView = mock(WordSetVocabularyView.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "wordSetVocabularyView", wordSetVocabularyView);


        practiceWordSetVocabularyFragment = new PracticeWordSetVocabularyFragment();
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "waitingForProgressBarManagerFactory", waitingForProgressBarManagerFactory);
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "presenterFactory", presenterFactory);
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "progressBarView", mock(View.class));
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "eventBus", mock(EventBus.class));
        Whitebox.setInternalState(practiceWordSetVocabularyFragment, "wordSetVocabularyView", mock(WordSetVocabularyView.class));
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
        daoHelper.releaseHelper();
    }

    @Test
    public void testCapitalLetterInNewWord() throws SQLException {
        addingNewWordSetFragment.init();
        String phrasalVerb = "look for";
        wordSet = createWordSet(1000000, "solemn", "grip", "wink", "adoption", "voluntary", phrasalVerb + "|искать", "preamble",
                "conquer", "adore", "deplete", "cease", "ratification");
        List<Word2Tokens> words = wordSet.getWords();
        List<WordTranslation> translations = new LinkedList<>();
        for (Word2Tokens word : words) {
            WordTranslation translation = new WordTranslation();
            String[] split = word.getWord().split("\\|");
            if (split.length == 1) {
                translation.setWord(split[0]);
                translation.setTranslation("");
            } else {
                translation.setWord(split[0]);
                translation.setTranslation(split[1]);
            }
            translations.add(translation);
        }
        when(wordSetVocabularyView.getVocabulary()).thenReturn(translations);

        for (Word2Tokens word : words) {
            String[] split = word.getWord().split("\\|");
            if (split.length != 2) {
                dialogInputTextViewController.handle(new PhraseTranslationInputPopupOkClickedEM(1, split[0].trim(), null));
                continue;
            }
            WordTranslation wordTranslation = new WordTranslation();
            wordTranslation.setLanguage(RUSSIAN_LANGUAGE);
            wordTranslation.setTranslation(split[1].trim());
            wordTranslation.setWord(split[0].trim());
            wordTranslation.setTokens(split[0].trim());
            wordTranslationDaoMock.save(asList(wordTranslationMapper.toMapping(wordTranslation)));
            dialogInputTextViewController.handle(new PhraseTranslationInputPopupOkClickedEM(1, split[0].trim(), split[1].trim()));
        }

        reset(eventBusMock);
        try {
            addingNewWordSetFragment.onButtonSubmitClick();
        } catch (NullPointerException e) {
        }
        ArgumentCaptor<AddNewWordSetButtonSubmitClickedEM> objectArgumentCaptor = ArgumentCaptor.forClass(AddNewWordSetButtonSubmitClickedEM.class);
        verify(eventBusMock).post(objectArgumentCaptor.capture());
        addingNewWordSetFragment.onMessageEvent(objectArgumentCaptor.getValue());

        wordSet = wordSetMapper.toDto(daoHelper.getWordSetDao().findById(wordSetService.getCustomWordSetsStartsSince()));

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

        while (true) {
            String text = presenter.getCurrentSentence().getText();
            if (text.equals(phrasalVerb)) {
                when(answerTextMock.getText()).thenReturn(phrasalVerb.substring(0, 5));
                practiceWordSetFragment.onCheckAnswerButtonClick();
                break;
            }
            practiceWordSetFragment.onNextButtonClick();
        }
    }
}
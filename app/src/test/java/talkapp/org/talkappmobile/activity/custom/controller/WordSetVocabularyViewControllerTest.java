package talkapp.org.talkappmobile.activity.custom.controller;

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

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.DaoHelper;
import talkapp.org.talkappmobile.dao.TopicDao;
import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.events.NewWordIsDuplicateEM;
import talkapp.org.talkappmobile.events.NewWordSentencesWereFoundEM;
import talkapp.org.talkappmobile.events.NewWordSentencesWereNotFoundEM;
import talkapp.org.talkappmobile.events.NewWordSuccessfullySubmittedEM;
import talkapp.org.talkappmobile.events.NewWordTranslationWasNotFoundEM;
import talkapp.org.talkappmobile.events.PhraseTranslationInputPopupOkClickedEM;
import talkapp.org.talkappmobile.events.PhraseTranslationInputWasValidatedSuccessfullyEM;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.WordSetExperienceUtils;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.service.impl.LocalDataServiceImpl;
import talkapp.org.talkappmobile.service.impl.LoggerBean;
import talkapp.org.talkappmobile.service.impl.RequestExecutor;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;
import talkapp.org.talkappmobile.service.impl.WordSetServiceImpl;
import talkapp.org.talkappmobile.service.impl.WordTranslationServiceImpl;
import talkapp.org.talkappmobile.service.mapper.WordSetMapper;
import talkapp.org.talkappmobile.service.mapper.WordTranslationMapper;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class WordSetVocabularyViewControllerTest {
    private WordSetMapper mapper;
    private WordSetService wordSetService;
    private EventBus eventBus;
    private WordSetVocabularyViewController.LocalEventBus localEventBusMock = mock(WordSetVocabularyViewController.LocalEventBus.class);
    private WordSetVocabularyViewController controller;
    private DaoHelper daoHelper;
    private WordTranslationMapper wordTranslationMapper;

    @Before
    public void setUp() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        this.mapper = new WordSetMapper(mapper);
        this.wordTranslationMapper = new WordTranslationMapper(mapper);
        daoHelper = new DaoHelper();
        WordTranslationDao wordTranslationDao = mock(WordTranslationDao.class);
        LocalDataServiceImpl localDataService = new LocalDataServiceImpl(daoHelper.getWordSetDao(), mock(TopicDao.class), daoHelper.getSentenceDao(), wordTranslationDao, mapper, new LoggerBean());
        wordSetService = new WordSetServiceImpl(daoHelper.getWordSetDao(), daoHelper.getNewWordSetDraftDao(), mock(WordSetExperienceUtils.class), this.mapper);
        wordSetService = new WordSetServiceImpl(daoHelper.getWordSetDao(), daoHelper.getNewWordSetDraftDao(), mock(WordSetExperienceUtils.class), this.mapper);

        BackendServerFactoryBean factory = new BackendServerFactoryBean();
        Whitebox.setInternalState(factory, "logger", new LoggerBean());
        ServiceFactoryBean mockServiceFactoryBean = mock(ServiceFactoryBean.class);
        when(mockServiceFactoryBean.getLocalDataService()).thenReturn(localDataService);
        WordTranslationServiceImpl wordTranslationService = new WordTranslationServiceImpl(wordTranslationDao, wordTranslationMapper);
        when(mockServiceFactoryBean.getWordTranslationService()).thenReturn(wordTranslationService);
        when(mockServiceFactoryBean.getWordSetExperienceRepository()).thenReturn(wordSetService);
        Whitebox.setInternalState(factory, "serviceFactory", mockServiceFactoryBean);
        Whitebox.setInternalState(factory, "requestExecutor", new RequestExecutor());
        DataServer server = factory.get();

        eventBus = mock(EventBus.class);
        controller = new WordSetVocabularyViewController(localEventBusMock, server, mockServiceFactoryBean);
    }

    @Test
    public void submit_noSentencesForAnyWord() {
        controller.handle(new PhraseTranslationInputPopupOkClickedEM(1, "  sdfds ", null));

        verify(localEventBusMock).onMessageEvent(any(NewWordSentencesWereNotFoundEM.class));

        verify(eventBus, times(0)).post(any(NewWordSentencesWereFoundEM.class));
        verify(eventBus, times(0)).post(any(NewWordSuccessfullySubmittedEM.class));
        verify(eventBus, times(0)).post(any(NewWordIsDuplicateEM.class));
        verify(eventBus, times(0)).post(any(NewWordTranslationWasNotFoundEM.class));
    }

    @Test
    public void submit_noSentencesForFewWord() {
        controller.handle(new PhraseTranslationInputPopupOkClickedEM(1, "house", null));

        verify(eventBus, times(0)).post(new NewWordSentencesWereNotFoundEM());

        verify(localEventBusMock).onMessageEvent(any(NewWordSentencesWereFoundEM.class));
        verify(eventBus, times(0)).post(any(NewWordSuccessfullySubmittedEM.class));
        verify(eventBus, times(0)).post(any(NewWordIsDuplicateEM.class));
        verify(eventBus, times(0)).post(any(NewWordTranslationWasNotFoundEM.class));
    }

    @Test
    public void submit_allSentencesWereFound() throws SQLException {
        String word0 = "house";
        controller.handle(new PhraseTranslationInputPopupOkClickedEM(1, word0, null));

        verify(localEventBusMock).onMessageEvent(any(NewWordSentencesWereFoundEM.class));

        verify(localEventBusMock).onMessageEvent(any(NewWordSentencesWereFoundEM.class));
        verify(eventBus, times(0)).post(any(NewWordSentencesWereNotFoundEM.class));
        verify(eventBus, times(0)).post(any(NewWordTranslationWasNotFoundEM.class));
        verify(localEventBusMock, times(1)).onMessageEvent(any(PhraseTranslationInputWasValidatedSuccessfullyEM.class));
    }


    @Test
    public void submit_allSentencesWereFoundButThereFewExpressions() throws SQLException {
        String word = "make out";
        String translation = "разглядеть, различить, разбирать";
        controller.handle(new PhraseTranslationInputPopupOkClickedEM(1, "  " + word + " ", "  " + translation + " "));

        verify(localEventBusMock).onMessageEvent(any(NewWordSentencesWereFoundEM.class));

        verify(localEventBusMock).onMessageEvent(any(NewWordSentencesWereFoundEM.class));
        verify(eventBus, times(0)).post(any(NewWordSentencesWereNotFoundEM.class));
        verify(eventBus, times(0)).post(any(NewWordTranslationWasNotFoundEM.class));
    }

    @After
    public void tearDown() {
        daoHelper.releaseHelper();
    }
}
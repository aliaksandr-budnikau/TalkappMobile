package talkapp.org.talkappmobile.activity.presenter;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.sql.SQLException;
import java.util.List;

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.DaoHelper;
import talkapp.org.talkappmobile.activity.interactor.impl.StudyingWordSetsListInteractor;
import talkapp.org.talkappmobile.activity.view.WordSetsListView;
import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.dao.TopicDao;
import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.model.RepetitionClass;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.CachedWordSetServiceDecorator;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.service.impl.LocalDataServiceImpl;
import talkapp.org.talkappmobile.service.impl.LoggerBean;
import talkapp.org.talkappmobile.service.impl.RequestExecutor;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;
import talkapp.org.talkappmobile.service.impl.WordRepetitionProgressServiceImpl;
import talkapp.org.talkappmobile.service.impl.WordSetExperienceUtilsImpl;
import talkapp.org.talkappmobile.service.impl.WordSetServiceImpl;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FINISHED;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class WordSetsListPresenterAndInteractorIntegTest extends PresenterAndInteractorIntegTest {
    private WordSetsListView view;
    private StudyingWordSetsListInteractor studyingWordSetsInteractor;
    private WordSetExperienceUtilsImpl experienceUtils;
    private DaoHelper daoHelper;

    @Before
    public void setup() throws SQLException {
        view = mock(WordSetsListView.class);
        ObjectMapper mapper = new ObjectMapper();
        daoHelper = new DaoHelper();
        WordRepetitionProgressServiceImpl exerciseService = new WordRepetitionProgressServiceImpl(daoHelper.getWordRepetitionProgressDao(), daoHelper.getWordSetDao(), mock(SentenceDao.class), mapper);
        experienceUtils = new WordSetExperienceUtilsImpl();

        LocalDataServiceImpl localDataService = new LocalDataServiceImpl(daoHelper.getWordSetDao(), mock(TopicDao.class), mock(SentenceDao.class), mock(WordTranslationDao.class), mapper, new LoggerBean());

        BackendServerFactoryBean factory = new BackendServerFactoryBean();
        Whitebox.setInternalState(factory, "logger", new LoggerBean());
        ServiceFactoryBean mockServiceFactoryBean = mock(ServiceFactoryBean.class);
        when(mockServiceFactoryBean.getLocalDataService()).thenReturn(localDataService);
        Whitebox.setInternalState(factory, "serviceFactory", mockServiceFactoryBean);
        Whitebox.setInternalState(factory, "requestExecutor", new RequestExecutor());
        DataServer server = factory.get();
        WordSetService experienceService = new CachedWordSetServiceDecorator(new WordSetServiceImpl(server, daoHelper.getWordSetDao(), daoHelper.getNewWordSetDraftDao(), mapper));
        studyingWordSetsInteractor = new StudyingWordSetsListInteractor(server, experienceService, exerciseService);
    }

    @After
    public void tearDown() {
        daoHelper.releaseHelper();
    }

    @Test
    public void test_withoutTopic() throws SQLException {
        WordSetsListPresenter presenter = new WordSetsListPresenter(null, view, studyingWordSetsInteractor);
        presenter.initialize();
        ArgumentCaptor<List<WordSet>> setsCaptor = forClass(List.class);
        verify(view).onWordSetsInitialized(setsCaptor.capture(), (RepetitionClass) isNull());
        assertFalse(setsCaptor.getValue().isEmpty());

        List<WordSet> wordSets = setsCaptor.getValue();
        int clickedItemNumber = 4;
        presenter.itemClick(wordSets.get(0), clickedItemNumber);
        verify(view).onWordSetNotFinished(null, wordSets.get(0));
        verify(view, times(0)).onWordSetFinished(wordSets.get(0), clickedItemNumber);
        reset(view);

        WordSetMapping wordSetMapping = new WordSetMapping();
        wordSetMapping.setId(String.valueOf(wordSets.get(0).getId()));
        wordSetMapping.setTopicId("34");
        wordSetMapping.setWords("34");
        wordSets.get(0).setStatus(FINISHED);
        daoHelper.getWordSetDao().createNewOrUpdate(wordSetMapping);
        presenter.itemClick(wordSets.get(0), clickedItemNumber);
        verify(view).onWordSetFinished(wordSets.get(0), clickedItemNumber);
        verify(view, times(0)).onWordSetNotFinished(null, wordSets.get(0));
        reset(view);

        presenter.itemLongClick(wordSets.get(0), clickedItemNumber);
        verify(view).onItemLongClick(wordSets.get(0), clickedItemNumber);
        reset(view);

        presenter.resetExperienceClick(wordSets.get(0), clickedItemNumber);
        verify(view).onResetExperienceClick(eq(wordSets.get(0)), eq(clickedItemNumber));
        assertEquals(0, wordSets.get(0).getTrainingExperience());
        assertEquals(FIRST_CYCLE, wordSets.get(0).getStatus());
    }

    @Test
    public void test_withTopic() throws SQLException {
        Topic topic = new Topic();
        topic.setId(1);
        WordSetsListPresenter presenter = new WordSetsListPresenter(topic, view, studyingWordSetsInteractor);
        presenter.initialize();
        ArgumentCaptor<List<WordSet>> setsCaptor = forClass(List.class);
        verify(view).onWordSetsInitialized(setsCaptor.capture(), (RepetitionClass) isNull());
        assertFalse(setsCaptor.getValue().isEmpty());

        List<WordSet> wordSets = setsCaptor.getValue();
        int clickedItemNumber = 5;
        presenter.itemClick(wordSets.get(0), clickedItemNumber);
        verify(view).onWordSetNotFinished(topic, wordSets.get(0));
        verify(view, times(0)).onWordSetFinished(wordSets.get(0), clickedItemNumber);
        reset(view);

        WordSetMapping wordSetMapping = new WordSetMapping();
        wordSetMapping.setId(String.valueOf(wordSets.get(0).getId()));
        wordSetMapping.setTopicId("34");
        wordSetMapping.setWords("34");
        wordSets.get(0).setStatus(FINISHED);
        daoHelper.getWordSetDao().createNewOrUpdate(wordSetMapping);
        presenter.itemClick(wordSets.get(0), clickedItemNumber);
        verify(view).onWordSetFinished(wordSets.get(0), clickedItemNumber);
        verify(view, times(0)).onWordSetNotFinished(topic, wordSets.get(0));
        reset(view);

        presenter.itemLongClick(wordSets.get(0), clickedItemNumber);
        verify(view).onItemLongClick(wordSets.get(0), clickedItemNumber);
        reset(view);

        presenter.resetExperienceClick(wordSets.get(0), clickedItemNumber);
        verify(view).onResetExperienceClick(eq(wordSets.get(0)), eq(clickedItemNumber));
        assertEquals(0, wordSets.get(0).getTrainingExperience());
        assertEquals(FIRST_CYCLE, wordSets.get(0).getStatus());
    }
}
package talkapp.org.talkappmobile.activity.presenter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.talkappmobile.DatabaseHelper;
import org.talkappmobile.dao.SentenceDao;
import org.talkappmobile.dao.TopicDao;
import org.talkappmobile.dao.WordRepetitionProgressDao;
import org.talkappmobile.dao.WordSetDao;
import org.talkappmobile.dao.WordTranslationDao;
import org.talkappmobile.dao.impl.WordRepetitionProgressDaoImpl;
import org.talkappmobile.dao.impl.WordSetDaoImpl;
import org.talkappmobile.mappings.WordRepetitionProgressMapping;
import org.talkappmobile.mappings.WordSetMapping;
import org.talkappmobile.model.Topic;
import org.talkappmobile.model.WordSet;

import java.sql.SQLException;
import java.util.List;

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.activity.interactor.impl.StudyingWordSetsListInteractor;
import talkapp.org.talkappmobile.activity.view.WordSetsListView;
import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.component.backend.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.component.backend.impl.RequestExecutor;
import talkapp.org.talkappmobile.component.database.impl.LocalDataServiceImpl;
import talkapp.org.talkappmobile.component.database.impl.ServiceFactoryBean;
import talkapp.org.talkappmobile.component.database.impl.WordRepetitionProgressServiceImpl;
import talkapp.org.talkappmobile.component.database.impl.WordSetServiceImpl;
import talkapp.org.talkappmobile.component.impl.LoggerBean;
import talkapp.org.talkappmobile.component.impl.WordSetExperienceUtilsImpl;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.talkappmobile.model.WordSetProgressStatus.FINISHED;
import static org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "org.talkappmobile.dao.impl")
public class WordSetsListPresenterAndInteractorIntegTest extends PresenterAndInteractorIntegTest {
    private WordSetsListView view;
    private StudyingWordSetsListInteractor studyingWordSetsInteractor;
    private WordSetExperienceUtilsImpl experienceUtils;
    private WordSetDao wordSetDao;

    @Before
    public void setup() throws SQLException {
        view = mock(WordSetsListView.class);
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(RuntimeEnvironment.application, DatabaseHelper.class);
        WordRepetitionProgressDao exerciseDao = new WordRepetitionProgressDaoImpl(databaseHelper.getConnectionSource(), WordRepetitionProgressMapping.class);
        wordSetDao = new WordSetDaoImpl(databaseHelper.getConnectionSource(), WordSetMapping.class);
        WordRepetitionProgressServiceImpl exerciseService = new WordRepetitionProgressServiceImpl(exerciseDao, wordSetDao, mock(SentenceDao.class), new ObjectMapper());
        experienceUtils = new WordSetExperienceUtilsImpl();
        WordSetServiceImpl experienceService = new WordSetServiceImpl(wordSetDao, experienceUtils);

        LocalDataServiceImpl localDataService = new LocalDataServiceImpl(wordSetDao, mock(TopicDao.class), mock(SentenceDao.class), mock(WordTranslationDao.class), new ObjectMapper(), new LoggerBean());

        BackendServerFactoryBean factory = new BackendServerFactoryBean();
        Whitebox.setInternalState(factory, "logger", new LoggerBean());
        ServiceFactoryBean mockServiceFactoryBean = mock(ServiceFactoryBean.class);
        when(mockServiceFactoryBean.getLocalDataService()).thenReturn(localDataService);
        Whitebox.setInternalState(factory, "serviceFactory", mockServiceFactoryBean);
        Whitebox.setInternalState(factory, "requestExecutor", new RequestExecutor());
        DataServer server = factory.get();
        studyingWordSetsInteractor = new StudyingWordSetsListInteractor(server, experienceService, exerciseService);
    }

    @After
    public void tearDown() {
        OpenHelperManager.releaseHelper();
    }

    @Test
    public void test_withoutTopic() {
        WordSetsListPresenter presenter = new WordSetsListPresenter(null, view, studyingWordSetsInteractor);
        presenter.initialize();
        ArgumentCaptor<List<WordSet>> setsCaptor = forClass(List.class);
        verify(view).onWordSetsInitialized(setsCaptor.capture());
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
        wordSetDao.createNewOrUpdate(wordSetMapping);
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
    public void test_withTopic() {
        Topic topic = new Topic();
        topic.setId(1);
        WordSetsListPresenter presenter = new WordSetsListPresenter(topic, view, studyingWordSetsInteractor);
        presenter.initialize();
        ArgumentCaptor<List<WordSet>> setsCaptor = forClass(List.class);
        verify(view).onWordSetsInitialized(setsCaptor.capture());
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
        wordSetDao.createNewOrUpdate(wordSetMapping);
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
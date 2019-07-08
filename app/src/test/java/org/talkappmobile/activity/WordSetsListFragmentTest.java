package org.talkappmobile.activity;

import android.view.View;
import android.widget.TabHost;

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
import org.talkappmobile.BuildConfig;
import org.talkappmobile.activity.custom.WaitingForProgressBarManager;
import org.talkappmobile.activity.custom.WaitingForProgressBarManagerFactory;
import org.talkappmobile.activity.custom.WordSetsListListView;
import org.talkappmobile.dao.DatabaseHelper;
import org.talkappmobile.dao.SentenceDao;
import org.talkappmobile.dao.TopicDao;
import org.talkappmobile.dao.WordRepetitionProgressDao;
import org.talkappmobile.dao.WordSetDao;
import org.talkappmobile.dao.WordTranslationDao;
import org.talkappmobile.dao.impl.SentenceDaoImpl;
import org.talkappmobile.dao.impl.WordRepetitionProgressDaoImpl;
import org.talkappmobile.dao.impl.WordSetDaoImpl;
import org.talkappmobile.events.OpenWordSetForStudyingEM;
import org.talkappmobile.events.ParentScreenOutdatedEM;
import org.talkappmobile.events.WordSetsNewFilterAppliedEM;
import org.talkappmobile.mappings.SentenceMapping;
import org.talkappmobile.mappings.WordRepetitionProgressMapping;
import org.talkappmobile.mappings.WordSetMapping;
import org.talkappmobile.model.Word2Tokens;
import org.talkappmobile.model.WordSet;
import org.talkappmobile.service.impl.BackendServerFactoryBean;
import org.talkappmobile.service.impl.LocalDataServiceImpl;
import org.talkappmobile.service.impl.LoggerBean;
import org.talkappmobile.service.impl.RequestExecutor;
import org.talkappmobile.service.impl.ServiceFactoryBean;
import org.talkappmobile.service.impl.WordRepetitionProgressServiceImpl;
import org.talkappmobile.service.impl.WordSetExperienceUtilsImpl;
import org.talkappmobile.service.impl.WordSetServiceImpl;
import org.talkappmobile.service.mapper.WordSetMapper;

import java.sql.SQLException;
import java.util.List;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "org.talkappmobile.dao.impl")
public class WordSetsListFragmentTest extends BaseTest {

    private WordSetsListFragment wordSetsListFragment;
    private WordRepetitionProgressServiceImpl repetitionProgressService;
    private WordSetExperienceUtilsImpl experienceUtils;
    private WordSetServiceImpl wordSetService;
    private EventBus eventBus;
    private WordSetsListListView wordSetsListView;
    private LocalDataServiceImpl localDataService;

    @Before
    public void setup() throws SQLException {
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(RuntimeEnvironment.application, DatabaseHelper.class);
        SentenceDao sentenceDao = new SentenceDaoImpl(databaseHelper.getConnectionSource(), SentenceMapping.class);
        WordSetDao wordSetDao = new WordSetDaoImpl(databaseHelper.getConnectionSource(), WordSetMapping.class);
        WordRepetitionProgressDao exerciseDao = new WordRepetitionProgressDaoImpl(databaseHelper.getConnectionSource(), WordRepetitionProgressMapping.class);

        LoggerBean logger = new LoggerBean();
        ObjectMapper mapper = new ObjectMapper();
        localDataService = new LocalDataServiceImpl(wordSetDao, mock(TopicDao.class), sentenceDao, mock(WordTranslationDao.class), mapper, logger);

        BackendServerFactoryBean factory = new BackendServerFactoryBean();
        Whitebox.setInternalState(factory, "logger", new LoggerBean());
        Whitebox.setInternalState(factory, "requestExecutor", new RequestExecutor());
        ServiceFactoryBean mockServiceFactoryBean = mock(ServiceFactoryBean.class);
        when(mockServiceFactoryBean.getLocalDataService()).thenReturn(localDataService);

        repetitionProgressService = new WordRepetitionProgressServiceImpl(exerciseDao, wordSetDao, sentenceDao, mapper);
        when(mockServiceFactoryBean.getPracticeWordSetExerciseRepository()).thenReturn(repetitionProgressService);

        experienceUtils = new WordSetExperienceUtilsImpl();
        wordSetService = new WordSetServiceImpl(wordSetDao, experienceUtils, new WordSetMapper(mapper));
        when(mockServiceFactoryBean.getWordSetExperienceRepository()).thenReturn(wordSetService);

        WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory = mock(WaitingForProgressBarManagerFactory.class);
        when(waitingForProgressBarManagerFactory.get(any(View.class), any(WordSetsListListView.class))).thenReturn(mock(WaitingForProgressBarManager.class));

        Whitebox.setInternalState(factory, "serviceFactory", mockServiceFactoryBean);
        eventBus = mock(EventBus.class);

        wordSetsListFragment = new WordSetsListFragment();
        Whitebox.setInternalState(wordSetsListFragment, "backendServerFactory", factory);
        Whitebox.setInternalState(wordSetsListFragment, "serviceFactory", mockServiceFactoryBean);
        TabHost tabHost = mock(TabHost.class);
        when(tabHost.newTabSpec(anyString())).thenReturn(mock(TabHost.TabSpec.class));
        Whitebox.setInternalState(wordSetsListFragment, "tabHost", tabHost);
        Whitebox.setInternalState(wordSetsListFragment, "eventBus", eventBus);
        Whitebox.setInternalState(wordSetsListFragment, "waitingForProgressBarManagerFactory", waitingForProgressBarManagerFactory);
        Whitebox.setInternalState(wordSetsListFragment, "progressBarView", mock(View.class));
        wordSetsListView = mock(WordSetsListListView.class);
        Whitebox.setInternalState(wordSetsListFragment, "wordSetsListView", wordSetsListView);
    }

    @Test
    public void testOpeningAndClosingOfPreparedWordSet() {
        Whitebox.setInternalState(wordSetsListFragment, "repetitionMode", false);

        wordSetsListFragment.init();
        ArgumentCaptor<List<WordSet>> captorWords = forClass(List.class);
        verify(wordSetsListView).addAll(captorWords.capture());
        List<WordSet> wordSetList = captorWords.getValue();

        int position = 1;
        when(wordSetsListView.getWordSet(position)).thenReturn(wordSetList.get(position));
        wordSetsListFragment.onItemClick(position);

        OpenWordSetForStudyingEM em = getEM(OpenWordSetForStudyingEM.class, eventBus);

        WordSet wordSet = em.getWordSet();
        List<Word2Tokens> words = wordSet.getWords();
        assertNotNull(wordSet);
        assertFalse(words.isEmpty());
        checkWord2Tokens(wordSet, words);
        assertFalse(em.isRepetitionMode());

        List<WordSet> allWordSets = localDataService.findAllWordSets();
        for (WordSet set : allWordSets) {
            checkWord2Tokens(set, set.getWords());
        }

        reset(wordSetsListView);
        /*
          closing
         */
        wordSetsListFragment.onMessageEvent(new ParentScreenOutdatedEM());

        ArgumentCaptor<List<WordSet>> captor = forClass(List.class);
        verify(wordSetsListView).addAll(captor.capture());
        List<WordSet> wordSetListForRefresh = captor.getValue();
        assertNotEquals(0, wordSetListForRefresh.size() % 100);
        verify(wordSetsListView).refreshModel();

        for (WordSet set : wordSetListForRefresh) {
            checkWord2Tokens(set, set.getWords());
        }

        verify(eventBus).post(any(WordSetsNewFilterAppliedEM.class));
    }

    @After
    public void tearDown() {
        OpenHelperManager.releaseHelper();
    }

    private void checkWord2Tokens(WordSet wordSet, List<Word2Tokens> words) {
        for (Word2Tokens word : words) {
            assertNotNull(word.getSourceWordSetId());
            assertEquals(wordSet.getId(), word.getSourceWordSetId().intValue());
        }
    }
}
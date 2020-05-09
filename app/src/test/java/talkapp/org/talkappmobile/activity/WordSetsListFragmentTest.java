package talkapp.org.talkappmobile.activity;

import android.view.View;
import android.widget.TabHost;

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
import java.util.List;

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.TestHelper;
import talkapp.org.talkappmobile.activity.custom.PhraseSetsRecyclerView;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.component.PresenterFactoryProvider;
import talkapp.org.talkappmobile.events.OpenWordSetForStudyingEM;
import talkapp.org.talkappmobile.events.ParentScreenOutdatedEM;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.presenter.PresenterFactoryImpl;
import talkapp.org.talkappmobile.repository.RepositoryFactory;
import talkapp.org.talkappmobile.repository.RepositoryFactoryProvider;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.ServiceFactoryImpl;
import talkapp.org.talkappmobile.widget.adapter.filterable.FilterableAdapter;

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
import static talkapp.org.talkappmobile.activity.WordSetsListFragment.WordSetFilter.ONLY_NEW_WORD_SETS;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class WordSetsListFragmentTest {

    private WordSetsListFragment wordSetsListFragment;
    private PhraseSetsRecyclerView wordSetsListView;
    private TestHelper testHelper;
    private EventBus eventBus;
    private FilterableAdapter adapter;
    private ServiceFactory serviceFactory;

    @Before
    public void setup() throws SQLException {
        testHelper = new TestHelper();

        RepositoryFactory repositoryFactory = RepositoryFactoryProvider.get(RuntimeEnvironment.application);
        serviceFactory = new ServiceFactoryImpl(repositoryFactory);

        WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory = mock(WaitingForProgressBarManagerFactory.class);
        when(waitingForProgressBarManagerFactory.get(any(View.class), any(PhraseSetsRecyclerView.class))).thenReturn(mock(WaitingForProgressBarManager.class));
        eventBus = testHelper.getEventBusMock();

        wordSetsListFragment = new WordSetsListFragment();
        TabHost tabHost = mock(TabHost.class);
        when(tabHost.newTabSpec(anyString())).thenReturn(mock(TabHost.TabSpec.class));
        Whitebox.setInternalState(wordSetsListFragment, "tabHost", tabHost);
        Whitebox.setInternalState(wordSetsListFragment, "eventBus", eventBus);
        PresenterFactoryProvider presenterFactoryProvider = new PresenterFactoryProvider();
        presenterFactoryProvider.setPresenterFactory(new PresenterFactoryImpl(serviceFactory));
        Whitebox.setInternalState(wordSetsListFragment, "presenterFactoryProvider", presenterFactoryProvider);
        Whitebox.setInternalState(wordSetsListFragment, "waitingForProgressBarManagerFactory", waitingForProgressBarManagerFactory);
        Whitebox.setInternalState(wordSetsListFragment, "progressBarView", mock(View.class));
        wordSetsListView = mock(PhraseSetsRecyclerView.class);
        adapter = mock(FilterableAdapter.class);
        when(wordSetsListView.getAdapter()).thenReturn(adapter);
        Whitebox.setInternalState(wordSetsListFragment, "phraseSetsRecyclerView", wordSetsListView);
    }

    @Test
    public void testOpeningAndClosingOfPreparedWordSet() {
        Whitebox.setInternalState(wordSetsListFragment, "repetitionMode", false);

        wordSetsListFragment.init();
        ArgumentCaptor<FilterableAdapter> argumentCaptor = forClass(FilterableAdapter.class);
        verify(wordSetsListView).setAdapter(argumentCaptor.capture());
        FilterableAdapter adapter = argumentCaptor.getValue();
        List<WordSet> wordSetList = adapter.getItems();

        int position = 1;
        when(this.adapter.get(position)).thenReturn(wordSetList.get(position));
        wordSetsListFragment.onItemClick(position);

        OpenWordSetForStudyingEM em = testHelper.getEM(OpenWordSetForStudyingEM.class, 1);

        WordSet wordSet = em.getWordSet();
        List<Word2Tokens> words = wordSet.getWords();
        assertNotNull(wordSet);
        assertFalse(words.isEmpty());
        checkWord2Tokens(wordSet, words);
        assertFalse(em.isRepetitionMode());

        List<WordSet> allWordSets = serviceFactory.getWordSetService().getWordSets(null);
        for (WordSet set : allWordSets) {
            checkWord2Tokens(set, set.getWords());
        }

        reset(wordSetsListView);
        when(wordSetsListView.getAdapter()).thenReturn(this.adapter);
        /*
          closing
         */
        wordSetsListFragment.onMessageEvent(new ParentScreenOutdatedEM());

        argumentCaptor = forClass(FilterableAdapter.class);
        verify(this.wordSetsListView).setAdapter(argumentCaptor.capture());
        adapter = argumentCaptor.getValue();
        List<WordSet> wordSetListForRefresh = adapter.getItems();
        assertNotEquals(0, wordSetListForRefresh.size() % 100);

        for (WordSet set : wordSetListForRefresh) {
            checkWord2Tokens(set, set.getWords());
        }

        verify(this.adapter).filterOut(ONLY_NEW_WORD_SETS.getFilter());
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
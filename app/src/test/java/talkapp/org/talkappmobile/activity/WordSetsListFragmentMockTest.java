package talkapp.org.talkappmobile.activity;

import android.widget.TabHost;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import java.util.LinkedList;

import talkapp.org.talkappmobile.activity.custom.PhraseSetsListView;
import talkapp.org.talkappmobile.model.RepetitionClass;
import talkapp.org.talkappmobile.model.WordSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static talkapp.org.talkappmobile.activity.WordSetsListFragment.NEW;
import static talkapp.org.talkappmobile.model.RepetitionClass.SEEN;

@RunWith(MockitoJUnitRunner.class)
public class WordSetsListFragmentMockTest {
    @Mock
    private PhraseSetsListView.Adapter adapter;
    @Mock
    private PhraseSetsListView wordSetsListView;
    @Mock
    private TabHost tabHost;
    @Mock
    private EventBus eventBus;
    private WordSetsListFragment wordSetsListFragment = new WordSetsListFragment();

    @Before
    public void init() {
        when(wordSetsListView.getAdapter()).thenReturn(adapter);
        Whitebox.setInternalState(wordSetsListFragment, "phraseSetsListView", wordSetsListView);
        Whitebox.setInternalState(wordSetsListFragment, "tabHost", tabHost);
        Whitebox.setInternalState(wordSetsListFragment, "eventBus", eventBus);
    }

    @Test
    public void testOnWordSetsRefreshed_repetitionClassNull() {
        LinkedList<WordSet> wordSets = new LinkedList<>();
        wordSetsListFragment.onWordSetsRefreshed(wordSets, null);
        verify(adapter).addAll(wordSets);
        verify(adapter).filterNew();
        verify(tabHost).setCurrentTabByTag(NEW);
    }

    @Test
    public void testOnWordSetsRefreshed_repetitionClassNew() {
        LinkedList<WordSet> wordSets = new LinkedList<>();

        wordSetsListFragment.onWordSetsRefreshed(wordSets, RepetitionClass.NEW);
        verify(adapter).addAll(wordSets);
        verify(adapter).filterNewRep();
        verify(tabHost).setCurrentTabByTag(RepetitionClass.NEW.name());
    }

    @Test
    public void testOnWordSetsRefreshed_repetitionClassSeen() {
        LinkedList<WordSet> wordSets = new LinkedList<>();

        wordSetsListFragment.onWordSetsRefreshed(wordSets, SEEN);
        verify(adapter).addAll(wordSets);
        verify(adapter).filterSeenRep();
        verify(tabHost).setCurrentTabByTag(SEEN.name());
    }
}
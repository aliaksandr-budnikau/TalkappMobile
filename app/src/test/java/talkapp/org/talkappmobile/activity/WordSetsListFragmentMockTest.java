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

import talkapp.org.talkappmobile.activity.custom.WordSetsListListView;
import talkapp.org.talkappmobile.events.WordSetsNewFilterAppliedEM;
import talkapp.org.talkappmobile.events.WordSetsNewRepFilterAppliedEM;
import talkapp.org.talkappmobile.events.WordSetsSeenRepFilterAppliedEM;
import talkapp.org.talkappmobile.model.RepetitionClass;
import talkapp.org.talkappmobile.model.WordSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static talkapp.org.talkappmobile.activity.WordSetsListFragment.NEW;
import static talkapp.org.talkappmobile.model.RepetitionClass.SEEN;

@RunWith(MockitoJUnitRunner.class)
public class WordSetsListFragmentMockTest {
    @Mock
    private WordSetsListListView wordSetsListView;
    @Mock
    private TabHost tabHost;
    @Mock
    private EventBus eventBus;
    private WordSetsListFragment wordSetsListFragment = new WordSetsListFragment();

    @Before
    public void init() {
        Whitebox.setInternalState(wordSetsListFragment, "wordSetsListView", wordSetsListView);
        Whitebox.setInternalState(wordSetsListFragment, "tabHost", tabHost);
        Whitebox.setInternalState(wordSetsListFragment, "eventBus", eventBus);
    }

    @Test
    public void testOnWordSetsRefreshed_repetitionClassNull() {
        LinkedList<WordSet> wordSets = new LinkedList<>();

        wordSetsListFragment.onWordSetsRefreshed(wordSets, null);
        verify(wordSetsListView).refreshModel();
        verify(wordSetsListView).addAll(wordSets);
        verify(eventBus).post(any(WordSetsNewFilterAppliedEM.class));
        verify(tabHost).setCurrentTabByTag(NEW);
    }

    @Test
    public void testOnWordSetsRefreshed_repetitionClassNew() {
        LinkedList<WordSet> wordSets = new LinkedList<>();

        wordSetsListFragment.onWordSetsRefreshed(wordSets, RepetitionClass.NEW);
        verify(wordSetsListView).refreshModel();
        verify(wordSetsListView).addAll(wordSets);
        verify(eventBus).post(any(WordSetsNewRepFilterAppliedEM.class));
        verify(tabHost).setCurrentTabByTag(RepetitionClass.NEW.name());
    }

    @Test
    public void testOnWordSetsRefreshed_repetitionClassSeen() {
        LinkedList<WordSet> wordSets = new LinkedList<>();

        wordSetsListFragment.onWordSetsRefreshed(wordSets, SEEN);
        verify(wordSetsListView).refreshModel();
        verify(wordSetsListView).addAll(wordSets);
        verify(eventBus).post(any(WordSetsSeenRepFilterAppliedEM.class));
        verify(tabHost).setCurrentTabByTag(SEEN.name());
    }
}
package talkapp.org.talkappmobile.activity;

import android.app.Activity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import java.util.List;

import talkapp.org.talkappmobile.TestHelper;
import talkapp.org.talkappmobile.activity.custom.WordSetVocabularyView;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddingNewWordSetFragmentTest {

    @Test
    public void testOnMessageEventNewWordSuccessfullySubmittedEM() {
        AddingNewWordSetFragment addingNewWordSetFragment = mock(AddingNewWordSetFragment.class, CALLS_REAL_METHODS);
        when(addingNewWordSetFragment.getActivity()).thenReturn(mock(Activity.class));
        TestHelper testHelper = new TestHelper();

        WordSetVocabularyView wordSetVocabularyView = mock(WordSetVocabularyView.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "wordSetVocabularyView", wordSetVocabularyView);
        Whitebox.setInternalState(addingNewWordSetFragment, "eventBus", testHelper.getEventBusMock());

        addingNewWordSetFragment.onNewWordSuccessfullySubmitted(mock(WordSet.class));

        ArgumentCaptor<WordSetVocabularyView.VocabularyAdapter> captor = ArgumentCaptor.forClass(WordSetVocabularyView.VocabularyAdapter.class);
        verify(wordSetVocabularyView).setAdapter(captor.capture());
        List<WordTranslation> translations = captor.getValue().getTranslations();
        for (WordTranslation translation : translations) {
            assertNull(translation.getWord());
            assertNull(translation.getTranslation());
        }
    }
}
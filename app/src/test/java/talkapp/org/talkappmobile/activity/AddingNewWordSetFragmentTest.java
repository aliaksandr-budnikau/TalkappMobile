package talkapp.org.talkappmobile.activity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import java.util.List;

import talkapp.org.talkappmobile.TestHelper;
import talkapp.org.talkappmobile.activity.custom.WordSetVocabularyView;
import talkapp.org.talkappmobile.events.NewWordSetDraftWasChangedEM;
import talkapp.org.talkappmobile.events.NewWordSuccessfullySubmittedEM;
import talkapp.org.talkappmobile.events.WordSetVocabularyLoadedEM;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class AddingNewWordSetFragmentTest {

    @Test
    public void testOnMessageEventNewWordSuccessfullySubmittedEM() {
        AddingNewWordSetFragment addingNewWordSetFragment = new AddingNewWordSetFragment();
        TestHelper testHelper = new TestHelper();

        WordSetVocabularyView wordSetVocabularyView = mock(WordSetVocabularyView.class);
        Whitebox.setInternalState(addingNewWordSetFragment, "wordSetVocabularyView", wordSetVocabularyView);
        Whitebox.setInternalState(addingNewWordSetFragment, "eventBus", testHelper.getEventBusMock());

        addingNewWordSetFragment.onMessageEvent(new NewWordSuccessfullySubmittedEM(new WordSet()));

        WordSetVocabularyLoadedEM vocabularyLoadedEM = testHelper.getEM(WordSetVocabularyLoadedEM.class, false);
        WordTranslation[] translations = vocabularyLoadedEM.getTranslations();
        for (WordTranslation translation : translations) {
            assertTrue(translation.getWord().startsWith("Word # "));
            assertNull(translation.getTranslation());
        }
        NewWordSetDraftWasChangedEM draftWasChangedEM = testHelper.getEM(NewWordSetDraftWasChangedEM.class);
        List<String> words = draftWasChangedEM.getWords();
        for (String word : words) {
            assertEquals("", word);
        }
    }
}
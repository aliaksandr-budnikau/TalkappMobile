package talkapp.org.talkappmobile.activity.presenter;

import android.os.AsyncTask;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetVocabularyInteractor;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetVocabularyView;
import talkapp.org.talkappmobile.component.Speaker;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PracticeWordSetVocabularyPresenterAndInteractorIntegTest extends PresenterAndInteractorIntegTest {
    @Mock
    private PracticeWordSetVocabularyView view;
    private Speaker speaker;
    private PracticeWordSetVocabularyInteractor interactor;
    private AsyncTask<Void, Void, Void> value;

    @Before
    public void setup() {
        interactor = getClassForInjection().getPracticeWordSetVocabularyInteractor();
        speaker = getClassForInjection().getSpeaker();
    }

    @Test
    public void test() {
        login();

        List<String> words = asList("age", "anniversary", "birth");

        int id = -1;
        WordSet wordSet = new WordSet();
        wordSet.setId(id);
        wordSet.setWords(words);
        wordSet.setTopicId("topicId");

        PracticeWordSetVocabularyPresenter presenter = new PracticeWordSetVocabularyPresenter(wordSet, view, interactor);

        presenter.onResume();
        ArgumentCaptor<List<WordTranslation>> wordTranslationsCaptor = forClass(List.class);
        verify(view).setWordSetVocabularyList(wordTranslationsCaptor.capture());
        assertFalse(wordTranslationsCaptor.getValue().isEmpty());
        assertEquals(words.size(), wordTranslationsCaptor.getValue().size());
        List<WordTranslation> translations = wordTranslationsCaptor.getValue();
        for (WordTranslation translation : translations) {
            assertTrue(words.contains(translation.getWord()));
        }
        verify(speaker, times(0)).speak(anyString());
        reset(view);
        reset(speaker);

        value = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                return null;
            }
        };
        when(speaker.speak(translations.get(0).getWord())).thenReturn(value);
        presenter.onPronounceWordButtonClick(translations.get(0));
        verify(speaker).speak(translations.get(0).getWord());
        reset(view);
        reset(speaker);

        presenter.onPronounceWordButtonClick(null);
        verify(speaker, times(0)).speak(anyString());
    }
}
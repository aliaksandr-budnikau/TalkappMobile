package talkapp.org.talkappmobile.activity.interactor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.LinkedList;

import talkapp.org.talkappmobile.activity.listener.OnMainActivityDefaultFragmentListener;
import talkapp.org.talkappmobile.model.Task;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static talkapp.org.talkappmobile.model.RepetitionClass.NEW;


@RunWith(MockitoJUnitRunner.class)
public class MainActivityDefaultFragmentInteractorTest {

    public static final int COUNT = 12;
    @Mock
    private WordRepetitionProgressService exerciseService;
    @Mock
    private OnMainActivityDefaultFragmentListener listener;
    private MainActivityDefaultFragmentInteractor interactor;

    @Before
    public void setUp() throws Exception {
        interactor = new MainActivityDefaultFragmentInteractor(exerciseService, "%s", null, null, null, null, null);
    }

    @Test
    public void initWordsForRepetition() {
        LinkedList<WordSet> wordSets = new LinkedList<>();
        wordSets.addLast(new WordSet());
        wordSets.getLast().setWords(new LinkedList<Word2Tokens>());
        String word = "sdfsd";
        wordSets.getLast().getWords().add(new Word2Tokens(word, word, 3));
        wordSets.getLast().getWords().add(new Word2Tokens(word, word, 3));
        wordSets.addLast(new WordSet());
        wordSets.getLast().setWords(new LinkedList<Word2Tokens>());
        wordSets.getLast().getWords().add(new Word2Tokens(word, word, 3));

        when(exerciseService.findFinishedWordSetsSortByUpdatedDate(24 * 2)).thenReturn(wordSets);
        when(exerciseService.getMaxWordSetSize()).thenReturn(2);
        interactor.initWordsForRepetition(listener);

        verify(listener).onWordsForRepetitionCounted(1);
    }

    @Test
    public void findTasks() {
        LinkedList<WordSet> wordSets = new LinkedList<>();
        wordSets.addLast(new WordSet());
        String word = "sdfsd";
        wordSets.getLast().setWords(new LinkedList<Word2Tokens>());
        wordSets.getLast().setRepetitionClass(NEW);
        wordSets.getLast().getWords().add(new Word2Tokens(word, word, 3));
        wordSets.getLast().getWords().add(new Word2Tokens(word, word, 3));
        wordSets.addLast(new WordSet());
        wordSets.getLast().setRepetitionClass(NEW);
        wordSets.getLast().setWords(new LinkedList<Word2Tokens>());
        wordSets.getLast().getWords().add(new Word2Tokens(word, word, 3));

        when(exerciseService.getMaxWordSetSize()).thenReturn(2);
        when(exerciseService.findFinishedWordSetsSortByUpdatedDate(24 * 2)).thenReturn(wordSets);

        interactor.findTasks(listener);

        ArgumentCaptor<LinkedList> captor = ArgumentCaptor.forClass(LinkedList.class);
        verify(listener).onFoundTasks(captor.capture());
        Task firstTask = (Task) captor.getValue().get(0);
        assertEquals("1", firstTask.getTitle());
    }
}
package talkapp.org.talkappmobile.activity.interactor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.talkappmobile.model.Word2Tokens;
import org.talkappmobile.model.WordSet;
import org.talkappmobile.service.WordRepetitionProgressService;

import java.util.LinkedList;

import talkapp.org.talkappmobile.activity.listener.OnMainActivityDefaultFragmentListener;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class MainActivityDefaultFragmentInteractorTest {

    @Mock
    private WordRepetitionProgressService exerciseService;
    @Mock
    private OnMainActivityDefaultFragmentListener listener;
    @InjectMocks
    private MainActivityDefaultFragmentInteractor interactor;

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
}
package talkapp.org.talkappmobile.component.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseService;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SentenceProviderRepetitionStrategyTest {
    @Mock
    DataServer server;
    @Mock
    PracticeWordSetExerciseService exerciseService;
    @InjectMocks
    private SentenceProviderRepetitionStrategy strategy;

    @Test
    public void findByWordAndWordSetId() {
        // setup
        int wordSetId = 3;
        Word2Tokens word = new Word2Tokens();

        // when
        when(exerciseService.findByWordAndWordSetId(word, wordSetId)).thenReturn(new Sentence());
        List<Sentence> list = strategy.findByWordAndWordSetId(word, wordSetId);

        // then
        list.clear();
    }
}
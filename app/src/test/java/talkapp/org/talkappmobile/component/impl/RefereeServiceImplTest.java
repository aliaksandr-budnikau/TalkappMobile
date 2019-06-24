package talkapp.org.talkappmobile.component.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import talkapp.org.talkappmobile.component.EqualityScorer;
import talkapp.org.talkappmobile.component.SentenceService;
import org.talkappmobile.model.UncheckedAnswer;
import org.talkappmobile.model.WordSet;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static talkapp.org.talkappmobile.component.impl.RefereeServiceImpl.EQUALITY_THRESHOLD;

@RunWith(MockitoJUnitRunner.class)
public class RefereeServiceImplTest {
    @Mock
    private SentenceService sentenceService;
    @Mock
    private EqualityScorer equalityScorer;
    @InjectMocks
    private RefereeServiceImpl service;

    @Test
    public void checkAnswer_thereIsNoErrorAndGoodAnswer1() {
        // setup
        UncheckedAnswer answer = new UncheckedAnswer();
        answer.setActualAnswer("Who is duty today?");
        answer.setExpectedAnswer("Who is duty today?");

        WordSet wordSet = new WordSet();
        wordSet.setTrainingExperience(0);

        // when
        when(equalityScorer.score(answer.getExpectedAnswer(), answer.getActualAnswer(), answer.getCurrentWord())).thenReturn(EQUALITY_THRESHOLD);
        boolean result = service.checkAnswer(answer);

        // then
        assertTrue(result);
    }

    @Test
    public void checkAnswer_thereIsNoErrorButNotGoodAnswer() {
        // setup
        UncheckedAnswer answer = new UncheckedAnswer();
        answer.setActualAnswer("Who are duty today?");
        answer.setExpectedAnswer("Who are duty today?");

        // when
        when(equalityScorer.score(answer.getExpectedAnswer(), answer.getActualAnswer(), answer.getCurrentWord())).thenReturn(79);
        boolean result = service.checkAnswer(answer);

        // then
        assertTrue(!result);
    }
}
package talkapp.org.talkappmobile.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import talkapp.org.talkappmobile.model.UncheckedAnswer;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.EqualityScorer;
import talkapp.org.talkappmobile.service.SentenceService;

import static org.junit.Assert.assertTrue;
import static talkapp.org.talkappmobile.service.impl.RefereeServiceImpl.EQUALITY_THRESHOLD;

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
        Mockito.when(equalityScorer.score(answer.getExpectedAnswer(), answer.getActualAnswer(), answer.getCurrentWord())).thenReturn(EQUALITY_THRESHOLD);
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
        Mockito.when(equalityScorer.score(answer.getExpectedAnswer(), answer.getActualAnswer(), answer.getCurrentWord())).thenReturn(79);
        boolean result = service.checkAnswer(answer);

        // then
        assertTrue(!result);
    }
}
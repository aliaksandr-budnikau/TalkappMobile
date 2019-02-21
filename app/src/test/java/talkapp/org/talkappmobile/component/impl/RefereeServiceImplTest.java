package talkapp.org.talkappmobile.component.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import talkapp.org.talkappmobile.component.EqualityScorer;
import talkapp.org.talkappmobile.component.GrammarCheckService;
import talkapp.org.talkappmobile.model.UncheckedAnswer;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static talkapp.org.talkappmobile.component.impl.RefereeServiceImpl.EQUALITY_THRESHOLD;

@RunWith(MockitoJUnitRunner.class)
public class RefereeServiceImplTest {
    @Mock
    private GrammarCheckService grammarCheckService;
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
        answer.setWordSetExperienceId(3);

        WordSet wordSet = new WordSet();
        wordSet.setMaxTrainingExperience(12);
        wordSet.setTrainingExperience(0);

        // when
        when(equalityScorer.score(answer.getExpectedAnswer(), answer.getActualAnswer())).thenReturn(EQUALITY_THRESHOLD);
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
        when(equalityScorer.score(answer.getExpectedAnswer(), answer.getActualAnswer())).thenReturn(79);
        boolean result = service.checkAnswer(answer);

        // then
        assertTrue(!result);
    }
}
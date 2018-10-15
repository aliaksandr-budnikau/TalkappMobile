package talkapp.org.talkappmobile.component.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import talkapp.org.talkappmobile.component.EqualityScorer;
import talkapp.org.talkappmobile.component.GrammarCheckService;
import talkapp.org.talkappmobile.component.database.WordSetExperienceRepository;
import talkapp.org.talkappmobile.model.AnswerCheckingResult;
import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.model.UncheckedAnswer;
import talkapp.org.talkappmobile.model.WordSetExperience;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RefereeServiceImplTest {
    @Mock
    private GrammarCheckService grammarCheckService;
    @Mock
    private WordSetExperienceRepository experienceRepository;
    @Mock
    private EqualityScorer equalityScorer;
    @InjectMocks
    private RefereeServiceImpl service;

    @Test
    public void checkAnswer_thereIsOneError() {
        // setup
        UncheckedAnswer answer = new UncheckedAnswer();
        answer.setActualAnswer("Hello worlad");
        answer.setExpectedAnswer("Hello world");

        // when
        when(grammarCheckService.check(answer.getActualAnswer())).thenReturn(singletonList(new GrammarError()));
        AnswerCheckingResult result = service.checkAnswer(answer);

        // then
        assertEquals(1, result.getErrors().size());
        verify(experienceRepository, times(0)).increaseExperience(anyString(), anyInt());
    }

    @Test
    public void checkAnswer_thereAreTreeErrors() {
        // setup
        UncheckedAnswer answer = new UncheckedAnswer();
        answer.setActualAnswer("I is a enginear");
        answer.setExpectedAnswer("I is a engineer");

        // when
        when(grammarCheckService.check(answer.getActualAnswer())).thenReturn(asList(new GrammarError(), new GrammarError(), new GrammarError()));
        AnswerCheckingResult result = service.checkAnswer(answer);

        // then
        assertEquals(3, result.getErrors().size());
        verify(experienceRepository, times(0)).increaseExperience(anyString(), anyInt());
    }

    @Test
    public void checkAnswer_thereIsNoErrorAndGoodAnswer1() {
        // setup
        UncheckedAnswer answer = new UncheckedAnswer();
        answer.setActualAnswer("Who is duty today?");
        answer.setExpectedAnswer("Who is duty today?");
        answer.setWordSetExperienceId("233");

        WordSetExperience experience = new WordSetExperience();
        experience.setTrainingExperience(0);
        experience.setMaxTrainingExperience(12);

        int value = 1223;
        int delta = 1;

        // when
        when(equalityScorer.score(answer.getExpectedAnswer(), answer.getActualAnswer())).thenReturn(80);
        when(grammarCheckService.check(answer.getActualAnswer())).thenReturn(new ArrayList<GrammarError>());
        when(experienceRepository.increaseExperience(answer.getWordSetExperienceId(), delta)).thenReturn(value);
        AnswerCheckingResult result = service.checkAnswer(answer);

        // then
        assertTrue(result.getErrors().isEmpty());
        verify(experienceRepository).increaseExperience(answer.getWordSetExperienceId(), delta);
    }

    @Test
    public void checkAnswer_thereIsNoErrorButNotGoodAnswer() {
        // setup
        UncheckedAnswer answer = new UncheckedAnswer();
        answer.setActualAnswer("Who are duty today?");
        answer.setExpectedAnswer("Who are duty today?");

        // when
        when(equalityScorer.score(answer.getExpectedAnswer(), answer.getActualAnswer())).thenReturn(79);
        when(grammarCheckService.check(answer.getActualAnswer())).thenReturn(new ArrayList<GrammarError>());
        AnswerCheckingResult result = service.checkAnswer(answer);

        // then
        assertTrue(result.getErrors().isEmpty());
    }
}
package talkapp.org.talkappmobile.component.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EqualityScorerImplTest {

    private EqualityScorerImpl scorer = new EqualityScorerImpl();

    @Test
    public void score_100() {
        int score = scorer.score("Who is on duty today?", "Who is on duty today?");
        assertEquals(100, score);
    }

    @Test
    public void score_101() {
        int score = scorer.score("Who is on duty today?", "Who is duty today?");
        assertEquals(80, score);
    }

    @Test
    public void score_102() {
        int score = scorer.score("Who is on duty today?", "Who is duty tomorrow?");
        assertEquals(60, score);
    }

    @Test
    public void score_103() {
        int score = scorer.score("Who is on duty today?", "Who is on duty tomorrow?");
        assertEquals(80, score);
    }

    @Test
    public void score_104() {
        int score = scorer.score("Gray cats run.", "The gray cats run");
        assertEquals(75, score);
    }
}
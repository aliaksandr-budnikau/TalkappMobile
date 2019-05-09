package talkapp.org.talkappmobile.component.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;

import talkapp.org.talkappmobile.model.Word2Tokens;

import static org.junit.Assert.assertEquals;

public class EqualityScorerBeanTest {

    private EqualityScorerBean scorer = new EqualityScorerBean();
    private Word2Tokens word2Tokens;

    @Before
    public void setUp() throws Exception {
        String wordAsText = "{\n" +
                "  \"word\" : \"abandon\",\n" +
                "  \"tokens\" : \"abandon\"\n" +
                "}";
        word2Tokens = new ObjectMapper().readValue(wordAsText, Word2Tokens.class);
    }

    @Test
    public void score_100() {
        int score = scorer.score("Who is on duty today?", "Who is on duty today?", word2Tokens);
        assertEquals(100, score);
    }

    @Test
    public void score_100_2() {
        int score = scorer.score("Who is on duty today, you?", "Who is on duty today you?", word2Tokens);
        assertEquals(100, score);
    }

    @Test
    public void score_101() {
        int score = scorer.score("Who is on duty today?", "Who is duty today?", word2Tokens);
        assertEquals(80, score);
    }

    @Test
    public void score_102() {
        int score = scorer.score("Who is on duty today?", "Who is duty tomorrow?", word2Tokens);
        assertEquals(60, score);
    }

    @Test
    public void score_103() {
        int score = scorer.score("Who is on duty today?", "Who is on duty tomorrow?", word2Tokens);
        assertEquals(80, score);
    }

    @Test
    public void score_104() {
        int score = scorer.score("Gray cats run.", "The gray cats run", word2Tokens);
        assertEquals(75, score);
    }

    @Test
    public void score_105() {
        int score = scorer.score("Abandoned building on Bayard Street.", "Flee building on Bayard Street", word2Tokens);
        assertEquals(0, score);
    }
}
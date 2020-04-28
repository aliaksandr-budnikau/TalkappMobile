package talkapp.org.talkappmobile.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.service.EqualityScorerImpl;

import static org.junit.Assert.assertEquals;

public class EqualityScorerImplTest {

    private EqualityScorerImpl scorer = new EqualityScorerImpl();
    private Word2Tokens abandon;
    private Word2Tokens indigenous;
    private Word2Tokens specie;

    @Before
    public void setUp() throws Exception {
        String abandonAsText = "{\n" +
                "  \"word\" : \"abandon\",\n" +
                "  \"tokens\" : \"abandon\"\n" +
                "}";
        abandon = new ObjectMapper().readValue(abandonAsText, Word2Tokens.class);

        String indigenousAsText = "{\n" +
                "  \"word\" : \"indigenous\",\n" +
                "  \"tokens\" : \"indigenous\"\n" +
                "}";
        indigenous = new ObjectMapper().readValue(indigenousAsText, Word2Tokens.class);

        String specieAsText = "{\n" +
                "  \"word\" : \"species\",\n" +
                "  \"tokens\" : \"specie\"\n" +
                "}";
        specie = new ObjectMapper().readValue(specieAsText, Word2Tokens.class);
    }

    @Test
    public void score_100() {
        int score = scorer.score("Who is on duty today?", "Who is on duty today?", abandon);
        assertEquals(100, score);
    }

    @Test
    public void score_100_2() {
        int score = scorer.score("Who is on duty today, you?", "Who is on duty today you?", abandon);
        assertEquals(100, score);
    }

    @Test
    public void score_101() {
        int score = scorer.score("Who is on duty today?", "Who is duty today?", abandon);
        assertEquals(80, score);
    }

    @Test
    public void score_102() {
        int score = scorer.score("Who is on duty today?", "Who is duty tomorrow?", abandon);
        assertEquals(60, score);
    }

    @Test
    public void score_103() {
        int score = scorer.score("Who is on duty today?", "Who is on duty tomorrow?", abandon);
        assertEquals(80, score);
    }

    @Test
    public void score_104() {
        int score = scorer.score("Gray cats run.", "The gray cats run", abandon);
        assertEquals(75, score);
    }

    @Test
    public void score_105() {
        int score = scorer.score("Abandoned building on Bayard Street.", "Flee building on Bayard Street", abandon);
        assertEquals(0, score);
    }

    @Test
    public void score_106() {
        int score = scorer.score("Indigenous species, the human race.", "Local species the human race.", indigenous);
        assertEquals(0, score);
    }

    @Test
    public void score_107() {
        int score = scorer.score("Indigenous species, the human race.", "Local species the human race.", specie);
        assertEquals(80, score);
    }
}
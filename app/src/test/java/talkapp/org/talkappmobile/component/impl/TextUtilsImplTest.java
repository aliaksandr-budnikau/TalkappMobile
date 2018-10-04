package talkapp.org.talkappmobile.component.impl;

import org.junit.Test;

import java.util.List;

import talkapp.org.talkappmobile.model.GrammarError;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static talkapp.org.talkappmobile.module.GameplayModule.ARTICLES;
import static talkapp.org.talkappmobile.module.GameplayModule.LAST_SYMBOLS;
import static talkapp.org.talkappmobile.module.GameplayModule.PLACEHOLDER;
import static talkapp.org.talkappmobile.module.GameplayModule.PUNCTUATION_MARKS;

/**
 * @author Budnikau Aliaksandr
 */
public class TextUtilsImplTest {
    private TextUtilsImpl utils = new TextUtilsImpl(PLACEHOLDER, ARTICLES, LAST_SYMBOLS, PUNCTUATION_MARKS);

    @Test
    public void screenTextWith_withoutChanges() throws Exception {
        // setup
        String text = "sdfsdf safasad fsdffa sdf sfs s";
        String expected = "... ... ... ... ... ...";

        // when
        String actual = utils.screenTextWith(text);

        // then
        assertEquals(expected, actual);
    }

    @Test
    public void screenTextWith_withChanges() throws Exception {
        // setup
        String text = "sdthefsdf a safasad an fsandffa the sdf sfs s";
        String expected = "... a ... an ... the ... ... ...";

        // when
        String actual = utils.screenTextWith(text);

        // then
        assertEquals(expected, actual);
    }

    @Test
    public void screenTextWith_withChangesAndCase() throws Exception {
        // setup
        String text = "The A An sdthefsdf a safasad an fsandffa the sdf sfs s";
        String expected = "The A An ... a ... an ... the ... ... ...";

        // when
        String actual = utils.screenTextWith(text);

        // then
        assertEquals(expected, actual);
    }

    @Test
    public void screenTextWith_comma() throws Exception {
        // setup
        String text = "Happy anniversary, Mike.";
        String expected = "... ..., ....";

        // when
        String actual = utils.screenTextWith(text);

        // then
        assertEquals(expected, actual);
    }

    @Test
    public void toUpperCaseFirstLetter_whenAlreadyUpperCase() throws Exception {
        // setup
        String text = "Safasad an fsandffa the sdf sfs s";
        String expected = "Safasad an fsandffa the sdf sfs s";

        // when
        String actual = utils.toUpperCaseFirstLetter(text);

        // then
        assertEquals(expected, actual);
    }

    @Test
    public void toUpperCaseFirstLetter_whenNotAlreadyUpperCase() throws Exception {
        // setup
        String text = "safasad an fsandffa the sdf sfs s";
        String expected = "Safasad an fsandffa the sdf sfs s";

        // when
        String actual = utils.toUpperCaseFirstLetter(text);

        // then
        assertEquals(expected, actual);
    }

    @Test
    public void appendLastSymbol_question() throws Exception {
        // setup
        String text = "safasad an fsandffa the sdf sfs s";
        String translation = "safasad an fsandffa the sdf sfs s?";

        // when
        String actual = utils.appendLastSymbol(text, translation);

        // then
        assertEquals(translation, actual);
    }

    @Test
    public void appendLastSymbol_normalSentens() throws Exception {
        // setup
        String text = "safasad an fsandffa the sdf sfs s";
        String translation = "safasad an fsandffa the sdf sfs s.";

        // when
        String actual = utils.appendLastSymbol(text, translation);

        // then
        assertEquals(translation, actual);
    }

    @Test
    public void appendLastSymbol_exclamation() throws Exception {
        // setup
        String text = "safasad an fsandffa the sdf sfs s";
        String translation = "safasad an fsandffa the sdf sfs s!";

        // when
        String actual = utils.appendLastSymbol(text, translation);

        // then
        assertEquals(translation, actual);
    }

    @Test
    public void appendLastSymbol_alreadyAdded() throws Exception {
        // setup
        String text = "safasad an fsandffa the sdf sfs s!";
        String translation = "safasad an fsandffa the sdf sfs s!";

        // when
        String actual = utils.appendLastSymbol(text, translation);

        // then
        assertEquals(translation, actual);
    }


    @Test
    public void appendLastSymbol_diffInLength() throws Exception {
        // setup
        String text = "safasad an the sdf sfs s";
        String translation = "safasad an fsandffa the sdf sfs s!";

        // when
        String actual = utils.appendLastSymbol(text, translation);

        // then
        assertEquals(text + "!", actual);
    }

    @Test
    public void hidePassword_whenNull() {
        // setup
        String text = null;
        String translation = "";

        // when
        String actual = utils.hideText(text);

        // then
        assertEquals(translation, actual);
    }

    @Test
    public void hidePassword_whenEmpty() {
        // setup
        String text = "";
        String translation = "";

        // when
        String actual = utils.hideText(text);

        // then
        assertEquals(translation, actual);
    }

    @Test
    public void hidePassword_whenOneSymbol() {
        // setup
        String text = "1";
        String translation = "***";

        // when
        String actual = utils.hideText(text);

        // then
        assertEquals(translation, actual);
    }

    @Test
    public void hidePassword_whenOrdinaryText() {
        // setup
        String text = "dsfsdfs fsdf sdf dsfsdf ff 3223 232 21";
        String translation = "***";

        // when
        String actual = utils.hideText(text);

        // then
        assertEquals(translation, actual);
    }

    @Test
    public void buildSpellingGrammarErrorMessage_noSuggestion() {
        // setup
        GrammarError e = new GrammarError();
        e.setMessage("message");
        e.setBad("bad");
        e.setSuggestions(null);

        // when
        String actual = utils.buildSpellingGrammarErrorMessage(e);

        // then
        assertEquals("message in \"bad\".", actual);
    }

    @Test
    public void buildSpellingGrammarErrorMessage_oneSuggestion() {
        // setup
        GrammarError e = new GrammarError();
        e.setMessage("message");
        e.setBad("bad");
        e.setSuggestions(asList("sug1"));

        // when
        String actual = utils.buildSpellingGrammarErrorMessage(e);

        // then
        assertEquals("message in \"bad\". Try \"sug1\".", actual);
    }

    @Test
    public void buildSpellingGrammarErrorMessage_twoSuggestions() {
        // setup
        GrammarError e = new GrammarError();
        e.setMessage("message");
        e.setBad("bad");
        e.setSuggestions(asList("sug1", "sug2"));

        // when
        String actual = utils.buildSpellingGrammarErrorMessage(e);

        // then
        assertEquals("message in \"bad\". Try \"sug1\", \"sug2\".", actual);
    }

    @Test
    public void hideIntervalsInText_oneWord() {
        // setup
        List<Integer> intervalsOrigin = asList(8, 12);
        List<Integer> intervals = asList(8, 12);

        String textOriginal = "Ben, we know you have it.";
        String text = "Ben, we know you have it.";

        // when
        String actual = utils.hideIntervalsInText(text, intervals);

        // then
        assertEquals("Ben, we " + PLACEHOLDER + " you have it.", actual);
        assertEquals(intervalsOrigin, intervals);
        assertEquals(textOriginal, text);
    }

    @Test
    public void hideIntervalsInText_twoWords() {
        // setup
        List<Integer> intervalsOrigin = asList(8, 12, 22, 26);
        List<Integer> integers = asList(8, 12, 22, 26);

        String textOriginal = "Ben, we know you have know it.";
        String text = "Ben, we know you have know it.";

        // when
        String actual = utils.hideIntervalsInText(text, integers);

        // then
        assertEquals("Ben, we " + PLACEHOLDER + " you have " + PLACEHOLDER + " it.", actual);
        assertEquals(intervalsOrigin, integers);
        assertEquals(textOriginal, text);
    }

    @Test
    public void hideIntervalsInText_oneShortWord() {
        // setup
        List<Integer> intervalsOrigin = asList(15, 16);
        List<Integer> intervals = asList(15, 16);

        String textOriginal = "Is it required a name for that?";
        String text = "Is it required a name for that?";

        // when
        String actual = utils.hideIntervalsInText(text, intervals);

        // then
        assertEquals("Is it required " + PLACEHOLDER + " name for that?", actual);
        assertEquals(intervalsOrigin, intervals);
        assertEquals(textOriginal, text);
    }

    @Test
    public void hideIntervalsInText_twoShortWord() {
        // setup
        List<Integer> intervalsOrigin = asList(15, 16, 26, 27);
        List<Integer> intervals = asList(15, 16, 26, 27);

        String textOriginal = "Is it required a name for a that?";
        String text = "Is it required a name for a that?";

        // when
        String actual = utils.hideIntervalsInText(text, intervals);

        // then
        assertEquals("Is it required " + PLACEHOLDER + " name for " + PLACEHOLDER + " that?", actual);
        assertEquals(intervalsOrigin, intervals);
        assertEquals(textOriginal, text);
    }

    @Test
    public void hideIntervalsInText_fourShortWordAndTwoInEdges() {
        // setup
        List<Integer> intervalsOrigin = asList(0, 1, 17, 18, 28, 29, 35, 36);
        List<Integer> intervals = asList(0, 1, 17, 18, 28, 29, 35, 36);

        String textOriginal = "a Is it required a name for a that a";
        String text = "a Is it required a name for a that a";

        // when
        String actual = utils.hideIntervalsInText(text, intervals);

        // then
        assertEquals(PLACEHOLDER + " Is it required " + PLACEHOLDER +
                " name for " + PLACEHOLDER + " that " + PLACEHOLDER, actual);
        assertEquals(intervalsOrigin, intervals);
        assertEquals(textOriginal, text);
    }
}
package talkapp.org.talkappmobile.service.impl;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static talkapp.org.talkappmobile.service.impl.TextUtilsImpl.PLACEHOLDER;

/**
 * @author Budnikau Aliaksandr
 */
public class TextUtilsImplTest {
    private TextUtilsImpl utils = new TextUtilsImpl();

    @Test
    public void screenTextWith_withoutChanges() {
        // setup
        String text = "sdfsdf safasad fsdffa sdf sfs s";
        String expected = PLACEHOLDER + " " + PLACEHOLDER + " " + PLACEHOLDER + " " + PLACEHOLDER + " " + PLACEHOLDER + " " + PLACEHOLDER;

        // when
        String actual = utils.screenTextWith(text);

        // then
        assertEquals(expected, actual);
    }

    @Test
    public void screenTextWith_withChanges() {
        // setup
        String text = "sdthefsdf a safasad an fsandffa the sdf sfs s";
        String expected = PLACEHOLDER + " a " + PLACEHOLDER + " an " + PLACEHOLDER + " the " + PLACEHOLDER + " " + PLACEHOLDER + " " + PLACEHOLDER;

        // when
        String actual = utils.screenTextWith(text);

        // then
        assertEquals(expected, actual);
    }

    @Test
    public void screenTextWith_withChangesAndCase() {
        // setup
        String text = "The A An sdthefsdf a safasad an fsandffa the sdf sfs s";
        String expected = "The A An " + PLACEHOLDER + " a " + PLACEHOLDER + " an " + PLACEHOLDER + " the " + PLACEHOLDER + " " + PLACEHOLDER + " " + PLACEHOLDER;

        // when
        String actual = utils.screenTextWith(text);

        // then
        assertEquals(expected, actual);
    }

    @Test
    public void screenTextWith_comma() {
        // setup
        String text = "Happy anniversary, Mike.";
        String expected = PLACEHOLDER + " " + PLACEHOLDER + ", " + PLACEHOLDER + ".";

        // when
        String actual = utils.screenTextWith(text);

        // then
        assertEquals(expected, actual);
    }

    @Test
    public void toUpperCaseFirstLetter_whenAlreadyUpperCase() {
        // setup
        String text = "Safasad an fsandffa the sdf sfs s";
        String expected = "Safasad an fsandffa the sdf sfs s";

        // when
        String actual = utils.toUpperCaseFirstLetter(text);

        // then
        assertEquals(expected, actual);
    }

    @Test
    public void toUpperCaseFirstLetter_whenNotAlreadyUpperCase() {
        // setup
        String text = "safasad an fsandffa the sdf sfs s";
        String expected = "Safasad an fsandffa the sdf sfs s";

        // when
        String actual = utils.toUpperCaseFirstLetter(text);

        // then
        assertEquals(expected, actual);
    }

    @Test
    public void appendLastSymbol_question() {
        // setup
        String text = "safasad an fsandffa the sdf sfs s";
        String translation = "safasad an fsandffa the sdf sfs s?";

        // when
        String actual = utils.appendLastSymbol(text, translation);

        // then
        assertEquals(translation, actual);
    }

    @Test
    public void appendLastSymbol_normalSentens() {
        // setup
        String text = "safasad an fsandffa the sdf sfs s";
        String translation = "safasad an fsandffa the sdf sfs s.";

        // when
        String actual = utils.appendLastSymbol(text, translation);

        // then
        assertEquals(translation, actual);
    }

    @Test
    public void appendLastSymbol_exclamation() {
        // setup
        String text = "safasad an fsandffa the sdf sfs s";
        String translation = "safasad an fsandffa the sdf sfs s!";

        // when
        String actual = utils.appendLastSymbol(text, translation);

        // then
        assertEquals(translation, actual);
    }

    @Test
    public void appendLastSymbol_alreadyAdded() {
        // setup
        String text = "safasad an fsandffa the sdf sfs s!";
        String translation = "safasad an fsandffa the sdf sfs s!";

        // when
        String actual = utils.appendLastSymbol(text, translation);

        // then
        assertEquals(translation, actual);
    }


    @Test
    public void appendLastSymbol_diffInLength() {
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

    @Test
    public void validateEmail() {
        TextUtilsImpl utils = new TextUtilsImpl();

        assertTrue(utils.validateEmail("sasha-ne@tut.by"));
        assertTrue(utils.validateEmail("sasha-ne@gmail.com"));
        assertFalse(utils.validateEmail("sasha-negmail.com"));
        assertFalse(utils.validateEmail("sasha-ne@gmail"));
        assertFalse(utils.validateEmail("sasha-ne@@gmail.com"));
        assertFalse(utils.validateEmail("s"));
        assertFalse(utils.validateEmail(""));
    }

    @Test
    public void validatePassword() {
        TextUtilsImpl utils = new TextUtilsImpl();

        assertFalse(utils.validatePassword("password"));
        assertFalse(utils.validatePassword("passwor"));
        assertFalse(utils.validatePassword("s"));
        assertFalse(utils.validatePassword(""));
        assertTrue(utils.validatePassword("password0A"));
        assertTrue(utils.validatePassword("Password0"));
        assertFalse(utils.validatePassword("sasha-negmail.com"));
        assertFalse(utils.validatePassword("PASSWORD0"));
        assertFalse(utils.validatePassword("password0"));
    }
}
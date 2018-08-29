package talkapp.org.talkappmobile.service.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Budnikau Aliaksandr
 */
public class TextUtilsImplTest {
    String[] articles = new String[]{"a", "an", "the"};
    String[] lastSymbols = new String[]{".", "!", "?"};
    private TextUtilsImpl utils = new TextUtilsImpl("...", articles, lastSymbols);

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
}
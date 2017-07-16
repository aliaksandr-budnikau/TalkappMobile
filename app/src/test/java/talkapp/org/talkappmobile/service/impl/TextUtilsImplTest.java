package talkapp.org.talkappmobile.service.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Budnikau Aliaksandr
 */
public class TextUtilsImplTest {
    private TextUtilsImpl utils = new TextUtilsImpl("...", "a", "an", "the");

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
}
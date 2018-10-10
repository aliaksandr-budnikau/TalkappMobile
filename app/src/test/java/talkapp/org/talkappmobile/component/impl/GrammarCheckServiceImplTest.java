package talkapp.org.talkappmobile.component.impl;

import org.junit.Before;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;

import java.util.List;

import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.module.InfraModule;
import talkapp.org.talkappmobile.module.LanguageModule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GrammarCheckServiceImplTest {
    private GrammarCheckServiceImpl service;

    @Before
    public void setUp() {
        LanguageModule languageModule = new LanguageModule();
        AmericanEnglish americanEnglish = languageModule.provideAmericanEnglish();
        JLanguageTool jLanguageTool = languageModule.provideJLanguageTool(americanEnglish);
        Logger logger = new InfraModule().provideLogger();
        service = new GrammarCheckServiceImpl(jLanguageTool, logger);
    }

    @Test
    public void check_helloWorld() {
        // setup
        String text = "Hello worlad";

        // when
        List<GrammarError> errors = service.check(text);

        // then
        assertFalse(errors.isEmpty());
        assertEquals(errors.size(), 1);
        assertEquals(errors.get(0).getBad(), "worlad");
        assertEquals(errors.get(0).getLength(), 6);
        assertEquals(errors.get(0).getOffset(), 6);
        assertTrue(errors.get(0).getSuggestions().contains("world"));
        assertFalse(errors.get(0).getMessage().isEmpty());
    }

    @Test
    public void check_iAmAnEngineer() throws Exception {
        // setup
        String text = "I is a enginear";

        // when
        List<GrammarError> errors = service.check(text);

        // then
        assertFalse(errors.isEmpty());
        assertEquals(errors.size(), 3);

        assertEquals(errors.get(0).getBad(), "is");
        assertEquals(errors.get(0).getLength(), 2);
        assertEquals(errors.get(0).getOffset(), 2);
        assertTrue(errors.get(0).getSuggestions().contains("am"));
        assertFalse(errors.get(0).getMessage().isEmpty());

        assertEquals(errors.get(1).getBad(), "a");
        assertEquals(errors.get(1).getLength(), 1);
        assertEquals(errors.get(1).getOffset(), 5);
        assertTrue(errors.get(1).getSuggestions().contains("an"));
        assertFalse(errors.get(1).getMessage().isEmpty());

        assertEquals(errors.get(2).getBad(), "enginear");
        assertEquals(errors.get(2).getLength(), 8);
        assertEquals(errors.get(2).getOffset(), 7);
        assertTrue(errors.get(2).getSuggestions().contains("engineer"));
        assertFalse(errors.get(2).getMessage().isEmpty());
    }

    @Test
    public void check_whoIsDutyToday() {
        // setup
        String text = "Who is duty today?";

        // when
        List<GrammarError> errors = service.check(text);

        // then
        assertTrue(errors.isEmpty());
    }

    @Test
    public void check_whoAreDutyToday() {
        // setup
        String text = "Who are duty today?";

        // when
        List<GrammarError> errors = service.check(text);

        // then
        assertTrue(errors.isEmpty());
    }
}
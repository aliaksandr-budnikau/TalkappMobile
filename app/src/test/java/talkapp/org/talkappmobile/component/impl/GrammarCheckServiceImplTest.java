package talkapp.org.talkappmobile.component.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.model.GrammarError;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GrammarCheckServiceImplTest {
    @Mock
    private DataServer server;
    @Mock
    private Logger logger;
    @InjectMocks
    private GrammarCheckServiceImpl service;

    @Test
    public void check() {
        // setup
        String text = "Hello worlad";

        List<GrammarError> errorList = asList(new GrammarError(), new GrammarError());

        // when
        when(server.checkText(text)).thenReturn(errorList);
        List<GrammarError> errors = service.check(text);

        // then
        assertFalse(errors.isEmpty());
        assertEquals(errors.size(), 2);
    }
}
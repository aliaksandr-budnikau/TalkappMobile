package talkapp.org.talkappmobile.component.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.backend.TextGrammarCheckService;
import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.module.InfraModule;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static retrofit2.Response.success;

@RunWith(MockitoJUnitRunner.class)
public class GrammarCheckServiceImplTest {
    @Mock
    private TextGrammarCheckService textGrammarCheckService;
    private AuthSign authSign;
    private GrammarCheckServiceImpl service;

    @Before
    public void setUp() {
        Logger logger = new InfraModule().provideLogger();
        authSign = new AuthSign();
        service = new GrammarCheckServiceImpl(textGrammarCheckService, authSign, logger);
    }

    private void whenRefereeServiceCheckAnswer(String text, List<GrammarError> errorList) throws IOException {
        Call call = mock(Call.class);
        when(call.execute()).thenReturn(success(errorList));
        when(textGrammarCheckService.check(text, authSign)).thenReturn(call);
    }

    @Test
    public void check() throws IOException {
        // setup
        String text = "Hello worlad";

        List<GrammarError> errorList = asList(new GrammarError(), new GrammarError());

        // when
        whenRefereeServiceCheckAnswer(text, errorList);
        List<GrammarError> errors = service.check(text);

        // then
        assertFalse(errors.isEmpty());
        assertEquals(errors.size(), 2);
    }
}
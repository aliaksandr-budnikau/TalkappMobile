package talkapp.org.talkappmobile.repository;

import android.content.Context;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class RepositoryFactoryProviderTest {

    @Test
    public void testGetIsNotInstanceOfContextClass() {
        try {
            RepositoryFactoryProvider.get(new Object());
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("java.lang.RuntimeException: argument is not instance of android.content.Context"));
            return;
        }
        fail();
    }

    @Test
    public void testTalkappMobileApplicationClass() {
        RepositoryFactoryProvider.get(mock(Context.class));
    }
}
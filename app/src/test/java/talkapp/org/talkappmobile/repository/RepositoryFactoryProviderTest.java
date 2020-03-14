package talkapp.org.talkappmobile.repository;

import org.junit.Test;

import talkapp.org.talkappmobile.app.TalkappMobileApplication;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
        RepositoryFactoryProvider.get(new TalkappMobileApplication());
    }
}
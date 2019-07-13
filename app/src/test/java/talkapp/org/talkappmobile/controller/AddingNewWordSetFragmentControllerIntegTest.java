package talkapp.org.talkappmobile.controller;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.greenrobot.eventbus.EventBus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.activity.BaseTest;
import talkapp.org.talkappmobile.events.AddingNewWordSetFragmentReadyEM;
import talkapp.org.talkappmobile.events.NewWordSetDraftChangedEM;
import talkapp.org.talkappmobile.events.NewWordSetDraftLoadedEM;
import talkapp.org.talkappmobile.model.NewWordSetDraft;
import talkapp.org.talkappmobile.service.WordSetService;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class AddingNewWordSetFragmentControllerIntegTest extends BaseTest {

    private EventBus eventBus;
    private AddingNewWordSetFragmentController controller;

    @Before
    public void setUp() throws Exception {
        eventBus = getEventBus();
        controller = new AddingNewWordSetFragmentController(eventBus, getServiceFactoryBean());
    }

    @After
    public void tearDown() {
        OpenHelperManager.releaseHelper();
    }

    @Test
    public void testHandleAddingNewWordSetFragmentReadyEM_DraftIsEmpty() {
        // when
        controller.handle(new AddingNewWordSetFragmentReadyEM());

        // then
        NewWordSetDraftLoadedEM newWordSetDraftLoadedEM = getEM(NewWordSetDraftLoadedEM.class);
        assertNotNull(newWordSetDraftLoadedEM);
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft());
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft().getWords());
        assertTrue(newWordSetDraftLoadedEM.getNewWordSetDraft().getWords().isEmpty());
    }

    @Test
    public void testHandleAddingNewWordSetFragmentReadyEM_DraftExistsButEmpty() throws SQLException {
        WordSetService wordSetService = getServiceFactoryBean().getWordSetExperienceRepository();
        wordSetService.save(new NewWordSetDraft(Collections.<String>emptyList()));

        // when
        controller.handle(new AddingNewWordSetFragmentReadyEM());

        // then
        NewWordSetDraftLoadedEM newWordSetDraftLoadedEM = getEM(NewWordSetDraftLoadedEM.class);
        assertNotNull(newWordSetDraftLoadedEM);
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft());
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft().getWords());
        assertEquals(1, newWordSetDraftLoadedEM.getNewWordSetDraft().getWords().size());
    }

    @Test
    public void testHandleAddingNewWordSetFragmentReadyEM_DraftExistsWith1Word() throws SQLException {
        String house = "house";

        WordSetService wordSetService = getServiceFactoryBean().getWordSetExperienceRepository();
        wordSetService.save(new NewWordSetDraft(singletonList(house)));

        // when
        controller.handle(new AddingNewWordSetFragmentReadyEM());

        // then
        NewWordSetDraftLoadedEM newWordSetDraftLoadedEM = getEM(NewWordSetDraftLoadedEM.class);
        assertNotNull(newWordSetDraftLoadedEM);
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft());
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft().getWords());
        assertEquals(1, newWordSetDraftLoadedEM.getNewWordSetDraft().getWords().size());
        assertEquals(house, newWordSetDraftLoadedEM.getNewWordSetDraft().getWords().get(0));
    }

    @Test
    public void testHandleAddingNewWordSetFragmentReadyEM_DraftExistsWith12Words() {
        LinkedList<String> words = new LinkedList<>();
        for (int i = 0; i < 12; i++) {
            words.add("house" + i);
        }

        // when
        controller.handle(new NewWordSetDraftChangedEM(words));
        controller.handle(new AddingNewWordSetFragmentReadyEM());

        // then
        NewWordSetDraftLoadedEM newWordSetDraftLoadedEM = getEM(NewWordSetDraftLoadedEM.class);
        assertNotNull(newWordSetDraftLoadedEM);
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft());
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft().getWords());
        assertEquals(12, newWordSetDraftLoadedEM.getNewWordSetDraft().getWords().size());
        assertEquals(words, newWordSetDraftLoadedEM.getNewWordSetDraft().getWords());
    }

    @Test
    public void testHandleAddingNewWordSetFragmentReadyEM_DraftExistsWith12EmptyWords() {
        LinkedList<String> words = new LinkedList<>();
        for (int i = 0; i < 12; i++) {
            words.add("");
        }

        // when
        controller.handle(new NewWordSetDraftChangedEM(words));
        controller.handle(new AddingNewWordSetFragmentReadyEM());

        // then
        NewWordSetDraftLoadedEM newWordSetDraftLoadedEM = getEM(NewWordSetDraftLoadedEM.class);
        assertNotNull(newWordSetDraftLoadedEM);
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft());
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft().getWords());
        assertEquals(12, newWordSetDraftLoadedEM.getNewWordSetDraft().getWords().size());
        assertEquals(words, newWordSetDraftLoadedEM.getNewWordSetDraft().getWords());
    }
}
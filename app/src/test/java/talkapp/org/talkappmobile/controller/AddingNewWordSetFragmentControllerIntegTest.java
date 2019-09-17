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
import talkapp.org.talkappmobile.DaoHelper;
import talkapp.org.talkappmobile.ServiceHelper;
import talkapp.org.talkappmobile.TestHelper;
import talkapp.org.talkappmobile.events.AddingNewWordSetFragmentGotReadyEM;
import talkapp.org.talkappmobile.events.NewWordSetDraftLoadedEM;
import talkapp.org.talkappmobile.events.NewWordSetDraftWasChangedEM;
import talkapp.org.talkappmobile.model.NewWordSetDraft;
import talkapp.org.talkappmobile.service.WordSetService;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class AddingNewWordSetFragmentControllerIntegTest {

    private EventBus eventBus;
    private AddingNewWordSetFragmentController controller;
    private TestHelper testHelper;
    private DaoHelper daoHelper;
    private ServiceHelper serviceHelper;

    @Before
    public void setUp() throws Exception {
        testHelper = new TestHelper();
        eventBus = testHelper.getEventBusMock();
        daoHelper = new DaoHelper();
        serviceHelper = new ServiceHelper(daoHelper);
        controller = new AddingNewWordSetFragmentController(eventBus, serviceHelper.getServiceFactoryBean());
    }

    @After
    public void tearDown() {
        daoHelper.releaseHelper();
    }

    @Test
    public void testHandleAddingNewWordSetFragmentReadyEM_DraftIsEmpty() {
        // when
        controller.handle(new AddingNewWordSetFragmentGotReadyEM());

        // then
        NewWordSetDraftLoadedEM newWordSetDraftLoadedEM = testHelper.getEM(NewWordSetDraftLoadedEM.class);
        assertNotNull(newWordSetDraftLoadedEM);
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft());
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft().getWords());
        assertTrue(newWordSetDraftLoadedEM.getNewWordSetDraft().getWords().isEmpty());
    }

    @Test
    public void testHandleAddingNewWordSetFragmentReadyEM_DraftExistsButEmpty() throws SQLException {
        WordSetService wordSetService = serviceHelper.getServiceFactoryBean().getWordSetExperienceRepository();
        wordSetService.save(new NewWordSetDraft(Collections.<String>emptyList()));

        // when
        controller.handle(new AddingNewWordSetFragmentGotReadyEM());

        // then
        NewWordSetDraftLoadedEM newWordSetDraftLoadedEM = testHelper.getEM(NewWordSetDraftLoadedEM.class);
        assertNotNull(newWordSetDraftLoadedEM);
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft());
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft().getWords());
        assertEquals(1, newWordSetDraftLoadedEM.getNewWordSetDraft().getWords().size());
    }

    @Test
    public void testHandleAddingNewWordSetFragmentReadyEM_DraftExistsWith1Word() throws SQLException {
        String house = "house";

        WordSetService wordSetService = serviceHelper.getServiceFactoryBean().getWordSetExperienceRepository();
        wordSetService.save(new NewWordSetDraft(singletonList(house)));

        // when
        controller.handle(new AddingNewWordSetFragmentGotReadyEM());

        // then
        NewWordSetDraftLoadedEM newWordSetDraftLoadedEM = testHelper.getEM(NewWordSetDraftLoadedEM.class);
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
        controller.handle(new NewWordSetDraftWasChangedEM(words));
        controller.handle(new AddingNewWordSetFragmentGotReadyEM());

        // then
        NewWordSetDraftLoadedEM newWordSetDraftLoadedEM = testHelper.getEM(NewWordSetDraftLoadedEM.class);
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
        controller.handle(new NewWordSetDraftWasChangedEM(words));
        controller.handle(new AddingNewWordSetFragmentGotReadyEM());

        // then
        NewWordSetDraftLoadedEM newWordSetDraftLoadedEM = testHelper.getEM(NewWordSetDraftLoadedEM.class);
        assertNotNull(newWordSetDraftLoadedEM);
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft());
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft().getWords());
        assertEquals(0, newWordSetDraftLoadedEM.getNewWordSetDraft().getWords().size());
    }

    @Test
    public void testHandleAddingNewWordSetFragmentReadyEM_DraftExistsWith11EmptyWords() {
        LinkedList<String> words = new LinkedList<>();
        for (int i = 0; i < 12; i++) {
            if (i == 5) {
                words.add("fsdfs");
            } else {
                words.add("");
            }
        }

        // when
        controller.handle(new NewWordSetDraftWasChangedEM(words));
        controller.handle(new AddingNewWordSetFragmentGotReadyEM());

        // then
        NewWordSetDraftLoadedEM newWordSetDraftLoadedEM = testHelper.getEM(NewWordSetDraftLoadedEM.class);
        assertNotNull(newWordSetDraftLoadedEM);
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft());
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft().getWords());
        assertEquals(6, newWordSetDraftLoadedEM.getNewWordSetDraft().getWords().size());
        assertEquals(words.subList(0, 6), newWordSetDraftLoadedEM.getNewWordSetDraft().getWords());
    }

    @Test
    public void testHandleAddingNewWordSetFragmentReadyEM_DraftExistsWith10HasCommasAnd1Word() {
        LinkedList<String> words = new LinkedList<>();
        for (int i = 0; i < 12; i++) {
            if (i == 5 || i == 8) {
                words.add(",");
            } else {
                if (i == 2) {
                    words.add("sdffs");
                } else {
                    words.add("");
                }
            }
        }

        // when
        controller.handle(new NewWordSetDraftWasChangedEM(words));
        controller.handle(new AddingNewWordSetFragmentGotReadyEM());

        // then
        NewWordSetDraftLoadedEM newWordSetDraftLoadedEM = testHelper.getEM(NewWordSetDraftLoadedEM.class);
        assertNotNull(newWordSetDraftLoadedEM);
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft());
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft().getWords());
        assertEquals(3, newWordSetDraftLoadedEM.getNewWordSetDraft().getWords().size());
        assertEquals(words.subList(0, 3), newWordSetDraftLoadedEM.getNewWordSetDraft().getWords());
    }
}
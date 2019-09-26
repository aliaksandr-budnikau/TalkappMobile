package talkapp.org.talkappmobile.controller;

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
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.WordSetService;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class AddingNewWordSetFragmentControllerIntegTest {

    private EventBus eventBusMock;
    private AddingNewWordSetFragmentController controller;
    private TestHelper testHelper;
    private DaoHelper daoHelper;
    private ServiceHelper serviceHelper;

    @Before
    public void setUp() throws Exception {
        testHelper = new TestHelper();
        eventBusMock = testHelper.getEventBusMock();
        daoHelper = new DaoHelper();
        serviceHelper = new ServiceHelper(daoHelper);
        controller = new AddingNewWordSetFragmentController(eventBusMock, mock(DataServer.class), serviceHelper.getServiceFactoryBean());
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
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft().getWordTranslations());
        assertTrue(newWordSetDraftLoadedEM.getNewWordSetDraft().getWordTranslations().isEmpty());
    }

    @Test
    public void testHandleAddingNewWordSetFragmentReadyEM_DraftExistsButEmpty() throws SQLException {
        WordSetService wordSetService = serviceHelper.getServiceFactoryBean().getWordSetExperienceRepository();
        wordSetService.save(new NewWordSetDraft(Collections.<WordTranslation>emptyList()));

        // when
        controller.handle(new AddingNewWordSetFragmentGotReadyEM());

        // then
        NewWordSetDraftLoadedEM newWordSetDraftLoadedEM = testHelper.getEM(NewWordSetDraftLoadedEM.class);
        assertNotNull(newWordSetDraftLoadedEM);
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft());
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft().getWordTranslations());
        assertEquals(0, newWordSetDraftLoadedEM.getNewWordSetDraft().getWordTranslations().size());
    }

    @Test
    public void testHandleAddingNewWordSetFragmentReadyEM_DraftExistsWith1Word() throws SQLException {
        WordTranslation wordTranslation = new WordTranslation();
        wordTranslation.setWord("house");

        WordSetService wordSetService = serviceHelper.getServiceFactoryBean().getWordSetExperienceRepository();
        wordSetService.save(new NewWordSetDraft(singletonList(wordTranslation)));

        // when
        controller.handle(new AddingNewWordSetFragmentGotReadyEM());

        // then
        NewWordSetDraftLoadedEM newWordSetDraftLoadedEM = testHelper.getEM(NewWordSetDraftLoadedEM.class);
        assertNotNull(newWordSetDraftLoadedEM);
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft());
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft().getWordTranslations());
        assertEquals(1, newWordSetDraftLoadedEM.getNewWordSetDraft().getWordTranslations().size());
        assertEquals(wordTranslation, newWordSetDraftLoadedEM.getNewWordSetDraft().getWordTranslations().get(0));
    }

    @Test
    public void testHandleAddingNewWordSetFragmentReadyEM_DraftExistsWith12Words() {

        LinkedList<WordTranslation> words = new LinkedList<>();
        for (int i = 0; i < 12; i++) {
            WordTranslation wordTranslation = new WordTranslation();
            wordTranslation.setWord("house" + i);
            words.add(wordTranslation);
        }

        // when
        controller.handle(new NewWordSetDraftWasChangedEM(words));
        controller.handle(new AddingNewWordSetFragmentGotReadyEM());

        // then
        NewWordSetDraftLoadedEM newWordSetDraftLoadedEM = testHelper.getEM(NewWordSetDraftLoadedEM.class);
        assertNotNull(newWordSetDraftLoadedEM);
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft());
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft().getWordTranslations());
        assertEquals(12, newWordSetDraftLoadedEM.getNewWordSetDraft().getWordTranslations().size());
        assertEquals(words, newWordSetDraftLoadedEM.getNewWordSetDraft().getWordTranslations());
    }

    @Test
    public void testHandleAddingNewWordSetFragmentReadyEM_WhenWeHaveCommasBug() {
        LinkedList<WordTranslation> words = new LinkedList<>();
        WordTranslation wordTranslation1 = new WordTranslation();
        wordTranslation1.setWord("Well, how is on duty today.| Хорошо, кто дежурный.");
        words.add(wordTranslation1);
        WordTranslation wordTranslation2 = new WordTranslation();
        wordTranslation2.setWord("Well, how is on duty today.| Хорошо, кто дежурный.");
        words.add(wordTranslation2);


        // when
        controller.handle(new NewWordSetDraftWasChangedEM(words));
        controller.handle(new AddingNewWordSetFragmentGotReadyEM());

        // then
        NewWordSetDraftLoadedEM newWordSetDraftLoadedEM = testHelper.getEM(NewWordSetDraftLoadedEM.class);
        assertNotNull(newWordSetDraftLoadedEM);
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft());
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft().getWordTranslations());
        assertEquals(2, newWordSetDraftLoadedEM.getNewWordSetDraft().getWordTranslations().size());
        assertEquals(words, newWordSetDraftLoadedEM.getNewWordSetDraft().getWordTranslations());
    }

    @Test
    public void testHandleAddingNewWordSetFragmentReadyEM_DraftExistsWith12EmptyWords() {
        LinkedList<WordTranslation> words = new LinkedList<>();
        for (int i = 0; i < 12; i++) {
            WordTranslation wordTranslation = new WordTranslation();
            wordTranslation.setWord("");
            words.add(wordTranslation);
        }

        // when
        controller.handle(new NewWordSetDraftWasChangedEM(words));
        controller.handle(new AddingNewWordSetFragmentGotReadyEM());

        // then
        NewWordSetDraftLoadedEM newWordSetDraftLoadedEM = testHelper.getEM(NewWordSetDraftLoadedEM.class);
        assertNotNull(newWordSetDraftLoadedEM);
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft());
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft().getWordTranslations());
        assertEquals(12, newWordSetDraftLoadedEM.getNewWordSetDraft().getWordTranslations().size());
    }

    @Test
    public void testHandleAddingNewWordSetFragmentReadyEM_DraftExistsWith11EmptyWords() {
        LinkedList<WordTranslation> words = new LinkedList<>();
        for (int i = 0; i < 12; i++) {
            WordTranslation wordTranslation = new WordTranslation();
            if (i == 5) {
                wordTranslation.setWord("fsdfs");
            } else {
                wordTranslation.setWord("");
            }
            words.add(wordTranslation);
        }

        // when
        controller.handle(new NewWordSetDraftWasChangedEM(words));
        controller.handle(new AddingNewWordSetFragmentGotReadyEM());

        // then
        NewWordSetDraftLoadedEM newWordSetDraftLoadedEM = testHelper.getEM(NewWordSetDraftLoadedEM.class);
        assertNotNull(newWordSetDraftLoadedEM);
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft());
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft().getWordTranslations());
        assertEquals(12, newWordSetDraftLoadedEM.getNewWordSetDraft().getWordTranslations().size());
        assertEquals(words, newWordSetDraftLoadedEM.getNewWordSetDraft().getWordTranslations());
    }

    @Test
    public void testHandleAddingNewWordSetFragmentReadyEM_DraftExistsWith10HasCommasAnd1Word() {
        LinkedList<WordTranslation> words = new LinkedList<>();
        for (int i = 0; i < 12; i++) {
            WordTranslation wordTranslation = new WordTranslation();
            if (i == 5 || i == 8) {
                wordTranslation.setWord(",");
            } else {
                if (i == 2) {
                    wordTranslation.setWord("sdffs");
                } else {
                    wordTranslation.setWord("");
                }
            }
            words.add(wordTranslation);
        }

        // when
        controller.handle(new NewWordSetDraftWasChangedEM(words));
        controller.handle(new AddingNewWordSetFragmentGotReadyEM());

        // then
        NewWordSetDraftLoadedEM newWordSetDraftLoadedEM = testHelper.getEM(NewWordSetDraftLoadedEM.class);
        assertNotNull(newWordSetDraftLoadedEM);
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft());
        assertNotNull(newWordSetDraftLoadedEM.getNewWordSetDraft().getWordTranslations());
        assertEquals(12, newWordSetDraftLoadedEM.getNewWordSetDraft().getWordTranslations().size());
        assertEquals(words, newWordSetDraftLoadedEM.getNewWordSetDraft().getWordTranslations());
    }
}
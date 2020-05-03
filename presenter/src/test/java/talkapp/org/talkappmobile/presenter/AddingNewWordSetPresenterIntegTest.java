package talkapp.org.talkappmobile.presenter;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.sql.SQLException;
import java.util.LinkedList;

import talkapp.org.talkappmobile.RepositoryModule;
import talkapp.org.talkappmobile.model.NewWordSetDraft;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.ServiceFactoryProvider;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.view.AddingNewWordSetView;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class AddingNewWordSetPresenterIntegTest {

    private RepositoryModule repositoryFactory;
    private AddingNewWordSetPresenter addingNewWordSetPresenter;
    private AddingNewWordSetView addingNewWordSetViewMock;
    private ServiceFactory serviceFactory;

    @Before
    public void setUp() throws Exception {
        PresenterFactory presenterFactory = PresenterFactoryProvider.getOrCreateNew(RuntimeEnvironment.application);
        serviceFactory = ServiceFactoryProvider.getOrCreateNew(RuntimeEnvironment.application);
        addingNewWordSetViewMock = mock(AddingNewWordSetView.class);
        addingNewWordSetPresenter = presenterFactory.create(addingNewWordSetViewMock);
    }

    @After
    public void tearDown() {
        OpenHelperManager.releaseHelper();
    }

    @Test
    public void testHandleAddingNewWordSetFragmentReadyEM_DraftIsEmpty() {
        // when
        addingNewWordSetPresenter.initialize();

        // then
        ArgumentCaptor<WordTranslation[]> captor = forClass(WordTranslation[].class);
        verify(addingNewWordSetViewMock).onNewWordSetDraftLoaded(captor.capture());
        WordTranslation[] translations = captor.getValue();
        assertNotNull(translations);
        assertEquals(12, translations.length);
    }

    @Test
    public void testHandleAddingNewWordSetFragmentReadyEM_DraftExistsButEmpty() throws SQLException {
        WordSetService wordSetService = serviceFactory.getWordSetService();
        LinkedList<WordTranslation> words = new LinkedList<>();
        for (int i = 0; i < 12; i++) {
            words.add(new WordTranslation());
        }
        wordSetService.save(new NewWordSetDraft(words));

        // when
        addingNewWordSetPresenter.initialize();

        // then
        ArgumentCaptor<WordTranslation[]> captor = forClass(WordTranslation[].class);
        verify(addingNewWordSetViewMock).onNewWordSetDraftLoaded(captor.capture());
        WordTranslation[] translations = captor.getValue();
        assertNotNull(translations);
        assertEquals(12, translations.length);
    }

    @Test
    public void testHandleAddingNewWordSetFragmentReadyEM_DraftExistsWith1Word() throws SQLException {
        LinkedList<WordTranslation> words = new LinkedList<>();
        WordTranslation wordTranslation = new WordTranslation();
        wordTranslation.setWord("house");
        words.add(wordTranslation);
        for (int i = 0; i < 11; i++) {
            words.add(new WordTranslation());
        }

        WordSetService wordSetService = serviceFactory.getWordSetService();
        wordSetService.save(new NewWordSetDraft(words));

        // when
        addingNewWordSetPresenter.initialize();

        // then

        ArgumentCaptor<WordTranslation[]> captor = forClass(WordTranslation[].class);
        verify(addingNewWordSetViewMock).onNewWordSetDraftLoaded(captor.capture());
        WordTranslation[] translations = captor.getValue();
        assertNotNull(translations);
        assertEquals(12, translations.length);
        assertEquals(wordTranslation, translations[0]);
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
        addingNewWordSetPresenter.saveChangedDraft(words);
        addingNewWordSetPresenter.initialize();

        // then
        ArgumentCaptor<WordTranslation[]> captor = forClass(WordTranslation[].class);
        verify(addingNewWordSetViewMock).onNewWordSetDraftLoaded(captor.capture());
        WordTranslation[] translations = captor.getValue();
        assertNotNull(translations);
        assertEquals(12, translations.length);
        assertEquals(words.toArray(), translations);
    }

    @Test
    public void testHandleAddingNewWordSetFragmentReadyEM_WhenWeHaveCommasBug() {
        LinkedList<WordTranslation> words = new LinkedList<>();
        for (int i = 0; i < 12; i++) {
            WordTranslation wordTranslation = new WordTranslation();
            wordTranslation.setWord("Well, how is on duty today.| Хорошо, кто дежурный.");
            words.add(wordTranslation);
        }


        // when
        addingNewWordSetPresenter.saveChangedDraft(words);
        addingNewWordSetPresenter.initialize();

        // then
        ArgumentCaptor<WordTranslation[]> captor = forClass(WordTranslation[].class);
        verify(addingNewWordSetViewMock).onNewWordSetDraftLoaded(captor.capture());
        WordTranslation[] translations = captor.getValue();
        assertNotNull(translations);
        assertEquals(12, translations.length);
        assertEquals(words.toArray(), translations);
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
        addingNewWordSetPresenter.saveChangedDraft(words);
        addingNewWordSetPresenter.initialize();

        // then
        ArgumentCaptor<WordTranslation[]> captor = forClass(WordTranslation[].class);
        verify(addingNewWordSetViewMock).onNewWordSetDraftLoaded(captor.capture());
        WordTranslation[] translations = captor.getValue();
        assertNotNull(translations);
        assertEquals(12, translations.length);
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
        addingNewWordSetPresenter.saveChangedDraft(words);
        addingNewWordSetPresenter.initialize();

        // then
        ArgumentCaptor<WordTranslation[]> captor = forClass(WordTranslation[].class);
        verify(addingNewWordSetViewMock).onNewWordSetDraftLoaded(captor.capture());
        WordTranslation[] translations = captor.getValue();
        assertNotNull(translations);
        assertEquals(12, translations.length);
        assertEquals(words.toArray(), translations);
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
        addingNewWordSetPresenter.saveChangedDraft(words);
        addingNewWordSetPresenter.initialize();

        // then
        ArgumentCaptor<WordTranslation[]> captor = forClass(WordTranslation[].class);
        verify(addingNewWordSetViewMock).onNewWordSetDraftLoaded(captor.capture());
        WordTranslation[] translations = captor.getValue();
        assertNotNull(translations);
        assertEquals(12, translations.length);
        assertEquals(words.toArray(), translations);
    }
}
package talkapp.org.talkappmobile.activity.presenter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.activity.interactor.AddingNewWordSetInteractor;
import talkapp.org.talkappmobile.activity.view.AddingNewWordSetFragmentView;
import talkapp.org.talkappmobile.dao.DatabaseHelper;
import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.dao.TopicDao;
import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.dao.impl.SentenceDaoImpl;
import talkapp.org.talkappmobile.dao.impl.WordSetDaoImpl;
import talkapp.org.talkappmobile.mappings.SentenceMapping;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.WordSetExperienceUtils;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.service.impl.LocalDataServiceImpl;
import talkapp.org.talkappmobile.service.impl.LoggerBean;
import talkapp.org.talkappmobile.service.impl.RequestExecutor;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;
import talkapp.org.talkappmobile.service.impl.WordSetServiceImpl;
import talkapp.org.talkappmobile.service.mapper.WordSetMapper;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "org.talkappmobile.dao.impl")
public class AddingNewWordSetPresenterAndInteractorIntegTest extends PresenterAndInteractorIntegTest {
    private AddingNewWordSetPresenter presenter;
    private AddingNewWordSetFragmentView view;
    private WordSetDaoImpl wordSetDao;
    private WordSetMapper mapper;
    private WordSetService wordSetService;

    @Before
    public void setUp() throws Exception {
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(RuntimeEnvironment.application, DatabaseHelper.class);
        SentenceDao sentenceDao = new SentenceDaoImpl(databaseHelper.getConnectionSource(), SentenceMapping.class);
        wordSetDao = new WordSetDaoImpl(databaseHelper.getConnectionSource(), WordSetMapping.class);
        ObjectMapper mapper = new ObjectMapper();
        this.mapper = new WordSetMapper(mapper);
        LocalDataServiceImpl localDataService = new LocalDataServiceImpl(wordSetDao, mock(TopicDao.class), sentenceDao, mock(WordTranslationDao.class), mapper, new LoggerBean());
        wordSetService = new WordSetServiceImpl(wordSetDao, mock(WordSetExperienceUtils.class), this.mapper);

        BackendServerFactoryBean factory = new BackendServerFactoryBean();
        Whitebox.setInternalState(factory, "logger", new LoggerBean());
        ServiceFactoryBean mockServiceFactoryBean = mock(ServiceFactoryBean.class);
        when(mockServiceFactoryBean.getLocalDataService()).thenReturn(localDataService);
        Whitebox.setInternalState(factory, "serviceFactory", mockServiceFactoryBean);
        Whitebox.setInternalState(factory, "requestExecutor", new RequestExecutor());
        DataServer server = factory.get();

        AddingNewWordSetInteractor interactor = new AddingNewWordSetInteractor(server, wordSetService);
        view = mock(AddingNewWordSetFragmentView.class);
        presenter = new AddingNewWordSetPresenter(view, interactor);
    }

    @Test
    public void submit_empty12() {
        presenter.submit(asList("", "", "", "", "", "", "", "", "", "", "", ""));

        verify(view).markWordIsEmpty(0);
        verify(view).markWordIsEmpty(1);
        verify(view).markWordIsEmpty(2);
        verify(view).markWordIsEmpty(3);
        verify(view).markWordIsEmpty(4);
        verify(view).markWordIsEmpty(5);
        verify(view).markWordIsEmpty(6);
        verify(view).markWordIsEmpty(7);
        verify(view).markWordIsEmpty(8);
        verify(view).markWordIsEmpty(9);
        verify(view).markWordIsEmpty(10);
        verify(view).markWordIsEmpty(11);

        verify(view, times(0)).markSentencesWereFound(anyInt());
        verify(view, times(0)).markSentencesWereNotFound(anyInt());
        verify(view, times(0)).submitSuccessfully(any(WordSet.class));
        verify(view, times(0)).markWordIsDuplicate(anyInt());
        verify(view, times(0)).markTranslationWasNotFound(anyInt());
    }

    @Test
    public void submit_spaces12() {
        presenter.submit(asList("   ", "  ", "   ", "  ", "    ", "    ", "    ", "   ", "  ", "    ", "   ", " "));

        verify(view).markWordIsEmpty(0);
        verify(view).markWordIsEmpty(1);
        verify(view).markWordIsEmpty(2);
        verify(view).markWordIsEmpty(3);
        verify(view).markWordIsEmpty(4);
        verify(view).markWordIsEmpty(5);
        verify(view).markWordIsEmpty(6);
        verify(view).markWordIsEmpty(7);
        verify(view).markWordIsEmpty(8);
        verify(view).markWordIsEmpty(9);
        verify(view).markWordIsEmpty(10);
        verify(view).markWordIsEmpty(11);

        verify(view, times(0)).markSentencesWereFound(anyInt());
        verify(view, times(0)).markSentencesWereNotFound(anyInt());
        verify(view, times(0)).submitSuccessfully(any(WordSet.class));
        verify(view, times(0)).markWordIsDuplicate(anyInt());
        verify(view, times(0)).markTranslationWasNotFound(anyInt());
    }

    @Test
    public void submit_noSentencesForAnyWord() {
        presenter.submit(asList("  sdfds ", "  dsfss", " fdf3  ", "fdsfa3  ", "  dsfdf ", "   sdfsf ", " sfsd4s   ", "  sfdfs  ", " fsdf2  ", "  fsdfs  ", "  fsdf ", " 232"));

        verify(view).markSentencesWereNotFound(0);
        verify(view).markSentencesWereNotFound(1);
        verify(view).markSentencesWereNotFound(2);
        verify(view).markSentencesWereNotFound(3);
        verify(view).markSentencesWereNotFound(4);
        verify(view).markSentencesWereNotFound(5);
        verify(view).markSentencesWereNotFound(6);
        verify(view).markSentencesWereNotFound(7);
        verify(view).markSentencesWereNotFound(8);
        verify(view).markSentencesWereNotFound(9);
        verify(view).markSentencesWereNotFound(10);
        verify(view).markSentencesWereNotFound(11);

        verify(view, times(0)).markWordIsEmpty(anyInt());
        verify(view, times(0)).markSentencesWereFound(anyInt());
        verify(view, times(0)).submitSuccessfully(any(WordSet.class));
        verify(view, times(0)).markWordIsDuplicate(anyInt());
        verify(view, times(0)).markTranslationWasNotFound(anyInt());
    }

    @Test
    public void submit_noSentencesForFewWord() {
        presenter.submit(asList("  house ", "  dsfss", " door  ", "fdsfa3  ", "  dsfdf ", "   cool ", " sfsd4s   ", "  sfdfs  ", " fsdf2  ", "  fsdfs  ", "  fsdf ", " 232"));

        verify(view, times(0)).markSentencesWereNotFound(0);
        verify(view).markSentencesWereFound(0);
        verify(view).markSentencesWereNotFound(1);
        verify(view, times(0)).markSentencesWereNotFound(2);
        verify(view).markSentencesWereFound(2);
        verify(view).markSentencesWereNotFound(3);
        verify(view).markSentencesWereNotFound(4);
        verify(view, times(0)).markSentencesWereNotFound(5);
        verify(view).markSentencesWereFound(5);
        verify(view).markSentencesWereNotFound(6);
        verify(view).markSentencesWereNotFound(7);
        verify(view).markSentencesWereNotFound(8);
        verify(view).markSentencesWereNotFound(9);
        verify(view).markSentencesWereNotFound(10);
        verify(view).markSentencesWereNotFound(11);

        verify(view, times(0)).markWordIsEmpty(anyInt());
        verify(view, times(3)).markSentencesWereFound(anyInt());
        verify(view, times(0)).submitSuccessfully(any(WordSet.class));
        verify(view, times(0)).markWordIsDuplicate(anyInt());
        verify(view, times(0)).markTranslationWasNotFound(anyInt());
    }

    @Test
    public void submit_allSentencesWereFound() {
        String word0 = "house";
        String word1 = "fox";
        String word2 = "door";
        String word3 = "earthly";
        String word4 = "book";
        String word5 = "cool";
        String word6 = "angel";
        String word7 = "window";
        String word8 = "fork";
        String word9 = "pillow";
        String word10 = "fog";
        presenter.submit(asList("  " + word0 + " ", "  " + word1, " " + word2 + "  ", word3 + "  ", "  " + word4 + " ", "   " + word5 + " ", " " + word6 + "   ", "  " + word7 + "  ", " " + word8 + "  ", "  " + word9 + "  ", "  " + word10 + " "));

        verify(view).markSentencesWereFound(0);
        verify(view).markSentencesWereFound(1);
        verify(view).markSentencesWereFound(2);
        verify(view).markSentencesWereFound(3);
        verify(view).markSentencesWereFound(4);
        verify(view).markSentencesWereFound(5);
        verify(view).markSentencesWereFound(6);
        verify(view).markSentencesWereFound(7);
        verify(view).markSentencesWereFound(8);
        verify(view).markSentencesWereFound(9);
        verify(view).markSentencesWereFound(10);

        verify(view, times(0)).markWordIsEmpty(anyInt());
        verify(view, times(11)).markSentencesWereFound(anyInt());
        verify(view, times(0)).markSentencesWereNotFound(anyInt());
        verify(view, times(0)).markTranslationWasNotFound(anyInt());

        ArgumentCaptor<WordSet> wordSetCaptor = ArgumentCaptor.forClass(WordSet.class);
        verify(view, times(1)).submitSuccessfully(wordSetCaptor.capture());
        verify(view, times(1)).resetWords();

        WordSet wordSet = wordSetCaptor.getValue();
        assertEquals(word0, wordSet.getWords().get(0).getWord());
        assertEquals(word1, wordSet.getWords().get(1).getWord());
        assertEquals(word2, wordSet.getWords().get(2).getWord());
        assertEquals(word3, wordSet.getWords().get(3).getWord());
        assertEquals(word4, wordSet.getWords().get(4).getWord());
        assertEquals(word5, wordSet.getWords().get(5).getWord());
        assertEquals(word6, wordSet.getWords().get(6).getWord());
        assertEquals(word7, wordSet.getWords().get(7).getWord());
        assertEquals(word8, wordSet.getWords().get(8).getWord());
        assertEquals(word9, wordSet.getWords().get(9).getWord());
        assertEquals(word10, wordSet.getWords().get(10).getWord());

        assertEquals(wordSet.getWords().get(0).getWord(), wordSet.getWords().get(0).getTokens());
        assertEquals(wordSet.getWords().get(1).getWord(), wordSet.getWords().get(1).getTokens());
        assertEquals(wordSet.getWords().get(2).getWord(), wordSet.getWords().get(2).getTokens());
        assertEquals("earthly,earth", wordSet.getWords().get(3).getTokens());
        assertEquals(wordSet.getWords().get(4).getWord(), wordSet.getWords().get(4).getTokens());
        assertEquals(wordSet.getWords().get(5).getWord(), wordSet.getWords().get(5).getTokens());
        assertEquals(wordSet.getWords().get(6).getWord(), wordSet.getWords().get(6).getTokens());
        assertEquals(wordSet.getWords().get(7).getWord(), wordSet.getWords().get(7).getTokens());
        assertEquals(wordSet.getWords().get(8).getWord(), wordSet.getWords().get(8).getTokens());
        assertEquals(wordSet.getWords().get(9).getWord(), wordSet.getWords().get(9).getTokens());
        assertEquals(wordSet.getWords().get(10).getWord(), wordSet.getWords().get(10).getTokens());

        assertEquals(new Integer(4964), wordSet.getTop());
        assertEquals(wordSetService.getCustomWordSetsStartsSince(), wordSet.getId());

        wordSet = mapper.toDto(wordSetDao.findById(wordSetService.getCustomWordSetsStartsSince()));
        assertEquals(word0, wordSet.getWords().get(0).getWord());
        assertEquals(word1, wordSet.getWords().get(1).getWord());
        assertEquals(word2, wordSet.getWords().get(2).getWord());
        assertEquals(word3, wordSet.getWords().get(3).getWord());
        assertEquals(word4, wordSet.getWords().get(4).getWord());
        assertEquals(word5, wordSet.getWords().get(5).getWord());
        assertEquals(word6, wordSet.getWords().get(6).getWord());
        assertEquals(word7, wordSet.getWords().get(7).getWord());
        assertEquals(word8, wordSet.getWords().get(8).getWord());
        assertEquals(word9, wordSet.getWords().get(9).getWord());
        assertEquals(word10, wordSet.getWords().get(10).getWord());

        assertEquals(wordSet.getWords().get(0).getWord(), wordSet.getWords().get(0).getTokens());
        assertEquals(wordSet.getWords().get(1).getWord(), wordSet.getWords().get(1).getTokens());
        assertEquals(wordSet.getWords().get(2).getWord(), wordSet.getWords().get(2).getTokens());
        assertEquals("earthly,earth", wordSet.getWords().get(3).getTokens());
        assertEquals(wordSet.getWords().get(4).getWord(), wordSet.getWords().get(4).getTokens());
        assertEquals(wordSet.getWords().get(5).getWord(), wordSet.getWords().get(5).getTokens());
        assertEquals(wordSet.getWords().get(6).getWord(), wordSet.getWords().get(6).getTokens());
        assertEquals(wordSet.getWords().get(7).getWord(), wordSet.getWords().get(7).getTokens());
        assertEquals(wordSet.getWords().get(8).getWord(), wordSet.getWords().get(8).getTokens());
        assertEquals(wordSet.getWords().get(9).getWord(), wordSet.getWords().get(9).getTokens());
        assertEquals(wordSet.getWords().get(10).getWord(), wordSet.getWords().get(10).getTokens());

        assertEquals(new Integer(4964), wordSet.getTop());
        assertEquals(wordSetService.getCustomWordSetsStartsSince(), wordSet.getId());
    }

    @Test
    public void submit_fewWordSetsWereSaved() {
        // new first set
        String word0 = "house";
        String word1 = "fox";
        String word2 = "door";

        presenter.submit(asList("  " + word0 + " ", "  " + word1, " " + word2 + "  "));

        verify(view).markSentencesWereFound(0);
        verify(view).markSentencesWereFound(1);
        verify(view).markSentencesWereFound(2);
        verify(view, times(0)).markWordIsEmpty(anyInt());
        verify(view, times(3)).markSentencesWereFound(anyInt());
        verify(view, times(0)).markSentencesWereNotFound(anyInt());

        verify(view, times(1)).resetWords();
        ArgumentCaptor<WordSet> wordSetCaptor = ArgumentCaptor.forClass(WordSet.class);
        verify(view, times(1)).submitSuccessfully(wordSetCaptor.capture());
        WordSet wordSet = wordSetCaptor.getValue();
        assertEquals(word0, wordSet.getWords().get(0).getWord());
        assertEquals(word1, wordSet.getWords().get(1).getWord());
        assertEquals(word2, wordSet.getWords().get(2).getWord());

        assertEquals(wordSet.getWords().get(0).getWord(), wordSet.getWords().get(0).getTokens());
        assertEquals(wordSet.getWords().get(1).getWord(), wordSet.getWords().get(1).getTokens());
        assertEquals(wordSet.getWords().get(2).getWord(), wordSet.getWords().get(2).getTokens());

        assertEquals(new Integer(1656), wordSet.getTop());
        assertEquals(wordSetService.getCustomWordSetsStartsSince(), wordSet.getId());
        reset(view);


        // new second set
        String word3 = "earthly";
        String word4 = "book";

        presenter.submit(asList("  " + word3 + " ", "  " + word4));

        verify(view).markSentencesWereFound(0);
        verify(view).markSentencesWereFound(1);
        verify(view, times(0)).markWordIsEmpty(anyInt());
        verify(view, times(2)).markSentencesWereFound(anyInt());
        verify(view, times(0)).markSentencesWereNotFound(anyInt());
        verify(view, times(1)).resetWords();
        wordSetCaptor = ArgumentCaptor.forClass(WordSet.class);
        verify(view, times(1)).submitSuccessfully(wordSetCaptor.capture());
        wordSet = wordSetCaptor.getValue();
        assertEquals(word3, wordSet.getWords().get(0).getWord());
        assertEquals(word4, wordSet.getWords().get(1).getWord());

        assertEquals("earthly,earth", wordSet.getWords().get(0).getTokens());
        assertEquals(wordSet.getWords().get(1).getWord(), wordSet.getWords().get(1).getTokens());

        assertEquals(new Integer(10088), wordSet.getTop());
        assertEquals(wordSetService.getCustomWordSetsStartsSince() + 1, wordSet.getId());
        reset(view);


        // already saved first set
        wordSet = mapper.toDto(wordSetDao.findById(wordSetService.getCustomWordSetsStartsSince()));
        assertEquals(word0, wordSet.getWords().get(0).getWord());
        assertEquals(word1, wordSet.getWords().get(1).getWord());
        assertEquals(word2, wordSet.getWords().get(2).getWord());

        assertEquals(wordSet.getWords().get(0).getWord(), wordSet.getWords().get(0).getTokens());
        assertEquals(wordSet.getWords().get(1).getWord(), wordSet.getWords().get(1).getTokens());
        assertEquals(wordSet.getWords().get(2).getWord(), wordSet.getWords().get(2).getTokens());

        assertEquals(new Integer(1656), wordSet.getTop());
        assertEquals(wordSetService.getCustomWordSetsStartsSince(), wordSet.getId());
        reset(view);


        // already saved second set
        wordSet = mapper.toDto(wordSetDao.findById(wordSetService.getCustomWordSetsStartsSince() + 1));
        assertEquals(word3, wordSet.getWords().get(0).getWord());
        assertEquals(word4, wordSet.getWords().get(1).getWord());

        assertEquals("earthly,earth", wordSet.getWords().get(0).getTokens());
        assertEquals(wordSet.getWords().get(1).getWord(), wordSet.getWords().get(1).getTokens());

        assertEquals(new Integer(10088), wordSet.getTop());
        assertEquals(wordSetService.getCustomWordSetsStartsSince() + 1, wordSet.getId());
        reset(view);
    }

    @Test
    public void submit_wordSetWithDuplicates() {
        String word0 = "house";
        String word1 = "fox";
        String word2 = "house";

        presenter.submit(asList("  " + word0 + " ", "  " + word1, " " + word2 + "  "));

        verify(view, times(0)).markSentencesWereNotFound(anyInt());
        verify(view, times(0)).markSentencesWereFound(anyInt());
        verify(view, times(0)).markWordIsEmpty(anyInt());
        verify(view, times(0)).submitSuccessfully(any(WordSet.class));
        verify(view, times(0)).markTranslationWasNotFound(anyInt());
        verify(view).markWordIsDuplicate(2);
    }

    @Test
    public void submit_allAreDuplicates() {
        String word0 = "house";
        String word1 = "house";
        String word2 = "house";

        presenter.submit(asList("  " + word0 + " ", "  " + word1, " " + word2 + "  "));

        verify(view, times(0)).markSentencesWereNotFound(anyInt());
        verify(view, times(0)).markSentencesWereFound(anyInt());
        verify(view, times(0)).markWordIsEmpty(anyInt());
        verify(view, times(0)).submitSuccessfully(any(WordSet.class));
        verify(view, times(0)).markTranslationWasNotFound(anyInt());
        verify(view).markWordIsDuplicate(2);
        verify(view).markWordIsDuplicate(1);
    }

    @After
    public void tearDown() {
        OpenHelperManager.releaseHelper();
    }
}
package talkapp.org.talkappmobile.activity.presenter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.greenrobot.eventbus.EventBus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.sql.SQLException;
import java.util.List;

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.activity.interactor.AddingNewWordSetInteractor;
import talkapp.org.talkappmobile.activity.view.AddingNewWordSetFragmentView;
import talkapp.org.talkappmobile.dao.TopicDao;
import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.events.NewWordIsDuplicateEM;
import talkapp.org.talkappmobile.events.NewWordIsEmptyEM;
import talkapp.org.talkappmobile.events.NewWordSentencesWereFoundEM;
import talkapp.org.talkappmobile.events.NewWordSentencesWereNotFoundEM;
import talkapp.org.talkappmobile.events.NewWordSuccessfullySubmittedEM;
import talkapp.org.talkappmobile.events.NewWordTranslationWasNotFoundEM;
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
import talkapp.org.talkappmobile.service.impl.WordTranslationServiceImpl;
import talkapp.org.talkappmobile.service.mapper.WordSetMapper;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class AddingNewWordSetPresenterAndInteractorIntegTest extends PresenterAndInteractorIntegTest {
    private AddingNewWordSetPresenter presenter;
    private AddingNewWordSetFragmentView view;
    private WordSetMapper mapper;
    private WordSetService wordSetService;
    private EventBus eventBus;

    @Before
    public void setUp() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        this.mapper = new WordSetMapper(mapper);
        WordTranslationDao wordTranslationDao = mock(WordTranslationDao.class);
        LocalDataServiceImpl localDataService = new LocalDataServiceImpl(getWordSetDao(), mock(TopicDao.class), getSentenceDao(), wordTranslationDao, mapper, new LoggerBean());
        wordSetService = new WordSetServiceImpl(getWordSetDao(), getNewWordSetDraftDao(), mock(WordSetExperienceUtils.class), this.mapper);

        BackendServerFactoryBean factory = new BackendServerFactoryBean();
        Whitebox.setInternalState(factory, "logger", new LoggerBean());
        ServiceFactoryBean mockServiceFactoryBean = mock(ServiceFactoryBean.class);
        when(mockServiceFactoryBean.getLocalDataService()).thenReturn(localDataService);
        Whitebox.setInternalState(factory, "serviceFactory", mockServiceFactoryBean);
        Whitebox.setInternalState(factory, "requestExecutor", new RequestExecutor());
        DataServer server = factory.get();

        WordTranslationServiceImpl wordTranslationService = new WordTranslationServiceImpl(wordTranslationDao);
        eventBus = mock(EventBus.class);
        AddingNewWordSetInteractor interactor = new AddingNewWordSetInteractor(server, wordSetService, wordTranslationService, eventBus);
        view = mock(AddingNewWordSetFragmentView.class);
        presenter = new AddingNewWordSetPresenter(view, interactor);
    }

    @Test
    public void submit_empty12() {
        presenter.submit(asList("", "", "", "", "", "", "", "", "", "", "", ""));

        verify(eventBus).post(new NewWordIsEmptyEM(0));
        verify(eventBus).post(new NewWordIsEmptyEM(1));
        verify(eventBus).post(new NewWordIsEmptyEM(2));
        verify(eventBus).post(new NewWordIsEmptyEM(3));
        verify(eventBus).post(new NewWordIsEmptyEM(4));
        verify(eventBus).post(new NewWordIsEmptyEM(5));
        verify(eventBus).post(new NewWordIsEmptyEM(6));
        verify(eventBus).post(new NewWordIsEmptyEM(7));
        verify(eventBus).post(new NewWordIsEmptyEM(8));
        verify(eventBus).post(new NewWordIsEmptyEM(9));
        verify(eventBus).post(new NewWordIsEmptyEM(10));
        verify(eventBus).post(new NewWordIsEmptyEM(11));

        verify(eventBus, times(0)).post(any(NewWordSentencesWereFoundEM.class));
        verify(eventBus, times(0)).post(any(NewWordSentencesWereNotFoundEM.class));
        verify(eventBus, times(0)).post(any(NewWordSuccessfullySubmittedEM.class));
        verify(eventBus, times(0)).post(any(NewWordIsDuplicateEM.class));
        verify(eventBus, times(0)).post(any(NewWordTranslationWasNotFoundEM.class));
    }

    @Test
    public void submit_spaces12() {
        presenter.submit(asList("   ", "  ", "   ", "  ", "    ", "    ", "    ", "   ", "  ", "    ", "   ", " "));

        verify(eventBus).post(new NewWordIsEmptyEM(0));
        verify(eventBus).post(new NewWordIsEmptyEM(1));
        verify(eventBus).post(new NewWordIsEmptyEM(2));
        verify(eventBus).post(new NewWordIsEmptyEM(3));
        verify(eventBus).post(new NewWordIsEmptyEM(4));
        verify(eventBus).post(new NewWordIsEmptyEM(5));
        verify(eventBus).post(new NewWordIsEmptyEM(6));
        verify(eventBus).post(new NewWordIsEmptyEM(7));
        verify(eventBus).post(new NewWordIsEmptyEM(8));
        verify(eventBus).post(new NewWordIsEmptyEM(9));
        verify(eventBus).post(new NewWordIsEmptyEM(10));
        verify(eventBus).post(new NewWordIsEmptyEM(11));

        verify(eventBus, times(0)).post(any(NewWordSentencesWereFoundEM.class));
        verify(eventBus, times(0)).post(any(NewWordSentencesWereNotFoundEM.class));
        verify(eventBus, times(0)).post(any(NewWordSuccessfullySubmittedEM.class));
        verify(eventBus, times(0)).post(any(NewWordIsDuplicateEM.class));
        verify(eventBus, times(0)).post(any(NewWordTranslationWasNotFoundEM.class));
    }

    @Test
    public void submit_noSentencesForAnyWord() {
        presenter.submit(asList("  sdfds ", "  dsfss", " fdf3  ", "fdsfa3  ", "  dsfdf ", "   sdfsf ", " sfsd4s   ", "  sfdfs  ", " fsdf2  ", "  fsdfs  ", "  fsdf ", " 232"));

        verify(eventBus).post(new NewWordSentencesWereNotFoundEM(0));
        verify(eventBus).post(new NewWordSentencesWereNotFoundEM(1));
        verify(eventBus).post(new NewWordSentencesWereNotFoundEM(2));
        verify(eventBus).post(new NewWordSentencesWereNotFoundEM(3));
        verify(eventBus).post(new NewWordSentencesWereNotFoundEM(4));
        verify(eventBus).post(new NewWordSentencesWereNotFoundEM(5));
        verify(eventBus).post(new NewWordSentencesWereNotFoundEM(6));
        verify(eventBus).post(new NewWordSentencesWereNotFoundEM(7));
        verify(eventBus).post(new NewWordSentencesWereNotFoundEM(8));
        verify(eventBus).post(new NewWordSentencesWereNotFoundEM(9));
        verify(eventBus).post(new NewWordSentencesWereNotFoundEM(10));
        verify(eventBus).post(new NewWordSentencesWereNotFoundEM(11));

        verify(eventBus, times(0)).post(any(NewWordIsEmptyEM.class));
        verify(eventBus, times(0)).post(any(NewWordSentencesWereFoundEM.class));
        verify(eventBus, times(0)).post(any(NewWordSuccessfullySubmittedEM.class));
        verify(eventBus, times(0)).post(any(NewWordIsDuplicateEM.class));
        verify(eventBus, times(0)).post(any(NewWordTranslationWasNotFoundEM.class));
    }

    @Test
    public void submit_noSentencesForFewWord() {
        presenter.submit(asList("  house ", "  dsfss", " door  ", "fdsfa3  ", "  dsfdf ", "   cool ", " sfsd4s   ", "  sfdfs  ", " fsdf2  ", "  fsdfs  ", "  fsdf ", " 232"));

        verify(eventBus, times(0)).post(new NewWordSentencesWereNotFoundEM(0));
        verify(eventBus).post(new NewWordSentencesWereFoundEM(0));
        verify(eventBus).post(new NewWordSentencesWereNotFoundEM(1));
        verify(eventBus, times(0)).post(new NewWordSentencesWereNotFoundEM(2));
        verify(eventBus).post(new NewWordSentencesWereFoundEM(2));
        verify(eventBus).post(new NewWordSentencesWereNotFoundEM(3));
        verify(eventBus).post(new NewWordSentencesWereNotFoundEM(4));
        verify(eventBus, times(0)).post(new NewWordSentencesWereNotFoundEM(5));
        verify(eventBus).post(new NewWordSentencesWereFoundEM(5));
        verify(eventBus).post(new NewWordSentencesWereNotFoundEM(6));
        verify(eventBus).post(new NewWordSentencesWereNotFoundEM(7));
        verify(eventBus).post(new NewWordSentencesWereNotFoundEM(8));
        verify(eventBus).post(new NewWordSentencesWereNotFoundEM(9));
        verify(eventBus).post(new NewWordSentencesWereNotFoundEM(10));
        verify(eventBus).post(new NewWordSentencesWereNotFoundEM(11));

        verify(eventBus, times(0)).post(any(NewWordIsEmptyEM.class));
        verify(eventBus, times(3)).post(any(NewWordSentencesWereFoundEM.class));
        verify(eventBus, times(0)).post(any(NewWordSuccessfullySubmittedEM.class));
        verify(eventBus, times(0)).post(any(NewWordIsDuplicateEM.class));
        verify(eventBus, times(0)).post(any(NewWordTranslationWasNotFoundEM.class));
    }

    @Test
    public void submit_allSentencesWereFound() throws SQLException {
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

        verify(eventBus).post(new NewWordSentencesWereFoundEM(0));
        verify(eventBus).post(new NewWordSentencesWereFoundEM(1));
        verify(eventBus).post(new NewWordSentencesWereFoundEM(2));
        verify(eventBus).post(new NewWordSentencesWereFoundEM(3));
        verify(eventBus).post(new NewWordSentencesWereFoundEM(4));
        verify(eventBus).post(new NewWordSentencesWereFoundEM(5));
        verify(eventBus).post(new NewWordSentencesWereFoundEM(6));
        verify(eventBus).post(new NewWordSentencesWereFoundEM(7));
        verify(eventBus).post(new NewWordSentencesWereFoundEM(8));
        verify(eventBus).post(new NewWordSentencesWereFoundEM(9));
        verify(eventBus).post(new NewWordSentencesWereFoundEM(10));

        verify(eventBus, times(0)).post(any(NewWordIsEmptyEM.class));
        verify(eventBus, times(11)).post(any(NewWordSentencesWereFoundEM.class));
        verify(eventBus, times(0)).post(any(NewWordSentencesWereNotFoundEM.class));
        verify(eventBus, times(0)).post(any(NewWordTranslationWasNotFoundEM.class));

        ArgumentCaptor<NewWordSuccessfullySubmittedEM> wordSetCaptor = ArgumentCaptor.forClass(NewWordSuccessfullySubmittedEM.class);
        verify(eventBus, times(12)).post(wordSetCaptor.capture());

        WordSet wordSet = getClass(wordSetCaptor, NewWordSuccessfullySubmittedEM.class).getWordSet();
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

        wordSet = mapper.toDto(getWordSetDao().findById(wordSetService.getCustomWordSetsStartsSince()));
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
    public void submit_fewWordSetsWereSaved() throws SQLException {
        // new first set
        String word0 = "house";
        String word1 = "fox";
        String word2 = "door";

        presenter.submit(asList("  " + word0 + " ", "  " + word1, " " + word2 + "  "));

        verify(eventBus).post(new NewWordSentencesWereFoundEM(0));
        verify(eventBus).post(new NewWordSentencesWereFoundEM(1));
        verify(eventBus).post(new NewWordSentencesWereFoundEM(2));
        verify(eventBus, times(0)).post(any(NewWordIsEmptyEM.class));
        verify(eventBus, times(3)).post(any(NewWordSentencesWereFoundEM.class));
        verify(eventBus, times(0)).post(any(NewWordSentencesWereNotFoundEM.class));

        ArgumentCaptor<NewWordSuccessfullySubmittedEM> wordSetCaptor = ArgumentCaptor.forClass(NewWordSuccessfullySubmittedEM.class);
        verify(eventBus, times(4)).post(wordSetCaptor.capture());
        WordSet wordSet = getClass(wordSetCaptor, NewWordSuccessfullySubmittedEM.class).getWordSet();
        assertEquals(word0, wordSet.getWords().get(0).getWord());
        assertEquals(word1, wordSet.getWords().get(1).getWord());
        assertEquals(word2, wordSet.getWords().get(2).getWord());

        assertEquals(wordSet.getWords().get(0).getWord(), wordSet.getWords().get(0).getTokens());
        assertEquals(wordSet.getWords().get(1).getWord(), wordSet.getWords().get(1).getTokens());
        assertEquals(wordSet.getWords().get(2).getWord(), wordSet.getWords().get(2).getTokens());

        assertEquals(new Integer(1656), wordSet.getTop());
        assertEquals(wordSetService.getCustomWordSetsStartsSince(), wordSet.getId());
        reset(view);
        reset(eventBus);


        // new second set
        String word3 = "earthly";
        String word4 = "book";

        presenter.submit(asList("  " + word3 + " ", "  " + word4));

        verify(eventBus).post(new NewWordSentencesWereFoundEM(0));
        verify(eventBus).post(new NewWordSentencesWereFoundEM(1));
        verify(eventBus, times(0)).post(any(NewWordIsEmptyEM.class));
        verify(eventBus, times(2)).post(any(NewWordSentencesWereFoundEM.class));
        verify(eventBus, times(0)).post(any(NewWordSentencesWereNotFoundEM.class));
        wordSetCaptor = ArgumentCaptor.forClass(NewWordSuccessfullySubmittedEM.class);
        verify(eventBus, times(3)).post(wordSetCaptor.capture());
        wordSet = getClass(wordSetCaptor, NewWordSuccessfullySubmittedEM.class).getWordSet();
        assertEquals(word3, wordSet.getWords().get(0).getWord());
        assertEquals(word4, wordSet.getWords().get(1).getWord());

        assertEquals("earthly,earth", wordSet.getWords().get(0).getTokens());
        assertEquals(wordSet.getWords().get(1).getWord(), wordSet.getWords().get(1).getTokens());

        assertEquals(new Integer(10088), wordSet.getTop());
        assertEquals(wordSetService.getCustomWordSetsStartsSince() + 1, wordSet.getId());
        reset(view);


        // already saved first set
        wordSet = mapper.toDto(getWordSetDao().findById(wordSetService.getCustomWordSetsStartsSince()));
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
        wordSet = mapper.toDto(getWordSetDao().findById(wordSetService.getCustomWordSetsStartsSince() + 1));
        assertEquals(word3, wordSet.getWords().get(0).getWord());
        assertEquals(word4, wordSet.getWords().get(1).getWord());

        assertEquals("earthly,earth", wordSet.getWords().get(0).getTokens());
        assertEquals(wordSet.getWords().get(1).getWord(), wordSet.getWords().get(1).getTokens());

        assertEquals(new Integer(10088), wordSet.getTop());
        assertEquals(wordSetService.getCustomWordSetsStartsSince() + 1, wordSet.getId());
        reset(view);
    }

    private <T> T getClass(ArgumentCaptor<T> wordSetCaptor, Class<T> clazz) {
        List<T> allValues = wordSetCaptor.getAllValues();
        for (int i = 0; i < allValues.size(); i++) {
            if (allValues.get(i).getClass().equals(clazz)) {
                return allValues.get(i);
            }
        }
        return null;
    }

    @Test
    public void submit_wordSetWithDuplicates() {
        String word0 = "house";
        String word1 = "fox";
        String word2 = "house";

        presenter.submit(asList("  " + word0 + " ", "  " + word1, " " + word2 + "  "));

        verify(eventBus, times(0)).post(any(NewWordSentencesWereNotFoundEM.class));
        verify(eventBus, times(0)).post(any(NewWordSentencesWereFoundEM.class));
        verify(eventBus, times(0)).post(any(NewWordIsEmptyEM.class));
        verify(eventBus, times(0)).post(any(NewWordSuccessfullySubmittedEM.class));
        verify(eventBus, times(0)).post(any(NewWordTranslationWasNotFoundEM.class));
        verify(eventBus).post(new NewWordIsDuplicateEM(2));
    }

    @Test
    public void submit_allAreDuplicates() {
        String word0 = "house";
        String word1 = "house";
        String word2 = "house";

        presenter.submit(asList("  " + word0 + " ", "  " + word1, " " + word2 + "  "));

        verify(eventBus, times(0)).post(any(NewWordSentencesWereNotFoundEM.class));
        verify(eventBus, times(0)).post(any(NewWordSentencesWereFoundEM.class));
        verify(eventBus, times(0)).post(any(NewWordIsEmptyEM.class));
        verify(eventBus, times(0)).post(any(NewWordSuccessfullySubmittedEM.class));
        verify(eventBus, times(0)).post(any(NewWordTranslationWasNotFoundEM.class));
        verify(eventBus).post(new NewWordIsDuplicateEM(2));
        verify(eventBus).post(new NewWordIsDuplicateEM(1));
    }

    @Test
    public void submit_allSentencesWereFoundButThereFewExpressions() throws SQLException {
        String word0 = "look for|искать";
        String word1 = "fox";
        String word2 = "door";
        String word3 = "make out|разглядеть, различить, разбирать";
        String word4 = "book";
        String word5 = "cool";
        String word6 = "angel";
        String word7 = "window";
        String word8 = "in fact|по факту, фактически, на самом деле, по сути";
        String word9 = "pillow";
        String word10 = "fog";
        presenter.submit(asList("  " + word0 + " ", "  " + word1, " " + word2 + "  ", word3 + "  ", "  " + word4 + " ", "   " + word5 + " ", " " + word6 + "   ", "  " + word7 + "  ", " " + word8 + "  ", "  " + word9 + "  ", "  " + word10 + " "));

        verify(eventBus).post(new NewWordSentencesWereFoundEM(0));
        verify(eventBus).post(new NewWordSentencesWereFoundEM(1));
        verify(eventBus).post(new NewWordSentencesWereFoundEM(2));
        verify(eventBus).post(new NewWordSentencesWereFoundEM(3));
        verify(eventBus).post(new NewWordSentencesWereFoundEM(4));
        verify(eventBus).post(new NewWordSentencesWereFoundEM(5));
        verify(eventBus).post(new NewWordSentencesWereFoundEM(6));
        verify(eventBus).post(new NewWordSentencesWereFoundEM(7));
        verify(eventBus).post(new NewWordSentencesWereFoundEM(8));
        verify(eventBus).post(new NewWordSentencesWereFoundEM(9));
        verify(eventBus).post(new NewWordSentencesWereFoundEM(10));

        verify(eventBus, times(0)).post(any(NewWordIsEmptyEM.class));
        verify(eventBus, times(11)).post(any(NewWordSentencesWereFoundEM.class));
        verify(eventBus, times(0)).post(any(NewWordSentencesWereNotFoundEM.class));
        verify(eventBus, times(0)).post(any(NewWordTranslationWasNotFoundEM.class));

        ArgumentCaptor<NewWordSuccessfullySubmittedEM> wordSetCaptor = ArgumentCaptor.forClass(NewWordSuccessfullySubmittedEM.class);
        verify(eventBus, times(12)).post(wordSetCaptor.capture());

        WordSet wordSet = getClass(wordSetCaptor, NewWordSuccessfullySubmittedEM.class).getWordSet();
        assertEquals(word0.split("\\|")[0], wordSet.getWords().get(0).getWord());
        assertEquals(word1, wordSet.getWords().get(1).getWord());
        assertEquals(word2, wordSet.getWords().get(2).getWord());
        assertEquals(word3.split("\\|")[0], wordSet.getWords().get(3).getWord());
        assertEquals(word4, wordSet.getWords().get(4).getWord());
        assertEquals(word5, wordSet.getWords().get(5).getWord());
        assertEquals(word6, wordSet.getWords().get(6).getWord());
        assertEquals(word7, wordSet.getWords().get(7).getWord());
        assertEquals(word8.split("\\|")[0], wordSet.getWords().get(8).getWord());
        assertEquals(word9, wordSet.getWords().get(9).getWord());
        assertEquals(word10, wordSet.getWords().get(10).getWord());

        assertEquals(null, wordSet.getWords().get(0).getTokens());
        assertEquals(wordSet.getWords().get(1).getWord(), wordSet.getWords().get(1).getTokens());
        assertEquals(wordSet.getWords().get(2).getWord(), wordSet.getWords().get(2).getTokens());
        assertEquals(null, wordSet.getWords().get(3).getTokens());
        assertEquals(wordSet.getWords().get(4).getWord(), wordSet.getWords().get(4).getTokens());
        assertEquals(wordSet.getWords().get(5).getWord(), wordSet.getWords().get(5).getTokens());
        assertEquals(wordSet.getWords().get(6).getWord(), wordSet.getWords().get(6).getTokens());
        assertEquals(wordSet.getWords().get(7).getWord(), wordSet.getWords().get(7).getTokens());
        assertEquals(null, wordSet.getWords().get(8).getTokens());
        assertEquals(wordSet.getWords().get(9).getWord(), wordSet.getWords().get(9).getTokens());
        assertEquals(wordSet.getWords().get(10).getWord(), wordSet.getWords().get(10).getTokens());

        assertEquals(new Integer(7900), wordSet.getTop());
        assertEquals(wordSetService.getCustomWordSetsStartsSince(), wordSet.getId());

        wordSet = mapper.toDto(getWordSetDao().findById(wordSetService.getCustomWordSetsStartsSince()));
        assertEquals(word0.split("\\|")[0], wordSet.getWords().get(0).getWord());
        assertEquals(word1, wordSet.getWords().get(1).getWord());
        assertEquals(word2, wordSet.getWords().get(2).getWord());
        assertEquals(word3.split("\\|")[0], wordSet.getWords().get(3).getWord());
        assertEquals(word4, wordSet.getWords().get(4).getWord());
        assertEquals(word5, wordSet.getWords().get(5).getWord());
        assertEquals(word6, wordSet.getWords().get(6).getWord());
        assertEquals(word7, wordSet.getWords().get(7).getWord());
        assertEquals(word8.split("\\|")[0], wordSet.getWords().get(8).getWord());
        assertEquals(word9, wordSet.getWords().get(9).getWord());
        assertEquals(word10, wordSet.getWords().get(10).getWord());

        assertEquals(null, wordSet.getWords().get(0).getTokens());
        assertEquals(wordSet.getWords().get(1).getWord(), wordSet.getWords().get(1).getTokens());
        assertEquals(wordSet.getWords().get(2).getWord(), wordSet.getWords().get(2).getTokens());
        assertEquals(null, wordSet.getWords().get(3).getTokens());
        assertEquals(wordSet.getWords().get(4).getWord(), wordSet.getWords().get(4).getTokens());
        assertEquals(wordSet.getWords().get(5).getWord(), wordSet.getWords().get(5).getTokens());
        assertEquals(wordSet.getWords().get(6).getWord(), wordSet.getWords().get(6).getTokens());
        assertEquals(wordSet.getWords().get(7).getWord(), wordSet.getWords().get(7).getTokens());
        assertEquals(null, wordSet.getWords().get(8).getTokens());
        assertEquals(wordSet.getWords().get(9).getWord(), wordSet.getWords().get(9).getTokens());
        assertEquals(wordSet.getWords().get(10).getWord(), wordSet.getWords().get(10).getTokens());

        assertEquals(new Integer(7900), wordSet.getTop());
        assertEquals(wordSetService.getCustomWordSetsStartsSince(), wordSet.getId());
    }

    @After
    public void tearDown() {
        OpenHelperManager.releaseHelper();
    }
}
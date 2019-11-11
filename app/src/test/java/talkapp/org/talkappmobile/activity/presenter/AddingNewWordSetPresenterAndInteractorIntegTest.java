package talkapp.org.talkappmobile.activity.presenter;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;

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
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.DaoHelper;
import talkapp.org.talkappmobile.controller.AddingNewWordSetFragmentController;
import talkapp.org.talkappmobile.dao.TopicDao;
import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.events.AddNewWordSetButtonSubmitClickedEM;
import talkapp.org.talkappmobile.events.NewWordSentencesWereNotFoundEM;
import talkapp.org.talkappmobile.events.NewWordSuccessfullySubmittedEM;
import talkapp.org.talkappmobile.events.NewWordTranslationWasNotFoundEM;
import talkapp.org.talkappmobile.events.SomeWordIsEmptyEM;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
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
import talkapp.org.talkappmobile.service.mapper.WordTranslationMapper;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static talkapp.org.talkappmobile.service.impl.AddingEditingNewWordSetsServiceImpl.RUSSIAN_LANGUAGE;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class AddingNewWordSetPresenterAndInteractorIntegTest {
    private WordSetMapper mapper;
    private WordSetService wordSetService;
    private EventBus eventBus;
    private AddingNewWordSetFragmentController controller;
    private DaoHelper daoHelper;
    private WordTranslationDao wordTranslationDao;
    private WordTranslationMapper wordTranslationMapper;

    @Before
    public void setUp() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        this.mapper = new WordSetMapper(mapper);
        this.wordTranslationMapper = new WordTranslationMapper(mapper);
        daoHelper = new DaoHelper();
        wordTranslationDao = daoHelper.getWordTranslationDao();
        LocalDataServiceImpl localDataService = new LocalDataServiceImpl(daoHelper.getWordSetDao(), mock(TopicDao.class), daoHelper.getSentenceDao(), wordTranslationDao, mapper, new LoggerBean());
        wordSetService = new WordSetServiceImpl(daoHelper.getWordSetDao(), daoHelper.getCurrentWordSetDao(), daoHelper.getNewWordSetDraftDao(), mock(WordSetExperienceUtils.class), this.mapper, mapper);
        wordSetService = new WordSetServiceImpl(daoHelper.getWordSetDao(), daoHelper.getCurrentWordSetDao(), daoHelper.getNewWordSetDraftDao(), mock(WordSetExperienceUtils.class), this.mapper, mapper);

        BackendServerFactoryBean factory = new BackendServerFactoryBean();
        Whitebox.setInternalState(factory, "logger", new LoggerBean());
        ServiceFactoryBean mockServiceFactoryBean = mock(ServiceFactoryBean.class);
        when(mockServiceFactoryBean.getLocalDataService()).thenReturn(localDataService);
        WordTranslationServiceImpl wordTranslationService = new WordTranslationServiceImpl(wordTranslationDao, wordTranslationMapper);
        when(mockServiceFactoryBean.getWordTranslationService()).thenReturn(wordTranslationService);
        when(mockServiceFactoryBean.getWordSetExperienceRepository()).thenReturn(wordSetService);
        Whitebox.setInternalState(factory, "serviceFactory", mockServiceFactoryBean);
        Whitebox.setInternalState(factory, "requestExecutor", new RequestExecutor());
        DataServer server = factory.get();

        eventBus = mock(EventBus.class);
        controller = new AddingNewWordSetFragmentController(eventBus, server, mockServiceFactoryBean);
    }

    @Test
    public void submit_empty12() {
        LinkedList<WordTranslation> wordTranslations = getWordTranslations(asList("", "", "", "", "", "", "", "", "", "", "", ""), false);
        controller.handle(new AddNewWordSetButtonSubmitClickedEM(wordTranslations));

        verify(eventBus).post(any(SomeWordIsEmptyEM.class));

        verify(eventBus, times(0)).post(any(NewWordSentencesWereNotFoundEM.class));
        verify(eventBus, times(0)).post(any(NewWordSuccessfullySubmittedEM.class));
        verify(eventBus, times(0)).post(any(NewWordTranslationWasNotFoundEM.class));
    }

    @NonNull
    private LinkedList<WordTranslation> getWordTranslations(List<String> strings, boolean setTranslation) {
        LinkedList<WordTranslation> wordTranslations = new LinkedList<>();
        for (String string : strings) {
            WordTranslation input = new WordTranslation();
            input.setWord(string);
            if (setTranslation) {
                input.setTranslation(string);
            }
            wordTranslations.add(input);
        }
        return wordTranslations;
    }

    @NonNull
    private LinkedList<WordTranslation> getWordTranslations(List<String> strings) {
        LinkedList<WordTranslation> wordTranslations = new LinkedList<>();
        int id = 0;
        for (String string : strings) {
            WordTranslation input = new WordTranslation();
            input.setId(String.valueOf(id++));
            String[] split = string.split("\\|");
            if (split.length == 2) {
                input.setWord(split[0]);
                input.setTranslation(split[1]);
            } else {
                input.setWord(split[0]);
            }
            wordTranslations.add(input);
        }
        return wordTranslations;
    }

    @Test
    public void submit_spaces12() {
        LinkedList<WordTranslation> wordTranslations = getWordTranslations(asList("   ", "  ", "   ", "  ", "    ", "    ", "    ", "   ", "  ", "    ", "   ", " "), false);
        controller.handle(new AddNewWordSetButtonSubmitClickedEM(wordTranslations));

        verify(eventBus).post(any(SomeWordIsEmptyEM.class));

        verify(eventBus, times(0)).post(any(NewWordSentencesWereNotFoundEM.class));
        verify(eventBus, times(0)).post(any(NewWordSuccessfullySubmittedEM.class));
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
        controller.handle(new AddNewWordSetButtonSubmitClickedEM(getWordTranslations(asList("  " + word0 + " ", "  " + word1, " " + word2 + "  ", word3 + "  ", "  " + word4 + " ", "   " + word5 + " ", " " + word6 + "   ", "  " + word7 + "  ", " " + word8 + "  ", "  " + word9 + "  ", "  " + word10 + " "), false)));

        verify(eventBus, times(0)).post(any(NewWordSentencesWereNotFoundEM.class));
        verify(eventBus, times(0)).post(any(NewWordTranslationWasNotFoundEM.class));

        ArgumentCaptor<NewWordSuccessfullySubmittedEM> wordSetCaptor = ArgumentCaptor.forClass(NewWordSuccessfullySubmittedEM.class);
        verify(eventBus, times(1)).post(wordSetCaptor.capture());

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

        wordSet = mapper.toDto(daoHelper.getWordSetDao().findById(wordSetService.getCustomWordSetsStartsSince()));
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

        controller.handle(new AddNewWordSetButtonSubmitClickedEM(getWordTranslations(asList("  " + word0 + " ", "  " + word1, " " + word2 + "  "), false)));

        verify(eventBus, times(0)).post(any(NewWordSentencesWereNotFoundEM.class));

        ArgumentCaptor<NewWordSuccessfullySubmittedEM> wordSetCaptor = ArgumentCaptor.forClass(NewWordSuccessfullySubmittedEM.class);
        verify(eventBus, times(1)).post(wordSetCaptor.capture());
        WordSet wordSet = getClass(wordSetCaptor, NewWordSuccessfullySubmittedEM.class).getWordSet();
        assertEquals(word0, wordSet.getWords().get(0).getWord());
        assertEquals(word1, wordSet.getWords().get(1).getWord());
        assertEquals(word2, wordSet.getWords().get(2).getWord());

        assertEquals(wordSet.getWords().get(0).getWord(), wordSet.getWords().get(0).getTokens());
        assertEquals(wordSet.getWords().get(1).getWord(), wordSet.getWords().get(1).getTokens());
        assertEquals(wordSet.getWords().get(2).getWord(), wordSet.getWords().get(2).getTokens());

        assertEquals(new Integer(1656), wordSet.getTop());
        assertEquals(wordSetService.getCustomWordSetsStartsSince(), wordSet.getId());
        reset(eventBus);


        // new second set
        String word3 = "earthly";
        String word4 = "book";

        controller.handle(new AddNewWordSetButtonSubmitClickedEM(getWordTranslations(asList("  " + word3 + " ", "  " + word4), false)));

        verify(eventBus, times(0)).post(any(NewWordSentencesWereNotFoundEM.class));
        wordSetCaptor = ArgumentCaptor.forClass(NewWordSuccessfullySubmittedEM.class);
        verify(eventBus, times(1)).post(wordSetCaptor.capture());
        wordSet = getClass(wordSetCaptor, NewWordSuccessfullySubmittedEM.class).getWordSet();
        assertEquals(word3, wordSet.getWords().get(0).getWord());
        assertEquals(word4, wordSet.getWords().get(1).getWord());

        assertEquals("earthly,earth", wordSet.getWords().get(0).getTokens());
        assertEquals(wordSet.getWords().get(1).getWord(), wordSet.getWords().get(1).getTokens());

        assertEquals(new Integer(10088), wordSet.getTop());
        assertEquals(wordSetService.getCustomWordSetsStartsSince() + 1, wordSet.getId());

        // already saved first set
        wordSet = mapper.toDto(daoHelper.getWordSetDao().findById(wordSetService.getCustomWordSetsStartsSince()));
        assertEquals(word0, wordSet.getWords().get(0).getWord());
        assertEquals(word1, wordSet.getWords().get(1).getWord());
        assertEquals(word2, wordSet.getWords().get(2).getWord());

        assertEquals(wordSet.getWords().get(0).getWord(), wordSet.getWords().get(0).getTokens());
        assertEquals(wordSet.getWords().get(1).getWord(), wordSet.getWords().get(1).getTokens());
        assertEquals(wordSet.getWords().get(2).getWord(), wordSet.getWords().get(2).getTokens());

        assertEquals(new Integer(1656), wordSet.getTop());
        assertEquals(wordSetService.getCustomWordSetsStartsSince(), wordSet.getId());

        // already saved second set
        wordSet = mapper.toDto(daoHelper.getWordSetDao().findById(wordSetService.getCustomWordSetsStartsSince() + 1));
        assertEquals(word3, wordSet.getWords().get(0).getWord());
        assertEquals(word4, wordSet.getWords().get(1).getWord());

        assertEquals("earthly,earth", wordSet.getWords().get(0).getTokens());
        assertEquals(wordSet.getWords().get(1).getWord(), wordSet.getWords().get(1).getTokens());

        assertEquals(new Integer(10088), wordSet.getTop());
        assertEquals(wordSetService.getCustomWordSetsStartsSince() + 1, wordSet.getId());
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
        List<WordTranslation> words = getWordTranslations(asList("  " + word0 + " ", "  " + word1, " " + word2 + "  ", word3 + "  ", "  " + word4 + " ", "   " + word5 + " ", " " + word6 + "   ", "  " + word7 + "  ", " " + word8 + "  ", "  " + word9 + "  ", "  " + word10 + " "));
        for (WordTranslation word : words) {
            if (word.getTranslation() == null) {
                continue;
            }
            WordTranslation wordTranslation = new WordTranslation();
            wordTranslation.setId(word.getId());
            wordTranslation.setLanguage(RUSSIAN_LANGUAGE);
            wordTranslation.setTranslation(word.getTranslation().trim());
            wordTranslation.setWord(word.getWord().trim());
            wordTranslation.setTokens(word.getWord().trim());
            wordTranslationDao.save(asList(wordTranslationMapper.toMapping(wordTranslation)));
        }
        controller.handle(new AddNewWordSetButtonSubmitClickedEM(words));

        verify(eventBus, times(0)).post(any(NewWordSentencesWereNotFoundEM.class));
        verify(eventBus, times(0)).post(any(NewWordTranslationWasNotFoundEM.class));

        ArgumentCaptor<NewWordSuccessfullySubmittedEM> wordSetCaptor = ArgumentCaptor.forClass(NewWordSuccessfullySubmittedEM.class);
        verify(eventBus, times(1)).post(wordSetCaptor.capture());

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

        assertEquals(wordSet.getWords().get(0).getWord(), wordSet.getWords().get(0).getTokens());
        assertEquals(wordSet.getWords().get(1).getWord(), wordSet.getWords().get(1).getTokens());
        assertEquals(wordSet.getWords().get(2).getWord(), wordSet.getWords().get(2).getTokens());
        assertEquals(wordSet.getWords().get(3).getWord(), wordSet.getWords().get(3).getTokens());
        assertEquals(wordSet.getWords().get(4).getWord(), wordSet.getWords().get(4).getTokens());
        assertEquals(wordSet.getWords().get(5).getWord(), wordSet.getWords().get(5).getTokens());
        assertEquals(wordSet.getWords().get(6).getWord(), wordSet.getWords().get(6).getTokens());
        assertEquals(wordSet.getWords().get(7).getWord(), wordSet.getWords().get(7).getTokens());
        assertEquals(wordSet.getWords().get(8).getWord(), wordSet.getWords().get(8).getTokens());
        assertEquals(wordSet.getWords().get(9).getWord(), wordSet.getWords().get(9).getTokens());
        assertEquals(wordSet.getWords().get(10).getWord(), wordSet.getWords().get(10).getTokens());

        assertEquals(new Integer(7900), wordSet.getTop());
        assertEquals(wordSetService.getCustomWordSetsStartsSince(), wordSet.getId());

        wordSet = mapper.toDto(daoHelper.getWordSetDao().findById(wordSetService.getCustomWordSetsStartsSince()));
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

        assertEquals(wordSet.getWords().get(0).getWord(), wordSet.getWords().get(0).getTokens());
        assertEquals(wordSet.getWords().get(1).getWord(), wordSet.getWords().get(1).getTokens());
        assertEquals(wordSet.getWords().get(2).getWord(), wordSet.getWords().get(2).getTokens());
        assertEquals(wordSet.getWords().get(3).getWord(), wordSet.getWords().get(3).getTokens());
        assertEquals(wordSet.getWords().get(4).getWord(), wordSet.getWords().get(4).getTokens());
        assertEquals(wordSet.getWords().get(5).getWord(), wordSet.getWords().get(5).getTokens());
        assertEquals(wordSet.getWords().get(6).getWord(), wordSet.getWords().get(6).getTokens());
        assertEquals(wordSet.getWords().get(7).getWord(), wordSet.getWords().get(7).getTokens());
        assertEquals(wordSet.getWords().get(8).getWord(), wordSet.getWords().get(8).getTokens());
        assertEquals(wordSet.getWords().get(9).getWord(), wordSet.getWords().get(9).getTokens());
        assertEquals(wordSet.getWords().get(10).getWord(), wordSet.getWords().get(10).getTokens());

        assertEquals(new Integer(7900), wordSet.getTop());
        assertEquals(wordSetService.getCustomWordSetsStartsSince(), wordSet.getId());
    }

    @After
    public void tearDown() {
        daoHelper.releaseHelper();
    }
}
package talkapp.org.talkappmobile.activity.presenter;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import java.util.HashSet;
import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetVocabularyInteractor;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetVocabularyView;
import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.dao.TopicDao;
import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.service.impl.CurrentPracticeStateServiceImpl;
import talkapp.org.talkappmobile.service.impl.TopicServiceImpl;
import talkapp.org.talkappmobile.service.impl.LoggerBean;
import talkapp.org.talkappmobile.service.impl.RequestExecutor;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;
import talkapp.org.talkappmobile.service.impl.WordTranslationServiceImpl;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PracticeWordSetVocabularyPresenterAndInteractorIntegTest extends PresenterAndInteractorIntegTest {
    @Mock
    private PracticeWordSetVocabularyView view;
    private PracticeWordSetVocabularyInteractor interactor;

    @Before
    public void setup() {
        ObjectMapper mapper = new ObjectMapper();
        WordSetDao wordSetDao = mock(WordSetDao.class);
        TopicServiceImpl localDataService = new TopicServiceImpl(mock(TopicDao.class));

        BackendServerFactoryBean factory = new BackendServerFactoryBean();
        ServiceFactoryBean mockServiceFactoryBean = mock(ServiceFactoryBean.class);
        Whitebox.setInternalState(factory, "serviceFactory", mockServiceFactoryBean);
        Whitebox.setInternalState(factory, "requestExecutor", new RequestExecutor());
        DataServer server = factory.get();
        Whitebox.setInternalState(factory, "logger", new LoggerBean());
        WordTranslationServiceImpl translationService = new WordTranslationServiceImpl(server, mock(WordTranslationDao.class), wordSetDao, mapper);
        when(mockServiceFactoryBean.getWordTranslationService()).thenReturn(translationService);
        when(mockServiceFactoryBean.getWordSetExperienceRepository()).thenReturn(mock(WordSetService.class));

        CurrentPracticeStateService currentPracticeStateService = new CurrentPracticeStateServiceImpl(wordSetDao, mapper);
        interactor = new PracticeWordSetVocabularyInteractor(mockServiceFactoryBean.getWordSetExperienceRepository(), mockServiceFactoryBean.getWordTranslationService(), mockServiceFactoryBean.getPracticeWordSetExerciseRepository(), currentPracticeStateService);
    }

    @Test
    public void test() {
        int id = -1;
        List<Word2Tokens> words = asList(new Word2Tokens("age", "age", id), new Word2Tokens("anniversary", "anniversary", id), new Word2Tokens("birth", "birth", id));

        WordSet wordSet = new WordSet();
        wordSet.setId(id);
        wordSet.setWords(words);
        wordSet.setTopicId("topicId");

        PracticeWordSetVocabularyPresenter presenter = new PracticeWordSetVocabularyPresenter(view, interactor);

        presenter.initialise(wordSet);
        ArgumentCaptor<List<WordTranslation>> wordTranslationsCaptor = forClass(List.class);
        verify(view).setWordSetVocabularyList(wordTranslationsCaptor.capture());
        assertFalse(wordTranslationsCaptor.getValue().isEmpty());
        assertEquals(words.size(), wordTranslationsCaptor.getValue().size());
        List<WordTranslation> translations = wordTranslationsCaptor.getValue();
        for (WordTranslation translation : translations) {
            HashSet<String> wordsForCheck = new HashSet<>();
            for (Word2Tokens word2Tokens : words) {
                wordsForCheck.add(word2Tokens.getWord());
            }
            assertTrue(wordsForCheck.contains(translation.getWord()));
        }
    }
}
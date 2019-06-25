package talkapp.org.talkappmobile.activity.presenter;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;
import org.talkappmobile.dao.SentenceDao;
import org.talkappmobile.dao.TopicDao;
import org.talkappmobile.dao.WordSetDao;
import org.talkappmobile.dao.WordTranslationDao;
import org.talkappmobile.model.Word2Tokens;
import org.talkappmobile.model.WordSet;
import org.talkappmobile.model.WordTranslation;
import org.talkappmobile.service.DataServer;
import org.talkappmobile.service.impl.BackendServerFactoryBean;
import org.talkappmobile.service.impl.LocalDataServiceImpl;
import org.talkappmobile.service.impl.LoggerBean;
import org.talkappmobile.service.impl.RequestExecutor;
import org.talkappmobile.service.impl.ServiceFactoryBean;

import java.util.HashSet;
import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetVocabularyInteractor;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetVocabularyView;

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
        LocalDataServiceImpl localDataService = new LocalDataServiceImpl(mock(WordSetDao.class), mock(TopicDao.class), mock(SentenceDao.class), mock(WordTranslationDao.class), new ObjectMapper(), new LoggerBean());

        BackendServerFactoryBean factory = new BackendServerFactoryBean();
        Whitebox.setInternalState(factory, "logger", new LoggerBean());
        ServiceFactoryBean mockServiceFactoryBean = mock(ServiceFactoryBean.class);
        when(mockServiceFactoryBean.getLocalDataService()).thenReturn(localDataService);
        Whitebox.setInternalState(factory, "serviceFactory", mockServiceFactoryBean);
        Whitebox.setInternalState(factory, "requestExecutor", new RequestExecutor());
        DataServer server = factory.get();

        interactor = new PracticeWordSetVocabularyInteractor(server);
    }

    @Test
    public void test() {
        int id = -1;
        List<Word2Tokens> words = asList(new Word2Tokens("age", "age", id), new Word2Tokens("anniversary", "anniversary", id), new Word2Tokens("birth", "birth", id));

        WordSet wordSet = new WordSet();
        wordSet.setId(id);
        wordSet.setWords(words);
        wordSet.setTopicId("topicId");

        PracticeWordSetVocabularyPresenter presenter = new PracticeWordSetVocabularyPresenter(wordSet, view, interactor);

        presenter.initialise();
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
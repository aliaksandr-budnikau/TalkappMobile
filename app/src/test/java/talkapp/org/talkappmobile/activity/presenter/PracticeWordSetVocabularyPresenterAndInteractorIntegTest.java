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
import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.component.backend.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.component.backend.impl.RequestExecutor;
import talkapp.org.talkappmobile.component.database.dao.SentenceDao;
import talkapp.org.talkappmobile.component.database.dao.TopicDao;
import talkapp.org.talkappmobile.component.database.dao.WordSetDao;
import talkapp.org.talkappmobile.component.database.dao.WordTranslationDao;
import talkapp.org.talkappmobile.component.database.impl.LocalDataServiceImpl;
import talkapp.org.talkappmobile.component.database.impl.ServiceFactoryBean;
import talkapp.org.talkappmobile.component.impl.LoggerBean;
import org.talkappmobile.model.Word2Tokens;
import org.talkappmobile.model.WordSet;
import org.talkappmobile.model.WordTranslation;

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
        List<Word2Tokens> words = asList(new Word2Tokens("age", id), new Word2Tokens("anniversary", id), new Word2Tokens("birth", id));

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
package talkapp.org.talkappmobile.presenter;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.Collections;
import java.util.List;

import talkapp.org.talkappmobile.interactor.impl.StudyingWordSetsListInteractor;
import talkapp.org.talkappmobile.model.RepetitionClass;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.repository.RepositoryFactory;
import talkapp.org.talkappmobile.repository.RepositoryFactoryProvider;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.ServiceFactoryImpl;
import talkapp.org.talkappmobile.view.WordSetsListView;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FINISHED;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class WordSetsListPresenterAndInteractorIntegTest extends PresenterAndInteractorIntegTest {
    private WordSetsListView view;
    private StudyingWordSetsListInteractor studyingWordSetsInteractor;
    private ServiceFactory serviceFactory;

    @Before
    public void setup() {
        view = mock(WordSetsListView.class);

        RepositoryFactory repositoryFactory = RepositoryFactoryProvider.get(RuntimeEnvironment.application);
        serviceFactory = new ServiceFactoryImpl(repositoryFactory);
        studyingWordSetsInteractor = new StudyingWordSetsListInteractor(serviceFactory.getWordTranslationService(), serviceFactory.getWordSetService(), serviceFactory.getWordRepetitionProgressService());
    }

    @After
    public void tearDown() {
        OpenHelperManager.releaseHelper();
    }

    @Test
    public void test_withoutTopic() {
        WordSetsListPresenter presenter = new WordSetsListPresenterImpl(null, view, studyingWordSetsInteractor);
        presenter.initialize();
        ArgumentCaptor<List<WordSet>> setsCaptor = forClass(List.class);
        verify(view).onWordSetsInitialized(setsCaptor.capture(), (RepetitionClass) isNull());
        assertFalse(setsCaptor.getValue().isEmpty());

        List<WordSet> wordSets = setsCaptor.getValue();
        int clickedItemNumber = 4;
        presenter.itemClick(wordSets.get(0), clickedItemNumber);
        verify(view).onWordSetNotFinished(null, wordSets.get(0));
        verify(view, times(0)).onWordSetFinished(wordSets.get(0), clickedItemNumber);
        reset(view);

        WordSet wordSetMapping = new WordSet();
        wordSetMapping.setId(wordSets.get(0).getId());
        wordSetMapping.setTopicId("34");
        wordSetMapping.setWords(Collections.<Word2Tokens>emptyList());
        wordSets.get(0).setStatus(FINISHED);
        serviceFactory.getWordSetService().save(wordSetMapping);
        presenter.itemClick(wordSets.get(0), clickedItemNumber);
        verify(view).onWordSetFinished(wordSets.get(0), clickedItemNumber);
        verify(view, times(0)).onWordSetNotFinished(null, wordSets.get(0));
        reset(view);

        presenter.itemLongClick(wordSets.get(0), clickedItemNumber);
        verify(view).onItemLongClick(wordSets.get(0), clickedItemNumber);
        reset(view);

        presenter.resetExperienceClick(wordSets.get(0), clickedItemNumber);
        verify(view).onResetExperienceClick(eq(wordSets.get(0)), eq(clickedItemNumber));
        assertEquals(0, wordSets.get(0).getTrainingExperience());
        assertEquals(FIRST_CYCLE, wordSets.get(0).getStatus());
    }

    @Test
    public void test_withTopic() throws SQLException, JsonProcessingException {
        Topic topic = new Topic();
        topic.setId(1);
        WordSetsListPresenter presenter = new WordSetsListPresenterImpl(topic, view, studyingWordSetsInteractor);
        presenter.initialize();
        ArgumentCaptor<List<WordSet>> setsCaptor = forClass(List.class);
        verify(view).onWordSetsInitialized(setsCaptor.capture(), (RepetitionClass) isNull());
        assertFalse(setsCaptor.getValue().isEmpty());

        List<WordSet> wordSets = setsCaptor.getValue();
        int clickedItemNumber = 5;
        presenter.itemClick(wordSets.get(0), clickedItemNumber);
        verify(view).onWordSetNotFinished(topic, wordSets.get(0));
        verify(view, times(0)).onWordSetFinished(wordSets.get(0), clickedItemNumber);
        reset(view);

        WordSet wordSetMapping = new WordSet();
        wordSetMapping.setId(wordSets.get(0).getId());
        wordSetMapping.setTopicId("34");
        wordSetMapping.setWords(Collections.<Word2Tokens>emptyList());
        wordSets.get(0).setStatus(FINISHED);
        serviceFactory.getWordSetService().save(wordSetMapping);
        presenter.itemClick(wordSets.get(0), clickedItemNumber);
        verify(view).onWordSetFinished(wordSets.get(0), clickedItemNumber);
        verify(view, times(0)).onWordSetNotFinished(topic, wordSets.get(0));
        reset(view);

        presenter.itemLongClick(wordSets.get(0), clickedItemNumber);
        verify(view).onItemLongClick(wordSets.get(0), clickedItemNumber);
        reset(view);

        presenter.resetExperienceClick(wordSets.get(0), clickedItemNumber);
        verify(view).onResetExperienceClick(eq(wordSets.get(0)), eq(clickedItemNumber));
        assertEquals(0, wordSets.get(0).getTrainingExperience());
        assertEquals(FIRST_CYCLE, wordSets.get(0).getStatus());
    }
}
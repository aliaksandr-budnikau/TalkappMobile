package talkapp.org.talkappmobile.activity.presenter;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.impl.StudyingWordSetsListInteractor;
import talkapp.org.talkappmobile.activity.view.WordSetsListView;
import talkapp.org.talkappmobile.component.database.dao.PracticeWordSetExerciseDao;
import talkapp.org.talkappmobile.component.database.dao.WordSetExperienceDao;
import talkapp.org.talkappmobile.component.database.impl.PracticeWordSetExerciseServiceImpl;
import talkapp.org.talkappmobile.component.database.impl.WordSetExperienceServiceImpl;
import talkapp.org.talkappmobile.component.database.mappings.WordSetExperienceMapping;
import talkapp.org.talkappmobile.component.impl.LoggerBean;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.FINISHED;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.FIRST_CYCLE;

@RunWith(MockitoJUnitRunner.class)
public class WordSetsListPresenterAndInteractorIntegTest extends PresenterAndInteractorIntegTest {
    @Mock
    private WordSetsListView view;
    private StudyingWordSetsListInteractor studyingWordSetsInteractor;
    private WordSetExperienceDao wordSetExperienceDao;

    @Before
    public void setup() {
        PracticeWordSetExerciseDao exerciseDao = providePracticeWordSetExerciseDao();
        wordSetExperienceDao = provideWordSetExperienceDao();
        PracticeWordSetExerciseServiceImpl exerciseService = new PracticeWordSetExerciseServiceImpl(exerciseDao, wordSetExperienceDao, new ObjectMapper());
        WordSetExperienceServiceImpl experienceService = new WordSetExperienceServiceImpl(wordSetExperienceDao, new LoggerBean());
        studyingWordSetsInteractor = new StudyingWordSetsListInteractor(getServer(), experienceService, exerciseService);
    }

    @Test
    public void test_withoutTopic() {
        WordSetsListPresenter presenter = new WordSetsListPresenter(null, view, studyingWordSetsInteractor);
        presenter.initialize();
        ArgumentCaptor<List<WordSet>> setsCaptor = forClass(List.class);
        verify(view).onWordSetsInitialized(setsCaptor.capture());
        assertFalse(setsCaptor.getValue().isEmpty());

        List<WordSet> wordSets = setsCaptor.getValue();
        int clickedItemNumber = 4;
        presenter.itemClick(wordSets.get(0), clickedItemNumber);
        verify(view).onWordSetNotFinished(null, wordSets.get(0));
        verify(view, times(0)).onWordSetFinished(wordSets.get(0), clickedItemNumber);
        reset(view);

        WordSetExperienceMapping experience = new WordSetExperienceMapping();
        experience.setId(wordSets.get(0).getId());
        experience.setStatus(FINISHED);
        wordSetExperienceDao.createNewOrUpdate(experience);
        presenter.itemClick(wordSets.get(0), clickedItemNumber);
        verify(view).onWordSetFinished(wordSets.get(0), clickedItemNumber);
        verify(view, times(0)).onWordSetNotFinished(null, wordSets.get(0));
        reset(view);

        presenter.itemLongClick(wordSets.get(0), clickedItemNumber);
        verify(view).onItemLongClick(wordSets.get(0), clickedItemNumber);
        reset(view);

        ArgumentCaptor<WordSetExperience> expCaptor = forClass(WordSetExperience.class);
        presenter.resetExperienceClick(wordSets.get(0), clickedItemNumber);
        verify(view).onResetExperienceClick(eq(wordSets.get(0)), expCaptor.capture(), eq(clickedItemNumber));
        WordSetExperience newExp = expCaptor.getValue();
        assertEquals(wordSets.get(0).getWords().size() * 2, newExp.getMaxTrainingExperience());
        assertEquals(0, newExp.getTrainingExperience());
        assertEquals(FIRST_CYCLE, newExp.getStatus());
    }

    @Test
    public void test_withTopic() {
        Topic topic = new Topic();
        topic.setId(1);
        WordSetsListPresenter presenter = new WordSetsListPresenter(topic, view, studyingWordSetsInteractor);
        presenter.initialize();
        ArgumentCaptor<List<WordSet>> setsCaptor = forClass(List.class);
        verify(view).onWordSetsInitialized(setsCaptor.capture());
        assertFalse(setsCaptor.getValue().isEmpty());

        List<WordSet> wordSets = setsCaptor.getValue();
        int clickedItemNumber = 5;
        presenter.itemClick(wordSets.get(0), clickedItemNumber);
        verify(view).onWordSetNotFinished(topic, wordSets.get(0));
        verify(view, times(0)).onWordSetFinished(wordSets.get(0), clickedItemNumber);
        reset(view);

        WordSetExperienceMapping experience = new WordSetExperienceMapping();
        experience.setId(wordSets.get(0).getId());
        experience.setStatus(FINISHED);
        wordSetExperienceDao.createNewOrUpdate(experience);
        presenter.itemClick(wordSets.get(0), clickedItemNumber);
        verify(view).onWordSetFinished(wordSets.get(0), clickedItemNumber);
        verify(view, times(0)).onWordSetNotFinished(topic, wordSets.get(0));
        reset(view);

        presenter.itemLongClick(wordSets.get(0), clickedItemNumber);
        verify(view).onItemLongClick(wordSets.get(0), clickedItemNumber);
        reset(view);

        ArgumentCaptor<WordSetExperience> expCaptor = forClass(WordSetExperience.class);
        presenter.resetExperienceClick(wordSets.get(0), clickedItemNumber);
        verify(view).onResetExperienceClick(eq(wordSets.get(0)), expCaptor.capture(), eq(clickedItemNumber));
        WordSetExperience newExp = expCaptor.getValue();
        assertEquals(wordSets.get(0).getWords().size() * 2, newExp.getMaxTrainingExperience());
        assertEquals(0, newExp.getTrainingExperience());
        assertEquals(FIRST_CYCLE, newExp.getStatus());
    }
}
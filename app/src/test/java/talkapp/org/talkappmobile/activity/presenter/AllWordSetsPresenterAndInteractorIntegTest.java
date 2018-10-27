package talkapp.org.talkappmobile.activity.presenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.AllWordSetsInteractor;
import talkapp.org.talkappmobile.activity.view.AllWordSetsView;
import talkapp.org.talkappmobile.component.database.dao.WordSetExperienceDao;
import talkapp.org.talkappmobile.component.database.mappings.WordSetExperienceMapping;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.FINISHED;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.STUDYING;

@RunWith(MockitoJUnitRunner.class)
public class AllWordSetsPresenterAndInteractorIntegTest extends PresenterAndInteractorIntegTest {
    @Mock
    private AllWordSetsView view;
    private AllWordSetsInteractor allWordSetsInteractor;
    private WordSetExperienceDao wordSetExperienceDao;

    @Before
    public void setup() {
        allWordSetsInteractor = getClassForInjection().getAllWordSetsInteractor();
        wordSetExperienceDao = getClassForInjection().getWordSetExperienceDao();
    }

    @Test
    public void test_withoutTopic() {
        login();

        AllWordSetsPresenter presenter = new AllWordSetsPresenter(-1, view, allWordSetsInteractor);
        presenter.initialize();
        ArgumentCaptor<List<WordSet>> setsCaptor = forClass(List.class);
        verify(view).onWordSetsInitialized(setsCaptor.capture());
        assertFalse(setsCaptor.getValue().isEmpty());

        List<WordSet> wordSets = setsCaptor.getValue();
        presenter.itemClick(wordSets.get(0));
        verify(view).onWordSetNotFinished(wordSets.get(0));
        verify(view, times(0)).onWordSetFinished(wordSets.get(0));
        reset(view);

        WordSetExperienceMapping experience = new WordSetExperienceMapping();
        experience.setId(wordSets.get(0).getId());
        experience.setStatus(FINISHED);
        wordSetExperienceDao.createNewOrUpdate(experience);
        presenter.itemClick(wordSets.get(0));
        verify(view).onWordSetFinished(wordSets.get(0));
        verify(view, times(0)).onWordSetNotFinished(wordSets.get(0));
        reset(view);

        presenter.itemLongClick(wordSets.get(0));
        verify(view).onItemLongClick(wordSets.get(0));
        reset(view);

        ArgumentCaptor<WordSetExperience> expCaptor = forClass(WordSetExperience.class);
        presenter.resetExperienceClick(wordSets.get(0));
        verify(view).onResetExperienceClick(expCaptor.capture());
        WordSetExperience newExp = expCaptor.getValue();
        assertEquals(wordSets.get(0).getWords().size() * 2, newExp.getMaxTrainingExperience());
        assertEquals(0, newExp.getTrainingExperience());
        assertEquals(STUDYING, newExp.getStatus());
    }

    @Test
    public void test_withTopic() {
        login();

        AllWordSetsPresenter presenter = new AllWordSetsPresenter(1, view, allWordSetsInteractor);
        presenter.initialize();
        ArgumentCaptor<List<WordSet>> setsCaptor = forClass(List.class);
        verify(view).onWordSetsInitialized(setsCaptor.capture());
        assertFalse(setsCaptor.getValue().isEmpty());

        List<WordSet> wordSets = setsCaptor.getValue();
        presenter.itemClick(wordSets.get(0));
        verify(view).onWordSetNotFinished(wordSets.get(0));
        verify(view, times(0)).onWordSetFinished(wordSets.get(0));
        reset(view);

        WordSetExperienceMapping experience = new WordSetExperienceMapping();
        experience.setId(wordSets.get(0).getId());
        experience.setStatus(FINISHED);
        wordSetExperienceDao.createNewOrUpdate(experience);
        presenter.itemClick(wordSets.get(0));
        verify(view).onWordSetFinished(wordSets.get(0));
        verify(view, times(0)).onWordSetNotFinished(wordSets.get(0));
        reset(view);

        presenter.itemLongClick(wordSets.get(0));
        verify(view).onItemLongClick(wordSets.get(0));
        reset(view);

        ArgumentCaptor<WordSetExperience> expCaptor = forClass(WordSetExperience.class);
        presenter.resetExperienceClick(wordSets.get(0));
        verify(view).onResetExperienceClick(expCaptor.capture());
        WordSetExperience newExp = expCaptor.getValue();
        assertEquals(wordSets.get(0).getWords().size() * 2, newExp.getMaxTrainingExperience());
        assertEquals(0, newExp.getTrainingExperience());
        assertEquals(STUDYING, newExp.getStatus());
    }
}
package talkapp.org.talkappmobile.activity.presenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import talkapp.org.talkappmobile.activity.interactor.AllWordSetsInteractor;
import talkapp.org.talkappmobile.activity.listener.OnAllWordSetsListener;
import talkapp.org.talkappmobile.component.backend.BackendServer;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseService;
import talkapp.org.talkappmobile.component.database.WordSetExperienceService;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AllWordSetsInteractorTest {
    @Mock
    private BackendServer server;
    @Mock
    private WordSetExperienceService experienceRepository;
    @Mock
    private PracticeWordSetExerciseService exerciseRepository;
    @Mock
    private OnAllWordSetsListener listener;

    private AllWordSetsInteractor interactor;

    @Before
    public void setUp() {
        interactor = new AllWordSetsInteractor(
                server,
                experienceRepository,
                exerciseRepository
        );
    }

    @Test
    public void itemClick_experienceNull() {
        // setup
        int id = 2;

        WordSet wordSet = new WordSet();
        wordSet.setId(id);

        Topic topic = new Topic();
        topic.setId(1);

        // when
        when(experienceRepository.findById(wordSet.getId())).thenReturn(null);
        interactor.itemClick(topic, wordSet, listener);

        // then
        verify(listener).onWordSetNotFinished(topic, wordSet);
    }
}
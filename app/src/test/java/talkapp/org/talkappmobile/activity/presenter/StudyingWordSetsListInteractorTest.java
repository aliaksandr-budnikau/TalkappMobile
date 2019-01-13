package talkapp.org.talkappmobile.activity.presenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import talkapp.org.talkappmobile.activity.interactor.impl.StudyingWordSetsListInteractor;
import talkapp.org.talkappmobile.activity.listener.OnWordSetsListListener;
import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseService;
import talkapp.org.talkappmobile.component.database.WordSetExperienceService;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StudyingWordSetsListInteractorTest {
    @Mock
    private DataServer server;
    @Mock
    private WordSetExperienceService experienceRepository;
    @Mock
    private PracticeWordSetExerciseService exerciseRepository;
    @Mock
    private OnWordSetsListListener listener;

    private StudyingWordSetsListInteractor interactor;

    @Before
    public void setUp() {
        interactor = new StudyingWordSetsListInteractor(
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
        interactor.itemClick(topic, wordSet, 3, listener);

        // then
        verify(listener).onWordSetNotFinished(topic, wordSet);
    }
}
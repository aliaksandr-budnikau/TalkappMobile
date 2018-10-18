package talkapp.org.talkappmobile.activity.presenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.backend.WordSetService;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseRepository;
import talkapp.org.talkappmobile.component.database.WordSetExperienceRepository;
import talkapp.org.talkappmobile.model.WordSet;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AllWordSetsInteractorTest {
    @Mock
    private WordSetService wordSetService;
    @Mock
    private WordSetExperienceRepository experienceRepository;
    @Mock
    private PracticeWordSetExerciseRepository exerciseRepository;
    @Mock
    private AuthSign authSign;
    @Mock
    private OnAllWordSetsListener listener;

    private AllWordSetsInteractor interactor;

    @Before
    public void setUp() {
        interactor = new AllWordSetsInteractor(
                wordSetService,
                experienceRepository,
                exerciseRepository,
                authSign
        );
    }

    @Test
    public void itemClick_experienceNull() {
        // setup
        String id = "sfs";

        WordSet wordSet = new WordSet();
        wordSet.setId(id);

        // when
        when(experienceRepository.findById(wordSet.getId())).thenReturn(null);
        interactor.itemClick(wordSet, listener);

        // then
        verify(listener).onWordSetNotFinished(wordSet);
    }
}
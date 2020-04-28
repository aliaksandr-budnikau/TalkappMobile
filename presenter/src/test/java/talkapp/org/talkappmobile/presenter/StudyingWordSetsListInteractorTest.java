package talkapp.org.talkappmobile.presenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import talkapp.org.talkappmobile.interactor.impl.StudyingWordSetsListInteractor;
import talkapp.org.talkappmobile.listener.OnWordSetsListListener;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.WordTranslationService;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StudyingWordSetsListInteractorTest {
    @Mock
    private DataServer server;
    @Mock
    private WordSetService experienceRepository;
    @Mock
    private WordTranslationService wordTranslationService;
    @Mock
    private WordRepetitionProgressService exerciseRepository;
    @Mock
    private OnWordSetsListListener listener;

    private StudyingWordSetsListInteractor interactor;

    @Before
    public void setUp() {
        interactor = new StudyingWordSetsListInteractor(
                wordTranslationService,
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
        interactor.itemClick(topic, wordSet, 3, listener);

        // then
        verify(listener).onWordSetNotFinished(topic, wordSet);
    }
}
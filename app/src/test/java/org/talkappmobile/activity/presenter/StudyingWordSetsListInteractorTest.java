package org.talkappmobile.activity.presenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.talkappmobile.model.Topic;
import org.talkappmobile.model.WordSet;
import org.talkappmobile.service.DataServer;
import org.talkappmobile.service.WordRepetitionProgressService;
import org.talkappmobile.service.WordSetService;

import org.talkappmobile.activity.interactor.impl.StudyingWordSetsListInteractor;
import org.talkappmobile.activity.listener.OnWordSetsListListener;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StudyingWordSetsListInteractorTest {
    @Mock
    private DataServer server;
    @Mock
    private WordSetService experienceRepository;
    @Mock
    private WordRepetitionProgressService exerciseRepository;
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
        interactor.itemClick(topic, wordSet, 3, listener);

        // then
        verify(listener).onWordSetNotFinished(topic, wordSet);
    }
}
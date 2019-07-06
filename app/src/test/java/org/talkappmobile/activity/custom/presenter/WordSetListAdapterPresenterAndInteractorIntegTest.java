package org.talkappmobile.activity.custom.presenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.talkappmobile.activity.custom.interactor.WordSetListAdapterInteractor;
import org.talkappmobile.activity.custom.view.WordSetListAdapterView;
import org.talkappmobile.activity.presenter.PresenterAndInteractorIntegTest;
import org.talkappmobile.model.RepetitionClass;
import org.talkappmobile.model.WordSet;
import org.talkappmobile.model.WordSetProgressStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.talkappmobile.model.RepetitionClass.LEARNED;
import static org.talkappmobile.model.RepetitionClass.NEW;
import static org.talkappmobile.model.RepetitionClass.REPEATED;
import static org.talkappmobile.model.RepetitionClass.SEEN;
import static org.talkappmobile.model.WordSetProgressStatus.FINISHED;
import static org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;
import static org.talkappmobile.model.WordSetProgressStatus.SECOND_CYCLE;

@RunWith(MockitoJUnitRunner.class)
public class WordSetListAdapterPresenterAndInteractorIntegTest extends PresenterAndInteractorIntegTest {

    private int wordSetIdCounter;
    @Captor
    private ArgumentCaptor<List<WordSet>> captor;

    @Mock
    private WordSetListAdapterView view;
    private WordSetListAdapterPresenter presenter;

    @Before
    public void setup() {
        WordSetListAdapterInteractor interactor = new WordSetListAdapterInteractor();
        presenter = new WordSetListAdapterPresenter(interactor, view);

        LinkedList<WordSet> wordSetList = new LinkedList<>();
        addWordSetsForListOfRep(wordSetList);
        presenter.setModel(wordSetList);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoval() {
        // setup
        LinkedList<WordSet> wordSetList = new LinkedList<>();
        addWordSetsForListOfStuding(wordSetList);
        addWordSetsForListOfStuding(wordSetList);
        int origSize = wordSetList.size();

        // when
        presenter.setModel(wordSetList);
        presenter.refreshModel();
        reset(view);
        presenter.filterStarted();
        try {
            presenter.getWordSet(3);
        } catch (Exception e) {
            fail();
        }
        presenter.remove(presenter.getWordSet(1));

        // then
        verify(view).onWordSetRemoved();
        assertEquals(origSize - 1, presenter.getModel().size());
        for (WordSet wordSet : wordSetList) {
            if (wordSet.getId() == 3) {
                fail();
            }
        }

        presenter.getWordSet(3);
    }

    @Test
    public void testStudyingNew() {
        // setup
        LinkedList<WordSet> wordSetList = new LinkedList<>();
        addWordSetsForListOfStuding(wordSetList);
        addWordSetsForListOfStuding(wordSetList);

        // when
        presenter.setModel(wordSetList);
        presenter.refreshModel();
        reset(view);
        presenter.filterNew();

        // then
        verify(view).onModelPrepared(captor.capture());
        assertEquals(2, captor.getValue().size());
    }

    @Test
    public void testStudyingStarted() {
        // setup
        LinkedList<WordSet> wordSetList = new LinkedList<>();
        addWordSetsForListOfStuding(wordSetList);
        addWordSetsForListOfStuding(wordSetList);

        // when
        presenter.setModel(wordSetList);
        presenter.refreshModel();
        reset(view);
        presenter.filterStarted();

        // then
        verify(view).onModelPrepared(captor.capture());
        assertEquals(4, captor.getValue().size());
    }

    @Test
    public void testStudyingFinished() {
        // setup
        LinkedList<WordSet> wordSetList = new LinkedList<>();
        addWordSetsForListOfStuding(wordSetList);
        addWordSetsForListOfStuding(wordSetList);

        // when
        presenter.setModel(wordSetList);
        presenter.refreshModel();
        reset(view);
        presenter.filterFinished();

        // then
        verify(view).onModelPrepared(captor.capture());
        assertEquals(2, captor.getValue().size());
    }

    @Test
    public void testRepNew() {
        // setup
        LinkedList<WordSet> wordSetList = new LinkedList<>();
        addWordSetsForListOfRep(wordSetList);
        addWordSetsForListOfRep(wordSetList);

        // when
        presenter.setModel(wordSetList);
        presenter.refreshModel();
        reset(view);
        presenter.filterNewRep();

        // then
        verify(view).onModelPrepared(captor.capture());
        assertEquals(6, captor.getValue().size());
    }

    @Test
    public void testRepSeen() {
        // setup
        LinkedList<WordSet> wordSetList = new LinkedList<>();
        addWordSetsForListOfRep(wordSetList);
        addWordSetsForListOfRep(wordSetList);

        // when
        presenter.setModel(wordSetList);
        presenter.refreshModel();
        reset(view);
        presenter.filterSeenRep();

        // then
        verify(view).onModelPrepared(captor.capture());
        assertEquals(6, captor.getValue().size());
    }

    @Test
    public void testRepRepeated() {
        // setup
        LinkedList<WordSet> wordSetList = new LinkedList<>();
        addWordSetsForListOfRep(wordSetList);
        addWordSetsForListOfRep(wordSetList);

        // when
        presenter.setModel(wordSetList);
        presenter.refreshModel();
        reset(view);
        presenter.filterRepeatedRep();

        // then
        verify(view).onModelPrepared(captor.capture());
        assertEquals(6, captor.getValue().size());
    }

    @Test
    public void testRepLearned() {
        // setup
        LinkedList<WordSet> wordSetList = new LinkedList<>();
        addWordSetsForListOfRep(wordSetList);
        addWordSetsForListOfRep(wordSetList);

        // when
        presenter.setModel(wordSetList);
        presenter.refreshModel();
        reset(view);
        presenter.filterLearnedRep();

        // then
        verify(view).onModelPrepared(captor.capture());
        assertEquals(6, captor.getValue().size());
    }

    private void addWordSetsForListOfRep(LinkedList<WordSet> wordSetList) {
        Random random = new Random();
        WordSetProgressStatus[] progressStatuses = WordSetProgressStatus.values();
        wordSetList.add(new WordSet());
        wordSetList.getLast().setRepetitionClass(NEW);
        wordSetList.getLast().setStatus(getRandomEnum(progressStatuses));
        wordSetList.getLast().setTrainingExperience(random.nextInt(12));
        wordSetList.add(new WordSet());
        wordSetList.getLast().setRepetitionClass(NEW);
        wordSetList.getLast().setStatus(getRandomEnum(progressStatuses));
        wordSetList.getLast().setTrainingExperience(random.nextInt(12));
        wordSetList.add(new WordSet());
        wordSetList.getLast().setRepetitionClass(NEW);
        wordSetList.getLast().setStatus(getRandomEnum(progressStatuses));
        wordSetList.getLast().setTrainingExperience(random.nextInt(12));
        wordSetList.add(new WordSet());
        wordSetList.getLast().setRepetitionClass(SEEN);
        wordSetList.getLast().setStatus(getRandomEnum(progressStatuses));
        wordSetList.getLast().setTrainingExperience(random.nextInt(12));
        wordSetList.add(new WordSet());
        wordSetList.getLast().setRepetitionClass(SEEN);
        wordSetList.getLast().setStatus(getRandomEnum(progressStatuses));
        wordSetList.getLast().setTrainingExperience(random.nextInt(12));
        wordSetList.add(new WordSet());
        wordSetList.getLast().setRepetitionClass(SEEN);
        wordSetList.getLast().setStatus(getRandomEnum(progressStatuses));
        wordSetList.getLast().setTrainingExperience(random.nextInt(12));
        wordSetList.add(new WordSet());
        wordSetList.getLast().setRepetitionClass(REPEATED);
        wordSetList.getLast().setStatus(getRandomEnum(progressStatuses));
        wordSetList.getLast().setTrainingExperience(random.nextInt(12));
        wordSetList.add(new WordSet());
        wordSetList.getLast().setRepetitionClass(REPEATED);
        wordSetList.getLast().setStatus(getRandomEnum(progressStatuses));
        wordSetList.getLast().setTrainingExperience(random.nextInt(12));
        wordSetList.add(new WordSet());
        wordSetList.getLast().setRepetitionClass(REPEATED);
        wordSetList.getLast().setStatus(getRandomEnum(progressStatuses));
        wordSetList.getLast().setTrainingExperience(random.nextInt(12));
        wordSetList.add(new WordSet());
        wordSetList.getLast().setRepetitionClass(LEARNED);
        wordSetList.getLast().setStatus(getRandomEnum(progressStatuses));
        wordSetList.getLast().setTrainingExperience(random.nextInt(12));
        wordSetList.add(new WordSet());
        wordSetList.getLast().setRepetitionClass(LEARNED);
        wordSetList.getLast().setStatus(getRandomEnum(progressStatuses));
        wordSetList.getLast().setTrainingExperience(random.nextInt(12));
        wordSetList.add(new WordSet());
        wordSetList.getLast().setRepetitionClass(LEARNED);
        wordSetList.getLast().setStatus(getRandomEnum(progressStatuses));
        wordSetList.getLast().setTrainingExperience(random.nextInt(12));
    }

    private void addWordSetsForListOfStuding(LinkedList<WordSet> wordSetList) {
        RepetitionClass[] values = RepetitionClass.values();
        wordSetList.add(new WordSet());
        wordSetList.getLast().setId(++wordSetIdCounter);
        wordSetList.getLast().setStatus(FIRST_CYCLE);
        wordSetList.getLast().setTrainingExperience(0);
        wordSetList.getLast().setRepetitionClass(getRandomEnum(values));
        wordSetList.add(new WordSet());
        wordSetList.getLast().setId(++wordSetIdCounter);
        wordSetList.getLast().setStatus(FIRST_CYCLE);
        wordSetList.getLast().setTrainingExperience(new Random().nextInt(12) + 1);
        wordSetList.getLast().setRepetitionClass(getRandomEnum(values));
        wordSetList.add(new WordSet());
        wordSetList.getLast().setId(++wordSetIdCounter);
        wordSetList.getLast().setStatus(SECOND_CYCLE);
        wordSetList.getLast().setTrainingExperience(new Random().nextInt(12) + 1);
        wordSetList.getLast().setRepetitionClass(getRandomEnum(values));
        wordSetList.add(new WordSet());
        wordSetList.getLast().setId(++wordSetIdCounter);
        wordSetList.getLast().setStatus(FINISHED);
        wordSetList.getLast().setTrainingExperience(new Random().nextInt(12) + 1);
        wordSetList.getLast().setRepetitionClass(getRandomEnum(values));
    }
}
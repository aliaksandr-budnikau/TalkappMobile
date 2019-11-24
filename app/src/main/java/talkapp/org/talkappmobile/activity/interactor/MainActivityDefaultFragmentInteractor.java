package talkapp.org.talkappmobile.activity.interactor;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.activity.listener.OnMainActivityDefaultFragmentListener;
import talkapp.org.talkappmobile.model.DifficultWordSetRepetitionTask;
import talkapp.org.talkappmobile.model.NewWordSetTask;
import talkapp.org.talkappmobile.model.Task;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetRepetitionTask;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;

public class MainActivityDefaultFragmentInteractor {

    private final WordRepetitionProgressService exerciseService;
    private final String wordSetsRepetitionTitle;
    private final String wordSetsRepetitionDescription;
    private final String wordSetsLearningTitle;
    private final String wordSetsLearningDescription;
    private final String wordSetsExtraRepetitionTitle;
    private final String wordSetsExtraRepetitionDescription;

    public MainActivityDefaultFragmentInteractor(WordRepetitionProgressService exerciseService,
                                                 String wordSetsRepetitionTitle, String wordSetsRepetitionDescription,
                                                 String wordSetsLearningTitle, String wordSetsLearningDescription,
                                                 String wordSetsExtraRepetitionTitle, String wordSetsExtraRepetitionDescription) {
        this.exerciseService = exerciseService;
        this.wordSetsRepetitionTitle = wordSetsRepetitionTitle;
        this.wordSetsRepetitionDescription = wordSetsRepetitionDescription;
        this.wordSetsLearningTitle = wordSetsLearningTitle;
        this.wordSetsLearningDescription = wordSetsLearningDescription;
        this.wordSetsExtraRepetitionTitle = wordSetsExtraRepetitionTitle;
        this.wordSetsExtraRepetitionDescription = wordSetsExtraRepetitionDescription;
    }

    public void initWordsForRepetition(OnMainActivityDefaultFragmentListener listener) {
        List<WordSet> sets = exerciseService.findFinishedWordSetsSortByUpdatedDate(24 * 2);
        filterOutSmallWordSets(sets);
        listener.onWordsForRepetitionCounted(sets.size());
    }

    private void filterOutSmallWordSets(List<WordSet> sets) {
        int maxWordSetSize = exerciseService.getMaxWordSetSize();
        Iterator<WordSet> iterator = sets.iterator();
        while (iterator.hasNext()) {
            WordSet set = iterator.next();
            if (set.getWords().size() < maxWordSetSize) {
                iterator.remove();
            }
        }
    }

    public void findTasks(OnMainActivityDefaultFragmentListener listener) {
        LinkedList<Task> tasks = new LinkedList<>();

        WordSetRepetitionTask repetitionTasks = findRepetitionTasks(listener);
        if (repetitionTasks != null) {
            tasks.add(repetitionTasks);
        }
        tasks.add(findStudyTask(listener));
        DifficultWordSetRepetitionTask difficultWordSetTasks = findRepetitionOfDifficultWordSetTasks(listener);
        if (difficultWordSetTasks != null) {
            tasks.add(difficultWordSetTasks);
        }
        listener.onFoundTasks(tasks);
    }

    private DifficultWordSetRepetitionTask findRepetitionOfDifficultWordSetTasks(final OnMainActivityDefaultFragmentListener listener) {
        final List<WordSet> wordSets = exerciseService.findWordSetOfDifficultWords();
        if (wordSets.isEmpty()) {
            return null;
        }
        return new DifficultWordSetRepetitionTask(wordSetsExtraRepetitionTitle, wordSetsExtraRepetitionDescription) {
            @Override
            public void start() {
                listener.onDifficultWordSetRepetitionTaskClicked(wordSets);
            }
        };
    }

    private NewWordSetTask findStudyTask(final OnMainActivityDefaultFragmentListener listener) {
        return new NewWordSetTask(wordSetsLearningTitle, wordSetsLearningDescription) {
            @Override
            public void start() {
                listener.onNewWordSetTaskClicked();
            }
        };
    }

    private WordSetRepetitionTask findRepetitionTasks(final OnMainActivityDefaultFragmentListener listener) {
        List<WordSet> sets = exerciseService.findFinishedWordSetsSortByUpdatedDate(24 * 2);
        Collections.shuffle(sets);
        for (final WordSet set : sets) {
            if (set.getWords().size() == exerciseService.getMaxWordSetSize()) {
                return new WordSetRepetitionTask(wordSetsRepetitionTitle, wordSetsRepetitionDescription, set.getRepetitionClass()) {
                    @Override
                    public void start() {
                        listener.onWordSetRepetitionTaskClick(set.getRepetitionClass());
                    }
                };
            }
        }
        return null;
    }
}
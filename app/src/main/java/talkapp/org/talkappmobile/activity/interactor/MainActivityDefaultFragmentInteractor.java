package talkapp.org.talkappmobile.activity.interactor;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.activity.listener.OnMainActivityDefaultFragmentListener;
import talkapp.org.talkappmobile.model.DifficultWordSetRepetitionTask;
import talkapp.org.talkappmobile.model.NewWordSetTask;
import talkapp.org.talkappmobile.model.RepetitionClass;
import talkapp.org.talkappmobile.model.Task;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetRepetitionTask;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;

import static java.lang.String.format;
import static talkapp.org.talkappmobile.model.RepetitionClass.LEARNED;

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

        int repetitionTasks = findRepetitionTasks(tasks, listener);
        if (tasks.size() < 3) {
            findStudyTaks(tasks, listener);
        }
        if (tasks.size() < 3) {
            findRepetitionOfDifficultWordSetTasks(tasks, listener);
        }

        listener.onFoundTasks(tasks);
    }

    private void findRepetitionOfDifficultWordSetTasks(LinkedList<Task> tasks, final OnMainActivityDefaultFragmentListener listener) {
        final List<WordSet> wordSets = exerciseService.findWordSetOfDifficultWords();
        String description = wordSetsExtraRepetitionDescription + " " + wordSetsRepetitionDescription;
        if (wordSets.isEmpty()) {
            return;
        }
        tasks.add(new DifficultWordSetRepetitionTask(wordSetsExtraRepetitionTitle, description) {
            @Override
            public void start() {
                listener.onDifficultWordSetRepetitionTaskClicked(wordSets);
            }
        });
    }

    private int findStudyTaks(LinkedList<Task> tasks, final OnMainActivityDefaultFragmentListener listener) {
        LinkedList<Task> result = new LinkedList<>();
        result.add(new NewWordSetTask(wordSetsLearningTitle, wordSetsLearningDescription) {
            @Override
            public void start() {
                listener.onNewWordSetTaskClicked();
            }
        });
        tasks.addAll(result);
        return result.size();
    }

    private int findRepetitionTasks(LinkedList<Task> tasks, final OnMainActivityDefaultFragmentListener listener) {
        LinkedList<Task> result = new LinkedList<>();
        List<WordSet> sets = exerciseService.findFinishedWordSetsSortByUpdatedDate(24 * 2);
        for (final RepetitionClass clazz : RepetitionClass.values()) {
            if (clazz.equals(LEARNED)) {
                continue;
            }
            for (WordSet set : sets) {
                if (set.getRepetitionClass().equals(clazz) && set.getWords().size() == exerciseService.getMaxWordSetSize()) {
                    int counter = countSetsOfThisClass(sets, clazz);
                    String title = format(wordSetsRepetitionTitle, counter);
                    result.add(new WordSetRepetitionTask(title, wordSetsRepetitionDescription, clazz) {
                        @Override
                        public void start() {
                            listener.onWordSetRepetitionTaskClick(clazz);
                        }
                    });
                    break;
                }
            }
        }
        tasks.addAll(result);
        return result.size();
    }

    private int countSetsOfThisClass(List<WordSet> sets, RepetitionClass clazz) {
        int result = 0;
        for (WordSet set : sets) {
            if (set.getRepetitionClass() == clazz) {
                result++;
            }
        }
        return result;
    }
}
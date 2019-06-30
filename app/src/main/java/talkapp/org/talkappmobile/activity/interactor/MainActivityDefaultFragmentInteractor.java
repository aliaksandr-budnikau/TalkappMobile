package talkapp.org.talkappmobile.activity.interactor;

import org.talkappmobile.model.DifficultWordSetRepetitionTask;
import org.talkappmobile.model.NewWordSetTask;
import org.talkappmobile.model.RepetitionClass;
import org.talkappmobile.model.Task;
import org.talkappmobile.model.WordSet;
import org.talkappmobile.model.WordSetRepetitionTask;
import org.talkappmobile.service.WordRepetitionProgressService;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.activity.listener.OnMainActivityDefaultFragmentListener;

import static java.lang.String.format;
import static org.talkappmobile.model.RepetitionClass.LEARNED;

public class MainActivityDefaultFragmentInteractor {

    private final WordRepetitionProgressService exerciseService;
    private final String repitionDescription = "The experience that you'll get depends on " +
            "number of repetitions you did. Than more you repeat any single word " +
            "than more you gain experience.";

    public MainActivityDefaultFragmentInteractor(WordRepetitionProgressService exerciseService) {
        this.exerciseService = exerciseService;
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
        String title = "Extra Repetition";
        String description = "Extra repetition for words with most mistakes. " + "\\n" + repitionDescription;
        if (wordSets.isEmpty()) {
            return;
        }
        tasks.add(new DifficultWordSetRepetitionTask(title, description) {
            @Override
            public void start() {
                listener.onDifficultWordSetRepetitionTaskClicked(wordSets);
            }
        });
    }

    private int findStudyTaks(LinkedList<Task> tasks, final OnMainActivityDefaultFragmentListener listener) {
        LinkedList<Task> result = new LinkedList<>();
        String title = "Studying new words";
        String description = "Start learning new words.";
        result.add(new NewWordSetTask(title, description) {
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
                    String title = format("Sets for repetition %s", counter);
                    result.add(new WordSetRepetitionTask(title, repitionDescription, clazz) {
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
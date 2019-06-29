package talkapp.org.talkappmobile.activity.interactor;

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

import static org.talkappmobile.model.RepetitionClass.LEARNED;

public class MainActivityDefaultFragmentInteractor {

    private final WordRepetitionProgressService exerciseService;

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
        if (repetitionTasks < 3) {
            findStudyTaks(tasks, listener);
        }

        listener.onFoundTasks(tasks);
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
                if (set.getRepetitionClass().equals(clazz)) {
                    String title = "Repetition number " + clazz.getFrom() + " - " + clazz.getTo();
                    String description = "The experience that you'll get depends on " +
                            "number of repetitions you did. Than more you repeat any single word " +
                            "than more you gain experience.";
                    result.add(new WordSetRepetitionTask(title, description, clazz) {
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
}
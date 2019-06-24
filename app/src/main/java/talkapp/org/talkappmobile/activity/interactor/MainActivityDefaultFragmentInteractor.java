package talkapp.org.talkappmobile.activity.interactor;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.activity.listener.OnMainActivityDefaultFragmentListener;
import talkapp.org.talkappmobile.component.database.WordRepetitionProgressService;
import org.talkappmobile.model.RepetitionClass;
import org.talkappmobile.model.Task;
import org.talkappmobile.model.WordSet;

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

        int repetitionTasks = findRepetitionTasks(tasks);
        if (repetitionTasks < 3) {
            findStudyTaks(tasks);
        }

        listener.onFoundTasks(tasks);
    }

    private int findStudyTaks(LinkedList<Task> tasks) {
        LinkedList<Task> result = new LinkedList<>();
        result.add(new Task());
        result.getLast().setTitle("Studying new words");
        result.getLast().setDescription("Start learning new words.");
        tasks.addAll(result);
        return result.size();
    }

    private int findRepetitionTasks(LinkedList<Task> tasks) {
        LinkedList<Task> result = new LinkedList<>();
        List<WordSet> sets = exerciseService.findFinishedWordSetsSortByUpdatedDate(24 * 2);
        for (RepetitionClass clazz : RepetitionClass.values()) {
            if (clazz.equals(LEARNED)) {
                continue;
            }
            for (WordSet set : sets) {
                if (set.getRepetitionClass().equals(clazz)) {
                    result.add(new Task());
                    result.getLast().setTitle("Repetition number " + clazz.getFrom() + " - " + clazz.getTo());
                    result.getLast().setDescription("The experience that you'll get depends on " +
                            "number of repetitions you did. Than more you repeat any single word " +
                            "than more you gain experience.");
                    break;
                }
            }
        }
        tasks.addAll(result);
        return result.size();
    }
}
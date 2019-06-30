package talkapp.org.talkappmobile.activity.listener;

import org.talkappmobile.model.RepetitionClass;
import org.talkappmobile.model.Task;
import org.talkappmobile.model.WordSet;

import java.util.LinkedList;
import java.util.List;

public interface OnMainActivityDefaultFragmentListener {
    void onWordsForRepetitionCounted(int counter);

    void onFoundTasks(LinkedList<Task> tasks);

    void onNewWordSetTaskClicked();

    void onWordSetRepetitionTaskClick(RepetitionClass clazz);

    void onDifficultWordSetRepetitionTaskClicked(List<WordSet> wordSets);
}
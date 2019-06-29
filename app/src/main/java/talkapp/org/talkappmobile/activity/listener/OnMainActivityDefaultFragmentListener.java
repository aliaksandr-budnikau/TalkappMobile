package talkapp.org.talkappmobile.activity.listener;

import org.talkappmobile.model.RepetitionClass;
import org.talkappmobile.model.Task;

import java.util.LinkedList;

public interface OnMainActivityDefaultFragmentListener {
    void onWordsForRepetitionCounted(int counter);

    void onFoundTasks(LinkedList<Task> tasks);

    void onNewWordSetTaskClicked();

    void onWordSetRepetitionTaskClick(RepetitionClass clazz);
}
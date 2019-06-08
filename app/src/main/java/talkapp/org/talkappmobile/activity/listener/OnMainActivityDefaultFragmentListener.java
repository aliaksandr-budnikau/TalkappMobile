package talkapp.org.talkappmobile.activity.listener;

import java.util.LinkedList;

import talkapp.org.talkappmobile.model.Task;

public interface OnMainActivityDefaultFragmentListener {
    void onWordsForRepetitionCounted(int counter);

    void onFoundTasks(LinkedList<Task> tasks);
}
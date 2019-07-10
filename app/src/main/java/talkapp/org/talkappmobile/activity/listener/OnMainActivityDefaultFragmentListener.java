package talkapp.org.talkappmobile.activity.listener;

import talkapp.org.talkappmobile.model.RepetitionClass;
import talkapp.org.talkappmobile.model.Task;
import talkapp.org.talkappmobile.model.WordSet;

import java.util.LinkedList;
import java.util.List;

public interface OnMainActivityDefaultFragmentListener {
    void onWordsForRepetitionCounted(int counter);

    void onFoundTasks(LinkedList<Task> tasks);

    void onNewWordSetTaskClicked();

    void onWordSetRepetitionTaskClick(RepetitionClass clazz);

    void onDifficultWordSetRepetitionTaskClicked(List<WordSet> wordSets);
}
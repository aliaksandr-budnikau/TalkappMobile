package talkapp.org.talkappmobile.activity.view;

import java.util.List;

import org.talkappmobile.model.Task;

public interface MainActivityDefaultFragmentView {
    void onWordsForRepetitionCounted(int counter);

    void setTasksList(List<Task> tasks);
}
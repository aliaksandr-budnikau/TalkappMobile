package talkapp.org.talkappmobile.activity.view;

import org.talkappmobile.model.RepetitionClass;
import org.talkappmobile.model.Task;

import java.util.List;

public interface MainActivityDefaultFragmentView {
    void onWordsForRepetitionCounted(int counter);

    void setTasksList(List<Task> tasks);

    void onNewWordSetTaskClicked();

    void onWordSetRepetitionTaskClick(RepetitionClass clazz);
}
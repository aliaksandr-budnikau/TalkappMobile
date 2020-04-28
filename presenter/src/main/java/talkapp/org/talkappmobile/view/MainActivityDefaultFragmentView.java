package talkapp.org.talkappmobile.view;

import talkapp.org.talkappmobile.model.RepetitionClass;
import talkapp.org.talkappmobile.model.Task;
import talkapp.org.talkappmobile.model.WordSet;

import java.util.List;

public interface MainActivityDefaultFragmentView {
    void onWordsForRepetitionCounted(int counter);

    void setTasksList(List<Task> tasks);

    void onNewWordSetTaskClicked();

    void onWordSetRepetitionTaskClick(RepetitionClass clazz);

    void onDifficultWordSetRepetitionTaskClicked(List<WordSet> wordSets);

    void onNewYourWordSetTaskClicked();
}
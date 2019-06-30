package talkapp.org.talkappmobile.activity.presenter;

import org.talkappmobile.model.RepetitionClass;
import org.talkappmobile.model.Task;
import org.talkappmobile.model.WordSet;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.MainActivityDefaultFragmentInteractor;
import talkapp.org.talkappmobile.activity.listener.OnMainActivityDefaultFragmentListener;
import talkapp.org.talkappmobile.activity.view.MainActivityDefaultFragmentView;

public class MainActivityDefaultFragmentPresenter implements OnMainActivityDefaultFragmentListener {
    private final MainActivityDefaultFragmentView view;
    private final MainActivityDefaultFragmentInteractor interactor;

    public MainActivityDefaultFragmentPresenter(MainActivityDefaultFragmentView view, MainActivityDefaultFragmentInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }


    public void init() {
        interactor.initWordsForRepetition(this);
    }

    @Override
    public void onWordsForRepetitionCounted(int counter) {
        view.onWordsForRepetitionCounted(counter);
    }

    @Override
    public void onFoundTasks(LinkedList<Task> tasks) {
        view.setTasksList(tasks);
    }

    @Override
    public void onNewWordSetTaskClicked() {
        view.onNewWordSetTaskClicked();
    }

    @Override
    public void onWordSetRepetitionTaskClick(RepetitionClass clazz) {
        view.onWordSetRepetitionTaskClick(clazz);
    }

    @Override
    public void onDifficultWordSetRepetitionTaskClicked(List<WordSet> wordSets) {
        view.onDifficultWordSetRepetitionTaskClicked(wordSets);
    }

    public void findTasks() {
        interactor.findTasks(this);
    }
}
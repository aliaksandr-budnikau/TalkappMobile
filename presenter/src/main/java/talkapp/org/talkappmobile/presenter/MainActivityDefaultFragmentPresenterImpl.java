package talkapp.org.talkappmobile.presenter;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.interactor.MainActivityDefaultFragmentInteractor;
import talkapp.org.talkappmobile.listener.OnMainActivityDefaultFragmentListener;
import talkapp.org.talkappmobile.model.RepetitionClass;
import talkapp.org.talkappmobile.model.Task;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.view.MainActivityDefaultFragmentView;

public class MainActivityDefaultFragmentPresenterImpl implements OnMainActivityDefaultFragmentListener, MainActivityDefaultFragmentPresenter {
    private final MainActivityDefaultFragmentView view;
    private final MainActivityDefaultFragmentInteractor interactor;

    public MainActivityDefaultFragmentPresenterImpl(MainActivityDefaultFragmentView view, MainActivityDefaultFragmentInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

    @Override
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

    @Override
    public void onNewYourWordSetTaskClicked() {
        view.onNewYourWordSetTaskClicked();
    }

    @Override
    public void findTasks() {
        interactor.findTasks(this);
    }
}
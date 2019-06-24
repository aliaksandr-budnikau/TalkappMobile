package talkapp.org.talkappmobile.activity.presenter;

import java.util.LinkedList;

import talkapp.org.talkappmobile.activity.interactor.MainActivityDefaultFragmentInteractor;
import talkapp.org.talkappmobile.activity.listener.OnMainActivityDefaultFragmentListener;
import talkapp.org.talkappmobile.activity.view.MainActivityDefaultFragmentView;
import org.talkappmobile.model.Task;

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

    public void findTasks() {
        interactor.findTasks(this);
    }
}
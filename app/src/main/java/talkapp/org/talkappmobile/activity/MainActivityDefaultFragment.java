package talkapp.org.talkappmobile.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.IgnoreWhen;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import talkapp.org.talkappmobile.events.TasksListLoadedEM;
import org.talkappmobile.model.Task;
import org.talkappmobile.service.ServiceFactory;
import org.talkappmobile.service.impl.ServiceFactoryBean;

import java.util.List;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.custom.TasksListView;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.activity.interactor.MainActivityDefaultFragmentInteractor;
import talkapp.org.talkappmobile.activity.presenter.MainActivityDefaultFragmentPresenter;
import talkapp.org.talkappmobile.activity.view.MainActivityDefaultFragmentView;

import static java.lang.String.format;
import static org.androidannotations.annotations.IgnoreWhen.State.VIEW_DESTROYED;
import static talkapp.org.talkappmobile.activity.WordSetsListFragment.REPETITION_MODE_MAPPING;

@EFragment(value = R.layout.main_activity_default_fragment_layout)
public class MainActivityDefaultFragment extends Fragment implements MainActivityDefaultFragmentView {
    @EventBusGreenRobot
    EventBus eventBus;
    @Bean
    WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory;
    @Bean(ServiceFactoryBean.class)
    ServiceFactory serviceFactory;

    @ViewById(R.id.wordsForRepetitionTextView)
    TextView wordsForRepetitionTextView;
    @ViewById(R.id.tasksListView)
    TasksListView tasksListView;
    @ViewById(R.id.please_wait_progress_bar)
    View progressBarView;
    @ViewById(R.id.wordSetVocabularyView)
    RecyclerView wordSetVocabularyView;

    private WaitingForProgressBarManager waitingForProgressBarManager;

    private MainActivityDefaultFragmentPresenter presenter;

    @AfterViews
    public void init() {
        waitingForProgressBarManager = waitingForProgressBarManagerFactory.get(progressBarView, wordSetVocabularyView);

        MainActivityDefaultFragmentInteractor interactor = new MainActivityDefaultFragmentInteractor(serviceFactory.getPracticeWordSetExerciseRepository());
        presenter = new MainActivityDefaultFragmentPresenter(this, interactor);
        presenter.init();
        findTasks();
    }

    @Background
    public void findTasks() {
        presenter.findTasks();
    }

    @Override
    public void onWordsForRepetitionCounted(int counter) {
        wordsForRepetitionTextView.setText(format("Words for repetition %s", counter));
    }

    @Click(R.id.wordsForRepetitionTextView)
    public void onWordsForRepetitionTextViewClick() {
        Bundle args = new Bundle();
        args.putBoolean(REPETITION_MODE_MAPPING, true);
        WordSetsListFragment fragment = new WordSetsListFragment_();
        fragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    @Override
    @UiThread
    @IgnoreWhen(VIEW_DESTROYED)
    public void setTasksList(final List<Task> tasks) {
        Task[] tasksArray = tasks.toArray(new Task[0]);
        TasksListLoadedEM event = new TasksListLoadedEM(tasksArray);
        eventBus.post(event);
    }
}
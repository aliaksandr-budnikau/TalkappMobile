package org.talkappmobile.activity;

import android.app.Fragment;
import android.content.Intent;
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
import org.talkappmobile.model.RepetitionClass;
import org.talkappmobile.model.Task;
import org.talkappmobile.model.WordSet;
import org.talkappmobile.service.ServiceFactory;
import org.talkappmobile.service.impl.ServiceFactoryBean;

import java.util.List;

import org.talkappmobile.R;
import org.talkappmobile.activity.custom.TasksListView;
import org.talkappmobile.activity.custom.WaitingForProgressBarManager;
import org.talkappmobile.activity.custom.WaitingForProgressBarManagerFactory;
import org.talkappmobile.activity.interactor.MainActivityDefaultFragmentInteractor;
import org.talkappmobile.activity.presenter.MainActivityDefaultFragmentPresenter;
import org.talkappmobile.activity.view.MainActivityDefaultFragmentView;
import org.talkappmobile.events.TasksListLoadedEM;

import static java.lang.String.format;
import static org.androidannotations.annotations.IgnoreWhen.State.VIEW_DESTROYED;
import static org.talkappmobile.activity.WordSetsListFragment.REPETITION_CLASS_MAPPING;
import static org.talkappmobile.activity.WordSetsListFragment.REPETITION_MODE_MAPPING;

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
        wordsForRepetitionTextView.setText(format("Sets for repetition %s", counter));
    }

    @Click(R.id.wordsForRepetitionTextView)
    public void onWordsForRepetitionTextViewClick() {
        openWordSetsListFragment(true);
    }

    @Override
    @UiThread
    @IgnoreWhen(VIEW_DESTROYED)
    public void setTasksList(final List<Task> tasks) {
        Task[] tasksArray = tasks.toArray(new Task[0]);
        TasksListLoadedEM event = new TasksListLoadedEM(tasksArray);
        eventBus.post(event);
    }

    @Override
    public void onNewWordSetTaskClicked() {
        openWordSetsListFragment(false);
    }

    @Override
    public void onWordSetRepetitionTaskClick(RepetitionClass clazz) {
        openWordSetsListFragment(true, clazz);
    }

    @Override
    public void onDifficultWordSetRepetitionTaskClicked(List<WordSet> wordSets) {
        Intent intent = new Intent(getActivity(), PracticeWordSetActivity_.class);
        intent.putExtra(PracticeWordSetActivity.WORD_SET_MAPPING, wordSets.get(0));
        intent.putExtra(PracticeWordSetActivity.REPETITION_MODE_MAPPING, true);
        startActivity(intent);
    }

    private void openWordSetsListFragment(boolean repetitionMode, RepetitionClass clazz1) {
        Bundle args = new Bundle();
        args.putBoolean(REPETITION_MODE_MAPPING, repetitionMode);
        args.putSerializable(REPETITION_CLASS_MAPPING, clazz1);
        WordSetsListFragment fragment = new WordSetsListFragment_();
        fragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    private void openWordSetsListFragment(boolean repetitionMode) {
        openWordSetsListFragment(repetitionMode, null);
    }
}
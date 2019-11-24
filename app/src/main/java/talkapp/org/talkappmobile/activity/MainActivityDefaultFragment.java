package talkapp.org.talkappmobile.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.IgnoreWhen;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.greenrobot.eventbus.EventBus;

import java.util.List;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.custom.TasksListView;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.activity.interactor.MainActivityDefaultFragmentInteractor;
import talkapp.org.talkappmobile.activity.presenter.MainActivityDefaultFragmentPresenter;
import talkapp.org.talkappmobile.activity.view.MainActivityDefaultFragmentView;
import talkapp.org.talkappmobile.events.TasksListLoadedEM;
import talkapp.org.talkappmobile.model.RepetitionClass;
import talkapp.org.talkappmobile.model.Task;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;

import static org.androidannotations.annotations.IgnoreWhen.State.VIEW_DESTROYED;
import static talkapp.org.talkappmobile.activity.WordSetsListFragment.REPETITION_CLASS_MAPPING;
import static talkapp.org.talkappmobile.activity.WordSetsListFragment.REPETITION_MODE_MAPPING;

@EFragment(value = R.layout.main_activity_default_fragment_layout)
public class MainActivityDefaultFragment extends Fragment implements MainActivityDefaultFragmentView {
    @EventBusGreenRobot
    EventBus eventBus;
    @Bean
    WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory;
    @Bean(ServiceFactoryBean.class)
    ServiceFactory serviceFactory;

    @ViewById(R.id.tasksListView)
    TasksListView tasksListView;
    @ViewById(R.id.please_wait_progress_bar)
    View progressBarView;
    @ViewById(R.id.wordSetVocabularyView)
    RecyclerView wordSetVocabularyView;
    @StringRes(R.string.word_set_task_repetitions_title)
    String wordSetsRepetitionTitle;
    @StringRes(R.string.word_set_task_repetitions_description)
    String wordSetsRepetitionDescription;
    @StringRes(R.string.word_set_task_learning_title)
    String wordSetsLearningTitle;
    @StringRes(R.string.word_set_task_learning_description)
    String wordSetsLearningDescription;
    @StringRes(R.string.word_set_task_extra_repetitions_title)
    String wordSetsExtraRepetitionTitle;
    @StringRes(R.string.word_set_task_extra_repetitions_description)
    String wordSetsExtraRepetitionDescription;

    private WaitingForProgressBarManager waitingForProgressBarManager;

    private MainActivityDefaultFragmentPresenter presenter;

    @AfterViews
    public void init() {
        waitingForProgressBarManager = waitingForProgressBarManagerFactory.get(progressBarView, wordSetVocabularyView);
        MainActivityDefaultFragmentInteractor interactor = new MainActivityDefaultFragmentInteractor(serviceFactory.getPracticeWordSetExerciseRepository(),
                wordSetsRepetitionTitle, wordSetsRepetitionDescription,
                wordSetsLearningTitle, wordSetsLearningDescription,
                wordSetsExtraRepetitionTitle, wordSetsExtraRepetitionDescription
        );
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
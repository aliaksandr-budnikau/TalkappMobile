package talkapp.org.talkappmobile.activity;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TabHost;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.IgnoreWhen;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ItemLongClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;

import java.util.List;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.custom.WordSetsListItemView;
import talkapp.org.talkappmobile.activity.custom.WordSetsListListView;
import talkapp.org.talkappmobile.activity.event.wordset.WordSetsFinishedFilterAppliedEM;
import talkapp.org.talkappmobile.activity.event.wordset.WordSetsLearnedRepFilterAppliedEM;
import talkapp.org.talkappmobile.activity.event.wordset.WordSetsNewFilterAppliedEM;
import talkapp.org.talkappmobile.activity.event.wordset.WordSetsNewRepFilterAppliedEM;
import talkapp.org.talkappmobile.activity.event.wordset.WordSetsRepeatedRepFilterAppliedEM;
import talkapp.org.talkappmobile.activity.event.wordset.WordSetsSeenRepFilterAppliedEM;
import talkapp.org.talkappmobile.activity.event.wordset.WordSetsStartedFilterAppliedEM;
import talkapp.org.talkappmobile.activity.interactor.WordSetsListInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.RepetitionWordSetsListInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.StudyingWordSetsListInteractor;
import talkapp.org.talkappmobile.activity.presenter.WordSetsListPresenter;
import talkapp.org.talkappmobile.activity.view.WordSetsListView;
import talkapp.org.talkappmobile.component.backend.BackendServerFactory;
import talkapp.org.talkappmobile.component.backend.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.component.database.ServiceFactory;
import talkapp.org.talkappmobile.component.database.impl.ServiceFactoryBean;
import talkapp.org.talkappmobile.component.view.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.component.view.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.model.RepetitionClass;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;

import static org.androidannotations.annotations.IgnoreWhen.State.VIEW_DESTROYED;
import static talkapp.org.talkappmobile.model.RepetitionClass.LEARNED;
import static talkapp.org.talkappmobile.model.RepetitionClass.REPEATED;
import static talkapp.org.talkappmobile.model.RepetitionClass.SEEN;

@EFragment(value = R.layout.word_sets_list_layout)
public class WordSetsListFragment extends Fragment implements WordSetsListView {
    public static final String TOPIC_MAPPING = "topic";
    public static final String REPETITION_MODE_MAPPING = "repetitionMode";
    public static final String NEW = "new";
    public static final String STARTED = "started";
    public static final String FINISHED = "finished";
    @Bean(ServiceFactoryBean.class)
    ServiceFactory serviceFactory;
    @Bean(BackendServerFactoryBean.class)
    BackendServerFactory backendServerFactory;
    @Bean
    WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory;

    @EventBusGreenRobot
    EventBus eventBus;

    @ViewById(R.id.wordSetsListView)
    WordSetsListListView wordSetsListView;
    @ViewById(R.id.please_wait_progress_bar)
    View progressBarView;
    @ViewById(R.id.tabHost)
    TabHost tabHost;

    @FragmentArg(TOPIC_MAPPING)
    Topic topic;
    @FragmentArg(REPETITION_MODE_MAPPING)
    boolean repetitionMode;
    private WaitingForProgressBarManager waitingForProgressBarManager;
    private WordSetsListPresenter presenter;

    @AfterViews
    public void init() {
        waitingForProgressBarManager = waitingForProgressBarManagerFactory.get(progressBarView, wordSetsListView);

        initPresenter();
    }

    @Background
    public void initPresenter() {
        WordSetsListInteractor interactor = new StudyingWordSetsListInteractor(backendServerFactory.get(), serviceFactory.getWordSetExperienceRepository(), serviceFactory.getPracticeWordSetExerciseRepository());
        if (repetitionMode) {
            interactor = new RepetitionWordSetsListInteractor(serviceFactory.getPracticeWordSetExerciseRepository());
        }
        presenter = new WordSetsListPresenter(topic, this, interactor);
        presenter.initialize();
    }

    @ItemClick(R.id.wordSetsListView)
    public void onItemClick(int position) {
        WordSet wordSet = wordSetsListView.getWordSet(position);
        presenter.itemClick(wordSet, position);
    }

    @ItemLongClick(R.id.wordSetsListView)
    public void onItemLongClick(final int position) {
        WordSet wordSet = wordSetsListView.getWordSet(position);
        presenter.itemLongClick(wordSet, position);
    }

    @Override
    public void onWordSetFinished(WordSet wordSet, int clickedItemNumber) {
        askToResetExperience(wordSet, clickedItemNumber);
    }

    @Override
    public void onResetExperienceClick(WordSet wordSet, int clickedItemNumber) {
        WordSetsListItemView itemView = wordSetsListView.getClickedItemView(clickedItemNumber);
        itemView.setModel(wordSet);
        itemView.refreshModel();
        itemView.hideProgress();
    }

    @Override
    public void onWordSetNotFinished(Topic topic, WordSet wordSet) {
        startWordSetActivity(topic, wordSet);
    }

    @Override
    public void onItemLongClick(WordSet wordSet, int clickedItemNumber) {
        askToResetExperience(wordSet, clickedItemNumber);
    }

    private void askToResetExperience(final WordSet wordSet, final int clickedItemNumber) {
        new AlertDialog.Builder(getActivity())
                .setMessage("Do you want to reset your progress?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        presenter.resetExperienceClick(wordSet, clickedItemNumber);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void startWordSetActivity(Topic topic, WordSet wordSet) {
        Intent intent = new Intent(getActivity(), PracticeWordSetActivity_.class);
        intent.putExtra(PracticeWordSetActivity.TOPIC_MAPPING, topic);
        intent.putExtra(PracticeWordSetActivity.WORD_SET_MAPPING, wordSet);
        intent.putExtra(PracticeWordSetActivity.REPETITION_MODE_MAPPING, repetitionMode);
        startActivity(intent);
    }

    @Override
    @UiThread
    @IgnoreWhen(VIEW_DESTROYED)
    public void onWordSetsInitialized(final List<WordSet> wordSets) {
        wordSetsListView.addAll(wordSets);
        wordSetsListView.refreshModel();

        if (repetitionMode) {
            tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                @Override
                public void onTabChanged(String tabId) {
                    RepetitionClass selectedClass = RepetitionClass.valueOf(tabId);
                    if (RepetitionClass.NEW == selectedClass) {
                        eventBus.post(new WordSetsNewRepFilterAppliedEM());
                    } else if (SEEN == selectedClass) {
                        eventBus.post(new WordSetsSeenRepFilterAppliedEM());
                    } else if (REPEATED == selectedClass) {
                        eventBus.post(new WordSetsRepeatedRepFilterAppliedEM());
                    } else if (LEARNED == selectedClass) {
                        eventBus.post(new WordSetsLearnedRepFilterAppliedEM());
                    }
                }
            });
            tabHost.setup();
            //Tab 1
            TabHost.TabSpec spec = tabHost.newTabSpec(RepetitionClass.NEW.name());
            spec.setContent(R.id.wordSetsListView);
            spec.setIndicator("> 0");
            tabHost.addTab(spec);

            //Tab 2
            spec = tabHost.newTabSpec(SEEN.name());
            spec.setContent(R.id.wordSetsListView);
            spec.setIndicator("> 1");
            tabHost.addTab(spec);

            //Tab 3
            spec = tabHost.newTabSpec(REPEATED.name());
            spec.setContent(R.id.wordSetsListView);
            spec.setIndicator("> 3");
            tabHost.addTab(spec);

            //Tab 4
            spec = tabHost.newTabSpec(LEARNED.name());
            spec.setContent(R.id.wordSetsListView);
            spec.setIndicator("> 7");
            tabHost.addTab(spec);
        } else {
            tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                @Override
                public void onTabChanged(String tabId) {
                    if (NEW.equals(tabId)) {
                        eventBus.post(new WordSetsNewFilterAppliedEM());
                    } else if (STARTED.equals(tabId)) {
                        eventBus.post(new WordSetsStartedFilterAppliedEM());
                    } else if (FINISHED.equals(tabId)) {
                        eventBus.post(new WordSetsFinishedFilterAppliedEM());
                    }
                }
            });
            tabHost.setup();
            //Tab 1
            TabHost.TabSpec spec = tabHost.newTabSpec(NEW);
            spec.setContent(R.id.wordSetsListView);
            spec.setIndicator("New");
            tabHost.addTab(spec);

            //Tab 2
            spec = tabHost.newTabSpec(STARTED);
            spec.setContent(R.id.wordSetsListView);
            spec.setIndicator("Started");
            tabHost.addTab(spec);

            //Tab 3
            spec = tabHost.newTabSpec(FINISHED);
            spec.setContent(R.id.wordSetsListView);
            spec.setIndicator("Finished");
            tabHost.addTab(spec);
        }
    }

    @Override
    @UiThread
    public void onInitializeBeginning() {
        waitingForProgressBarManager.showProgressBar();
    }

    @Override
    @UiThread
    public void onInitializeEnd() {
        waitingForProgressBarManager.hideProgressBar();
    }
}

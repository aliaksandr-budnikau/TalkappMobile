package talkapp.org.talkappmobile.activity;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

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
import talkapp.org.talkappmobile.events.WordSetsFinishedFilterAppliedEM;
import talkapp.org.talkappmobile.events.WordSetsLearnedRepFilterAppliedEM;
import talkapp.org.talkappmobile.events.WordSetsNewFilterAppliedEM;
import talkapp.org.talkappmobile.events.WordSetsNewRepFilterAppliedEM;
import talkapp.org.talkappmobile.events.WordSetsRemoveClickedEM;
import talkapp.org.talkappmobile.events.WordSetsRepeatedRepFilterAppliedEM;
import talkapp.org.talkappmobile.events.WordSetsSeenRepFilterAppliedEM;
import talkapp.org.talkappmobile.events.WordSetsStartedFilterAppliedEM;
import org.talkappmobile.model.RepetitionClass;
import org.talkappmobile.model.Topic;
import org.talkappmobile.model.WordSet;
import org.talkappmobile.service.BackendServerFactory;
import org.talkappmobile.service.ServiceFactory;
import org.talkappmobile.service.impl.BackendServerFactoryBean;
import org.talkappmobile.service.impl.ServiceFactoryBean;

import java.util.List;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.activity.custom.WordSetsListItemView;
import talkapp.org.talkappmobile.activity.custom.WordSetsListListView;
import talkapp.org.talkappmobile.activity.interactor.WordSetsListInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.RepetitionWordSetsListInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.StudyingWordSetsListInteractor;
import talkapp.org.talkappmobile.activity.presenter.WordSetsListPresenter;
import talkapp.org.talkappmobile.activity.view.WordSetsListView;

import static org.androidannotations.annotations.IgnoreWhen.State.VIEW_DESTROYED;
import static org.talkappmobile.model.RepetitionClass.LEARNED;
import static org.talkappmobile.model.RepetitionClass.REPEATED;
import static org.talkappmobile.model.RepetitionClass.SEEN;

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
                .setItems(new String[]{"Reset progress", "Edit words", "Delete words"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            new AlertDialog.Builder(getActivity())
                                    .setMessage("Do you want to reset your progress?")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            presenter.resetExperienceClick(wordSet, clickedItemNumber);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null).show();
                        } else if (which == 2) {
                            new AlertDialog.Builder(getActivity())
                                    .setMessage("Do you want to remove these words?")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            presenter.deleteWordSetClick(wordSet, clickedItemNumber);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null).show();
                        }
                        dialog.dismiss();
                    }
                }).show();
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
            spec.setIndicator("New");
            tabHost.addTab(spec);

            //Tab 2
            spec = tabHost.newTabSpec(SEEN.name());
            spec.setContent(R.id.wordSetsListView);
            spec.setIndicator("1 - 2");
            tabHost.addTab(spec);

            //Tab 3
            spec = tabHost.newTabSpec(REPEATED.name());
            spec.setContent(R.id.wordSetsListView);
            spec.setIndicator("3 - 6");
            tabHost.addTab(spec);

            //Tab 4
            spec = tabHost.newTabSpec(LEARNED.name());
            spec.setContent(R.id.wordSetsListView);
            spec.setIndicator("Learned");
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

    @Override
    @UiThread
    public void onWordSetRemoved(WordSet wordSet, int clickedItemNumber) {
        Toast.makeText(getActivity(), "The words were removed", Toast.LENGTH_LONG).show();
        eventBus.post(new WordSetsRemoveClickedEM(wordSet, clickedItemNumber));
    }

    @Override
    @UiThread
    public void onWordSetNotRemoved() {
        Toast.makeText(getActivity(), "Only custom words can be removed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onWordSetTooSmallForRepetition(int maxWordSetSize, int actualSize) {
        Toast.makeText(getActivity(), "The set is too small " + actualSize + "/" + maxWordSetSize + " for repetition", Toast.LENGTH_LONG).show();
    }
}

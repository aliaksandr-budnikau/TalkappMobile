package talkapp.org.talkappmobile.activity;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.IgnoreWhen;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import talkapp.org.talkappmobile.PresenterFactory;
import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.custom.PhraseSetsRecyclerView;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.activity.custom.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.component.BeanFactory;
import talkapp.org.talkappmobile.events.OpenWordSetForStudyingEM;
import talkapp.org.talkappmobile.events.ParentScreenOutdatedEM;
import talkapp.org.talkappmobile.model.NewWordSetDraftQRObject;
import talkapp.org.talkappmobile.model.RepetitionClass;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;
import talkapp.org.talkappmobile.presenter.WordSetsListPresenter;
import talkapp.org.talkappmobile.view.WordSetsListView;
import talkapp.org.talkappmobile.widget.adapter.filterable.AbstractFilter;
import talkapp.org.talkappmobile.widget.adapter.filterable.FilterableAdapter;
import talkapp.org.talkappmobile.widget.adapter.filterable.OnItemClickListener;
import talkapp.org.talkappmobile.widget.adapter.filterable.OnItemLongClickListener;

import static org.androidannotations.annotations.IgnoreWhen.State.VIEW_DESTROYED;
import static talkapp.org.talkappmobile.activity.WordSetsListFragment.WordSetFilter.ONLY_FINISHED_WORD_SETS;
import static talkapp.org.talkappmobile.activity.WordSetsListFragment.WordSetFilter.ONLY_LEARNED_REP_WORD_SETS;
import static talkapp.org.talkappmobile.activity.WordSetsListFragment.WordSetFilter.ONLY_NEW_REP_WORD_SETS;
import static talkapp.org.talkappmobile.activity.WordSetsListFragment.WordSetFilter.ONLY_NEW_WORD_SETS;
import static talkapp.org.talkappmobile.activity.WordSetsListFragment.WordSetFilter.ONLY_REPEATED_REP_WORD_SETS;
import static talkapp.org.talkappmobile.activity.WordSetsListFragment.WordSetFilter.ONLY_SEEN_REP_WORD_SETS;
import static talkapp.org.talkappmobile.activity.WordSetsListFragment.WordSetFilter.ONLY_STARTED_WORD_SETS;
import static talkapp.org.talkappmobile.model.RepetitionClass.LEARNED;
import static talkapp.org.talkappmobile.model.RepetitionClass.REPEATED;
import static talkapp.org.talkappmobile.model.RepetitionClass.SEEN;

@EFragment(value = R.layout.word_sets_list_layout)
public class WordSetsListFragment extends Fragment implements WordSetsListView, OnItemClickListener, OnItemLongClickListener {
    public static final String TOPIC_MAPPING = "topic";
    public static final String REPETITION_MODE_MAPPING = "repetitionMode";
    public static final String REPETITION_CLASS_MAPPING = "repetitionClass";
    public static final String NEW = "new";
    public static final String STARTED = "started";
    public static final String FINISHED = "finished";
    @Bean
    WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory;
    @EventBusGreenRobot
    EventBus eventBus;
    @ViewById(R.id.wordSetsListView)
    PhraseSetsRecyclerView phraseSetsRecyclerView;
    @ViewById(R.id.please_wait_progress_bar)
    View progressBarView;
    @ViewById(R.id.tabHost)
    TabHost tabHost;
    @StringRes(R.string.word_sets_list_fragment_tab_new_label)
    String tabNewLabel;
    @StringRes(R.string.word_sets_list_fragment_tab_started_label)
    String tabStartedLabel;
    @StringRes(R.string.word_sets_list_fragment_tab_finished_label)
    String tabFinishedLabel;
    @StringRes(R.string.word_sets_list_fragment_tab_rep_new_label)
    String tabRepNewLabel;
    @StringRes(R.string.word_sets_list_fragment_tab_rep_learned_label)
    String tabRepLearnedLabel;
    @StringRes(R.string.word_sets_list_fragment_sharing_option_label)
    String sharingOptionLabel;
    @StringRes(R.string.word_sets_list_fragment_opening_warning_too_early_message)
    String openingWarningTooEarlyMessage;
    @FragmentArg(TOPIC_MAPPING)
    Topic topic;
    @FragmentArg(REPETITION_MODE_MAPPING)
    boolean repetitionMode;
    @FragmentArg(REPETITION_CLASS_MAPPING)
    RepetitionClass repetitionClass;
    private WaitingForProgressBarManager waitingForProgressBarManager;
    private WordSetsListPresenter presenter;

    @AfterViews
    public void init() {
        waitingForProgressBarManager = waitingForProgressBarManagerFactory.get(progressBarView, phraseSetsRecyclerView);

        initPresenter();
    }

    @Background
    public void initPresenter() {
        PresenterFactory presenterFactory = BeanFactory.presenterFactory(getActivity());
        presenter = presenterFactory.create(this, repetitionMode, repetitionClass, topic);
        presenter.initialize();
    }

    @Override
    public void onWordSetFinished(WordSet wordSet, int clickedItemNumber) {
        askToResetExperience(wordSet, clickedItemNumber);
    }

    @Override
    public void onResetExperienceClick(WordSet wordSet, int clickedItemNumber) {
        phraseSetsRecyclerView.getAdapter().setItem(wordSet, clickedItemNumber);
    }

    @Override
    public void onWordSetNotFinished(Topic topic, WordSet wordSet) {
        eventBus.post(new OpenWordSetForStudyingEM(topic, wordSet, repetitionMode));
    }

    @Override
    public void onItemLongClick(WordSet wordSet, int clickedItemNumber) {
        askToResetExperience(wordSet, clickedItemNumber);
    }

    private void askToResetExperience(final WordSet wordSet, final int clickedItemNumber) {
        new AlertDialog.Builder(getActivity())
                .setItems(new String[]{"Reset progress", sharingOptionLabel, "Delete words"}, new DialogInterface.OnClickListener() {
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
                        } else if (which == 1) {
                            prepareWordSetDraftForQRCode(wordSet);
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

    @Background
    public void prepareWordSetDraftForQRCode(WordSet wordSet) {
        presenter.prepareWordSetDraftForQRCode(wordSet.getId());
    }

    @Override
    @UiThread
    @IgnoreWhen(VIEW_DESTROYED)
    public void onWordSetsInitialized(final List<WordSet> wordSets, RepetitionClass selectedClass) {
        FilterableAdapter<WordSet, PhraseSetsRecyclerView.ViewHolder> adapter = new FilterableAdapter<>(wordSets, new PhraseSetsRecyclerView.ViewHolderFactory(this, this));
        phraseSetsRecyclerView.setAdapter(adapter);

        if (repetitionMode) {
            tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                @Override
                public void onTabChanged(String tabId) {
                    pickRepetitionFilter(RepetitionClass.valueOf(tabId));
                }
            });
            tabHost.setup();
            //Tab 1
            TabHost.TabSpec spec = tabHost.newTabSpec(RepetitionClass.NEW.name());
            spec.setContent(R.id.wordSetsListView);
            spec.setIndicator(tabRepNewLabel);
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
            spec.setIndicator(tabRepLearnedLabel);
            tabHost.addTab(spec);

            tabHost.setCurrentTabByTag(selectedClass.name());
        } else {
            tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                @Override
                public void onTabChanged(String tabId) {
                    pickStudingFilter(tabId);
                }
            });
            tabHost.setup();
            //Tab 1
            TabHost.TabSpec spec = tabHost.newTabSpec(NEW);
            spec.setContent(R.id.wordSetsListView);
            spec.setIndicator(tabNewLabel);
            tabHost.addTab(spec);

            //Tab 2
            spec = tabHost.newTabSpec(STARTED);
            spec.setContent(R.id.wordSetsListView);
            spec.setIndicator(tabStartedLabel);
            tabHost.addTab(spec);

            //Tab 3
            spec = tabHost.newTabSpec(FINISHED);
            spec.setContent(R.id.wordSetsListView);
            spec.setIndicator(tabFinishedLabel);
            tabHost.addTab(spec);
        }
    }

    private void pickStudingFilter(String tabId) {
        if (NEW.equals(tabId)) {
            phraseSetsRecyclerView.getAdapter().filterOut(ONLY_NEW_WORD_SETS.getFilter());
        } else if (STARTED.equals(tabId)) {
            phraseSetsRecyclerView.getAdapter().filterOut(ONLY_STARTED_WORD_SETS.getFilter());
        } else if (FINISHED.equals(tabId)) {
            phraseSetsRecyclerView.getAdapter().filterOut(ONLY_FINISHED_WORD_SETS.getFilter());
        }
    }

    private void pickRepetitionFilter(RepetitionClass selectedClass) {
        if (RepetitionClass.NEW == selectedClass) {
            phraseSetsRecyclerView.getAdapter().filterOut(ONLY_NEW_REP_WORD_SETS.getFilter());
        } else if (SEEN == selectedClass) {
            phraseSetsRecyclerView.getAdapter().filterOut(ONLY_SEEN_REP_WORD_SETS.getFilter());
        } else if (REPEATED == selectedClass) {
            phraseSetsRecyclerView.getAdapter().filterOut(ONLY_REPEATED_REP_WORD_SETS.getFilter());
        } else if (LEARNED == selectedClass) {
            phraseSetsRecyclerView.getAdapter().filterOut(ONLY_LEARNED_REP_WORD_SETS.getFilter());
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
        phraseSetsRecyclerView.getAdapter().removeItem(clickedItemNumber);
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

    @Override
    @UiThread
    @IgnoreWhen(VIEW_DESTROYED)
    public void onWordSetsRefreshed(List<WordSet> wordSets, RepetitionClass repetitionClass) {
        PhraseSetsRecyclerView.ViewHolderFactory viewHolderFactory = new PhraseSetsRecyclerView.ViewHolderFactory(this, this);
        FilterableAdapter<WordSet, PhraseSetsRecyclerView.ViewHolder> adapter = new FilterableAdapter<>(wordSets, viewHolderFactory);
        phraseSetsRecyclerView.setAdapter(adapter);
        if (repetitionClass == null) {
            pickStudingFilter(NEW);
            tabHost.setCurrentTabByTag(NEW);
        } else {
            pickRepetitionFilter(repetitionClass);
            tabHost.setCurrentTabByTag(repetitionClass.name());
        }
    }

    @UiThread
    @Override
    public void onWordSetDraftPrepare(NewWordSetDraftQRObject qrObject) {
        Intent intent = new Intent(getActivity(), WordSetQRExportActivity_.class);
        intent.putExtra(WordSetQRExportActivity.WORD_SET_MAPPING, qrObject);
        startActivity(intent);
    }

    @UiThread
    @Override
    public void onWordSetCantBeShared() {
        Toast.makeText(getActivity(), "Only custom words can be shared", Toast.LENGTH_LONG).show();
    }

    @Override
    @UiThread
    public void onWordSetIsNotAvailableYet(int availableInHours) {
        Toast.makeText(getActivity(), String.format(openingWarningTooEarlyMessage, availableInHours), Toast.LENGTH_LONG).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OpenWordSetForStudyingEM event) {
        Intent intent = new Intent(getActivity(), PracticeWordSetActivity_.class);
        intent.putExtra(PracticeWordSetActivity.TOPIC_MAPPING, event.getTopic());
        intent.putExtra(PracticeWordSetActivity.WORD_SET_MAPPING, event.getWordSet());
        intent.putExtra(PracticeWordSetActivity.REPETITION_MODE_MAPPING, event.isRepetitionMode());
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(ParentScreenOutdatedEM event) {
        presenter.refresh();
    }

    @Override
    public void onItemLongClick(int position) {
        WordSet wordSet = phraseSetsRecyclerView.getAdapter().get(position);
        presenter.itemLongClick(wordSet, position);
    }

    @Override
    public void onItemClick(int position) {
        WordSet wordSet = phraseSetsRecyclerView.getAdapter().get(position);
        presenter.itemClick(wordSet, position);
    }

    public enum WordSetFilter {
        ONLY_NEW_WORD_SETS(new AbstractFilter<WordSet>() {
            @Override
            public boolean filter(WordSet wordSet) {
                return wordSet.getTrainingExperience() == 0;
            }
        }),
        ONLY_STARTED_WORD_SETS(new AbstractFilter<WordSet>() {
            @Override
            public boolean filter(WordSet wordSet) {
                return wordSet.getTrainingExperience() != 0 && WordSetProgressStatus.FINISHED != wordSet.getStatus();
            }
        }),
        ONLY_FINISHED_WORD_SETS(new AbstractFilter<WordSet>() {
            @Override
            public boolean filter(WordSet wordSet) {
                return WordSetProgressStatus.FINISHED == wordSet.getStatus();
            }
        }),
        ONLY_NEW_REP_WORD_SETS(new AbstractFilter<WordSet>() {
            @Override
            public boolean filter(WordSet wordSet) {
                return wordSet.getRepetitionClass() == RepetitionClass.NEW;
            }
        }),
        ONLY_SEEN_REP_WORD_SETS(new AbstractFilter<WordSet>() {
            @Override
            public boolean filter(WordSet wordSet) {
                return wordSet.getRepetitionClass() == SEEN;
            }
        }),
        ONLY_REPEATED_REP_WORD_SETS(new AbstractFilter<WordSet>() {
            @Override
            public boolean filter(WordSet wordSet) {
                return wordSet.getRepetitionClass() == REPEATED;
            }
        }),
        ONLY_LEARNED_REP_WORD_SETS(new AbstractFilter<WordSet>() {
            @Override
            public boolean filter(WordSet wordSet) {
                return wordSet.getRepetitionClass() == LEARNED;
            }
        });

        private final AbstractFilter<WordSet> filter;

        WordSetFilter(AbstractFilter<WordSet> filter) {
            this.filter = filter;
        }

        public AbstractFilter<WordSet> getFilter() {
            return filter;
        }
    }
}
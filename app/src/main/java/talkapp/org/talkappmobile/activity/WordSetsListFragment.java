package talkapp.org.talkappmobile.activity;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ItemLongClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.adapter.AdaptersFactory;
import talkapp.org.talkappmobile.activity.interactor.WordSetsListInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.RepetitionWordSetsListInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.StudyingWordSetsListInteractor;
import talkapp.org.talkappmobile.activity.presenter.WordSetsListPresenter;
import talkapp.org.talkappmobile.activity.view.WordSetsListView;
import talkapp.org.talkappmobile.component.view.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.component.view.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.config.DIContextUtils;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

@EFragment(value = R.layout.word_sets_list_layout)
public class WordSetsListFragment extends Fragment implements WordSetsListView {
    public static final String TOPIC_MAPPING = "topic";
    public static final String REPETITION_MODE_MAPPING = "repetitionMode";
    private final ThreadLocal<View> THREAD_LOCAL = new ThreadLocal<>();
    @Inject
    AdaptersFactory adaptersFactory;
    @Inject
    StudyingWordSetsListInteractor studyingWordSetsListInteractor;
    @Inject
    RepetitionWordSetsListInteractor repetitionWordSetsListInteractor;
    @Inject
    WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory;

    @ViewById(R.id.wordSetsListView)
    ListView wordSetsListView;
    @ViewById(R.id.please_wait_progress_bar)
    View progressBarView;

    @FragmentArg(TOPIC_MAPPING)
    Topic topic;
    @FragmentArg(REPETITION_MODE_MAPPING)
    boolean repetitionMode;

    private WaitingForProgressBarManager waitingForProgressBarManager;

    private ArrayAdapter<WordSet> adapter;
    private WordSetsListPresenter presenter;

    @AfterViews
    public void init() {
        DIContextUtils.get().inject(this);

        adapter = adaptersFactory.createWordSetListAdapter(this.getActivity());
        wordSetsListView.setAdapter(adapter);

        waitingForProgressBarManager = waitingForProgressBarManagerFactory.get(progressBarView, wordSetsListView);

        initPresenter();
    }

    @Background
    public void initPresenter() {
        WordSetsListInteractor interactor = studyingWordSetsListInteractor;
        if (repetitionMode) {
            interactor = repetitionWordSetsListInteractor;
        }
        presenter = new WordSetsListPresenter(topic, this, interactor);
        presenter.initialize();
    }

    @ItemClick(R.id.wordSetsListView)
    public void onItemClick(int position) {
        THREAD_LOCAL.set(adapter.getView(position, null, wordSetsListView));
        WordSet wordSet = adapter.getItem(position);
        presenter.itemClick(wordSet);
    }

    @ItemLongClick(R.id.wordSetsListView)
    public boolean onItemLongClick(final int position) {
        THREAD_LOCAL.set(adapter.getView(position, null, wordSetsListView));
        WordSet wordSet = adapter.getItem(position);
        presenter.itemLongClick(wordSet);
        return true;
    }

    @Override
    public void onWordSetFinished(WordSet wordSet) {
        askToResetExperience(wordSet);
    }

    @Override
    public void onResetExperienceClick(WordSetExperience experience) {
        View view = THREAD_LOCAL.get();
        THREAD_LOCAL.remove();
        ProgressBar wordSetProgress = view.findViewById(R.id.wordSetProgress);
        wordSetProgress.setProgress(experience.getTrainingExperience());
    }

    @Override
    public void onWordSetNotFinished(Topic topic, WordSet wordSet) {
        startWordSetActivity(topic, wordSet);
    }

    @Override
    public void onItemLongClick(WordSet wordSet) {
        askToResetExperience(wordSet);
    }

    private void askToResetExperience(final WordSet wordSet) {
        new AlertDialog.Builder(getActivity())
                .setMessage("Do you want to reset your progress?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        presenter.resetExperienceClick(wordSet);
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
    public void onWordSetsInitialized(final List<WordSet> wordSets) {
        adapter.addAll(wordSets);
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

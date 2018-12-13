package talkapp.org.talkappmobile.activity;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.IgnoreWhen;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ItemLongClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.custom.WordSetsListItemView;
import talkapp.org.talkappmobile.activity.custom.WordSetsListListView;
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

import static org.androidannotations.annotations.IgnoreWhen.State.VIEW_DESTROYED;

@EFragment(value = R.layout.word_sets_list_layout)
public class WordSetsListFragment extends Fragment implements WordSetsListView {
    public static final String TOPIC_MAPPING = "topic";
    public static final String REPETITION_MODE_MAPPING = "repetitionMode";
    @Inject
    StudyingWordSetsListInteractor studyingWordSetsListInteractor;
    @Inject
    RepetitionWordSetsListInteractor repetitionWordSetsListInteractor;
    @Inject
    WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory;

    @ViewById(R.id.wordSetsListView)
    WordSetsListListView wordSetsListView;
    @ViewById(R.id.please_wait_progress_bar)
    View progressBarView;

    @FragmentArg(TOPIC_MAPPING)
    Topic topic;
    @FragmentArg(REPETITION_MODE_MAPPING)
    boolean repetitionMode;
    private WaitingForProgressBarManager waitingForProgressBarManager;
    private WordSetsListPresenter presenter;

    @AfterViews
    public void init() {
        DIContextUtils.get().inject(this);

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
    public void onResetExperienceClick(WordSet wordSet, WordSetExperience experience, int clickedItemNumber) {
        WordSetsListItemView itemView = (WordSetsListItemView) wordSetsListView.getClickedItemView(clickedItemNumber);
        itemView.setModel(wordSet, experience);
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

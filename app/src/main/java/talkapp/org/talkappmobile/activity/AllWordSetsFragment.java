package talkapp.org.talkappmobile.activity;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.adapter.AdaptersFactory;
import talkapp.org.talkappmobile.activity.presenter.AllWordSetsPresenter;
import talkapp.org.talkappmobile.activity.presenter.AllWordSetsView;
import talkapp.org.talkappmobile.component.backend.WordSetService;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

public class AllWordSetsFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, AllWordSetsView {
    public static final String TOPIC_ID_MAPPING = "topicId";
    private final ThreadLocal<View> THREAD_LOCAL = new ThreadLocal<>();
    @Inject
    WordSetService wordSetService;
    @Inject
    Executor executor;
    @Inject
    AdaptersFactory adaptersFactory;
    private ListView wordSetsListView;
    private ArrayAdapter<WordSet> adapter;
    private AllWordSetsPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_word_sets_layout, container, false);
        DIContext.get().inject(this);

        adapter = adaptersFactory.createWordSetListAdapter(this.getActivity());

        wordSetsListView = view.findViewById(R.id.wordSetsListView);
        wordSetsListView.setAdapter(adapter);
        wordSetsListView.setOnItemClickListener(this);
        wordSetsListView.setOnItemLongClickListener(this);

        Bundle arguments = AllWordSetsFragment.this.getArguments();
        String topicId = null;
        if (arguments != null) {
            topicId = (String) arguments.get(TOPIC_ID_MAPPING);
        }

        presenter = new AllWordSetsPresenter(topicId, this);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                presenter.initialize();
                return null;
            }
        }.executeOnExecutor(executor);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        THREAD_LOCAL.set(view);
        WordSet wordSet = adapter.getItem(position);
        presenter.itemClick(wordSet);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
        THREAD_LOCAL.set(view);
        WordSet wordSet = adapter.getItem(position);
        presenter.itemLongClick(wordSet);
        return true;
    }

    @Override
    public void onWordSetsInitialized(List<WordSet> wordSets) {
        adapter.addAll(wordSets);
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
    public void onWordSetNotFinished(WordSet wordSet) {
        startWordSetActivity(wordSet);
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

    private void startWordSetActivity(WordSet wordSet) {
        Intent intent = new Intent(getActivity(), PracticeWordSetActivity.class);
        intent.putExtra(PracticeWordSetActivity.WORD_SET_MAPPING, wordSet);
        startActivity(intent);
    }
}

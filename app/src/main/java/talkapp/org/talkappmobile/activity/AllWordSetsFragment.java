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

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;
import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.adapter.AdaptersFactory;
import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.backend.WordSetService;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseRepository;
import talkapp.org.talkappmobile.component.database.WordSetExperienceRepository;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.FINISHED;

public class AllWordSetsFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    public static final String TOPIC_ID_MAPPING = "topicId";
    @Inject
    WordSetService wordSetService;
    @Inject
    WordSetExperienceRepository experienceRepository;
    @Inject
    AdaptersFactory adaptersFactory;
    @Inject
    PracticeWordSetExerciseRepository exerciseRepository;
    @Inject
    AuthSign authSign;
    private ListView wordSetsListView;
    private ArrayAdapter<WordSet> adapter;

    private AsyncTask<String, Object, List<WordSet>> loadingWordSets = new AsyncTask<String, Object, List<WordSet>>() {
        @Override
        protected List<WordSet> doInBackground(String... params) {
            Bundle arguments = AllWordSetsFragment.this.getArguments();
            String topicId = null;
            if (arguments != null) {
                topicId = (String) arguments.get(TOPIC_ID_MAPPING);
            }

            Call<List<WordSet>> wordSetCall;
            if (topicId == null) {
                wordSetCall = wordSetService.findAll(authSign);
            } else {
                wordSetCall = wordSetService.findByTopicId(topicId, authSign);
            }
            Response<List<WordSet>> wordSets = null;
            try {
                wordSets = wordSetCall.execute();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            return wordSets.body();
        }

        @Override
        protected void onPostExecute(List<WordSet> wordSets) {
            adapter.addAll(wordSets);
        }
    };

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

        loadingWordSets.execute();
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WordSet wordSet = adapter.getItem(position);
        WordSetExperience experience = experienceRepository.findById(wordSet.getId());
        if (FINISHED.equals(experience.getStatus())) {
            askToResetExperience(view, position);
        } else {
            startWordSetActivity(wordSet);
        }
    }

    private void startWordSetActivity(WordSet wordSet) {
        Intent intent = new Intent(getActivity(), PracticeWordSetActivity.class);
        intent.putExtra(PracticeWordSetActivity.WORD_SET_MAPPING, wordSet);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
        askToResetExperience(view, position);
        return true;
    }

    private void askToResetExperience(final View view, final int position) {
        new AlertDialog.Builder(getActivity())
                .setMessage("Do you want to reset your progress?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        WordSet wordSet = adapter.getItem(position);
                        exerciseRepository.cleanByWordSetId(wordSet.getId());
                        WordSetExperience experience = experienceRepository.createNew(wordSet);
                        ProgressBar wordSetProgress = view.findViewById(R.id.wordSetProgress);
                        wordSetProgress.setProgress(experience.getTrainingExperience());
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }
}

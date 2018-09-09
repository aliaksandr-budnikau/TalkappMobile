package talkapp.org.talkappmobile.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;
import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.adapter.AdaptersFactory;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;
import talkapp.org.talkappmobile.service.AuthSign;
import talkapp.org.talkappmobile.service.WordSetExperienceService;
import talkapp.org.talkappmobile.service.WordSetService;

public class AllWordSetsFragment extends Fragment implements AdapterView.OnItemClickListener {
    public static final String TOPIC_ID_MAPPING = "topicId";
    @Inject
    WordSetService wordSetService;
    @Inject
    WordSetExperienceService wordSetExperienceService;
    @Inject
    AdaptersFactory adaptersFactory;
    @Inject
    AuthSign authSign;
    private ListView wordSetsListView;
    private ArrayAdapter<WordSet> adapter;

    private AsyncTask<String, Object, List<WordSet>> loadingWordSets = new AsyncTask<String, Object, List<WordSet>>() {
        @Override
        protected List<WordSet> doInBackground(String... params) {
            String topicId = (String) AllWordSetsFragment.this.getArguments().get(TOPIC_ID_MAPPING);

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
                e.printStackTrace();
            }

            Call<List<WordSetExperience>> wordSetExperienceCall;
            if (topicId == null) {
                wordSetExperienceCall = wordSetExperienceService.findAll(authSign);
            } else {
                wordSetExperienceCall = wordSetExperienceService.findByTopicId(topicId, authSign);
            }
            Response<List<WordSetExperience>> wordSetExperiences = null;
            try {
                wordSetExperiences = wordSetExperienceCall.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            HashMap<String, WordSetExperience> experienceMap = new HashMap<>();
            for (WordSetExperience experience : wordSetExperiences.body()) {
                experienceMap.put(experience.getWordSetId(), experience);
            }
            List<WordSet> body = wordSets.body();
            for (WordSet set : body) {
                WordSetExperience experience = experienceMap.get(set.getId());
                set.setExperience(experience);
            }

            return body;
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

        loadingWordSets.execute();
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WordSet wordSet = adapter.getItem(position);
        Intent intent = new Intent(getActivity(), PracticeWordSetActivity.class);
        intent.putExtra(PracticeWordSetActivity.WORD_SET_MAPPING, wordSet);
        startActivity(intent);
    }
}

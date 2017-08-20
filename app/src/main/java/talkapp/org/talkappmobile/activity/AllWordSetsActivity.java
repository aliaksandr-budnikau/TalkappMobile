package talkapp.org.talkappmobile.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.commons.lang3.StringUtils;

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
import talkapp.org.talkappmobile.service.SaveSharedPreference;
import talkapp.org.talkappmobile.service.WordSetExperienceService;
import talkapp.org.talkappmobile.service.WordSetService;

public class AllWordSetsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    @Inject
    WordSetService wordSetService;
    @Inject
    WordSetExperienceService wordSetExperienceService;
    @Inject
    AdaptersFactory adaptersFactory;
    @Inject
    AuthSign authSign;
    @Inject
    SaveSharedPreference saveSharedPreference;
    private ListView wordSetsListView;
    private ArrayAdapter<WordSet> adapter;

    private AsyncTask<String, Object, List<WordSet>> loadingWordSets = new AsyncTask<String, Object, List<WordSet>>() {
        @Override
        protected List<WordSet> doInBackground(String... params) {
            Call<List<WordSet>> wordSetCall = wordSetService.findAll(authSign);
            Call<List<WordSetExperience>> wordSetExperienceCall =
                    wordSetExperienceService.findAll(authSign);
            Response<List<WordSet>> wordSets = null;
            try {
                wordSets = wordSetCall.execute();
            } catch (IOException e) {
                e.printStackTrace();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_word_sets);
        DIContext.get().inject(this);

        String headerKey = saveSharedPreference.getAuthorizationHeaderKey(AllWordSetsActivity.this);
        if (StringUtils.isEmpty(headerKey)) {
            Intent intent = new Intent(AllWordSetsActivity.this, LoginActivity.class);
            finish();
            startActivity(intent);
            return;
        } else {
            authSign.put(headerKey);
        }

        adapter = adaptersFactory.createWordSetListAdapter(this);

        wordSetsListView = (ListView) findViewById(R.id.wordSetsListView);
        wordSetsListView.setAdapter(adapter);
        wordSetsListView.setOnItemClickListener(this);

        loadingWordSets.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WordSet wordSet = adapter.getItem(position);
        Intent intent = new Intent(AllWordSetsActivity.this, PracticeWordSetActivity.class);
        intent.putExtra(PracticeWordSetActivity.WORD_SET_MAPPING, wordSet);
        finish();
        startActivity(intent);
    }
}

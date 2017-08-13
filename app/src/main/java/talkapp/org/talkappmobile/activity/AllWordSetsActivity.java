package talkapp.org.talkappmobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;
import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.adapter.AdaptersFactory;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.AuthSign;
import talkapp.org.talkappmobile.service.WordSetService;

public class AllWordSetsActivity extends Activity implements AdapterView.OnItemClickListener {
    @Inject
    WordSetService wordSetService;
    @Inject
    AdaptersFactory adaptersFactory;
    @Inject
    AuthSign authSign;
    private ListView wordSetsListView;
    private ArrayAdapter<WordSet> adapter;

    private AsyncTask<String, Object, List<WordSet>> loadingWordSets = new AsyncTask<String, Object, List<WordSet>>() {
        @Override
        protected List<WordSet> doInBackground(String... params) {
            Call<List<WordSet>> call = wordSetService.findAll(authSign);
            Response<List<WordSet>> execute = null;
            try {
                execute = call.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return execute.body();
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
        startActivity(intent);
    }
}

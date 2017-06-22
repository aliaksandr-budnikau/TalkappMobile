package talkapp.org.talkappmobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.adapter.AdaptersFactory;
import talkapp.org.talkappmobile.activity.async.GettingAllWordSetsAsyncTask;
import talkapp.org.talkappmobile.activity.async.GettingAllWordSetsAsyncTask.OnAllWordSetsLoadingListener;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.WordSetService;

public class AllWordSetsActivity extends Activity implements OnAllWordSetsLoadingListener {
    @Inject
    WordSetService wordSetService;
    @Inject
    AdaptersFactory adaptersFactory;
    private ListView wordSetsListView;
    private ArrayAdapter<WordSet> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_sets_list);
        DIContext.get().inject(this);

        initWordSetsAdapter();
        initWordSetsListView();
    }

    private void initWordSetsAdapter() {
        adapter = adaptersFactory.createWordSetListAdapter(this);
    }

    private void initWordSetsListView() {
        new GettingAllWordSetsAsyncTask(this).execute();
        wordSetsListView = (ListView) findViewById(R.id.wordSetsListView);
        wordSetsListView.setAdapter(adapter);
        wordSetsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WordSet wordSet = adapter.getItem(position);
                Intent intent = new Intent(AllWordSetsActivity.this, ExerciseActivity.class);
                intent.putExtra(ExerciseActivity.WORD_SET_MAPPING, wordSet);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onAllWordSetsLoaded(List<WordSet> wordSets) {
        adapter.addAll(wordSets);
    }
}

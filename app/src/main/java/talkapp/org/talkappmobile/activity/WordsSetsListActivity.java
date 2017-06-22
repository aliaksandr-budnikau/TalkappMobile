package talkapp.org.talkappmobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.adapter.AdaptersFactory;
import talkapp.org.talkappmobile.activity.adapter.GetWordSetListAsyncTask;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.WordSetService;

public class WordsSetsListActivity extends Activity {
    @Inject
    WordSetService wordSetService;
    @Inject
    AdaptersFactory adaptersFactory;
    @Inject
    GetWordSetListAsyncTask getWordSetListAsyncTask;
    private ListView exercisesList;
    private ArrayAdapter<WordSet> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_sets_list);
        DIContext.get().inject(this);
        initExercisesList();
    }

    private void initExercisesList() {
        exercisesList = (ListView) findViewById(R.id.exercisesList);
        adapter = adaptersFactory.createWordSetListAdapter(this);
        exercisesList.setAdapter(adapter);
        try {
            adapter.addAll(getWordSetListAsyncTask.execute().get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        exercisesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WordSet wordSet = adapter.getItem(position);

                Intent intent = new Intent(WordsSetsListActivity.this, ExerciseActivity.class);
                intent.putExtra(ExerciseActivity.WORD_SET_MAPPING, wordSet);
                startActivity(intent);
            }
        });
    }
}

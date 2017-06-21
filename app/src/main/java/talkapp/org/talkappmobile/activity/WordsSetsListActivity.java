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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.WordSetService;

public class WordsSetsListActivity extends Activity {
    @Inject
    WordSetService wordSetService;
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
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        exercisesList.setAdapter(adapter);
        wordSetService.findAll().enqueue(new Callback<List<WordSet>>() {
            @Override
            public void onResponse(Call<List<WordSet>> call, final Response<List<WordSet>> response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.addAll(response.body());
                    }
                });
            }


            @Override
            public void onFailure(Call<List<WordSet>> call, Throwable t) {

            }
        });
        exercisesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WordSet wordSet = adapter.getItem(position);

                Intent intent = new Intent(WordsSetsListActivity.this, ExerciseActivity.class);
                intent.putExtra("wordSet", wordSet);
                startActivity(intent);
            }
        });
    }
}

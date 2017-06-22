package talkapp.org.talkappmobile.activity.adapter;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.WordSetService;

/**
 * @author Budnikau Aliaksandr
 */
public class GetWordSetListAsyncTask extends AsyncTask<Object, Object, List<WordSet>> {
    private final WordSetService wordSetService;

    public GetWordSetListAsyncTask(WordSetService wordSetService) {
        this.wordSetService = wordSetService;
    }

    @Override
    protected List<WordSet> doInBackground(Object... params) {
        Call<List<WordSet>> call = wordSetService.findAll();
        Response<List<WordSet>> execute = null;
        try {
            execute = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return execute.body();
    }
}

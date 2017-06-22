package talkapp.org.talkappmobile.activity.async;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.WordSetService;

/**
 * @author Budnikau Aliaksandr
 */
public class GettingAllWordSetsAsyncTask extends AsyncTask<Object, Object, List<WordSet>> {
    private final OnAllWordSetsLoadingListener target;
    @Inject
    WordSetService wordSetService;

    public GettingAllWordSetsAsyncTask(OnAllWordSetsLoadingListener target) {
        DIContext.get().inject(this);
        this.target = target;
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

    @Override
    protected void onPostExecute(List<WordSet> wordSets) {
        target.onAllWordSetsLoaded(wordSets);
    }

    public interface OnAllWordSetsLoadingListener {
        void onAllWordSetsLoaded(List<WordSet> wordSets);
    }
}

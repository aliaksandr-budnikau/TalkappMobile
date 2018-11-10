package talkapp.org.talkappmobile.component.backend;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Query;
import talkapp.org.talkappmobile.model.WordSet;

/**
 * @author Budnikau Aliaksandr
 */
public interface WordSetRestClient {

    String WORD_SET_PATH = "/wordset";

    @GET(WORD_SET_PATH)
    Call<List<WordSet>> findAll(@HeaderMap Map<String, String> headers);

    @GET(WORD_SET_PATH)
    Call<List<WordSet>> findByTopicId(@Query("topicId") int topicId, @HeaderMap Map<String, String> headers);
}

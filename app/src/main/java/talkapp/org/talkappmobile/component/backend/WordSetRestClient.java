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

    @GET("/wordset")
    Call<List<WordSet>> findAll(@HeaderMap Map<String, String> headers);

    @GET("/wordset")
    Call<List<WordSet>> findByTopicId(@Query("topicId") String topicId, @HeaderMap Map<String, String> headers);
}

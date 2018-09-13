package talkapp.org.talkappmobile.service.backend;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Query;
import talkapp.org.talkappmobile.model.Sentence;

/**
 * @author Budnikau Aliaksandr
 */
public interface SentenceService {

    @GET("/sentence")
    Call<List<Sentence>> findByWords(@Query("words") String words, @Query("wordsNumber") int wordsNumber, @HeaderMap Map<String, String> headers);
}

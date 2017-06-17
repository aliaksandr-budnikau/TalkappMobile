package talkapp.org.talkappmobile.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import talkapp.org.talkappmobile.model.WordSet;

/**
 * @author Budnikau Aliaksandr
 */
public interface WordSetService {

    @GET("/wordset/{id}")
    Call<WordSet> findById(@Path("id") String id);
}

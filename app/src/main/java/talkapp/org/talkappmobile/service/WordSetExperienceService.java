package talkapp.org.talkappmobile.service;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Path;
import talkapp.org.talkappmobile.model.WordSetExperience;

/**
 * @author Budnikau Aliaksandr
 */
public interface WordSetExperienceService {

    @GET("/wordsetExperience/{id}")
    Call<WordSetExperience> findById(@Path("id") String id, @HeaderMap Map<String, String> headers);

    @GET("/wordsetExperience")
    Call<List<WordSetExperience>> findAll(@HeaderMap Map<String, String> headers);
}
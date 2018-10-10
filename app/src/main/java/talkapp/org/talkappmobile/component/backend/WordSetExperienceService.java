package talkapp.org.talkappmobile.component.backend;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Query;
import talkapp.org.talkappmobile.model.WordSetExperience;

/**
 * @author Budnikau Aliaksandr
 */
public interface WordSetExperienceService {
    @GET("/wordSetExperience")
    Call<List<WordSetExperience>> findAll(@HeaderMap Map<String, String> headers);

    @GET("/wordSetExperience")
    Call<List<WordSetExperience>> findByTopicId(@Query("topicId") String topicId, @HeaderMap Map<String, String> headers);
}
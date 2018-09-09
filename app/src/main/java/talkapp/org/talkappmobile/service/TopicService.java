package talkapp.org.talkappmobile.service;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Path;
import talkapp.org.talkappmobile.model.Topic;

/**
 * @author Budnikau Aliaksandr
 */
public interface TopicService {

    @GET("/topic/{id}")
    Call<Topic> findById(@Path("id") String id, @HeaderMap Map<String, String> headers);

    @GET("/topic")
    Call<List<Topic>> findAll(@HeaderMap Map<String, String> headers);
}

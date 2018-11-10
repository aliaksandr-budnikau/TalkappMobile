package talkapp.org.talkappmobile.component.backend;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import talkapp.org.talkappmobile.model.Topic;

/**
 * @author Budnikau Aliaksandr
 */
public interface TopicRestClient {

    String TOPIC_PATH = "/topic";

    @GET(TOPIC_PATH)
    Call<List<Topic>> findAll(@HeaderMap Map<String, String> headers);
}

package talkapp.org.talkappmobile.component.backend;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import talkapp.org.talkappmobile.model.Sentence;

/**
 * @author Budnikau Aliaksandr
 */
public interface GitHubSentenceRestClient {

    String SENTENCES_PATH = "/aliaksandr-budnikau/talkapp-data/master/sentences/l{wordsNumber}/ws{wordSetId}.json";

    @GET(SENTENCES_PATH)
    Call<Map<String, List<Sentence>>> findByWordSetId(@Path("wordSetId") int wordSetId, @Path("wordsNumber") int wordsNumber);
}
package talkapp.org.talkappmobile.component.backend;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import talkapp.org.talkappmobile.model.Sentence;

/**
 * @author Budnikau Aliaksandr
 */
public interface SentenceRestClient {

    String SENTENCE_PATH = "/sentence";
    String SENTENCE_SCORING_PATH = SENTENCE_PATH + "/score";

    @POST(SENTENCE_SCORING_PATH)
    Call<Boolean> saveSentenceScore(@Body Sentence sentence);
}

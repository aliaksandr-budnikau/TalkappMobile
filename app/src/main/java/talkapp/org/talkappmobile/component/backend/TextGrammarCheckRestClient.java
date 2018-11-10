package talkapp.org.talkappmobile.component.backend;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import talkapp.org.talkappmobile.model.GrammarError;

/**
 * @author Budnikau Aliaksandr
 */
public interface TextGrammarCheckRestClient {

    String GRAMMAR_CHECK_PATH = "/grammar/check";

    @POST(GRAMMAR_CHECK_PATH)
    @Headers("Content-Type: text/plain")
    Call<List<GrammarError>> check(@Body String text, @HeaderMap Map<String, String> headers);
}
package talkapp.org.talkappmobile.service;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import talkapp.org.talkappmobile.model.AnswerCheckingResult;
import talkapp.org.talkappmobile.model.UncheckedAnswer;

/**
 * @author Budnikau Aliaksandr
 */
public interface RefereeService {

    @POST("/referee/checkAnswer")
    Call<AnswerCheckingResult> checkAnswer(@Body UncheckedAnswer uncheckedAnswer, @HeaderMap Map<String, String> headers);
}

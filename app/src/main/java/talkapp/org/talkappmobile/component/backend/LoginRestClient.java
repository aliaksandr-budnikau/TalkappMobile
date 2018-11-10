package talkapp.org.talkappmobile.component.backend;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import talkapp.org.talkappmobile.model.LoginCredentials;

/**
 * @author Budnikau Aliaksandr
 */
public interface LoginRestClient {

    String LOGIN_PATH = "/login";

    @POST(LOGIN_PATH)
    Call<Boolean> login(@Body LoginCredentials credentials);
}

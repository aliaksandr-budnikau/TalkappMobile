package talkapp.org.talkappmobile.component.backend;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import talkapp.org.talkappmobile.model.LoginCredentials;

/**
 * @author Budnikau Aliaksandr
 */
public interface LoginRestClient {

    @POST("/login")
    Call<Boolean> login(@Body LoginCredentials credentials);
}

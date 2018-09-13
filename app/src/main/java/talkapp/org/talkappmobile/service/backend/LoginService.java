package talkapp.org.talkappmobile.service.backend;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import talkapp.org.talkappmobile.model.LoginCredentials;

/**
 * @author Budnikau Aliaksandr
 */
public interface LoginService {

    @POST("/login")
    Call<Boolean> login(@Body LoginCredentials credentials);
}

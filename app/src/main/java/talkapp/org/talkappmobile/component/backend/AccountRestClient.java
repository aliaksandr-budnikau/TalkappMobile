package talkapp.org.talkappmobile.component.backend;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import talkapp.org.talkappmobile.model.Account;

/**
 * @author Budnikau Aliaksandr
 */
public interface AccountRestClient {

    @POST("/account")
    Call<Void> register(@Body Account account);
}
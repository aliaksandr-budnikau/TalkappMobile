package talkapp.org.talkappmobile.service;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import talkapp.org.talkappmobile.model.Account;

/**
 * @author Budnikau Aliaksandr
 */
public interface AccountService {

    @POST("/account")
    Call<Boolean> register(@Body Account account);
}
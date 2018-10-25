package talkapp.org.talkappmobile.activity.presenter;

import android.util.Log;

import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.component.backend.BackendServer;
import talkapp.org.talkappmobile.component.backend.impl.LoginException;
import talkapp.org.talkappmobile.component.backend.impl.RegistrationException;
import talkapp.org.talkappmobile.model.Account;
import talkapp.org.talkappmobile.model.LoginCredentials;

public class LoginInteractor {
    public static final String TAG = LoginInteractor.class.getSimpleName();
    private final BackendServer server;
    private final Logger logger;
    private final TextUtils textUtils;

    public LoginInteractor(Logger logger, BackendServer server, TextUtils textUtils) {
        this.logger = logger;
        this.server = server;
        this.textUtils = textUtils;
    }

    public void signInButtonClick(String email, String password, OnLoginListener listener) {
        logger.i(TAG, "Attempt " + email + " " + textUtils.hideText(password) + " authentication against a network service");
        LoginCredentials credentials = new LoginCredentials();
        credentials.setEmail(email);
        credentials.setPassword(password);
        try {
            listener.onStartLoginProcess();
            server.loginUser(credentials);
        } catch (LoginException e) {
            logger.e(TAG, e, "Login failed");
            listener.onLoginFailed();
            return;
        } finally {
            listener.onStopLoginProcess();
        }
        logger.i(TAG, "Login " + email + " is done!");
        listener.onLoginSucceed();
    }

    public void signUpButtonClick(String email, String password, OnLoginListener listener) {
        logger.i(TAG, "Attempt " + email + " " + textUtils.hideText(password) + " registration against a network service");
        Account account = new Account();
        account.setEmail(email);
        account.setPassword(password);
        try {
            listener.onStartRegistrationProcess();
            server.registerAccount(account);
        } catch (RegistrationException e) {
            logger.e(TAG, e, "Account " + email + " already exists");
            listener.onRegistrationFailed();
            return;
        } finally {
            listener.onStopRegistrationProcess();
        }
        Log.i(TAG, "Registration " + email + " is done!");
        listener.onRegistrationSucceed(email, password);
    }
}
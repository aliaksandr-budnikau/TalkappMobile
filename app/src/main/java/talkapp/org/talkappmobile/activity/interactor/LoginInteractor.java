package talkapp.org.talkappmobile.activity.interactor;

import android.util.Log;

import talkapp.org.talkappmobile.activity.listener.OnLoginListener;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.component.backend.BackendServer;
import talkapp.org.talkappmobile.component.backend.impl.LoginException;
import talkapp.org.talkappmobile.component.backend.impl.RegistrationException;
import talkapp.org.talkappmobile.model.Account;
import talkapp.org.talkappmobile.model.LoginCredentials;

import static org.apache.commons.lang3.StringUtils.isEmpty;

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
        if (!isValidCredentials(email, password, listener)) {
            return;
        }
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
        if (!isValidCredentials(email, password, listener)) {
            return;
        }
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

    private boolean isValidCredentials(String email, String password, OnLoginListener listener) {
        listener.onBeforeValidation();
        if (!isPasswordValid(password)) {
            listener.onPasswordValidationFail();
            return false;
        }
        if (isEmpty(email)) {
            listener.onEmailEmpty();
            return false;
        } else if (!isEmailValid(email)) {
            listener.onEmailValidationFail();
            return false;
        }
        return true;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
}
package talkapp.org.talkappmobile.activity.presenter;

public interface OnLoginListener {
    void onLoginFailed();

    void onLoginSucceed();

    void onStartLoginProcess();

    void onStopLoginProcess();

    void onStartRegistrationProcess();

    void onRegistrationFailed();

    void onStopRegistrationProcess();

    void onRegistrationSucceed(String email, String password);
}

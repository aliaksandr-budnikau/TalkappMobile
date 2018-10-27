package talkapp.org.talkappmobile.activity.listener;

public interface OnLoginListener {
    void onLoginFailed();

    void onLoginSucceed();

    void onStartLoginProcess();

    void onStopLoginProcess();

    void onStartRegistrationProcess();

    void onRegistrationFailed();

    void onStopRegistrationProcess();

    void onRegistrationSucceed(String email, String password);

    void onBeforeValidation();

    void onPasswordValidationFail();

    void onEmailEmpty();

    void onEmailValidationFail();
}

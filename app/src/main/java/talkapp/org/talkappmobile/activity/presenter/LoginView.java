package talkapp.org.talkappmobile.activity.presenter;

public interface LoginView {

    void requestPasswordFocus();

    void finishLoginActivity();

    void openMainActivity();

    void hideProgress();

    void showProgress();

    void setEmailError(String text);

    void setPasswordError(String text);

    void requestEmailFocus();
}
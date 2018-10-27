package talkapp.org.talkappmobile.activity.view;

public interface LoginActivityView {

    void requestPasswordFocus();

    void finishLoginActivity();

    void openMainActivity();

    void hideProgress();

    void showProgress();

    void setEmailError(String text);

    void setPasswordError(String text);

    void requestEmailFocus();

    void signInButtonClick();
}
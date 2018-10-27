package talkapp.org.talkappmobile.activity.presenter;

import android.content.Context;

import talkapp.org.talkappmobile.R;

public class LoginPresenter implements OnLoginListener {
    private final LoginView view;
    private final LoginInteractor interactor;
    private final Context context;

    public LoginPresenter(Context context, LoginView view, LoginInteractor interactor) {
        this.context = context;
        this.view = view;
        this.interactor = interactor;
    }

    public void signInButtonClick(String email, String password) {
        interactor.signInButtonClick(email, password, this);
    }

    public void signUpButtonClick(String email, String password) {
        interactor.signUpButtonClick(email, password, this);
    }

    @Override
    public void onLoginFailed() {
        view.setEmailError(context.getString(R.string.error_incorrect_password));
        view.requestPasswordFocus();
    }

    @Override
    public void onLoginSucceed() {
        view.finishLoginActivity();
        view.openMainActivity();
    }

    @Override
    public void onStartLoginProcess() {
        view.showProgress();
    }

    @Override
    public void onStopLoginProcess() {
        view.hideProgress();
    }

    @Override
    public void onStartRegistrationProcess() {
        view.showProgress();
    }

    @Override
    public void onRegistrationFailed() {
        view.setEmailError("Already exists");
        view.requestEmailFocus();
    }

    @Override
    public void onStopRegistrationProcess() {
        view.hideProgress();
    }

    @Override
    public void onRegistrationSucceed(String email, String password) {
        view.signInButtonClick();
    }

    @Override
    public void onBeforeValidation() {
        view.setEmailError(null);
        view.setPasswordError(null);
    }

    @Override
    public void onPasswordValidationFail() {
        view.setPasswordError(context.getString(R.string.error_invalid_password));
        view.requestPasswordFocus();
    }

    @Override
    public void onEmailEmpty() {
        view.setEmailError(context.getString(R.string.error_field_required));
        view.requestEmailFocus();
    }

    @Override
    public void onEmailValidationFail() {
        view.setEmailError(context.getString(R.string.error_invalid_email));
        view.requestEmailFocus();
    }
}
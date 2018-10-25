package talkapp.org.talkappmobile.activity.presenter;

public class LoginPresenter implements OnLoginListener {
    private final LoginView view;
    private final LoginInteractor interactor;

    public LoginPresenter(LoginView view, LoginInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }
}
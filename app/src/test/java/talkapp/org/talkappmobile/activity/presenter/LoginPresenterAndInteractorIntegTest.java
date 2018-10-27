package talkapp.org.talkappmobile.activity.presenter;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import java.util.Random;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.component.backend.BackendServer;
import talkapp.org.talkappmobile.component.backend.impl.LoginException;
import talkapp.org.talkappmobile.model.LoginCredentials;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterAndInteractorIntegTest {
    @Mock
    private LoginView view;
    private LoginInteractor interactor;
    @Mock
    private Context context;
    private LoginPresenter presenter;

    @Before
    public void setup() {
        final ClassForInjection injection = new ClassForInjection();
        interactor = injection.getLoginInteractor();
    }

    @Test
    public void signInButtonClick_correctCredentials() {
        // setup
        presenter = new LoginPresenter(context, view, interactor);

        String email = "sasha-ne@tut.by";
        String password = "password0";

        // when
        presenter.signInButtonClick(email, password);

        // then
        verify(view, times(0)).setEmailError(anyString());
        verify(view, times(0)).requestEmailFocus();
        verify(view, times(0)).setPasswordError(anyString());
        verify(view, times(0)).requestPasswordFocus();
        verify(view, times(0)).setEmailError(anyString());
        verify(view, times(0)).requestEmailFocus();
        verify(view).setEmailError(null);
        verify(view).setPasswordError(null);
        verify(view).showProgress();
        verify(view).hideProgress();
        verify(view).finishLoginActivity();
        verify(view).openMainActivity();
    }

    @Test
    public void signUpButtonClick_correctCredentials() {
        // setup
        presenter = new LoginPresenter(context, view, interactor);

        Random random = new Random();
        int i = random.nextInt();
        String email = "sasha-ne@tut.by" + i;
        String password = "password" + i;

        // when
        presenter.signUpButtonClick(email, password);

        // then
        verify(view, times(0)).setPasswordError(anyString());
        verify(view, times(0)).requestPasswordFocus();
        verify(view, times(0)).setEmailError(anyString());
        verify(view, times(0)).requestEmailFocus();

        verify(view).setEmailError(null);
        verify(view).setPasswordError(null);
        verify(view).showProgress();
        verify(view).hideProgress();
        verify(view).signInButtonClick();
    }

    @Test
    public void signUpButtonClick_correctCredentialsButAlreadyExists() {
        // setup
        presenter = new LoginPresenter(context, view, interactor);

        String email = "sasha-ne@tut.by";
        String password = "password0";
        String message = "Already exists";

        // when
        presenter.signUpButtonClick(email, password);

        // then
        verify(view, times(0)).setPasswordError(anyString());
        verify(view, times(0)).requestPasswordFocus();

        verify(view).setEmailError(null);
        verify(view).setPasswordError(null);
        verify(view).showProgress();
        verify(view).hideProgress();
        verify(view).setEmailError(message);
        verify(view).requestEmailFocus();
    }

    @Test
    public void signInButtonClick_correctCredentialsButException() throws LoginException {
        // setup
        BackendServer server = mock(BackendServer.class);
        Whitebox.setInternalState(interactor, "server", server);
        presenter = new LoginPresenter(context, view, interactor);

        int error_incorrect_password = R.string.error_incorrect_password;
        String email = "sasha-ne@tut.by";
        String password = "password0";
        String message = "error_incorrect_password";

        // when
        when(context.getString(error_incorrect_password)).thenReturn(message);
        when(server.loginUser(any(LoginCredentials.class))).thenThrow(LoginException.class);
        presenter.signInButtonClick(email, password);

        // then
        verify(view).setEmailError(null);
        verify(view).setPasswordError(null);
        verify(view).showProgress();
        verify(view).hideProgress();
        verify(view).setEmailError(message);
        verify(view).requestPasswordFocus();

        verify(view, times(0)).requestEmailFocus();
        verify(view, times(0)).setPasswordError(anyString());
        verify(view, times(0)).requestEmailFocus();
        verify(view, times(0)).finishLoginActivity();
        verify(view, times(0)).openMainActivity();
    }

    @Test
    public void signInButtonClick_emptyEmail() {
        //setup
        presenter = new LoginPresenter(context, view, interactor);

        int error_field_required = R.string.error_field_required;
        String email = "";
        String password = "password0";
        String message = "error_field_required";

        // when
        when(context.getString(error_field_required)).thenReturn(message);
        presenter.signInButtonClick(email, password);

        // then
        verify(view).setEmailError(null);
        verify(view).setPasswordError(null);
        verify(view).setEmailError(message);
        verify(view).requestEmailFocus();
        verify(view, times(0)).setPasswordError(message);
        verify(view, times(0)).requestPasswordFocus();
        verify(view, times(0)).showProgress();
        verify(view, times(0)).hideProgress();
        verify(view, times(0)).finishLoginActivity();
        verify(view, times(0)).openMainActivity();
    }

    @Test
    public void signInButtonClick_nullEmail() {
        //setup
        presenter = new LoginPresenter(context, view, interactor);

        int error_field_required = R.string.error_field_required;
        String email = null;
        String password = "password0";
        String message = "error_field_required";

        // when
        when(context.getString(error_field_required)).thenReturn(message);
        presenter.signInButtonClick(email, password);

        // then
        verify(view).setEmailError(null);
        verify(view).setPasswordError(null);
        verify(view).setEmailError(message);
        verify(view).requestEmailFocus();
        verify(view, times(0)).setPasswordError(message);
        verify(view, times(0)).requestPasswordFocus();
        verify(view, times(0)).showProgress();
        verify(view, times(0)).hideProgress();
        verify(view, times(0)).finishLoginActivity();
        verify(view, times(0)).openMainActivity();
    }

    @Test
    public void signInButtonClick_notEmail() {
        //setup
        presenter = new LoginPresenter(context, view, interactor);

        int error_invalid_email = R.string.error_invalid_email;
        String email = "sasha-netut.by";
        String password = "password0";
        String message = "error_invalid_email";

        // when
        when(context.getString(error_invalid_email)).thenReturn(message);
        presenter.signInButtonClick(email, password);

        // then
        verify(view).setEmailError(null);
        verify(view).setPasswordError(null);
        verify(view).setEmailError(message);
        verify(view).requestEmailFocus();
        verify(view, times(0)).setPasswordError(message);
        verify(view, times(0)).requestPasswordFocus();
        verify(view, times(0)).showProgress();
        verify(view, times(0)).hideProgress();
        verify(view, times(0)).finishLoginActivity();
        verify(view, times(0)).openMainActivity();
    }

    @Test
    public void signInButtonClick_badPassword() {
        //setup
        presenter = new LoginPresenter(context, view, interactor);

        int error_invalid_password = R.string.error_invalid_password;
        String email = "sasha-ne@tut.by";
        String password = "pas";
        String message = "error_invalid_email";

        // when
        when(context.getString(error_invalid_password)).thenReturn(message);
        presenter.signInButtonClick(email, password);

        // then
        verify(view).setEmailError(null);
        verify(view).setPasswordError(null);
        verify(view).setPasswordError(message);
        verify(view).requestPasswordFocus();
        verify(view, times(0)).setEmailError(message);
        verify(view, times(0)).requestEmailFocus();
        verify(view, times(0)).showProgress();
        verify(view, times(0)).hideProgress();
        verify(view, times(0)).finishLoginActivity();
        verify(view, times(0)).openMainActivity();
    }

    @Test
    public void signUpButtonClick_emptyEmail() {
        //setup
        presenter = new LoginPresenter(context, view, interactor);

        int error_field_required = R.string.error_field_required;
        String email = "";
        String password = "password0";
        String message = "error_field_required";

        // when
        when(context.getString(error_field_required)).thenReturn(message);
        presenter.signUpButtonClick(email, password);

        // then
        verify(view).setEmailError(null);
        verify(view).setPasswordError(null);
        verify(view).setEmailError(message);
        verify(view).requestEmailFocus();
        verify(view, times(0)).setPasswordError(message);
        verify(view, times(0)).requestPasswordFocus();
        verify(view, times(0)).showProgress();
        verify(view, times(0)).hideProgress();
        verify(view, times(0)).finishLoginActivity();
        verify(view, times(0)).openMainActivity();
    }

    @Test
    public void signUpButtonClick_nullEmail() {
        //setup
        presenter = new LoginPresenter(context, view, interactor);

        int error_field_required = R.string.error_field_required;
        String email = null;
        String password = "password0";
        String message = "error_field_required";

        // when
        when(context.getString(error_field_required)).thenReturn(message);
        presenter.signUpButtonClick(email, password);

        // then
        verify(view).setEmailError(null);
        verify(view).setPasswordError(null);
        verify(view).setEmailError(message);
        verify(view).requestEmailFocus();
        verify(view, times(0)).setPasswordError(message);
        verify(view, times(0)).requestPasswordFocus();
        verify(view, times(0)).showProgress();
        verify(view, times(0)).hideProgress();
        verify(view, times(0)).finishLoginActivity();
        verify(view, times(0)).openMainActivity();
    }

    @Test
    public void signUpButtonClick_notEmail() {
        //setup
        presenter = new LoginPresenter(context, view, interactor);

        int error_invalid_email = R.string.error_invalid_email;
        String email = "sasha-netut.by";
        String password = "password0";
        String message = "error_invalid_email";

        // when
        when(context.getString(error_invalid_email)).thenReturn(message);
        presenter.signUpButtonClick(email, password);

        // then
        verify(view).setEmailError(null);
        verify(view).setPasswordError(null);
        verify(view).setEmailError(message);
        verify(view).requestEmailFocus();
        verify(view, times(0)).setPasswordError(message);
        verify(view, times(0)).requestPasswordFocus();
        verify(view, times(0)).showProgress();
        verify(view, times(0)).hideProgress();
        verify(view, times(0)).finishLoginActivity();
        verify(view, times(0)).openMainActivity();
    }

    @Test
    public void signUpButtonClick_badPassword() {
        //setup
        presenter = new LoginPresenter(context, view, interactor);

        int error_invalid_password = R.string.error_invalid_password;
        String email = "sasha-ne@tut.by";
        String password = "pas";
        String message = "error_invalid_email";

        // when
        when(context.getString(error_invalid_password)).thenReturn(message);
        presenter.signUpButtonClick(email, password);

        // then
        verify(view).setEmailError(null);
        verify(view).setPasswordError(null);
        verify(view).setPasswordError(message);
        verify(view).requestPasswordFocus();
        verify(view, times(0)).setEmailError(message);
        verify(view, times(0)).requestEmailFocus();
        verify(view, times(0)).showProgress();
        verify(view, times(0)).hideProgress();
        verify(view, times(0)).finishLoginActivity();
        verify(view, times(0)).openMainActivity();
    }
}
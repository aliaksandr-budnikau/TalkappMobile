package talkapp.org.talkappmobile.activity;

import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.interactor.LoginInteractor;
import talkapp.org.talkappmobile.activity.presenter.LoginPresenter;
import talkapp.org.talkappmobile.activity.view.LoginActivityView;
import talkapp.org.talkappmobile.component.SaveSharedPreference_;
import talkapp.org.talkappmobile.component.backend.BackendServer;
import talkapp.org.talkappmobile.component.view.WaitingForProgressBarManager;
import talkapp.org.talkappmobile.component.view.WaitingForProgressBarManagerFactory;
import talkapp.org.talkappmobile.config.DIContextUtils;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
@EActivity(R.layout.activity_login)
public class LoginActivity extends BaseActivity implements LoaderCallbacks<Cursor>, LoginActivityView {
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    @Pref
    SaveSharedPreference_ saveSharedPreference;
    @Inject
    BackendServer server;
    @Inject
    LoginInteractor interactor;
    @Inject
    Context context;
    @Bean
    WaitingForProgressBarManagerFactory waitingForProgressBarManagerFactory;

    @ViewById(R.id.email)
    AutoCompleteTextView emailView;
    @ViewById(R.id.password)
    EditText passwordView;
    @ViewById(R.id.login_form)
    View loginFormView;
    @ViewById(R.id.login_progress)
    View progressView;

    private WaitingForProgressBarManager waitingForProgressBarManager;

    // UI references.
    private LoginPresenter presenter;

    @AfterViews
    public void init() {
        DIContextUtils.get().inject(this);
        // Set up the login form.
        populateAutoComplete();

        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    signIn();
                    return true;
                }
                return false;
            }
        });

        waitingForProgressBarManager = waitingForProgressBarManagerFactory.get(progressView, loginFormView);
        presenter = new LoginPresenter(context, this, interactor);
    }

    @Background
    public void signUp() {
        final String email = emailView.getText().toString();
        final String password = passwordView.getText().toString();
        presenter.signUpButtonClick(email, password);
    }

    @Background
    public void signIn() {
        final String email = emailView.getText().toString();
        final String password = passwordView.getText().toString();
        presenter.signInButtonClick(email, password);
    }

    @Click(R.id.email_sign_in_button)
    public void onSignInClick() {
        signIn();
    }

    @Click(R.id.email_sign_up_button)
    public void onSignUpClick() {
        signUp();
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(emailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        emailView.setAdapter(adapter);
    }

    @Override
    @UiThread
    public void requestPasswordFocus() {
        passwordView.requestFocus();
    }

    @Override
    public void finishLoginActivity() {
        finish();
    }

    @Override
    public void openMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity_.class);
        startActivity(intent);
    }

    @Override
    @UiThread
    public void hideProgress() {
        waitingForProgressBarManager.hideProgressBar();
    }

    @Override
    @UiThread
    public void showProgress() {
        waitingForProgressBarManager.showProgressBar();
    }

    @Override
    @UiThread
    public void setEmailError(final String text) {
        emailView.setError(text);
    }

    @Override
    @UiThread
    public void setPasswordError(final String text) {
        passwordView.setError(text);
    }

    @Override
    @UiThread
    public void requestEmailFocus() {
        emailView.requestFocus();
    }

    @Override
    public void signInButtonClick() {
        signIn();
    }

    @Override
    public void saveSignature(String signature) {
        saveSharedPreference.authorizationHeaderKey().put(signature);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
    }
}
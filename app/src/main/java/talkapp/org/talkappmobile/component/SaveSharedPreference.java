package talkapp.org.talkappmobile.component;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

import static org.androidannotations.annotations.sharedpreferences.SharedPref.Scope.APPLICATION_DEFAULT;

/**
 * @author Budnikau Aliaksandr
 */
@SharedPref(APPLICATION_DEFAULT)
public interface SaveSharedPreference {
    String authorizationHeaderKey();
}
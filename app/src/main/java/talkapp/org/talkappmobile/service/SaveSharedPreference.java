package talkapp.org.talkappmobile.service;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Budnikau Aliaksandr
 */
public interface SaveSharedPreference {
    SharedPreferences getSharedPreferences(Context ctx);

    void setAuthorizationHeaderKey(Context ctx, String key);

    String getAuthorizationHeaderKey(Context ctx);
}
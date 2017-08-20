package talkapp.org.talkappmobile.service;

import android.content.Context;

/**
 * @author Budnikau Aliaksandr
 */
public interface SaveSharedPreference {
    void setAuthorizationHeaderKey(Context ctx, String key);

    void clear(Context ctx);

    String getAuthorizationHeaderKey(Context ctx);
}
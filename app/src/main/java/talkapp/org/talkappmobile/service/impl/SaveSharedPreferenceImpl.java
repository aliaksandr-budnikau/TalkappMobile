package talkapp.org.talkappmobile.service.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import talkapp.org.talkappmobile.service.SaveSharedPreference;

import static talkapp.org.talkappmobile.service.AuthSign.AUTHORIZATION_HEADER_KEY;

/**
 * @author Budnikau Aliaksandr
 */
public class SaveSharedPreferenceImpl implements SaveSharedPreference {

    @Override
    public SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    @Override
    public void setAuthorizationHeaderKey(Context ctx, String key) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(AUTHORIZATION_HEADER_KEY, key);
        editor.commit();
    }

    @Override
    public String getAuthorizationHeaderKey(Context ctx) {
        return getSharedPreferences(ctx).getString(AUTHORIZATION_HEADER_KEY, "");
    }
}
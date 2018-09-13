package talkapp.org.talkappmobile.component.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import talkapp.org.talkappmobile.component.SaveSharedPreference;

import static talkapp.org.talkappmobile.component.AuthSign.AUTHORIZATION_HEADER_KEY;

/**
 * @author Budnikau Aliaksandr
 */
public class SaveSharedPreferenceImpl implements SaveSharedPreference {

    private SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    @Override
    public void setAuthorizationHeaderKey(Context ctx, String key) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(AUTHORIZATION_HEADER_KEY, key);
        editor.commit();
    }

    @Override
    public void clear(Context ctx) {
        getSharedPreferences(ctx).edit().clear().commit();
    }

    @Override
    public String getAuthorizationHeaderKey(Context ctx) {
        return getSharedPreferences(ctx).getString(AUTHORIZATION_HEADER_KEY, "");
    }
}
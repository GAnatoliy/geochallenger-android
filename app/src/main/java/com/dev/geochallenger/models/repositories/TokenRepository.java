package com.dev.geochallenger.models.repositories;

import android.content.Context;
import android.content.SharedPreferences;

import com.dev.geochallenger.models.repositories.interfaces.ITokenRepository;

/**
 * Created by Yuriy Diachenko on 23.04.2016.
 */
public class TokenRepository implements ITokenRepository {

    public static final String PREFERENCES_NAME = "token_preferences";
    public static final String TOKEN_PREFERENCE = "token";

    private SharedPreferences preferences;

    /**
     * @param context - use Application context
     */
    public TokenRepository(Context context) {
        this.preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void setToken(String token) {
        preferences.edit().putString(TOKEN_PREFERENCE, token).apply();
    }

    @Override
    public String getToken() {
        return preferences.getString(TOKEN_PREFERENCE, "");
    }

    @Override
    public void clearToken() {
        preferences.edit().remove(TOKEN_PREFERENCE).apply();
    }
}

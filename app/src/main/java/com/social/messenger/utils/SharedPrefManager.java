package com.social.messenger.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.social.messenger.dto.userlogin;
import com.social.messenger.models.User;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "messenger_prefs";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_USER_ID = "user_id";
    private static final String TOKEN = "user_token";


    private static SharedPrefManager instance;
    private final SharedPreferences sharedPreferences;

    private SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context);
        }
        return instance;
    }

    public void saveUser(userlogin user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_USER_ID, user.getId()); // backend cần trả userId
        editor.putString(TOKEN, user.getToken());
        editor.apply();
        Log.d("a", "Token đã được lưu: " + user.getToken());
    }

    public User getUser() {
        String email = sharedPreferences.getString(KEY_EMAIL, null);
        return email != null ? new User(email, null) : null; // Không cần mật khẩu
    }
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }
    public String getToken(){return sharedPreferences.getString(TOKEN, null);}
    public void clear() {
        sharedPreferences.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getString(KEY_EMAIL, null) != null;
    }
}


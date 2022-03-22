package com.app.bahokrider.managers;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private static final String APP_NAME = "deliveryApp";

    private static SharedPreferencesManager ourInstance = null;

    public static synchronized SharedPreferencesManager getInstance() {
        if (ourInstance == null)
            ourInstance = new SharedPreferencesManager();
        return ourInstance;
    }

    private SharedPreferencesManager() {
    }

    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
    }

    public String getSomeStringValue(Context context, String key) {
        return getSharedPreferences(context).getString(key, null);
    }

    public int getSomeIntegerValue(Context context, String key) {
        return getSharedPreferences(context).getInt(key, 0);
    }

    public Long getSomeLongValue(Context context, String key) {
        return getSharedPreferences(context).getLong(key, -1);
    }

    public void setSomeLongValue(Context context, String key, Long newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putLong(key, newValue);
        editor.apply();
    }

    public boolean getSomeBooleanValue(Context context, String key) {
        return getSharedPreferences(context).getBoolean(key, false);
    }

    public void setSomeStringValue(Context context, String key, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key, newValue);
        editor.apply();
    }

    public void setSomeIntegerValue(Context context, String key, Integer newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(key, newValue);
        editor.apply();
    }


    public void setSomeBooleanValue(Context context, String key, boolean newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(key, newValue);
        editor.apply();
    }

    public void clearAll(Context context) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.clear();
        editor.apply();
    }


}

package com.itsvks.jeditor.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.preference.PreferenceManager;

import com.itsvks.jeditor.BaseActivity;
import com.itsvks.jeditor.R;
import com.itsvks.jeditor.databinding.ActivityPreferencesBinding;
import com.itsvks.jeditor.fragments.PreferencesFragment;
import com.itsvks.jeditor.handler.CrashHandler;
import com.itsvks.jeditor.utils.AppUtils;

public class PreferencesActivity extends BaseActivity {

    private ActivityPreferencesBinding binding;

    private AppUtils utils = new AppUtils(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CrashHandler.INSTANCE.init(this);
        binding = ActivityPreferencesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frag, new PreferencesFragment())
                .commit();

        // utils.setupDynamicColors(this);
        utils.setupPreferencesActionBar(this);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    @Override
    public void onBackPressed() {
        utils.showShortToast("Settings saved");
        super.onBackPressed();
    }

    public int getInt(String key, int defaultValue, Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getInt(key, defaultValue);
    }

    public void setInt(String key, int value, Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        prefs.edit().putInt(key, value);
    }

    public String getString(String key, String defaultValue, Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getString(key, defaultValue);
    }

    public void setString(String key, String value, Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        prefs.edit().putString(key, value);
    }

    public boolean getBoolean(String key, boolean defaultValue, Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getBoolean(key, defaultValue);
    }

    public void setBoolean(String key, boolean value, Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        prefs.edit().putBoolean(key, value);
    }
}


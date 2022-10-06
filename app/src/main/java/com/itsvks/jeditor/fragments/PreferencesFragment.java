package com.itsvks.jeditor.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.itsvks.jeditor.R;

public class PreferencesFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setDivider(new ColorDrawable(Color.TRANSPARENT));
        setDividerHeight(0);
    }

    private void bindPreferenceSummaryToValue(Preference preference) {}

    @Override
    public boolean onPreferenceChange(Preference prefs, Object value) {
        var stringValue = value.toString();
        
        if (prefs instanceof ListPreference) {
            var listPreference = (ListPreference) prefs;
            var prefIndex = listPreference.findIndexOfValue(stringValue);
        }
        
        return true;
    }
}


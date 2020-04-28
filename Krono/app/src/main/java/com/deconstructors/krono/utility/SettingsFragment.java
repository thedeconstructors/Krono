package com.deconstructors.krono.utility;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.deconstructors.krono.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}

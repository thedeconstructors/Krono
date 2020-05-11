package com.deconstructors.krono.utility;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.deconstructors.krono.R;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        //Category's
        setUpSignOut();
    }

    private void setUpSignOut() {
        Preference signOutPref = findPreference("sign_out_button");
        assert signOutPref != null; //Check
        signOutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FirebaseAuth.getInstance().signOut();
                return true;
            }
        });
    }

}

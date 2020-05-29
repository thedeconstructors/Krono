package com.deconstructors.krono.utility;

import android.content.Intent;
import android.os.Bundle;


import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.deconstructors.krono.R;
import com.deconstructors.krono.auth.WelcomePage;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends PreferenceFragmentCompat
{
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        setUpSignOut();
    }

    private void setUpSignOut()
    {
        Preference signOutPref = findPreference(getString(R.string.settings_signout));
        assert signOutPref != null; //Check
        signOutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), WelcomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
                return true;
            }
        });
    }
}

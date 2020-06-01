package com.deconstructors.krono.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.deconstructors.krono.R;
import com.deconstructors.krono.auth.WelcomePage;
import com.deconstructors.krono.ui.MainPage;
import com.deconstructors.krono.ui.SettingsPage_Main;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.util.SharedPreferencesUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat
{
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        setLanguage();
        setSignOut();
    }

    private void setLanguage()
    {
        ListPreference languagePref = findPreference(getString(R.string.pref_language_key));
        assert languagePref != null;
        Resources res = getResources();
        Configuration config = res.getConfiguration();

        switch (config.locale.getLanguage().trim())
        {
            case "de":
                languagePref.setValueIndex(1);
                break;
            case "es":
                languagePref.setValueIndex(2);
                break;
            case "fr":
                languagePref.setValueIndex(3);
                break;
            case "zh":
                languagePref.setValueIndex(4);
                break;
            case "ja":
                languagePref.setValueIndex(5);
                break;
            case "ko":
                languagePref.setValueIndex(6);
                break;
            default:
                languagePref.setValueIndex(0);
        }

        languagePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                saveLocale(newValue.toString());
                changeLocale(newValue.toString());

                Intent intent = new Intent(getActivity(), MainPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            }
        });
    }

    private void saveLocale(String language)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String langPref = getString(R.string.pref_language_key);
        SharedPreferences prefs = getActivity().getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, language);
        editor.apply();
    }

    private void changeLocale(String language)
    {
        Locale locale = new Locale(language);
        Resources res = getResources();
        DisplayMetrics metrics = res.getDisplayMetrics();
        Configuration config = res.getConfiguration();

        Locale.setDefault(locale);
        config.locale = locale;
        res.updateConfiguration(config, metrics);
    }

    private void setSignOut()
    {
        Preference signOutPref = findPreference(getString(R.string.pref_signout_key));
        assert signOutPref != null; //Check
        signOutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {

                if (AccessToken.getCurrentAccessToken() != null)
                {
                    LoginManager.getInstance().logOut();
                }

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

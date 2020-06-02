package com.deconstructors.krono.utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat
{
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        setLanguage();
        setSignOut();
        setFeedback();
        setTermsAndConditions();
        setPrivacyPolicy();
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

    private void setFeedback()
    {
        Preference signOutPref = findPreference(getString(R.string.pref_feedback_key));
        assert signOutPref != null; //Check
        signOutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.pref_supemail)});
                intent.putExtra(Intent.EXTRA_SUBJECT, "[Krono] " + getString(R.string.plan_title));

                try
                {
                    startActivity(intent);
                }
                catch (ActivityNotFoundException ex)
                {
                    Toast.makeText(getActivity(),
                                   getString(R.string.error_feedback),
                                   Toast.LENGTH_SHORT)
                         .show();
                }

                return true;
            }
        });
    }

    private void setTermsAndConditions()
    {
        Preference termsPref = findPreference(getString(R.string.pref_terms_key));
        assert termsPref != null; //Check
        termsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                String terms = getTermsString();

                Dialog termsDialog = new Dialog(getContext());
                termsDialog.setContentView(R.layout.dialog_text);

                TextView titleText = termsDialog.findViewById(R.id.dialog_title);
                TextView bodyText = termsDialog.findViewById(R.id.dialog_body);

                titleText.setText(getContext().getString(R.string.terms_title));
                bodyText.setText(terms);
                
                termsDialog.show();

                return true;
            }
        });
    }

    private void setPrivacyPolicy()
    {
        Preference privacyPref = findPreference(getString(R.string.pref_privacy_key));
        assert privacyPref != null; //Check
        privacyPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                String privacy = getPrivacyString();

                Dialog privacyDialog = new Dialog(getContext());
                privacyDialog.setContentView(R.layout.dialog_text);

                TextView titleText = privacyDialog.findViewById(R.id.dialog_title);
                TextView bodyText = privacyDialog.findViewById(R.id.dialog_body);

                titleText.setText(getContext().getString(R.string.privacy_title));
                bodyText.setText(privacy);

                privacyDialog.show();

                return true;
            }
        });
    }

    private String getTermsString() {
        try {
            StringBuilder sb = new StringBuilder();
            InputStream ins = getContext().getAssets().open("terms.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(ins, StandardCharsets.UTF_8 ));
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
                sb.append("\n");
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getPrivacyString() {
        try {
            StringBuilder sb = new StringBuilder();
            InputStream ins = getContext().getAssets().open("privacy.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(ins, StandardCharsets.UTF_8 ));
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
                sb.append("\n");
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

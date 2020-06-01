package com.deconstructors.krono.auth;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.ImageDecoder;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.preference.PreferenceManager;

import com.deconstructors.krono.R;
import com.deconstructors.krono.ui.MainPage;
import com.deconstructors.krono.utility.Helper;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class WelcomePage extends AppCompatActivity implements View.OnClickListener
{
    // Error Log
    private static final String TAG = "WelcomePage";
    private static int LOGIN_ACTIVITY = 7001;

    // Firebase
    private FirebaseAuth AuthInstance;
    private AuthStateListener FirebaseAuthListener;
    private RegisterPage RegisterPage;
    private FirebaseAnalytics Analytics;

    // Background
    private static final int FADE_DURATION = 4000;
    public static final int ERROR_DIALOG_REQUEST = 9001;
    private CoordinatorLayout BackgroundLayout;

    // Layout Widgets
    private LinearLayout WelcomeLayout;
    private LinearLayout LoginLayout;
    private LinearLayout RegisterLayout;
    private TextView BackButton;
    private TextView SkipButton;
    private Button GoogleLogIn;
    private Button FacebookLogIn;
    private ProgressBar ProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.auth_welcome);

        this.loadLocale();
        this.setContents();
        this.startBGAnimation();
        this.setFirebaseAuth();
        //this.Logout();
    }

    private void loadLocale()
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String langPref = getString(R.string.pref_language_key);
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, Locale.getDefault().getLanguage().trim());

        this.changeLocale(language);
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

    /************************************************************************
     * Purpose:         DEBUG_CODE Manually Getting Hash Key For Facebook Dev
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    /*private void getHashKey()
    {
        try
        {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.deconstructors.krono",
                    PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e)
        {

        }
        catch (NoSuchAlgorithmException e)
        {

        }
    }*/

    /************************************************************************
     * Purpose:         DEBUG_CODE Logout
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public void Logout()
    {
        if (this.AuthInstance == null)
        {
            this.AuthInstance = FirebaseAuth.getInstance();
        }

        this.AuthInstance.signOut();
    }

    /************************************************************************
     * Purpose:         Set Contents
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setContents()
    {
        // Firebase
        this.AuthInstance = FirebaseAuth.getInstance();
        this.Analytics = FirebaseAnalytics.getInstance(this);

        // Background & Layout Widgets
        this.BackgroundLayout = findViewById(R.id.auth_welcomeBackground);
        this.WelcomeLayout = findViewById(R.id.auth_welcomeViews);
        this.LoginLayout = findViewById(R.id.auth_signinLayout);
        this.RegisterLayout = findViewById(R.id.auth_registerLayout);
        this.BackButton = findViewById(R.id.auth_back);
        this.SkipButton = findViewById(R.id.auth_anonymousSignIn);
        this.ProgressBar = findViewById(R.id.auth_progressBar);

        // SignIn & Register Pages
        EmailLoginPage EmailLoginPage = new EmailLoginPage(this);
        this.RegisterPage = new RegisterPage(this);
        AnonymousLoginPage AnonymousLoginPage = new AnonymousLoginPage(this);

        // Google Log In
        this.GoogleLogIn = findViewById(R.id.auth_googleLogIn);
        this.GoogleLogIn.setOnClickListener(this);

        this.FacebookLogIn = findViewById(R.id.auth_facebookLogIn);
        this.FacebookLogIn.setOnClickListener(this);
    }

    // For crash reports
    /*private String getCurrentImageTitle()
    {
        int position = mViewPager.getCurrentItem();
        ImageDecoder.ImageInfo info = IMAGE_INFOS[position];
        return getString(info.title);
    }

    private String getCurrentImageId()
    {
        int position = mViewPager.getCurrentItem();
        ImageDecoder.ImageInfo info = IMAGE_INFOS[position];
        return getString(info.id);
    }

    private void recordImageView()
    {
        String id = getCurrentImageId();
        String name = getCurrentImageTitle();

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        this.Analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }*/


    /************************************************************************
     * Purpose:         Background Animation
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void startBGAnimation()
    {
        AnimationDrawable animationDrawable = (AnimationDrawable) this.BackgroundLayout.getBackground();
        animationDrawable.setEnterFadeDuration(FADE_DURATION);
        animationDrawable.setExitFadeDuration(FADE_DURATION);
        animationDrawable.start();
    }

    /************************************************************************
     * Purpose:         Firebase Authentication Session Management
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setFirebaseAuth()
    {
        Log.d(TAG, "setupFirebaseAuth: started.");

        this.FirebaseAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null)
                {
                    Intent intent = new Intent(WelcomePage.this, MainPage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                }
            }
        };
    }

    @Override
    public void onStart()
    {
        super.onStart();
        this.AuthInstance.addAuthStateListener(this.FirebaseAuthListener);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (this.FirebaseAuthListener != null)
        {
            this.AuthInstance.removeAuthStateListener(this.FirebaseAuthListener);
        }
    }

    /************************************************************************
     * Purpose:         Button Click GUI Animation
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.auth_back:
            {
                this.WelcomeLayout.setVisibility(View.VISIBLE);
                this.LoginLayout.setVisibility(View.GONE);
                this.RegisterLayout.setVisibility(View.GONE);
                this.BackButton.setVisibility(View.GONE);
                //this.SkipButton.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.auth_welcome_signIn:
            {
                this.WelcomeLayout.setVisibility(View.GONE);
                this.LoginLayout.setVisibility(View.VISIBLE);
                this.RegisterLayout.setVisibility(View.GONE);
                this.BackButton.setVisibility(View.VISIBLE);
                //this.SkipButton.setVisibility(View.GONE);
                break;
            }
            case R.id.auth_welcome_register:
            {
                this.WelcomeLayout.setVisibility(View.GONE);
                this.LoginLayout.setVisibility(View.GONE);
                this.RegisterLayout.setVisibility(View.VISIBLE);
                this.BackButton.setVisibility(View.VISIBLE);
                //this.SkipButton.setVisibility(View.GONE);
                break;
            }
            // Clicks
            case R.id.auth_googleLogIn:
            {
                if (checkGoogleService())
                {
                    Helper.showProgressBar(this, this.ProgressBar);
                    Intent intent = new Intent(WelcomePage.this, GoogleLoginPage.class);
                    startActivityForResult(intent, LOGIN_ACTIVITY);
                }
                break;
            }
            case R.id.auth_facebookLogIn:
            {
                Helper.showProgressBar(this, this.ProgressBar);
                Intent intent = new Intent(WelcomePage.this, FacebookLoginPage.class);
                startActivityForResult(intent, LOGIN_ACTIVITY);
                break;
            }
        }
    }

    public boolean checkGoogleService()
    {
        Log.d(TAG, "checkGoogleService: checking google services version");

        int available = GoogleApiAvailability
                .getInstance()
                .isGooglePlayServicesAvailable(this);

        if(available == ConnectionResult.SUCCESS)
        {
            Log.d(TAG, "checkGoogleService: SUCCESS");
            return true;
        }
        else if(GoogleApiAvailability
                .getInstance()
                .isUserResolvableError(available))
        {
            Log.d(TAG, "checkGoogleService: user resolvable error");
            Dialog dialog = GoogleApiAvailability
                    .getInstance()
                    .getErrorDialog(this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else
        {
            Log.d(TAG, "checkGoogleService: failed");
            Helper.makeSnackbarMessage(this.BackgroundLayout,
                                       getString(R.string.error_auth_google));
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOGIN_ACTIVITY)
        {
            if(resultCode == RESULT_OK)
            {
                Helper.hideProgressBar(this, this.ProgressBar);
                // => Firebase Auth
            }
            else if (resultCode == RESULT_FIRST_USER)
            {
                FirebaseUser user = this.AuthInstance.getCurrentUser();
                this.RegisterPage.onRegister(user.getUid(), user.getDisplayName(), user.getEmail());
            }
            else if (resultCode == RESULT_CANCELED)
            {
                Helper.hideProgressBar(this, this.ProgressBar);
                Helper.makeSnackbarMessage(this.BackgroundLayout,
                                           getString(R.string.error_auth_failed));
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        if (this.WelcomeLayout.getVisibility() == View.VISIBLE)
        {
            super.onBackPressed();
        }
        else
        {
            this.onClick(this.BackButton);
        }
    }
}

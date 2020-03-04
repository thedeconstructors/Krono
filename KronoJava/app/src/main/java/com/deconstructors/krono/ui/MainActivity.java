package com.deconstructors.krono.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.deconstructors.krono.R;
import com.deconstructors.krono.activities.login.LoginPage;
import com.deconstructors.krono.module.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    // Error Handler Log Search
    public static final String _Tag = "MainActivity";

    // Firebase Authentication Listener
    private FirebaseAuth.AuthStateListener AuthStateListener;
    private User User;

    // XML Widgets
    private AppBarConfiguration AppBarConfiguration;
    private NavigationView NavigationView;
    private NavController NavController;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_main);

        setupFirebaseAuth();
        setContents();
        setProfile();

        //Toast.makeText(MainActivity.this, "Current user's id: " + SessionData.GetInstance().GetUserID(), Toast.LENGTH_SHORT).show();
    }

    private void setContents()
    {
        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // NavigationView and Controls
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        this.NavigationView = findViewById(R.id.nav_view);
        this.AppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_allActivity,
                                                                   R.id.nav_plans,
                                                                   R.id.nav_friends)
                .setDrawerLayout(drawer)
                .build();
        this.NavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, this.NavController, AppBarConfiguration);
        NavigationUI.setupWithNavController(this.NavigationView, this.NavController);
    }

    /************************************************************************
     * Purpose:         Navigation
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_plan, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        this.NavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(this.NavController, this.AppBarConfiguration) || super.onSupportNavigateUp();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.nav_friends:
            {
            }

            case R.id.nav_plans:
            {
            }
        }

        return false;
    }

    private void setProfile()
    {
        String x = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection(getString(R.string.collection_users))
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot)
                    {
                        String fn = documentSnapshot.getString("firstname");
                        String ln = documentSnapshot.getString("lastname");
                        String em = documentSnapshot.getString("loginEmail");

                        View header = NavigationView.getHeaderView(0);
                        TextView displayname = header.findViewById(R.id.profile_name);
                        TextView email = header.findViewById(R.id.profile_email);

                        displayname.setText(fn + " " + ln);
                        email.setText(em);
                    }
                });
    }

    /***********************************************************************
     * Purpose:         Setup Firebase Authentication
     * Precondition:    Authentication State Changed (Logged out)
     * Postcondition:   Change Intent when signed out
     ************************************************************************/
    private void setupFirebaseAuth()
    {
        Log.d(_Tag, "setupFirebaseAuth started");

        AuthStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null)
                {
                    Log.d(_Tag, "setupFirebaseAuth - signed in as: " + user.getUid());
                }
                else
                {
                    Log.d(_Tag, "setupFirebaseAuth - signed out");
                    Intent intent = new Intent(MainActivity.this, LoginPage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    /***********************************************************************
     * Purpose:         onStart
     * Precondition:    Comes right after onCreate
     * Postcondition:   Setup Firebase Authentication Listener On Start
     ************************************************************************/
    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this.AuthStateListener);
    }

    /***********************************************************************
     * Purpose:         Logout on Stop
     * Precondition:    Comes right after onPause and before onDestroy
     * Postcondition:   Remove Firebase Authentication Listener On Stop
     ************************************************************************/
    @Override
    protected void onStop()
    {
        super.onStop();
        if (this.AuthStateListener != null)
        {
            FirebaseAuth.getInstance().removeAuthStateListener(this.AuthStateListener);
        }
    }
}

package com.deconstructors.krono.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;

import com.deconstructors.krono.R;
import com.deconstructors.krono.activities.activities.Menu0_Activities;
import com.deconstructors.krono.activities.friends.Menu3_Friends;
import com.deconstructors.krono.activities.login.LoginPage;
import com.deconstructors.krono.activities.plans.Menu1_Plans;
import com.deconstructors.krono.activities.profile.Menu4_Users;
import com.deconstructors.krono.helpers.SessionData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class MainActivity extends AppCompatActivity
{
    // Error Handler Log Search
    public static final String _Tag = "Krono_Main";
    private static final String _dbTag = "Krono_FBAuth_Log";

    // Firebase Authentication Listener
    private FirebaseAuth.AuthStateListener _AuthStateListener;

    // XML Widgets
    private GridLayout _MainGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _MainGrid = findViewById(R.id.mainGrid);
        setSingleEvent(_MainGrid);

        setupFirebaseAuth();
        enablePersistence();

        Toast.makeText(MainActivity.this, "Current user's id: " + SessionData.GetInstance().GetUserID(), Toast.LENGTH_SHORT).show();
    }

    /**************************** Database *********************************/

    /***********************************************************************
     * Purpose:         Setup Firebase Authentication
     * Precondition:    Authentication State Changed
     * Postcondition:   Change Intent when signed out
     ************************************************************************/
    private void setupFirebaseAuth()
    {
        Log.d(_dbTag, "setupFirebaseAuth started");

        _AuthStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null)
                {
                    Log.d(_dbTag, "setupFirebaseAuth - signed in as: " + user.getUid());
                }
                else
                {
                    Log.d(_dbTag, "setupFirebaseAuth - signed out");
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
        FirebaseAuth.getInstance().addAuthStateListener(_AuthStateListener);
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
        if (_AuthStateListener != null)
        {
            FirebaseAuth.getInstance().removeAuthStateListener(_AuthStateListener);
        }
    }

    /***********************************************************************
     * Purpose:         enable Offline Database Persistence
     * Precondition:    Supposed to be on by default
     * Postcondition:   Configure offline persistence
     *                  Configure cache size - Needs Fix
     ************************************************************************/
    private void enablePersistence()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();

        db.setFirestoreSettings(settings);
    }

    /******************************* XML ***********************************/

    /************************************************************************
     * Purpose:         Set Event Handler
     * Precondition:    Menu items are w/o button events
     * Postcondition:   Menu items are set with onClick indexing events
     *                  This is mainly to separate the xml files
     ************************************************************************/
    private void setSingleEvent(GridLayout mainGrid)
    {
        for(int i = 0; i < mainGrid.getChildCount(); i++)
        {
            CardView cardView = (CardView)mainGrid.getChildAt(i);
            final int menuClickIndex = i;
            cardView.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View view)
                {
                    // TEST CODE for indexing
                    // Toast.makeText(MainActivity.this, "Clicked at index " + menuClickIndex, Toast.LENGTH_SHORT).show();

                    if (menuClickIndex == 0)
                    {
                        Intent intent = new Intent(MainActivity.this, Menu0_Activities.class);
                        startActivity(intent);
                    }
                    else if (menuClickIndex == 1)
                    {
                        Intent intent = new Intent(MainActivity.this, Menu1_Plans.class);
                        startActivity(intent);
                    }
                    else if (menuClickIndex == 3)
                    {
                        Intent intent = new Intent(MainActivity.this, Menu3_Friends.class);
                        startActivity(intent);
                    }
                    else if (menuClickIndex == 4)
                    {
                        Intent intent = new Intent(MainActivity.this, Menu4_Users.class);
                        startActivity(intent);
                    }
                    else if (menuClickIndex == 5)
                    {
                        FirebaseAuth.getInstance().signOut();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Error: No Activity Found in Menu", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}

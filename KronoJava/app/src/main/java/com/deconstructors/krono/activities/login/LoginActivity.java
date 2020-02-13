package com.deconstructors.krono.activities.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.deconstructors.firestoreinteract.FirestoreDB;
import com.deconstructors.krono.R;
import com.deconstructors.krono.activities.MainActivity;
import com.deconstructors.krono.helpers.SessionData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    //value for guest's user id
    private final String GUEST_USER_ID = "42";
    // Declare an instance of the Firebase Authenticator.
    private FirebaseAuth m_auth;

    // Declare a global changeable view.
    private View signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        // Initialize the Firebase Authenticator.
        m_auth = FirebaseAuth.getInstance();
        signInButton = findViewById(R.id.login_button_email);

        //Initialize library database reference
        FirestoreDB.SetDB(FirebaseFirestore.getInstance());
    }

    public void emailOnClick(View view) {
        Intent intent = new Intent(this, LoginEmailPass.class);
        startActivity(intent);
    }

    public void guestModeOnClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        SessionData.GetInstance().SetUserID(GUEST_USER_ID);
        startActivity(intent);
    }

}

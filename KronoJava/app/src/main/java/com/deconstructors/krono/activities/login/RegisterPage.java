package com.deconstructors.krono.activities.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.deconstructors.krono.R;
import com.deconstructors.krono.activities.MainActivity;
import com.deconstructors.krono.helpers.SessionData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterPage extends AppCompatActivity
{

    // Public static instance of the database
    //public static FirebaseFirestore db = FirebaseFirestore.getInstance();

    // View controls
    private View signUpButton;
    private TextView firstName;
    private TextView lastName;

    // HashMap to store the users base data
    Map<String, Object> user = new HashMap<>();

    // Used to get the email string from the previous activity
    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view and controls
        setContentView(R.layout.activity_registerpage);
        signUpButton = findViewById(R.id.email_register_button);
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);

        // Get email string from the previous intent
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
    }

    public void registerEmailOnClick(View view) {

        // When the user provides a first and last name...
        if (firstName.getText().toString() != "" && lastName.getText().toString() != "") {

            // Store the first and last name and email in the user map
            /*
            user.put("first_name", firstName.getText().toString());
            user.put("last_name", lastName.getText().toString());
            user.put("email", email);*/

            //add user to FirebaseAuth
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(RegisterPage.this,
                                    authResult.getUser().getEmail().toString() +
                                            " Added Successfully.",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterPage.this, "Error: Could Not Add User: " +
                                    e.getMessage().toString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}

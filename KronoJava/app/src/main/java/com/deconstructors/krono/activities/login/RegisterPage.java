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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    /* No Longer Required */
    //Used to add login id to user doc
    //String loginId = null;

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
            //add user to FirebaseAuth and Firestore
            CreateUserLogin();
        }
    }

    /**********************************
     * Begins process of adding user
     *  - Adds email and password to FirebaseAuth
     *  - Calls method to add user info to DB
     */
    private void CreateUserLogin()
    {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    // if successful, add user to db
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        /* No Longer Required after refactor */
                        //loginId = authResult.getUser().getUid();
                        AddUserToDatabase();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    //if unsuccessful, display error
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterPage.this, "Error: Could Not Add User:\n" +
                                        e.getMessage().toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void AddUserToDatabase()
    {
        user.put("firstname",firstName.getText().toString());
        user.put("lastname",lastName.getText().toString());
        user.put("loginEmail",email);

        /* No Longer Required */
        //user.put("loginId",loginId);

        FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(RegisterPage.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterPage.this, "Error: Could Not Add User:\n" +
                                        e.getMessage().toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

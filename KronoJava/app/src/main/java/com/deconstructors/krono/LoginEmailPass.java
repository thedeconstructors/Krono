package com.deconstructors.krono;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import android.util.Log;

import android.view.View;

import android.widget.EditText;

import android.widget.TextView;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class LoginEmailPass extends AppCompatActivity {

    private View signUpButton;
    private View signInButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email_pass);
        signUpButton = findViewById(R.id.email_sign_up_button);
        signInButton = findViewById(R.id.email_sign_in_button);
    }

    public void emailSignUpOnClick(View view) {
        //Map<String, Object> user = new HashMap<>();
        TextView textView = findViewById(R.id.user_email);
        String email = textView.getText().toString();
        if (email != "")
        {
            Intent intent = new Intent(LoginEmailPass.this, LoginEmailPassRegister.class);
            intent.putExtra("email", email);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(LoginEmailPass.this, "Please enter an email to sign up with",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void emailSignInOnClick(View view) {
        //Map<String, Object> user = new HashMap<>();
        TextView textView = findViewById(R.id.user_email);
        String email = textView.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference users = db.collection("users");
        Query query = users.whereEqualTo("email", email);

        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginEmailPass.this, "Welcome ",
                                    Toast.LENGTH_SHORT).show();
                            //Set user's id for session
                            SessionData.GetInstance().SetUserID(task.getResult().getDocuments().get(0).getId());
                            //change page
                            Intent intent = new Intent(LoginEmailPass.this, MainActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(LoginEmailPass.this, "Could not find email",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

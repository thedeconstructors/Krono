package com.deconstructors.krono.activities.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.deconstructors.krono.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class Menu4_Users extends AppCompatActivity
{
    public static final String USER_NAME = "com.deconstructors.krono.USER_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu4__users);
    }

    public void btnDisplayPersonClick(View view)
    {
        /*Intent intent = new Intent(this, DisplayPlansActivity.class);
        intent.putExtra(USER_NAME, ((EditText)findViewById(R.id.txtPersonName)).getText().toString());
        startActivity(intent);*/
    }
}

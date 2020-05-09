package com.deconstructors.krono.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.deconstructors.krono.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

public class ProfilePage_Edit extends AppCompatActivity implements View.OnClickListener {

    //Defines
    public static final int GET_FROM_GALLERY = 3;

    // UI
    private TextView NameTextView;
    private TextView EmailTextView;
    private TextView BioTextView;
    private ImageButton imageButtonElement;

    //Profile Picture
    private Bitmap data_bitmap = null;

    private FirebaseAuth AuthInstance;
    private FirebaseFirestore DBInstance;

    //Profile Data
    Map<String, Object> profile = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_edit);

        //Attach the listener for the profile picture editing
        imageButtonElement = findViewById(R.id.profile_picture_edit_button);

        //Listen for clicks
        imageButtonElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                        GET_FROM_GALLERY);
            }
        });

        //Load Profile Data
        setToolbar();
        setUserDB();
        setContents();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();

            try {
                data_bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                        selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Bitmap to be stored into firebase
            data_bitmap = Bitmap.createScaledBitmap(data_bitmap, 250, 250,
                    true);

            //Display the bitmap to the clicked button
            ViewTreeObserver vto = imageButtonElement.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    imageButtonElement.setImageBitmap(
                            Bitmap.createScaledBitmap(data_bitmap,
                                    imageButtonElement.getWidth(),
                                    imageButtonElement.getHeight(), true)
                    );

                    //Remove the listener for protection against multiple calls
                    ViewTreeObserver obs = imageButtonElement.getViewTreeObserver();
                    obs.removeOnGlobalLayoutListener(this);
                }
            });
        }
    }

    private void setToolbar() {
        androidx.appcompat.widget.Toolbar _toolbar = findViewById(R.id.profileEdit_toolbar);
        this.setSupportActionBar(_toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void setUserDB()
    {
        // User
        this.AuthInstance = FirebaseAuth.getInstance();
        this.DBInstance = FirebaseFirestore.getInstance();

        this.DBInstance.collection(getString(R.string.collection_users))
                .document(Objects.requireNonNull(this.AuthInstance.getCurrentUser()).getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>()
                {
                    @Override
                    public void onEvent(@androidx.annotation.Nullable DocumentSnapshot documentSnapshot,
                                        @androidx.annotation.Nullable FirebaseFirestoreException e)
                    {
                        if (documentSnapshot != null)
                        {
                            NameTextView.setText(Objects.requireNonNull(documentSnapshot.get("displayName")).toString());
                            EmailTextView.setText(Objects.requireNonNull(documentSnapshot.get("email")).toString());
                            BioTextView.setText(Objects.requireNonNull(documentSnapshot.get("bio")).toString());
                        }
                    }
                });
    }

    /************************************************************************
     * Purpose:         Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void saveProfile()
    {
        profile.put("displayName", this.NameTextView.getText().toString());
        profile.put("email", this.EmailTextView.getText().toString());
        profile.put("bio", this.BioTextView.getText().toString());

        this.DBInstance.collection(getString(R.string.collection_users))
                .document(Objects.requireNonNull(this.AuthInstance.getCurrentUser()).getUid())
                .update(profile)
                .addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(ProfilePage_Edit.this, "Edit Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setContents()
    {
        this.NameTextView = findViewById(R.id.profileEdit_titleText);
        this.EmailTextView = findViewById(R.id.profileEdit_emailText);
        this.BioTextView = findViewById(R.id.profileEdit_bioText);

        FloatingActionButton FAB = findViewById(R.id.profiledetail_fab);
        FAB.setOnClickListener(this);
    }

    public void onClick(View view)
    {
        if (view.getId() == R.id.profiledetail_fab) {
            this.saveProfile();
        }
    }

    /************************************************************************
     * Purpose:         Toolbar Back Button Animation Overrides
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Back Button Animation Override
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

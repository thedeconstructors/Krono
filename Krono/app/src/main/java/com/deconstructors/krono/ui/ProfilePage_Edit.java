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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
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

    //FireStorage
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    //FireStore
    private FirebaseAuth AuthInstance;
    private FirebaseFirestore DBInstance;

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
        if(requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
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
                            NameTextView.setText(Objects.requireNonNull(
                                    documentSnapshot.get("displayName")).toString());
                            EmailTextView.setText(Objects.requireNonNull(
                                    documentSnapshot.get("email")).toString());
                            BioTextView.setText(Objects.requireNonNull(
                                    documentSnapshot.get("bio")).toString());
                            //Load the users current profile picture
                            Picasso.get().load(
                                    Objects.requireNonNull(
                                            documentSnapshot.get("picture")
                                    ).toString()
                            ).into(imageButtonElement);
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
        //Disable the fab
        findViewById(R.id.profiledetail_fab).setEnabled(false);
        //Display the loading spinner
        findViewById(R.id.storage_progress).setVisibility(View.VISIBLE);

        //Creating an uploadable byte array for upload to Google's Firebase FireStorage service
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        data_bitmap.compress(Bitmap.CompressFormat.PNG, 100, boas);
        byte[] data = boas.toByteArray();

        //Path for a unique profile picture
        String path = getString(R.string.fs_profile_picture_path) +
                AuthInstance.getUid() + getString(R.string.png_extension);

        //Storage Hook
        final StorageReference fireRef = storage.getReference(path);

        //Upload the file to FireStorage
        UploadTask uploadTask = fireRef.putBytes(data);
        uploadTask.addOnCompleteListener(ProfilePage_Edit.this,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        //TODO Possible Feedback
                    }
                });

        Task<Uri> getDownloadUriTask = uploadTask.continueWithTask(
                new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task)
                            throws Exception {
                                if (!task.isSuccessful()) {
                                    throw Objects.requireNonNull(task.getException());
                                }
                                return fireRef.getDownloadUrl();
                            }
                        }
                    );

        getDownloadUriTask.addOnCompleteListener(ProfilePage_Edit.this,
                new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        //Profile Data
                        Map<String, Object> profile = new HashMap<>();

                        profile.put("displayName", NameTextView.getText().toString());
                        profile.put("email", EmailTextView.getText().toString());
                        profile.put("bio", BioTextView.getText().toString());

                        if (task.isSuccessful()) {
                            profile.put("picture",
                                    Objects.requireNonNull(task.getResult()).toString());

                        } else {
                            //TODO Default Picture
                            profile.put("picture",
                                    "Error");
                        }
                        setDataBase(profile);
                    }
                });
    }

    private void setDataBase(Map<String, Object> profile) {
        DBInstance.collection(getString(R.string.collection_users))
                .document(Objects.requireNonNull(
                        AuthInstance.getCurrentUser()).getUid())
                .update(profile)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        findViewById(R.id.storage_progress).setVisibility(View.INVISIBLE);
                        findViewById(R.id.profiledetail_fab).setEnabled(true); //Clean up

                        finish();
                    }
                }).addOnFailureListener(ProfilePage_Edit.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),
                        "There was an error processing the profile data",
                        Toast.LENGTH_SHORT).show();

                findViewById(R.id.profiledetail_fab).setEnabled(true);
                findViewById(R.id.storage_progress).setVisibility(View.INVISIBLE); //Clean up
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

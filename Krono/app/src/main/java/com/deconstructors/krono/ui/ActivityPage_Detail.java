package com.deconstructors.krono.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.deconstructors.krono.R;
import com.deconstructors.krono.module.Activity;
import com.deconstructors.krono.module.Location;
import com.deconstructors.krono.module.Plan;
import com.deconstructors.krono.utility.Helper;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ActivityPage_Detail extends AppCompatActivity implements View.OnClickListener,
                                                                      DatePickerDialog.OnDateSetListener, OnMapReadyCallback
{
    // Error Log
    private static final String TAG = "NewActivityPage";
    public final float MAP_DEFAULT_ZOOM = 15.0F;
    public final int CAMERA_DEFAULT_SPEED = 1000;

    //result constant for extra
    private Toolbar Toolbar;
    private EditText Title;
    private EditText Description;
    private Button DateTime;
    private EditText Duration;
    private FloatingActionButton FAB_Save;
    private FloatingActionButton FAB_Delete;

    // Vars
    private Calendar CalendarInstance;
    private Location Location;
    private GoogleMap Map;
    private Activity Activity;
    private ActivityPage.EditMode Editable;

    // Database
    private FirebaseFirestore FirestoreDB;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        this.setContents();
        this.getActivityIntent();
        this.checkPermissions();
    }

    private void setContents()
    {
        // Toolbar
        this.Toolbar = findViewById(R.id.activitydetail_toolbar);
        this.Toolbar.setTitle("");
        this.setSupportActionBar(this.Toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Other Widgets
        this.Title = findViewById(R.id.activitydetail_titleEditText);
        this.Description = findViewById(R.id.activitydetail_descriptionText);
        this.DateTime = findViewById(R.id.activitydetail_dueDateEditText);
        this.DateTime.setOnClickListener(this);
        this.CalendarInstance = Calendar.getInstance();
        //TODO Create Duration Number Picker Dialog
        this.Duration = findViewById(R.id.activitydetail_durationEditText);
        this.Location = new Location("", "", new LatLng(0, 0));

        this.FAB_Save = findViewById(R.id.activitydetail_fab_save);
        this.FAB_Save.setOnClickListener(this);
        this.FAB_Delete = findViewById(R.id.activitydetail_fab_delete);
        this.FAB_Delete.setOnClickListener(this);

        // Firebase
        this.FirestoreDB = FirebaseFirestore.getInstance();

        //TODO Connect The Google Map with The Map Picker Page
        // Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getActivityIntent()
    {
        if(getIntent().hasExtra(getString(R.string.intent_activity)) && getIntent().hasExtra(getString(R.string.intent_editable)))
        {
            this.Activity = getIntent().getParcelableExtra(getString(R.string.intent_activity));
            this.Editable = (ActivityPage.EditMode) getIntent().getSerializableExtra(getString(R.string.intent_editable));
            this.getSupportActionBar().setTitle(this.Activity.getTitle());

            this.Title.setText(this.Activity.getTitle());
            this.Description.setText(this.Activity.getDescription());
            if (this.Activity.getTimestamp() != null)
            {
                this.DateTime.setText(this.Activity.getTimestamp());
            }
            if (this.Activity.getDuration() != null)
            {
                this.Duration.setText(this.Activity.getDuration().toString());
            }
            if (this.Activity.getLocation() != null)
            {
                this.Location = this.Activity.getLocation();
            }
        }
        else
        {
            finish();
        }
    }

    private void checkPermissions()
    {
        if (Editable == ActivityPage.EditMode.PUBLIC)
        {
            //disable textboxes
            this.Title.setEnabled(false);
            this.Description.setEnabled(false);
            this.DateTime.setEnabled(false);

            //hide buttons
            this.FAB_Save.setVisibility(View.GONE);
            this.FAB_Delete.setVisibility(View.GONE);

            //change title
            ((TextView)findViewById(R.id.activitydetail_editActivityHeader)).setText(getString(R.string.activitydetail_headerViewOnly));
        }
    }

    /************************************************************************
     * Purpose:         Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void saveActivity()
    {
        if (Editable == ActivityPage.EditMode.PUBLIC)
        {
            makeSnackbarMessage("This is not an editable activity.");
            return;
        }

        // Check Duration Field
        Integer activity_duration = 0;
        try
        {
            activity_duration = Integer.parseInt(this.Duration.getText().toString());
        }
        catch (NumberFormatException e)
        {
            makeSnackbarMessage("Invalid duration format. Please enter an integer.");
            return;
        }

        // Check Fields
        if (!Helper.isEmpty(this.Title))
        {
            Map<String, Object> activity = Helper.mapActivity(this,
                                                              this.Activity.getActivityID(),
                                                              this.Description.getText().toString(),
                                                              activity_duration,
                                                              this.Location,
                                                              null,
                                                              this.DateTime.getText().toString(),
                                                              this.Title.getText().toString());

            activity.remove(getString(R.string.collection_planIDs));

            FirestoreDB.collection(getString(R.string.collection_activities))
                    .document(this.Activity.getActivityID())
                    .update(activity)
                    .addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                finish();
                            }
                            else
                            {
                                Log.d(TAG, "Failed to save");
                            }
                        }
                    });
        }
        else
        {
            makeSnackbarMessage("Please fill in all the fields");
        }
    }

    private void deleteActivity()
    {
        if (Editable == ActivityPage.EditMode.PUBLIC)
        {
            Toast.makeText(this, "This plan is not editable", Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            this.FirestoreDB
                    .collection(getString(R.string.collection_activities))
                    .document(this.Activity.getActivityID())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            finish();
                        }
                    });
        }
    }

    /************************************************************************
     * Purpose:         Floating Action Button onClick Overrides
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.activitydetail_fab_save:
            {
                this.saveActivity();
                break;
            }
            case R.id.activitydetail_fab_delete:
            {
                this.deleteActivity();
                break;
            }
            case R.id.activitydetail_dueDateEditText:
            {
                this.showDatePicker();
                break;
            }
        }
    }

    /************************************************************************
     * Purpose:         Date Picker
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void showDatePicker()
    {
        DatePickerDialog dpd = new DatePickerDialog(
                this,
                this,
                this.CalendarInstance.get(Calendar.YEAR),
                this.CalendarInstance.get(Calendar.MONTH),
                this.CalendarInstance.get(Calendar.DAY_OF_MONTH)
        );

        dpd.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(Helper.displayDateFormat, Locale.getDefault());
        this.CalendarInstance.set(year, month, dayOfMonth);
        String dateString = sdf.format(this.CalendarInstance.getTime());
        this.DateTime.setText(dateString);
    }

    /************************************************************************
     * Purpose:         Map Loaded
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        this.Map = googleMap;
        this.Map.setMyLocationEnabled(false);
        this.Map.getUiSettings().setMyLocationButtonEnabled(false);
        this.setMarkerPosition();
    }

    /************************************************************************
     * Purpose:         Utility
     * Precondition:    .
     * Postcondition:   Change Google Map's marker position
     ************************************************************************/
    public void setMarkerPosition()
    {
        if (this.Location != null)
        {
            MarkerOptions options = new MarkerOptions().position(this.Location.getLatLng())
                                                       .title(this.Location.getName());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(this.Location.getLatLng(),
                                                                    MAP_DEFAULT_ZOOM);

            this.Map.clear();
            this.Map.addMarker(options);
            this.Map.animateCamera(update, CAMERA_DEFAULT_SPEED, null);
        }
    }

    /************************************************************************
     * Purpose:         Toolbar Back Button Animation Overrides
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            // Back Button Animation Override
            case android.R.id.home:
            {
                finish();
                break;
            }
            case R.id.activitydetail_menu_deleteActivity:
            {
                this.deleteActivity();
                break;
            }
        }
        return true;
    }

    /************************************************************************
     * Purpose:         Toolbar Menu Inflater
     * Precondition:    .
     * Postcondition:   Activates the toolbar menu by inflating it
     *                  See more from res/menu/activity_boolbar_menu
     *                  and layout/menu0_toolbar
     ************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_detail, menu);

        return true;
    }

    /************************************************************************
     * Purpose:         Utilities
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void makeSnackbarMessage(String string)
    {
        Snackbar.make(findViewById(R.id.activitydetail_background), string, Snackbar.LENGTH_SHORT).show();
    }
}

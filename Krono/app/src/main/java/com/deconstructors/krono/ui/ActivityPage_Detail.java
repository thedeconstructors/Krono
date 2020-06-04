package com.deconstructors.krono.ui;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.deconstructors.krono.R;
import com.deconstructors.krono.module.Activity;
import com.deconstructors.krono.module.Location;
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
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class ActivityPage_Detail extends AppCompatActivity implements View.OnClickListener,
        OnMapReadyCallback, GoogleMap.OnMapClickListener,
        DatePickerDialog.OnDateSetListener {
    // Error Log
    private static final String TAG = "NewActivityPage";
    public final float MAP_DEFAULT_ZOOM = 15.0F;
    public final int CAMERA_DEFAULT_SPEED = 1000;
    private static final int MAPACTIVITY_REQUESTCODE = 5002;
    private static final int LOCATIONSTR_SIZE = 10;

    //result constant for extra
    private Toolbar Toolbar;
    private EditText TitleText;
    private EditText DescriptionText;
    private EditText DurationText;
    private Button DateButton;
    private FloatingActionButton FAB_Save;
    private FloatingActionButton FAB_Delete;

    // Vars
    private String Timestamp;
    private Integer Duration;
    private Location Location;
    private GoogleMap Map;
    private Activity Activity;
    private ActivityPage.EditMode Editable;
    private SupportMapFragment MapFragment;
    private Calendar CalendarInstance;

    // Database
    private FirebaseFirestore FirestoreDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        this.setContents();
        this.getActivityIntent();
        this.checkPermissions();
    }

    private void setContents() {
        // Toolbar
        this.Toolbar = findViewById(R.id.activitydetail_toolbar);
        this.Toolbar.setTitle("");
        this.setSupportActionBar(this.Toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Other Widgets
        this.TitleText = findViewById(R.id.activitydetail_titleEditText);
        this.DescriptionText = findViewById(R.id.activitydetail_descriptionText);
        //TODO Create Duration Number Picker Dialog
        this.DateButton = findViewById(R.id.activitydetail_dateButton);
        this.DateButton.setOnClickListener(this);
        this.CalendarInstance = Calendar.getInstance();
        this.DurationText = findViewById(R.id.activitydetail_durationEditText);
        this.DurationText.setOnClickListener(this);
        this.Location = new Location("", "", new LatLng(0, 0));

        this.FAB_Save = findViewById(R.id.activitydetail_fab_save);
        this.FAB_Save.setOnClickListener(this);
        this.FAB_Delete = findViewById(R.id.activitydetail_fab_delete);
        this.FAB_Delete.setOnClickListener(this);

        // Firebase
        this.FirestoreDB = FirebaseFirestore.getInstance();

        //TODO Connect The Google Map with The Map Picker Page
        // Google Map
        this.MapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        this.MapFragment.getMapAsync(this);
    }

    private void getActivityIntent() {
        if (getIntent().hasExtra(getString(R.string.intent_activity)) && getIntent().hasExtra(getString(R.string.intent_editable))) {
            this.Activity = getIntent().getParcelableExtra(getString(R.string.intent_activity));
            this.Editable = (ActivityPage.EditMode) getIntent().getSerializableExtra(getString(R.string.intent_editable));
            this.getSupportActionBar().setTitle(this.Activity.getTitle());

            this.TitleText.setText(this.Activity.getTitle());
            this.DescriptionText.setText(this.Activity.getDescription());

            this.Duration = null;
            if (this.Activity.getDuration() != null) {
                this.Duration = this.Activity.getDuration();
                String tempDuration = this.Activity.getDuration().toString() + " " + getString(R.string.activitydetail_hours);
                this.DurationText.setText(tempDuration);
            }

            this.Timestamp = null;
            if (this.Activity.getTimestamp() != null) {
                this.Timestamp = this.Activity.getTimestamp();
                SimpleDateFormat sdf = new SimpleDateFormat(Helper.displayDateFormat, Locale.getDefault());
                Date savedDate = sdf.parse(this.Timestamp, new ParsePosition(0));
                if (savedDate != null) {
                    this.CalendarInstance.setTime(savedDate);
                    String dateString = sdf.format(this.CalendarInstance.getTime());
                    this.DateButton.setText(dateString);
                } else {
                    this.Timestamp = null;
                    this.DateButton.setText(R.string.newactivity_timestamp);
                }
            }

            this.Location = null;
            if (this.Activity.getLocation() != null) {
                this.Location = this.Activity.getLocation();
            }
        } else {
            finish();
        }
    }

    private void checkPermissions() {
        if (Editable == ActivityPage.EditMode.PUBLIC) {
            //disable textboxes
            this.TitleText.setEnabled(false);
            this.DescriptionText.setEnabled(false);
            this.DurationText.setEnabled(false);
            this.DateButton.setEnabled(false);

            //hide buttons
            this.FAB_Save.setVisibility(View.GONE);
            this.FAB_Delete.setVisibility(View.GONE);

            //change title
            ((TextView) findViewById(R.id.activitydetail_editActivityHeader)).setText(getString(R.string.activitydetail_headerViewOnly));
        }
    }

    /************************************************************************
     * Purpose:         Database
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void saveActivity() {
        if (Editable == ActivityPage.EditMode.PUBLIC) {
            makeSnackbarMessage("This is not an editable activity.");
            return;
        }

        // Check Duration Field
        /*Integer activity_duration = 0;
        try
        {
            activity_duration = Integer.parseInt(this.DurationText.getText().toString());
        }
        catch (NumberFormatException e)
        {
            makeSnackbarMessage("Invalid duration format. Please enter an integer.");
            return;
        }*/

        // Check Fields
        if (!Helper.isEmpty(this.TitleText)) {
            Map<String, Object> activity = Helper.mapActivity(this,
                    this.Activity.getActivityID(),
                    this.DescriptionText.getText().toString(),
                    this.Duration,
                    this.Location,
                    null,
                    this.TitleText.getText().toString());

            activity.remove(getString(R.string.plans_planIDs));

            if (this.Timestamp != null) {
                activity.put(getString(R.string.activities_timestamp), this.Timestamp);
            }

            this.FirestoreDB
                    .collection(getString(R.string.collection_activities))
                    .document(this.Activity.getActivityID())
                    .update(activity)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                finish();
                            } else {
                                Log.d(TAG, "Failed to save");
                            }
                        }
                    });
        } else {
            this.makeSnackbarMessage("Please fill in all the fields");
        }
    }

    private void deleteActivity() {
        if (Editable == ActivityPage.EditMode.PUBLIC) {
            Toast.makeText(this, "This plan is not editable", Toast.LENGTH_SHORT).show();
            return;
        } else {
            this.FirestoreDB
                    .collection(getString(R.string.collection_activities))
                    .document(this.Activity.getActivityID())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activitydetail_fab_save: {
                this.saveActivity();
                break;
            }
            case R.id.activitydetail_fab_delete: {
                this.deleteActivity();
                break;
            }
            case R.id.activitydetail_durationEditText: {
                this.showNumberPicker();
                break;
            }
            case R.id.activitydetail_dateButton: {
                this.showDatePicker();
                break;
            }
        }
    }

    /************************************************************************
     * Purpose:         Number Picker
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void showNumberPicker() {
        final Dialog npd = new Dialog(this);
        npd.setTitle(getString(R.string.activitydetail_selectduration));
        npd.setContentView(R.layout.activity_npd);

        final NumberPicker np = npd.findViewById(R.id.NPD_NumberPicker);
        np.setMaxValue(24);
        np.setMinValue(0);
        np.setWrapSelectorWheel(false);

        Button done = npd.findViewById(R.id.NPD_Done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Duration = np.getValue();
                String durationtext = np.getValue() + " " + getString(R.string.activitydetail_hours);
                DurationText.setText(durationtext);
                npd.dismiss();
            }
        });
        Button cancel = npd.findViewById(R.id.NPD_Cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                npd.dismiss();
            }
        });

        npd.show();
    }

    /************************************************************************
     * Purpose:         Date Picker
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void showDatePicker() {
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
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        SimpleDateFormat sdf = new SimpleDateFormat(Helper.displayDateFormat, Locale.getDefault());
        this.CalendarInstance.set(year, month, dayOfMonth);
        String dateString = sdf.format(this.CalendarInstance.getTime());
        this.Timestamp = dateString;
        this.DateButton.setText(dateString);
    }


    /************************************************************************
     * Purpose:         Map Loaded
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.Map = googleMap;
        this.Map.setOnMapClickListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        this.Map.setMyLocationEnabled(false);
        this.Map.getUiSettings().setMyLocationButtonEnabled(false);
        this.setMarkerPosition();
    }

    @Override
    public void onMapClick(LatLng latLng)
    {
        if (Editable != ActivityPage.EditMode.PUBLIC) {
            Intent intent = new Intent(this, ActivityPage_Map.class);
            startActivityForResult(intent, MAPACTIVITY_REQUESTCODE);
        }
    }


    /************************************************************************
     * Purpose:         On Google Map Activity Completed
     * Precondition:    .
     * Postcondition:   Set Returned Location Data To the Button GUI
     ************************************************************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MAPACTIVITY_REQUESTCODE
                && resultCode == android.app.Activity.RESULT_OK
                && data != null)
        {
            this.Location = data.getParcelableExtra(getString(R.string.intent_location));
            this.setMarkerPosition();
        }
    }

    /************************************************************************
     * Purpose:         Utility
     * Precondition:    .
     * Postcondition:   Change Google Map's marker position
     ************************************************************************/
    public void setMarkerPosition()
    {
        if (this.Location != null && !this.Location.getName().equals(""))
        {
            MarkerOptions options = new MarkerOptions().position(this.Location.getLatLng())
                                                       .title(this.Location.getName());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(this.Location.getLatLng(),
                                                                    MAP_DEFAULT_ZOOM);

            this.Map.clear();
            this.Map.addMarker(options).showInfoWindow();
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

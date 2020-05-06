package com.deconstructors.krono.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.deconstructors.krono.R;
import com.deconstructors.krono.module.Location;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.android.SphericalUtil;
import java.util.Arrays;
import java.util.List;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ActivityPage_Map extends AppCompatActivity implements OnMapReadyCallback,
                                                                   OnCompleteListener,
                                                                   View.OnClickListener
{
    // Error Log & Global variables
    private final static String TAG = "ActivityPage_Map";
    public final int LOCATION_PERMISSION_REQUEST_CODE = 5001;
    public final float MAP_DEFAULT_ZOOM = 15.0F;
    public final int CAMERA_DEFAULT_SPEED = 1000;
    public final float LOCATION_BIAS_RADIUS = 5000.0F;

    // Loc
    private Location Location;
    private GoogleMap Map;
    private PlacesClient PlacesClient;
    private AutocompleteSupportFragment AutoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        this.setContents();
        this.setAutoCompleteSupport();
    }

    /************************************************************************
     * Purpose:         Contents
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setContents()
    {
        // Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Google Places
        Places.initialize(ActivityPage_Map.this, getString(R.string.google_maps_key));
        this.PlacesClient = Places.createClient(this);

        // Other XML Contents
        ImageView myLocButton = findViewById(R.id.myloc_button);
        myLocButton.setOnClickListener(this);
        Button addLocButton = findViewById(R.id.complete_button);
        addLocButton.setOnClickListener(this);
    }

    /************************************************************************
     * Purpose:         Auto Complete Support
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setAutoCompleteSupport()
    {
        // Search Auto Complete
        this.AutoFragment = (AutocompleteSupportFragment) getSupportFragmentManager()
                .findFragmentById(R.id.autocomplete_fragment);
        this.AutoFragment.setPlaceFields(Arrays.asList(Place.Field.NAME,
                                                       Place.Field.ADDRESS,
                                                       Place.Field.LAT_LNG));
        this.AutoFragment.setOnPlaceSelectedListener(new PlaceSelectionListener()
        {
            @Override
            public void onPlaceSelected(@NonNull Place place)
            {

                ActivityPage_Map.this.Location = new Location(place.getName(),
                                                              place.getAddress(),
                                                              place.getLatLng());
                ActivityPage_Map.this.setMarkerPosition();
            }

            @Override
            public void onError(@NonNull Status status)
            {
                Log.i(TAG, "An error occurred: " + status);
            }
        });
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
        this.getMyLocation();
    }

    /************************************************************************
     * Purpose:         Button Click Handler
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.myloc_button:
            {
                this.getMyLocation();
                break;
            }
            case R.id.complete_button:
            {
                this.onActivityComplete();
                break;
            }
        }
    }

    /************************************************************************
     * Purpose:         MyLocation Button Click Handler
     * Precondition:    MyLocation button clicked
     * Postcondition:   check permission
     ************************************************************************/
    private void getMyLocation()
    {
        final int perm = ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION);
        final int rest = PackageManager.PERMISSION_GRANTED;
        if (perm != rest)
        {
            // Request the permission
            ActivityCompat.requestPermissions(ActivityPage_Map.this,
                                              new String[] {Manifest.permission.ACCESS_FINE_LOCATION },
                                              LOCATION_PERMISSION_REQUEST_CODE);
            // -> onRequestPermissionsResult(...)
        }
        else
        {
            this.moveToMyLocation();
        }
    }

    /************************************************************************
     * Purpose:         MyLocation Button Click Handler
     * Precondition:    On Request Permission Changed
     * Postcondition:   check granted permission result
     ************************************************************************/
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case LOCATION_PERMISSION_REQUEST_CODE:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED)
                {
                    this.moveToMyLocation();
                }
                else
                {
                    Toast.makeText(ActivityPage_Map.this,
                                   getString(R.string.permission_denied),
                                   Toast.LENGTH_LONG)
                         .show();
                }
            }
        }
    }

    /************************************************************************
     * Purpose:         MyLocation Button Click Handler
     * Precondition:    MyLocation button clicked & Permission granted
     * Postcondition:   Move Google Map to Current Location
     ************************************************************************/
    private void moveToMyLocation()
    {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG);
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

        this.PlacesClient.findCurrentPlace(request)
                         .addOnCompleteListener(this);
        // -> onComplete(...)
    }

    /************************************************************************
     * Purpose:         MyLocation Button Click Handler
     * Precondition:    moveToMyLocation Listener Completed
     * Postcondition:   Change Location Bias
     *                  Change Current Location Marker
     ************************************************************************/
    @Override
    public void onComplete(@NonNull Task task)
    {
        if (task.isSuccessful())
        {
            FindCurrentPlaceResponse response = (FindCurrentPlaceResponse) task.getResult();
            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods())
            {
                // Debug Purpose Only
                /*Log.i(TAG, String.format("Place '%s' has likelihood: %f",
                                         this.Location.getName(),
                                         placeLikelihood.getLikelihood()));*/

                // Search Location Bias
                final LatLng latlng = placeLikelihood.getPlace().getLatLng();
                this.AutoFragment.setLocationBias(this.toBounds(latlng));

                // Move Camera
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng,
                                                                        MAP_DEFAULT_ZOOM);
                this.Map.animateCamera(update, CAMERA_DEFAULT_SPEED, null);
            }
        }
        else
        {
            Exception exception = task.getException();
            if (exception instanceof ApiException)
            {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
            }
        }
    }

    /************************************************************************
     * Purpose:         Finish Activity
     * Precondition:    .
     * Postcondition:   Return to the previous activity with the position
     ************************************************************************/
    private void onActivityComplete()
    {
        if (this.Location != null)
        {
            Intent returnIntent = new Intent().putExtra(getString(R.string.intent_location), this.Location);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
        else
        {
            Toast.makeText(ActivityPage_Map.this,
                           getString(R.string.activitymap_notSelected),
                           Toast.LENGTH_LONG)
                 .show();
        }
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
     * Purpose:         Utility
     * Precondition:    .
     * Postcondition:   Get Bounds
     ************************************************************************/
    public RectangularBounds toBounds(LatLng center)
    {
        double distance = LOCATION_BIAS_RADIUS * Math.sqrt(2.0);
        LatLng southwest = SphericalUtil.computeOffset(center, distance, 225.0);
        LatLng northeast = SphericalUtil.computeOffset(center, distance, 45.0);

        return RectangularBounds.newInstance(southwest, northeast);
    }
}

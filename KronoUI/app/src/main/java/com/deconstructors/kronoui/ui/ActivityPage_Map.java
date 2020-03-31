package com.deconstructors.kronoui.ui;

import android.Manifest;
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

import com.deconstructors.kronoui.R;
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

    // Loc
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
        this.AutoFragment.setPlaceFields(Arrays.asList(Place.Field.ID,
                                                       Place.Field.NAME,
                                                       Place.Field.LAT_LNG));
        this.AutoFragment.setOnPlaceSelectedListener(new PlaceSelectionListener()
        {
            @Override
            public void onPlaceSelected(@NonNull Place place)
            {
                LatLng latlng = new LatLng(place.getLatLng().latitude,
                                           place.getLatLng().longitude);
                ActivityPage_Map.this.setMarkerPosition(latlng, place.getName());
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
                this.addLocation();
                break;
            }
        }
    }

    /************************************************************************
     * Purpose:         MyLocation Button Click Handler
     * Precondition:    My Location button clicked
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
                Log.i(TAG, String.format("Place '%s' has likelihood: %f",
                                         placeLikelihood.getPlace().getName(),
                                         placeLikelihood.getLikelihood()));

                // Search Location Bias
                LatLng latlng = placeLikelihood.getPlace().getLatLng();
                /*this.AutoFragment.setLocationBias(RectangularBounds.newInstance(
                        new LatLng(latlng.latitude, latlng.latitude),
                        new LatLng(latlng.latitude + 0.03, latlng.latitude + 0.03)));*/

                // Current Location Marker
                setMarkerPosition(latlng, "Your Location");
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
    private void addLocation()
    {

    }

    /************************************************************************
     * Purpose:         Utility
     * Precondition:    .
     * Postcondition:   Change Google Map's marker position
     ************************************************************************/
    public void setMarkerPosition(LatLng latlng, String name)
    {
        MarkerOptions options = new MarkerOptions().position(latlng).title(name);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng, MAP_DEFAULT_ZOOM);

        this.Map.clear();
        this.Map.addMarker(options);
        this.Map.animateCamera(update, CAMERA_DEFAULT_SPEED, null);
    }
}

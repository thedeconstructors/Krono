package com.deconstructors.krono.module;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Location implements Parcelable
{
    private String Name;
    private String Address;
    private LatLng LatLng;

    /************************************************************************
     * Purpose:         Constructors
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public Location() { }

    public Location(String name, String address, LatLng latlng)
    {
        this.Name = name;
        this.Address = address;
        this.LatLng = latlng;
    }

    public Location(String name, String address, double latitude, double longitude)
    {
        this.Name = name;
        this.Address = address;
        this.LatLng = new LatLng(latitude, longitude);
    }

    /************************************************************************
     * Purpose:         Parcelable Override Methods
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private Location(Parcel in)
    {
        this.Name = in.readString();
        this.Address = in.readString();
        this.LatLng = new LatLng(in.readDouble(), in.readDouble());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.Name);
        dest.writeString(this.Address);
        dest.writeDouble(this.LatLng.latitude);
        dest.writeDouble(this.LatLng.longitude);
    }

    @Override
    public int describeContents() { return 0; }

    public static final Creator<Location> CREATOR = new Creator<Location>()
    {
        @Override
        public Location createFromParcel(Parcel in) { return new Location(in); }

        @Override
        public Location[] newArray(int size) { return new Location[size]; }
    };

    /************************************************************************
     * Purpose:         Getters and Setters
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public String getName() { return this.Name; }
    public void setName(String name) { this.Name = name; }

    public String getAddress() { return this.Address; }
    public void setAddress(String address) { this.Address = address; }

    public LatLng getLatLng() { return this.LatLng; }
    public void setLatLng(LatLng latlng) { this.LatLng = latlng; }
}

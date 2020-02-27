package com.demo.planactivityuserdemo.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.firestore.GeoPoint;
import java.util.Date;

public class User implements Parcelable
{
    private String UserID;
    private String Email;
    private String DisplayName;
    private GeoPoint GeoPoint;

    /************************************************************************
     * Purpose:         Constructors
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public User() { }

    public User(String userID, String email, String displayName, GeoPoint geoPoint)
    {
        this.UserID = userID;
        this.Email = email;
        this.DisplayName = displayName;
        this.GeoPoint = geoPoint;
    }

    /************************************************************************
     * Purpose:         Parcel Stuffs
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    protected User(Parcel in)
    {
        this.UserID = in.readString();
        this.Email = in.readString();
        this.DisplayName = in.readString();
        this.GeoPoint = new GeoPoint(in.readDouble(), in.readDouble());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.UserID);
        dest.writeString(this.Email);
        dest.writeString(this.DisplayName);
        dest.writeDouble(this.GeoPoint.getLatitude());
        dest.writeDouble(this.GeoPoint.getLongitude());
    }

    @Override
    public int describeContents() { return 0; }

    public static final Creator<User> CREATOR = new Creator<User>()
    {
        @Override
        public User createFromParcel(Parcel in) { return new User(in); }

        @Override
        public User[] newArray(int size) { return new User[size]; }
    };

    /************************************************************************
     * Purpose:         Getters and Setters
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public String getUserID() { return this.UserID; }
    public void setUserID(String userID) { this.UserID = userID; }

    public String getEmail() { return this.Email; }
    public void setEmail(String email) { this.Email = email; }

    public String getDisplayName() { return this.DisplayName; }
    public void setDisplayName(String displayName) { this.DisplayName = displayName; }

    public GeoPoint getGeoPoint() { return this.GeoPoint; }
    public void setGeoPoint(GeoPoint geoPoint) { this.GeoPoint = geoPoint; }
}

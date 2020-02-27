package com.demo.planactivityuserdemo.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.firestore.GeoPoint;
import java.util.Date;

public class Activity implements Parcelable
{
    private String ActivityID;
    private String Title;
    private String Description;
    private Date DueDate;
    private GeoPoint GeoPoint;

    /************************************************************************
     * Purpose:         Constructors
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public Activity() { }

    public Activity(String activityID, String title, String description, Date dueDate, GeoPoint geoPoint)
    {
        this.ActivityID = activityID;
        this.Title = title;
        this.Description = description;
        this.DueDate = dueDate;
        this.GeoPoint = geoPoint;
    }

    /************************************************************************
     * Purpose:         Parcel Stuffs
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    protected Activity(Parcel in)
    {
        this.ActivityID = in.readString();
        this.Title = in.readString();
        this.Description = in.readString();
        this.DueDate = new Date(in.readLong());
        this.GeoPoint = new GeoPoint(in.readDouble(), in.readDouble());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.ActivityID);
        dest.writeString(this.Title);
        dest.writeString(this.Description);
        dest.writeLong(this.DueDate.getTime());
        dest.writeDouble(this.GeoPoint.getLatitude());
        dest.writeDouble(this.GeoPoint.getLongitude());
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<Activity> CREATOR = new Creator<Activity>()
    {
        @Override
        public Activity createFromParcel(Parcel in)
        {
            return new Activity(in);
        }

        @Override
        public Activity[] newArray(int size)
        {
            return new Activity[size];
        }
    };

    /************************************************************************
     * Purpose:         Getters and Setters
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public String getActivityID() { return this.ActivityID; }
    public void setActivityID(String activityID) { this.ActivityID = activityID; }

    public String getTitle() { return this.Title; }
    public void setTitle(String title) { this.Title = title; }

    public String getDescription() { return this.Description; }
    public void setDescription(String description) { this.Description = description; }

    public Date getDueDate() { return this.DueDate; }
    public void setDueDate(Date dueDate) { this.DueDate = dueDate; }

    public GeoPoint getGeoPoint() { return this.GeoPoint; }
    public void setGeoPoint(GeoPoint geoPoint) { this.GeoPoint = geoPoint; }
}

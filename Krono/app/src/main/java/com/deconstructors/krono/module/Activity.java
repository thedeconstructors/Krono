package com.deconstructors.krono.module;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class Activity implements Parcelable
{
    // Following Alphabetical Order
    // Because that's how it is in the DB
    // You guys can change this to whatever
    private String ActivityID;
    private String Description;
    private Integer Duration;
    private Location Location;
    private List<String> PlanIDs;
    private String Timestamp;
    private String Title;

    /************************************************************************
     * Purpose:         Default Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public Activity()
    {
        this.Location = new Location();
        this.PlanIDs = new ArrayList<String>();
    }

    public Activity(Activity activity)
    {
        this.ActivityID = activity.ActivityID;
        this.Description = activity.Description;
        this.Duration = activity.Duration;
        this.Location = activity.Location;
        this.PlanIDs = activity.PlanIDs;
        this.Timestamp = activity.Timestamp;
        this.Title = activity.Title;
    }

    public Activity(String activityID, String description, Integer duration, LatLng latlng,
                    String location, List<String> planIDs, String timestamp, String title)
    {
        this.ActivityID = activityID;
        this.Description = description;
        this.Duration = duration;
        this.Location.setLatLng(latlng);
        this.Location.setName(location);
        this.PlanIDs = new ArrayList<>(this.PlanIDs);
        this.Timestamp = timestamp;
        this.Title = title;
    }

    /************************************************************************
     * Purpose:         Parcelable Override Methods
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    protected Activity(Parcel in)
    {
        this.ActivityID = in.readString();
        this.Description = in.readString();
        this.Duration = in.readInt();
        this.Location = new Location(in.readString(),
                                     in.readString(),
                                     in.readDouble(),
                                     in.readDouble());
        this.PlanIDs = new ArrayList<>();
        in.readList(this.PlanIDs, null);
        this.Timestamp = in.readString();
        this.Title = in.readString();
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

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.ActivityID);
        dest.writeString(this.Description);
        dest.writeInt(this.Duration);
        dest.writeString(this.Location.getName());
        dest.writeString(this.Location.getAddress());
        dest.writeDouble(this.Location.getLatLng().longitude);
        dest.writeDouble(this.Location.getLatLng().latitude);
        dest.writeList(this.PlanIDs);
        dest.writeString(this.Timestamp);
        dest.writeString(this.Title);
    }

    /************************************************************************
     * Purpose:         Getters and Setters for Activity Class
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public String getActivityID() { return this.ActivityID; }
    public void setActivityID(String activityID) { this.ActivityID = activityID; }

    public String getDescription(){ return this.Description; }
    public void setDescription(String description) { this.Description = description; }

    public Integer getDuration(){ return this.Duration; }
    public void setDuration(Integer duration) { this.Duration = duration; }

    //public String getOwnerID(){ return this.OwnerID; }
    //public void setOwnerID(String ownerID) {this.OwnerID = ownerID; }

    // address
    public String getGeoAddr(){ return this.Location.getAddress(); }
    public void setGeoAddr(String geoAddr){ this.Location.setAddress(geoAddr); }

    public String getGeoName(){ return this.Location.getName(); }
    public void setGeoName(String geoName){ this.Location.setName(geoName); }

    public GeoPoint getGeoPoint(){ return new GeoPoint(this.Location.getLatLng().latitude,
                                                     this.Location.getLatLng().longitude); }
    public void setGeoPoint(GeoPoint geoPoint){ this.Location.setLatLng(new LatLng(geoPoint.getLatitude(),
                                                                                   geoPoint.getLongitude())); }

    public Location getLocation(){ return this.Location; }
    public void setLocation(Location location){ this.Location = location; }

    //
    public List<String> getPlanIDs(){ return this.PlanIDs; }
    public void setPlanIDs(List<String> planIDs){ this.PlanIDs = new ArrayList<>(this.PlanIDs);; }

    public String getTimestamp(){ return this.Timestamp; }
    public void setTimestamp(String timestamp){ this.Timestamp = timestamp; }

    public String getTitle() { return this.Title; }
    public void setTitle(String title) { this.Title = title; }
}

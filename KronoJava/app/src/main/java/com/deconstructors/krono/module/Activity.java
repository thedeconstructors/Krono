package com.deconstructors.krono.module;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class Activity implements Parcelable
{
    private String ActivityID;
    private String Title;
    private String Description;
    private Timestamp Timestamp;

    private boolean isSelected;

    /************************************************************************
     * Purpose:         Default Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public Activity() { }

    /************************************************************************
     * Purpose:         Copy Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public Activity(Activity activity)
    {
        this.ActivityID = activity.ActivityID;
        this.Title = activity.Title;
        this.Description = activity.Description;
        this.Timestamp = activity.Timestamp;

        this.isSelected = activity.isSelected;
    }

    /************************************************************************
     * Purpose:         3 Arg Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public Activity(String activityID, String title, String description, Timestamp timestamp)
    {
        this.ActivityID = activityID;
        this.Title = title;
        this.Description = description;
        this.Timestamp = timestamp; //timestamp.toString();

        this.isSelected = false;
    }

    /************************************************************************
     * Purpose:         Parcelable Override Methods
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    protected Activity(Parcel in)
    {
        ActivityID = in.readString();
        Title = in.readString();
        Description = in.readString();
        isSelected = in.readByte() != 0;
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
        dest.writeString(ActivityID);
        dest.writeString(Title);
        dest.writeString(Description);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    /************************************************************************
     * Purpose:         Getters and Setters for Activity Class
     * Precondition:    .
     * Postcondition:   .
     * Warning:         Changing "get" to "Get" crashed the activity even
     *                  though, it was used in only 3 locations.
     *                  I have no idea why this happened, but probably due
     *                  to the plan table using the activity array.
     ************************************************************************/
    public String getActivityID() { return this.ActivityID; }

    public String getTitle() { return this.Title; }
    public void setTitle(String title) { this.Title = title; }

    public String getDescription(){ return this.Description; }
    public void setDescription(String description) { this.Description = description; }

    public Timestamp getTimestamp(){ return this.Timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.Timestamp = timestamp; }

    public String getId() { return this.ActivityID; }
    public void setId(String id) { this.ActivityID = id; }

    /************************************************************************
     * Purpose:         Is this Activity Selected
     * Precondition:    .
     * Postcondition:   Maybe we should find a more resource saving method
     *                  like, Item Touch Helper if supported.
     *                  But it is what it is for the tomorrow's presentation
     ************************************************************************/
    public boolean isSelected() { return this.isSelected; }
    public void setSelected(boolean isSelected) { this.isSelected = isSelected; }
}

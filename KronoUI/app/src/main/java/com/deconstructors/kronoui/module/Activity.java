package com.deconstructors.kronoui.module;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.Timestamp;

public class Activity implements Parcelable
{
    private String ActivityID;
    private String PlanID;
    private String Title;
    private String Description;
    private String Timestamp;

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
        this.PlanID = activity.PlanID;
        this.Title = activity.Title;
        this.Description = activity.Description;
        this.Timestamp = activity.Timestamp;
    }

    /************************************************************************
     * Purpose:         3 Arg Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public Activity(String activityID, String planID, String title, String description, String timestamp)
    {
        this.ActivityID = activityID;
        this.PlanID = planID;
        this.Title = title;
        this.Description = description;
        this.Timestamp = timestamp;
    }

    /************************************************************************
     * Purpose:         Parcelable Override Methods
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    protected Activity(Parcel in)
    {
        this.ActivityID = in.readString();
        this.PlanID = in.readString();
        this.Title = in.readString();
        this.Description = in.readString();
        this.Timestamp = in.readString();
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
        dest.writeString(this.PlanID);
        dest.writeString(this.Title);
        dest.writeString(this.Description);
        dest.writeString(this.Timestamp);
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
    public void setActivityID(String activityID) { this.ActivityID = activityID; }

    public String getPlanID() { return this.PlanID; }
    public void setPlanID(String planID) { this.PlanID = planID; }

    public String getTitle() { return this.Title; }
    public void setTitle(String title) { this.Title = title; }

    public String getDescription(){ return this.Description; }
    public void setDescription(String description) { this.Description = description; }

    public String getTimestamp(){ return this.Timestamp; }
    public void setTimestamp(String timestamp) { this.Timestamp = timestamp; }
}

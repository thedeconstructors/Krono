package com.deconstructors.krono.module;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Activity implements Parcelable
{
    private String ActivityID;
    private ArrayList<String> PlanIDs;
    private String Title;
    private String Description;
    private Integer Duration;
    private String OwnerID;

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
        this.PlanIDs = activity.PlanIDs;
        this.Title = activity.Title;
        this.Description = activity.Description;
        this.Duration = activity.Duration;
        this.OwnerID = activity.OwnerID;
    }

    /************************************************************************
     * Purpose:         3 Arg Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    //public Activity(String activityID, String planID, String title, String description, String timestamp)
    public Activity(String activityID, String title, String description, Integer duration, String ownerID)
    {
        this.ActivityID = activityID;
        this.Title = title;
        this.Description = description;
        this.Duration = duration;
        this.PlanIDs = new ArrayList<>();
        this.OwnerID = ownerID;
    }

    /************************************************************************
     * Purpose:         Parcelable Override Methods
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    protected Activity(Parcel in)
    {
        this.ActivityID = in.readString();
        Object[] planid_data = in.readArray(String.class.getClassLoader());
        if (planid_data.length == 0)
            this.PlanIDs = new ArrayList<>();
        else
            this.PlanIDs = new ArrayList<>(
                Arrays.asList(
                        (String[])planid_data)
                );
        this.Title = in.readString();
        this.Description = in.readString();
        this.Duration = in.readInt();
        this.OwnerID = in.readString();
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
        dest.writeArray(this.PlanIDs.toArray());
        dest.writeString(this.Title);
        dest.writeString(this.Description);
        dest.writeInt(this.Duration);
        dest.writeString(this.OwnerID);
    }

    /************************************************************************
     * Purpose:         Getters and Setters for Activity Class
     * Precondition:    .
     * Postcondition:   .
     * Warning:         Changing "get" to "Get" crashed the activity_main even
     *                  though, it was used in only 3 locations.
     *                  I have no idea why this happened, but probably due
     *                  to the plan table using the activity_main array.
     ************************************************************************/
    public String getActivityID() { return this.ActivityID; }
    public void setActivityID(String activityID) { this.ActivityID = activityID; }

    public ArrayList<String> getPlanIDs() { return this.PlanIDs; }
    public void setPlanIDs(ArrayList<String> planIds) { this.PlanIDs = planIds; }

    public String getTitle() { return this.Title; }
    public void setTitle(String title) { this.Title = title; }

    public String getDescription(){ return this.Description; }
    public void setDescription(String description) { this.Description = description; }

    public Integer getDuration(){ return this.Duration; }
    public void setDuration(Integer duration) { this.Duration = duration; }

    public String getOwnerID(){ return this.OwnerID; }
    public void setOwnerID(String ownerID) {this.OwnerID = ownerID; }
}

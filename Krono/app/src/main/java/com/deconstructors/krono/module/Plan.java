package com.deconstructors.krono.module;

import android.os.Parcel;
import android.os.Parcelable;

public class Plan implements Parcelable
{
    private String PlanID;
    private String Title;
    private String Description;
    private String OwnerID;
    //private int ActivitySize;

    /************************************************************************
     * Purpose:         Constructors
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public Plan() { }

    public Plan(String planID, String ownerID, String title, String description)
    {
        this.PlanID = planID;
        this.Title = title;
        this.Description = description;
        this.OwnerID = ownerID;
        //this.ActivitySize = activitySize;
    }

    /************************************************************************
     * Purpose:         Parcelable Override Methods
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    protected Plan(Parcel in)
    {
        this.PlanID = in.readString();
        this.Title = in.readString();
        this.Description = in.readString();
        this.OwnerID = in.readString();
        //this.ActivitySize = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.PlanID);
        dest.writeString(this.Title);
        dest.writeString(this.Description);
        dest.writeString(this.OwnerID);
        //dest.writeInt(this.ActivitySize);
    }

    @Override
    public int describeContents() { return 0; }

    public static final Creator<Plan> CREATOR = new Creator<Plan>()
    {
        @Override
        public Plan createFromParcel(Parcel in) { return new Plan(in); }

        @Override
        public Plan[] newArray(int size) { return new Plan[size]; }
    };

    /************************************************************************
     * Purpose:         Getters and Setters
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public String getPlanID() { return this.PlanID; }
    public void setPlanID(String planID) { this.PlanID = planID; }

    public String getTitle() { return this.Title; }
    public void setTitle(String title) { this.Title = title; }

    public String getDescription() { return this.Description; }
    public void setDescription(String description) { this.Description = description; }

    public String getOwnerID() { return OwnerID; }
    public void setOwnerID(String ownerID) { OwnerID = ownerID; }

    //public int getActivitySize() { return ActivitySize; }
    //public void setActivitySize(int activitySize) { ActivitySize = activitySize; }
}
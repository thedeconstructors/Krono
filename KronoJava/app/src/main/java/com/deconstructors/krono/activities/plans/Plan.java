package com.deconstructors.krono.activities.plans;

import android.os.Parcel;
import android.os.Parcelable;

public class Plan implements Parcelable
{
    private String PlanID;
    private String Title;
    private String Description;

    /************************************************************************
     * Purpose:         Constructors
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public Plan() { }

    public Plan(String planID, String title, String description)
    {
        this.PlanID = planID;
        this.Title = title;
        this.Description = description;
    }

    /************************************************************************
     * Purpose:         Parcel Stuffs
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    protected Plan(Parcel in)
    {
        this.PlanID = in.readString();
        this.Title = in.readString();
        this.Description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.PlanID);
        dest.writeString(this.Title);
        dest.writeString(this.Description);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<Plan> CREATOR = new Creator<Plan>()
    {
        @Override
        public Plan createFromParcel(Parcel in)
        {
            return new Plan(in);
        }

        @Override
        public Plan[] newArray(int size)
        {
            return new Plan[size];
        }
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
}

/*
public class Plan {

    private String _name;
    //private Date _date;
    private boolean _isPublic;
    private Activity[] _activities;
    private int _currentIndex;

    public Plan() {
        _name = "Plan";
        //_date = new Date();
        _isPublic = false;
        _activities = new Activity[20];
        _currentIndex = 0;

    }

    public Plan(String name, boolean isPublic) {
        _name = name;

        //A lot of the methods in the Date class here are deprecated. Might update _date to a DateTime variable later.
        //currently commenting out date so as not to use Date type
        //_date = new Date(date.getYear(), date.getMonth(), date.getDay());

        _isPublic = isPublic;
        _activities = new Activity[20];
        _currentIndex = 0;
    }

    //Need to add logic to deal with overlapping activities.
    public void AddActivity(Activity activity) {
        _activities[_currentIndex] = activity;
        _currentIndex++;
    }

    public void RemoveActivity(String name) {
        Activity[] newActivities = new Activity[20];

        for (int i = 0; i < _currentIndex; i++) {
            int j = 0;
            if (name != _activities[i].getTitle()) {
                newActivities[j] = _activities[i];
                j++;
            }
        }

        _activities = newActivities;
        _currentIndex--;
    }


    //Setters and Getters
    public void SetName(String name) {
        _name = name;
    }

    */
/*
    public void SetDate(Date date) {
        _date = date;
    }*//*


    public void SetIsPublic(boolean isPublic) {
        _isPublic = isPublic;
    }

    public String GetName() {
        return _name;
    }

    */
/*
    public Date GetDate() {
        return _date;
    }*//*


    public boolean GetIsPublic() {
        return _isPublic;
    }

    public Activity[] GetActivities() {
        return _activities;
    }
}*/

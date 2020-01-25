package com.deconstructors.krono;

import java.util.Date;

public class Plan {

    private String _name;
    private Date _date;
    private boolean _isPublic;
    private Activity[] _activities;
    private int _currentIndex;

    public Plan() {
        _name = "Plan";
        _date = new Date();
        _isPublic = false;
        _activities = new Activity [20];
        _currentIndex = 0;

    }

    public Plan(String name, Date date, boolean isPublic) {
        _name = name;

        //A lot of the methods in the Date class here are deprecated. Might update _date to a DateTime variable later.
        _date = new Date(date.getYear(), date.getMonth(), date.getDay());

        _isPublic = isPublic;
        _activities = new Activity [20];
        _currentIndex = 0;
    }

    //Need to add logic to deal with overlapping activities.
    public void AddActivity(Activity activity) {
        _activities[_currentIndex] = activity;
        _currentIndex++;
    }

    public void RemoveActivity(String name) {
        Activity[] newActivities = new Activity [20];

        for (int i  = 0; i < _currentIndex; i++)
        {
            int j = 0;
            if (name != _activities[i].GetName())
            {
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

    public void SetDate(Date date) {
        _date = date;
    }

    public void SetIsPublic(boolean isPublic) {
        _isPublic = isPublic;
    }

    public String GetName() {
        return _name;
    }

    public Date GetDate() {
        return _date;
    }

    public boolean GetIsPublic() {
        return _isPublic;
    }

    public Activity[] GetActivities() {
        return _activities;
    }
package com.deconstructors.krono.activities.activities;

import com.google.firebase.Timestamp;

/************************************************************************
 * Class:           Activity
 * Purpose:         To Add the Values from Activity Table to Recycler View
 ************************************************************************/
public class Activity
{
    private String _activityId;
    private String _title;
    private String _description;
    //private Timestamp _timestamp;
    private String _duration;
    private boolean _isPublic;
    private String _ownerId;

    private boolean _isSelected;

    //String location_start;
    //String location_end;

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
        this._activityId = activity._activityId;
        this._title = activity._title;
        this._description = activity._description;
        //this._timestamp = activity._timestamp;
        this._duration = activity._duration;
        this._isPublic = activity._isPublic;
        this._ownerId = activity._ownerId;

        this._isSelected = activity._isSelected;
    }

    /************************************************************************
     * Purpose:         3 Arg Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public Activity(String title, String description, String duration, boolean isPublic, String ownerId)
    {
        //this._activityId = activityID;
        this._title = title;
        this._description = description;
        this._duration = duration; //timestamp.toString();
        this._isPublic = isPublic;
        this._ownerId = ownerId;

        this._isSelected = false;
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
    public String getActivityID() { return _activityId; }

    public String getTitle() { return _title; }
    public void setTitle(String title) { this._title = title; }

    public String getDescription(){ return _description; }
    public void setDescription(String description) { this._description = description; }

    //public Timestamp getTimestamp(){ return _timestamp; }
    //public void setTimestamp(Timestamp timestamp) { this._timestamp = timestamp; }

    public String getDuration() { return _duration; }
    public void setDuration(String duration) { _duration = duration; }

    public String getId() { return _activityId; }
    public void setId(String id) { _activityId = id; }

    public String getOwnerId() { return _ownerId; }
    public void setOwnerId(String id) { _ownerId = id; }

    public boolean IsPublic() { return _isPublic; }
    public void setIsPublic(boolean set) { _isPublic = set; }

    /************************************************************************
     * Purpose:         Is this Activity Selected
     * Precondition:    .
     * Postcondition:   Maybe we should find a more resource saving method
     *                  like, Item Touch Helper if supported.
     *                  But it is what it is for the tomorrow's presentation
     ************************************************************************/
    public boolean isSelected() { return _isSelected; }
    public void setSelected(boolean isSelected) { _isSelected = isSelected; }
}

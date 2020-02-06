package com.deconstructors.krono;

/************************************************************************
 * Class:           Activity
 * Purpose:         To Add the Values from Activity Table to Recycler View
 ************************************************************************/
public class Activity
{
    private String _title;
    private String _description;
    private boolean _isPublic;
    private String _duration;
    //Integer user_id;

    //boolean collaborative;
    //boolean publicity;

    //String location_start;
    //String location_end;
    //String time_start;
    //String time_end;

    /************************************************************************
     * Purpose:         Default Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public Activity() { }

    /************************************************************************
     * Purpose:         2 Arg Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public Activity(String title, String description, String duration)
    {
        this._title = title;
        this._description = description;
        this._duration = duration;
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
    public String getTitle()
    {
        return _title;
    }
    public void setTitle(String title)
    {
        this._title = title;
    }
    public String getDescription()
    {
        return _description;
    }
    public void setDescription(String description)
    {
        this._description = description;
    }
    public boolean GetIsPublic() {
        return _isPublic;
    }

    public void SetIsPublic(boolean isPublic) {
        _isPublic = isPublic;
    }

    public String getDuration() {
        return _duration + "m";
    }

    public void setDuration(String duration) {
        _duration = duration;
    }
}

package com.deconstructors.krono;

/************************************************************************
 * Class:           Activity
 * Purpose:         To Add the Values from Activity Table to Recycler View
 ************************************************************************/
public class Activity
{
    private String m_title;
    private String m_description;
    private boolean _isPublic;
    private int _duration;
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
    public Activity(String title, String description)
    {
        this.m_title = title;
        this.m_description = description;
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
        return m_title;
    }
    public void setTitle(String title)
    {
        this.m_title = title;
    }
    public String getDescription()
    {
        return m_description;
    }
    public void setDescription(String description)
    {
        this.m_description = description;
    }
    public boolean GetIsPublic() {
        return _isPublic;
    }

    public void SetIsPublic(boolean isPublic) {
        _isPublic = isPublic;
    }

    public int GetDuration() {
        return _duration;
    }

    public void SetDuration(int duration) {
        _duration = duration;
    }
}

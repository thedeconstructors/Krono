package com.deconstructors.krono.activities.plans;

/************************************************************************
 * Class:           Plans
 * Purpose:         To Add the Values from Plan Table to Recycler View
 ************************************************************************/
public class Plans
{
    String _planId;
    String _title;
    String _description;
    String _startTime;
    String _ownerId;
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
    public Plans() { }

    /************************************************************************
     * Purpose:         2 Arg Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public Plans(String title, String description, String startTime, String ownerId)
    {
        this._title = title;
        this._description = description;
        this._startTime = startTime;
        this._ownerId = ownerId;
    }

    /************************************************************************
     * Purpose:         Getters and Setters for Plan Class
     * Precondition:    .
     * Postcondition:   .
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

    public String getPlanId() { return _planId; }
    public void setPlanId(String planId) { _planId = planId; }

    public String getStartTime() { return _startTime; }
    public void setStartTime(String startTime) { _startTime = startTime; }

    public String getOwnerId() { return _ownerId; }
    public void setOwnerId(String id) { _ownerId = id; }
}

package com.deconstructors.krono.activities.plans;

/************************************************************************
 * Class:           Plans
 * Purpose:         To Add the Values from Plan Table to Recycler View
 ************************************************************************/
public class Plans
{
    String m_title;
    String m_description;
    String m_startTime;
    String m_planId;
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
    public Plans(String title, String startTime)
    {
        this.m_title = title;
        this.m_startTime = startTime;
    }

    /************************************************************************
     * Purpose:         Getters and Setters for Plan Class
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public String getTitle()
    {
        return m_title;
    }
    public void setTitle(String title)
    {
        this.m_title = title;
    }
    public String getStartTime() { return m_startTime; }
    public void setStartTime(String time) { m_startTime = time; }
    public String getDescription()
    {
        return m_description;
    }
    public void setDescription(String description)
    {
        this.m_description = description;
    }
    public String getId() { return m_planId; }
    public void setId(String id) { m_planId = id; }
}

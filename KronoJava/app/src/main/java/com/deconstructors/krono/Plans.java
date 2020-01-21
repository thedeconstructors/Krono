package com.deconstructors.krono;

/************************************************************************
 * Class:           Plans
 * Purpose:         To Add the Values from Plan Table to Recycler View
 ************************************************************************/
public class Plans
{
    String m_title;
    String m_description;
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
    public Plans(String title, String description)
    {
        this.m_title = title;
        this.m_description = description;
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
    public String getDescription()
    {
        return m_description;
    }
    public void setDescription(String description)
    {
        this.m_description = description;
    }
}

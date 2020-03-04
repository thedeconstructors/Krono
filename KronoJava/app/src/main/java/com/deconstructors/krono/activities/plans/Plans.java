package com.deconstructors.krono.activities.plans;

/************************************************************************
 * Class:           Plans
 * Purpose:         To Add the Values from Plan Table to Recycler View
 ************************************************************************/
public class Plans
{
    private String PlanID;
    private String Title;
    private String Description;
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
    public Plans(String planID, String title, String description)
    {
        this.PlanID = planID;
        this.Title = title;
        this.Description = description;
    }

    /************************************************************************
     * Purpose:         Getters and Setters for Plan Class
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/

    public String getPlanID()
    {
        return PlanID;
    }
    public void setPlanID(String planID)
    {
        PlanID = planID;
    }
    public String getTitle()
    {
        return Title;
    }
    public void setTitle(String title)
    {
        this.Title = title;
    }
    public String getDescription()
    {
        return Description;
    }
    public void setDescription(String description)
    {
        this.Description = description;
    }
}

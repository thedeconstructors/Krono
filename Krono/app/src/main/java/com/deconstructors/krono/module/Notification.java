package com.deconstructors.krono.module;

public class Notification
{
    private String Header;
    private String Description;

    /************************************************************************
     * Purpose:         Default Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public Notification()
    { }

    /************************************************************************
     * Purpose:         3 Arg Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public Notification(String header, String description)
    {
        this.Header = header;
        this.Description = description;
    }

    /************************************************************************
     * Purpose:         Getters and Setters for Notification Class
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public String getHeader() { return Header; }
    public String getDescription() { return Description; }

    public void setHeader(String header) { this.Header = header; }
    public void setDescription(String description) { this.Description = description; }
}

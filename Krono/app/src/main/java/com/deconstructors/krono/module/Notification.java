package com.deconstructors.krono.module;

public class Notification
{
    private String Header;
    private String Description;
    private String Timestamp;

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
    public Notification(String header, String description, String timestamp)
    {
        this.Header = header;
        this.Description = description;
        this.Timestamp = timestamp;
    }

    /************************************************************************
     * Purpose:         Getters and Setters for Notification Class
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public String getHeader() { return Header; }
    public String getDescription() { return Description; }
    public String getTimestamp() { return Timestamp; }

    public void setHeader(String header) { this.Header = header; }
    public void setDescription(String description) { this.Description = description; }
    public void setTimestamp(String timestamp) { this.Timestamp = timestamp; }
}

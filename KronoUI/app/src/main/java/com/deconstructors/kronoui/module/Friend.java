package com.deconstructors.kronoui.module;

public class Friend
{
    private String FriendEmail;
    private Boolean FriendRequest;

    /************************************************************************
     * Purpose:         Default Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public Friend() { }

    /************************************************************************
     * Purpose:         Copy Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public Friend(Friend friend)
    {
        this.FriendEmail = friend.FriendEmail;
        this.FriendRequest = friend.FriendRequest;
    }

    /************************************************************************
     * Purpose:         3 Arg Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public Friend(String friendEmail, Boolean friendRequest)
    {
        this.FriendEmail = friendEmail;
        this.FriendRequest = friendRequest;
    }

    /************************************************************************
     * Purpose:         Getters and Setters for Activity Class
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public String getFriendEmail() { return this.FriendEmail; }
    public void setFriendEmail(String friendEmail) { this.FriendEmail = friendEmail; }

    public Boolean getFriendRequest() { return this.FriendRequest; }
    public void setFriendRequest(Boolean friendRequest) { this.FriendRequest = friendRequest; }
}

package com.deconstructors.kronoui.module;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable
{
    private String DisplayName;
    private String Email;
    private String ProfileURL;
    private String Bio;
    private List<String> FriendList;

    /************************************************************************
     * Purpose:         Constructors
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public User(){}

    public User(String displayName, String email, String bio, List<String> friendList)
    {
        this.DisplayName = displayName;
        this.Email = email;
        this.Bio = bio;
        this.FriendList = new ArrayList<>(friendList);
    }

    /************************************************************************
     * Purpose:         Parcelable Override Methods
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    protected User(Parcel in)
    {
        this.DisplayName = in.readString();
        this.Email = in.readString();
        this.Bio = in.readString();
        this.ProfileURL = in.readString();
        this.FriendList = new ArrayList<String>();
        in.readList(this.FriendList, User.class.getClassLoader());
    }

    public static final Creator<User> CREATOR = new Creator<User>()
    {
        @Override
        public User createFromParcel(Parcel in)
        {
            return new User(in);
        }

        @Override
        public User[] newArray(int size)
        {
            return new User[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.DisplayName);
        dest.writeString(this.Email);
        dest.writeString(this.ProfileURL);
        dest.writeList(this.FriendList);
    }

    /************************************************************************
     * Purpose:         Getters and Setters
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public String getDisplayName() { return this.DisplayName; }
    public void setDisplayName(String displayName) { this.DisplayName = displayName; }

    public String getEmail() { return this.Email; }
    public void setEmail(String email) { this.Email = email; }

    public List<String> getFriendList() { return this.FriendList; }
    public void setFriendList(List<String> friendList) { this.FriendList = friendList; }

    public String getBio() { return this.Bio; }
    public void setBio(String bio) { this.Bio = bio; }
}

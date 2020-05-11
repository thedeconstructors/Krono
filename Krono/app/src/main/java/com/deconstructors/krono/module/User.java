package com.deconstructors.krono.module;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable
{
    private String Uid;
    private String DisplayName;
    private String Email;
    private String ProfileURL;
    private String Bio;

    /************************************************************************
     * Purpose:         Constructors
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public User(){}

    public User(String uid, String displayName, String email, String bio)
    {
        this.Uid = uid;
        this.DisplayName = displayName;
        this.Email = email;
        this.Bio = bio;
    }

    /************************************************************************
     * Purpose:         Parcelable Override Methods
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    protected User(Parcel in)
    {
        this.Uid = in.readString();
        this.DisplayName = in.readString();
        this.Email = in.readString();
        this.Bio = in.readString();
        this.ProfileURL = in.readString();
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
        dest.writeString(this.Uid);
        dest.writeString(this.DisplayName);
        dest.writeString(this.Email);
        dest.writeString(this.Bio);
        dest.writeString(this.ProfileURL);
    }

    /************************************************************************
     * Purpose:         Getters and Setters
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public String getUid() { return this.Uid; }
    public void setUid(String uid) { this.Uid = uid; }

    public String getDisplayName() { return this.DisplayName; }
    public void setDisplayName(String displayName) { this.DisplayName = displayName; }

    public String getEmail() { return this.Email; }
    public void setEmail(String email) { this.Email = email; }

    public String getBio() { return this.Bio; }
    public void setBio(String bio) { this.Bio = bio; }
}

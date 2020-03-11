package com.deconstructors.krono.module;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class User implements Parcelable
{
    private String DisplayName;
    private String Email;
    private String ProfileURL;
    private int FriendSize;

    /************************************************************************
     * Purpose:         Constructors
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public User(){}

    public User(String displayName, String email)
    {
        this.DisplayName = displayName;
        this.Email = email;
        this.FriendSize = 0;
    }

    public User(String displayName, String email, List<String> friendList)
    {
        this.DisplayName = displayName;
        this.Email = email;
        this.FriendSize = friendList.size();
    }

    /************************************************************************
     * Purpose:         Parcelable Override Methods
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    protected User(Parcel in)
    {
        DisplayName = in.readString();
        Email = in.readString();
        ProfileURL = in.readString();
        FriendSize = in.readInt();
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
        dest.writeString(DisplayName);
        dest.writeString(Email);
        dest.writeString(ProfileURL);
        dest.writeInt(FriendSize);
    }

    /************************************************************************
     * Purpose:         Getters and Setters
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public String getDisplayName() { return DisplayName; }
    public void setDisplayName(String displayName) { DisplayName = displayName; }

    public String getEmail() { return Email; }
    public void setEmail(String email) { Email = email; }

    public int getFriendSize() { return FriendSize; }
    public void setFriendSize(int friendSize) { FriendSize = friendSize; }

}

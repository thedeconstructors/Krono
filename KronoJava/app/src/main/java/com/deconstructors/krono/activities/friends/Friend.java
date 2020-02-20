package com.deconstructors.krono.activities.friends;

public class Friend
{
    private String _firstname;
    private String _lastname;

    public Friend(){}

    public Friend(Friend friend)
    {
        this._firstname = friend._firstname;
        this._lastname = friend._lastname;
    }

    public Friend(String firstname, String lastname)
    {
        _firstname = firstname;
        _lastname = lastname;
    }

    public String GetFirstName() { return _firstname; }
    public void SetFirstName(String firstname) { _firstname = firstname; }
    public String GetLastName() { return _lastname; }
    public void SetLastName(String lastname) { _lastname = lastname; }
}

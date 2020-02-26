package com.deconstructors.krono.activities.friends;

public class Friend
{
    private String _firstname;
    private String _lastname;
    private String _userid;

    public Friend(){}

    public Friend(Friend friend)
    {
        this._firstname = friend._firstname;
        this._lastname = friend._lastname;
    }

    public Friend(String firstname, String lastname, String id)
    {
        _firstname = firstname;
        _lastname = lastname;
        _userid = id;
    }

    public String GetFirstName() { return _firstname; }
    public void SetFirstName(String firstname) { _firstname = firstname; }
    public String GetLastName() { return _lastname; }
    public void SetLastName(String lastname) { _lastname = lastname; }
    public String GetID() { return _userid; }
}

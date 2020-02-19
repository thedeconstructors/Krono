package com.deconstructors.krono.activities.friends;

public class Friend {
    String _firstname = "";
    String _lastname = "";

    public Friend(String fname, String lname)
    {
        _firstname = fname;
        _lastname = lname;
    }

    public String GetFirstName() { return _firstname; }
    public String GetLastName() { return _lastname; }
}

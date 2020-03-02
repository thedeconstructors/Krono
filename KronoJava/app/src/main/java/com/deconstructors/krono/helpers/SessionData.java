package com.deconstructors.krono.helpers;

public class SessionData {

    //private data members
    private String _userid = null;

    //Singleton pattern
    public static SessionData _instance = null;
    public static SessionData GetInstance()
    {
        if (_instance == null)
            _instance = new SessionData();
        return _instance;
    }

    //Getters
    public String GetUserID()
    {
        if (HasUserID())
        {
            return new String(_userid);
        }
        else throw new NullPointerException("_userid is null");
    }
    //Setters
    public void SetUserID(String id)
    {
        _userid = id;
    }

    //Other functionality
    public boolean HasUserID()
    {
        return (_userid != null);
    }

}

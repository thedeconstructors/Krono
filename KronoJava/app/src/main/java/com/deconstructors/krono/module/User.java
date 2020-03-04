package com.deconstructors.krono.module;

public class User
{
    private String FirstName;
    private String LastName;
    private String Email;

    public User(){}

    public User(User user)
    {
        this.FirstName = user.FirstName;
        this.LastName = user.LastName;
    }

    public User(String firstname, String lastname, String loginEmail)
    {
        this.FirstName = firstname;
        this.LastName = lastname;
        this.Email = loginEmail;
    }

    public String GetFirstName() { return FirstName; }
    public void SetFirstName(String firstname) { FirstName = firstname; }
    public String GetLastName() { return LastName; }
    public void SetLastName(String lastname) { LastName = lastname; }
    public String getEmail()
    {
        return Email;
    }
    public void setEmail(String email)
    {
        Email = email;
    }
}

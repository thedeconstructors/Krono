package com.demo.planactivityuserdemo;

import android.app.Application;
import com.demo.planactivityuserdemo.model.User;

public class UserClient extends Application
{
    private User user = null;

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }
}

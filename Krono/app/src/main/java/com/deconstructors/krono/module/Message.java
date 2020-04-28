package com.deconstructors.krono.module;

import java.util.Date;

public class Message
{
    private String sender;
    private String text;
    private long time;

    public Message(String sender, String text )
    {
        this.sender = sender;
        this.text = text;
        time = new Date().getTime();
    }

    public Message()
    {}

    public String GetSender()
    {
        return sender;
    }

    public void SetSender(String sender)
    {
        this.sender = sender;
    }

    public String GetText()
    {
        return text;
    }

    public void SetText(String text)
    {
        this.text = text;
    }

    public long GetTime()
    {
        return time;
    }

    public void SetTime(long time)
    {
        this.time = time;
    }
}
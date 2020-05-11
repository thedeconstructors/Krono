package com.deconstructors.krono.module;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message
{
    private String sender;
    private String recipient;
    private String text;
    private String time;

    public Message(String sender, String recipient, String text)
    {
        this.sender = sender;
        this.recipient = recipient;
        this.text = text;
        //time = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
    }

    public Message()
    {}

    public String getSender()
    {
        return sender;
    }

    public void getSender(String sender)
    {
        this.sender = sender;
    }

    public String getRecipient()
    {
        return recipient;
    }

    public void setRecipient(String recipient)
    {
        this.recipient = recipient;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }
}
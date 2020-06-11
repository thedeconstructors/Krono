package com.deconstructors.krono.module;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message
{
    private String sender;
    private String recipient;
    private String text;
    private @ServerTimestamp Date Time;
    private String messageID;
    private String people;

    public Message(String sender, String recipient, String text, String people, Date time)
    {
        this.sender = sender;
        this.recipient = recipient;
        this.text = text;
        this.people = people;
        this.Time = time;
    }

    public Message()
    {}

    public String getSender()
    {
        return sender;
    }

    public void setSender(String sender)
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

    public Date getTime()
    {
        return Time;
    }

    public void setTime(Date time)
    {
        this.Time = time;
        //Log.e("MessageAdapter", "Inside Model: " + Time.toString());
    }

    public String getMessageID()
    {
        return messageID;
    }

    public void setMessageID(String messageID)
    {
        this.messageID = messageID;
    }

    public void setPeople (String people) { this.people = people; }

    public String getPeople () { return this.people; }
}
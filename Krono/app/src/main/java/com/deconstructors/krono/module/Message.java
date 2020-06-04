package com.deconstructors.krono.module;

import com.google.firebase.firestore.ServerTimestamp;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message
{
    private String sender;
    private String recipient;
    private String text;
    private @ServerTimestamp Date time;
    private String messageID;
    private String people;

    public Message(String sender, String recipient, String text, String people)
    {
        this.sender = sender;
        this.recipient = recipient;
        this.text = text;
        this.people = people;
        //time = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
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
        return time;
    }

    public void setTime(Date time)
    {
        this.time = time;
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
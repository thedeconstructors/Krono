package com.deconstructors.krono.module;

import java.util.Date;

public class Message
{
    private String m_sender;
    private String m_recipient;
    private String m_text;
    private long m_time;

    public Message(String sender, String recipient, String text)
    {
        this.m_sender = sender;
        this.m_recipient = recipient;
        this.m_text = text;
        m_time = new Date().getTime();
    }

    public Message()
    {}

    public String GetSender()
    {
        return m_sender;
    }

    public void SetSender(String sender)
    {
        m_sender = sender;
    }

    public String GetRecipient()
    {
        return m_recipient;
    }

    public void SetRecipient(String recipient)
    {
        m_recipient = recipient;
    }

    public String GetText()
    {
        return m_text;
    }

    public void SetText(String text)
    {
        m_text = text;
    }

    public long GetTime()
    {
        return m_time;
    }

    public void SetTime(long time)
    {
        m_time = time;
    }
}
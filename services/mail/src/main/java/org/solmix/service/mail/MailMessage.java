package org.solmix.service.mail;

import java.util.Date;


public interface MailMessage
{
    public void setFrom(String from) throws MailParseException;

    public void setReplyTo(String replyTo) throws MailParseException;

    public void setTo(String to) throws MailParseException;

    public void setTo(String[] to) throws MailParseException;

    public void setCc(String cc) throws MailParseException;

    public void setCc(String[] cc) throws MailParseException;

    public void setBcc(String bcc) throws MailParseException;

    public void setBcc(String[] bcc) throws MailParseException;

    public void setSentDate(Date sentDate) throws MailParseException;

    public void setSubject(String subject) throws MailParseException;

    public void setText(String text) throws MailParseException;
}

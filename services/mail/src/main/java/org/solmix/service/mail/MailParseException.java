package org.solmix.service.mail;


public class MailParseException extends MailException
{
    public MailParseException() {
        super();
    }

    public MailParseException(String message) {
        super(message);
    }

   
    public MailParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public MailParseException(Throwable cause) {
        super(cause);
    }
}

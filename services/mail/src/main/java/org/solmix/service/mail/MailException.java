package org.solmix.service.mail;


public class MailException extends RuntimeException
{
    public MailException() {
        super();
    }

    public MailException(String message) {
        super(message);
    }

   
    public MailException(String message, Throwable cause) {
        super(message, cause);
    }

    public MailException(Throwable cause) {
        super(cause);
    }

}

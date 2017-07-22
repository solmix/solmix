package org.solmix.service.mail;

import java.io.InputStream;

import javax.mail.internet.MimeMessage;

import org.solmix.service.mail.support.MimeMessagePreparator;


public interface JavaMailSender extends MailSender
{
    MimeMessage createMimeMessage();
    MimeMessage createMimeMessage(InputStream contentStream) throws MailException;
    void send(MimeMessage mimeMessage) throws MailException;
    void send(MimeMessage[] mimeMessages) throws MailException;
    void send(MimeMessagePreparator mimeMessagePreparator) throws MailException;
    void send(MimeMessagePreparator[] mimeMessagePreparators) throws MailException;
}

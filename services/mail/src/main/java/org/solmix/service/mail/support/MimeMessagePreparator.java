package org.solmix.service.mail.support;

import javax.mail.internet.MimeMessage;


public interface MimeMessagePreparator
{
    void prepare(MimeMessage mimeMessage) throws Exception;
}

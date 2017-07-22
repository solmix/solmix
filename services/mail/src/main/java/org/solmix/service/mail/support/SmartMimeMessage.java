package org.solmix.service.mail.support;

import javax.activation.FileTypeMap;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;


public class SmartMimeMessage extends MimeMessage
{
    private final String defaultEncoding;

    private final FileTypeMap defaultFileTypeMap;


    /**
     * Create a new SmartMimeMessage.
     * @param session the JavaMail Session to create the message for
     * @param defaultEncoding the default encoding, or <code>null</code> if none
     * @param defaultFileTypeMap the default FileTypeMap, or <code>null</code> if none
     */
    public SmartMimeMessage(Session session, String defaultEncoding, FileTypeMap defaultFileTypeMap) {
          super(session);
          this.defaultEncoding = defaultEncoding;
          this.defaultFileTypeMap = defaultFileTypeMap;
    }


    /**
     * Return the default encoding of this message, or <code>null</code> if none.
     */
    public final String getDefaultEncoding() {
          return this.defaultEncoding;
    }

    /**
     * Return the default FileTypeMap of this message, or <code>null</code> if none.
     */
    public final FileTypeMap getDefaultFileTypeMap() {
          return this.defaultFileTypeMap;
    }
}

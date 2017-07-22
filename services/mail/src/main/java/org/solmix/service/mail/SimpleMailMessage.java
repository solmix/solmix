package org.solmix.service.mail;

import java.io.Serializable;
import java.util.Date;

import org.solmix.commons.util.Assert;
import org.solmix.commons.util.ObjectUtils;
import org.solmix.commons.util.StringUtils;


public class SimpleMailMessage implements MailMessage, Serializable
{

    private String from;

    private String replyTo;

    private String[] to;

    private String[] cc;

    private String[] bcc;

    private Date sentDate;

    private String subject;

    private String text;


    /**
     * Create a new <code>SimpleMailMessage</code>.
     */
    public SimpleMailMessage() {
    }

    /**
     * Copy constructor for creating a new <code>SimpleMailMessage</code> from the state
     * of an existing <code>SimpleMailMessage</code> instance.
     * @throws IllegalArgumentException if the supplied message is <code>null</code> 
     */
    public SimpleMailMessage(SimpleMailMessage original) {
          Assert.isNotNull(original, "The 'original' message argument cannot be null");
          this.from = original.getFrom();
          this.replyTo = original.getReplyTo();
          if (original.getTo() != null) {
                this.to = copy(original.getTo());
          }
          if (original.getCc() != null) {
                this.cc = copy(original.getCc());
          }
          if (original.getBcc() != null) {
                this.bcc = copy(original.getBcc());
          }
          this.sentDate = original.getSentDate();
          this.subject = original.getSubject();
          this.text = original.getText();
    }


    public void setFrom(String from) {
          this.from = from;
    }

    public String getFrom() {
          return this.from;
    }

    public void setReplyTo(String replyTo) {
          this.replyTo = replyTo;
    }

    public String getReplyTo() {
          return replyTo;
    }

    public void setTo(String to) {
          this.to = new String[] {to};
    }

    public void setTo(String[] to) {
          this.to = to;
    }

    public String[] getTo() {
          return this.to;
    }

    public void setCc(String cc) {
          this.cc = new String[] {cc};
    }

    public void setCc(String[] cc) {
          this.cc = cc;
    }

    public String[] getCc() {
          return cc;
    }

    public void setBcc(String bcc) {
          this.bcc = new String[] {bcc};
    }

    public void setBcc(String[] bcc) {
          this.bcc = bcc;
    }

    public String[] getBcc() {
          return bcc;
    }

    public void setSentDate(Date sentDate) {
          this.sentDate = sentDate;
    }

    public Date getSentDate() {
          return sentDate;
    }

    public void setSubject(String subject) {
          this.subject = subject;
    }

    public String getSubject() {
          return this.subject;
    }

    public void setText(String text) {
          this.text = text;
    }

    public String getText() {
          return this.text;
    }


    /**
     * Copy the contents of this message to the given target message.
     * @param target the <code>MailMessage</code> to copy to
     * @throws IllegalArgumentException if the supplied <code>target</code> is <code>null</code> 
     */
    public void copyTo(MailMessage target) {
          Assert.isNotNull(target, "The 'target' message argument cannot be null");
          if (getFrom() != null) {
                target.setFrom(getFrom());
          }
          if (getReplyTo() != null) {
                target.setReplyTo(getReplyTo());
          }
          if (getTo() != null) {
                target.setTo(getTo());
          }
          if (getCc() != null) {
                target.setCc(getCc());
          }
          if (getBcc() != null) {
                target.setBcc(getBcc());
          }
          if (getSentDate() != null) {
                target.setSentDate(getSentDate());
          }
          if (getSubject() != null) {
                target.setSubject(getSubject());
          }
          if (getText() != null) {
                target.setText(getText());
          }
    }


    @Override
    public String toString() {
          StringBuilder sb = new StringBuilder("SimpleMailMessage: ");
          sb.append("from=").append(this.from).append("; ");
          sb.append("replyTo=").append(this.replyTo).append("; ");
          sb.append("to=").append(StringUtils.toString(this.to)).append("; ");
          sb.append("cc=").append(StringUtils.toString(this.cc)).append("; ");
          sb.append("bcc=").append(StringUtils.toString(this.bcc)).append("; ");
          sb.append("sentDate=").append(this.sentDate).append("; ");
          sb.append("subject=").append(this.subject).append("; ");
          sb.append("text=").append(this.text);
          return sb.toString();
    }

    @Override
    public boolean equals(Object other) {
          if (this == other) {
                return true;
          }
          if (!(other instanceof SimpleMailMessage)) {
                return false;
          }
          SimpleMailMessage otherMessage = (SimpleMailMessage) other;
          return (ObjectUtils.nullSafeEquals(this.from, otherMessage.from) &&
                      ObjectUtils.nullSafeEquals(this.replyTo, otherMessage.replyTo) &&
                      java.util.Arrays.equals(this.to, otherMessage.to) &&
                      java.util.Arrays.equals(this.cc, otherMessage.cc) &&
                      java.util.Arrays.equals(this.bcc, otherMessage.bcc) &&
                      ObjectUtils.nullSafeEquals(this.sentDate, otherMessage.sentDate) &&
                      ObjectUtils.nullSafeEquals(this.subject, otherMessage.subject) &&
                      ObjectUtils.nullSafeEquals(this.text, otherMessage.text));
    }

    @Override
    public int hashCode() {
          int hashCode = (this.from == null ? 0 : this.from.hashCode());
          hashCode = 29 * hashCode + (this.replyTo == null ? 0 : this.replyTo.hashCode());
          for (int i = 0; this.to != null && i < this.to.length; i++) {
                hashCode = 29 * hashCode + (this.to == null ? 0 : this.to[i].hashCode());
          }
          for (int i = 0; this.cc != null && i < this.cc.length; i++) {
                hashCode = 29 * hashCode + (this.cc == null ? 0 : this.cc[i].hashCode());
          }
          for (int i = 0; this.bcc != null && i < this.bcc.length; i++) {
                hashCode = 29 * hashCode + (this.bcc == null ? 0 : this.bcc[i].hashCode());
          }
          hashCode = 29 * hashCode + (this.sentDate == null ? 0 : this.sentDate.hashCode());
          hashCode = 29 * hashCode + (this.subject == null ? 0 : this.subject.hashCode());
          hashCode = 29 * hashCode + (this.text == null ? 0 : this.text.hashCode());
          return hashCode;
    }


    private static String[] copy(String[] state) {
          String[] copy = new String[state.length];
          System.arraycopy(state, 0, copy, 0, state.length);
          return copy;
    }

}

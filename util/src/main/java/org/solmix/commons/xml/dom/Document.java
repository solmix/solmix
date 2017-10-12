
package org.solmix.commons.xml.dom;

import org.solmix.commons.util.DOMUtils;

/**
 * The Class Document.
 *
 * @author Jeff Butler
 */
public class Document
{

    public enum Model
    {
        XSD , DTD
    }

    /** The public id. */
    private String publicId;

    /** The system id. */
    private String systemId;

    /** The root element. */
    private XmlElement rootElement;

    private Model model;

    /**
     * Instantiates a new document.
     *
     * @param publicId the public id
     * @param systemId the system id
     */
    public Document(String publicId, String systemId)
    {
        super();
        this.publicId = publicId;
        this.systemId = systemId;
    }

    /**
     * Instantiates a new document.
     */
    public Document()
    {
        super();
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * Gets the root element.
     *
     * @return Returns the rootElement.
     */
    public XmlElement getRootElement() {
        return rootElement;
    }

    /**
     * Sets the root element.
     *
     * @param rootElement The rootElement to set.
     */
    public void setRootElement(XmlElement rootElement) {
        this.rootElement = rootElement;
    }

    /**
     * Gets the public id.
     *
     * @return Returns the publicId.
     */
    public String getPublicId() {
        return publicId;
    }

    /**
     * Gets the system id.
     *
     * @return Returns the systemId.
     */
    public String getSystemId() {
        return systemId;
    }

    /**
     * Gets the formatted content.
     *
     * @return the formatted content
     */
    public String getFormattedContent() {
        StringBuilder sb = new StringBuilder();

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); 
        if (model == Model.DTD) {
            if (publicId != null && systemId != null) {
                DOMUtils.newLine(sb);
                sb.append("<!DOCTYPE "); 
                sb.append(rootElement.getName());
                sb.append(" PUBLIC \""); 
                sb.append(publicId);
                sb.append("\" \""); 
                sb.append(systemId);
                sb.append("\">"); 
            }
            DOMUtils.newLine(sb);
            sb.append(rootElement.getFormattedContent(0));
        } else {
            DOMUtils.newLine(sb);
            sb.append("<"); 
            sb.append(rootElement.getName());
            sb.append(" xmlns=\""); 
            sb.append(publicId);
            sb.append("\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\""); 
            sb.append(publicId).append(" ").append(systemId);
            sb.append("\">"); 
            DOMUtils.newLine(sb);
            sb.append(rootElement.getElements().get(0).getFormattedContent(0));
            DOMUtils.newLine(sb);
            sb.append("</").append(rootElement.getName()).append(">");
                
        }

        return sb.toString();
    }
}

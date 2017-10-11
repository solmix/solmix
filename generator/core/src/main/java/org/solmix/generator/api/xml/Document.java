package org.solmix.generator.api.xml;

import org.solmix.generator.api.OutputUtilities;

/**
 * The Class Document.
 *
 * @author Jeff Butler
 */
public class Document {

    /** The public id. */
    private String publicId;

    /** The system id. */
    private String systemId;

    /** The root element. */
    private XmlElement rootElement;

    /**
     * Instantiates a new document.
     *
     * @param publicId
     *            the public id
     * @param systemId
     *            the system id
     */
    public Document(String publicId, String systemId) {
        super();
        this.publicId = publicId;
        this.systemId = systemId;
    }

    /**
     * Instantiates a new document.
     */
    public Document() {
        super();
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
     * @param rootElement
     *            The rootElement to set.
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

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); //$NON-NLS-1$

        if (publicId != null && systemId != null) {
            OutputUtilities.newLine(sb);
            sb.append("<!DOCTYPE "); //$NON-NLS-1$
            sb.append(rootElement.getName());
            sb.append(" PUBLIC \""); //$NON-NLS-1$
            sb.append(publicId);
            sb.append("\" \""); //$NON-NLS-1$
            sb.append(systemId);
            sb.append("\">"); //$NON-NLS-1$
        }

        OutputUtilities.newLine(sb);
        sb.append(rootElement.getFormattedContent(0));

        return sb.toString();
    }
}

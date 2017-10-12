package org.solmix.commons.xml.dom;


public class Attribute implements Comparable<Attribute> {

    /** The name. */
    private String name;

    /** The value. */
    private String value;

    /**
     * Instantiates a new attribute.
     *
     * @param name
     *            the name
     * @param value
     *            the value
     */
    public Attribute(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }

    /**
     * Gets the name.
     *
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the value.
     *
     * @return Returns the value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets the formatted content.
     *
     * @return the formatted content
     */
    public String getFormattedContent() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append("=\""); 
        sb.append(value);
        sb.append('\"');

        return sb.toString();
    }

    @Override
    public int compareTo(Attribute o) {
        if (this.name == null) {
            return o.name == null ? 0 : -1;
        } else {
            if (o.name == null) {
                return 0;
            } else {
                return this.name.compareTo(o.name);
            }
        }
    }
}

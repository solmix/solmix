
package org.solmix.generator.config;

/**
 * @author Jeff Butler
 */
public abstract class TypedPropertyHolder extends PropertyHolder {

    private String type;

    /**
     *  
     */
    public TypedPropertyHolder() {
        super();
    }

    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type specified in the configuration. If the special
     * value DEFAULT is specified, then the value will be ignored.
     * 
     * @param configurationType
     *            the type specified in the configuration
     */
    public void setType(String configurationType) {
        if (!"DEFAULT".equalsIgnoreCase(configurationType)) { 
            this.type = configurationType;
        }
    }
}


package org.solmix.generator.config;

import java.util.List;

import org.solmix.commons.util.StringUtils;
import org.solmix.commons.xml.dom.Attribute;
import org.solmix.commons.xml.dom.XmlElement;
import org.solmix.generator.util.Messages;

public class JdbcConnectionInfo extends PropertyHolder
{

    private String driverClass;

    private String connectionURL;

    private String userId;

    private String password;

    public JdbcConnectionInfo()
    {
        super();
    }

    public String getConnectionURL() {
        return connectionURL;
    }

    public void setConnectionURL(String connectionURL) {
        this.connectionURL = connectionURL;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public XmlElement toXmlElement() {
        XmlElement xmlElement = new XmlElement("jdbcConnection"); //$NON-NLS-1$
        xmlElement.addAttribute(new Attribute("driverClass", driverClass)); //$NON-NLS-1$
        xmlElement.addAttribute(new Attribute("connectionURL", connectionURL)); //$NON-NLS-1$

        if (StringUtils.stringHasValue(userId)) {
            xmlElement.addAttribute(new Attribute("userId", userId)); //$NON-NLS-1$
        }

        if (StringUtils.stringHasValue(password)) {
            xmlElement.addAttribute(new Attribute("password", password)); //$NON-NLS-1$
        }

        addPropertyXmlElements(xmlElement);

        return xmlElement;
    }

    public void validate(List<String> errors) {
        if (!StringUtils.stringHasValue(driverClass)) {
            errors.add(Messages.getString("ValidationError.4")); //$NON-NLS-1$
        }

        if (!StringUtils.stringHasValue(connectionURL)) {
            errors.add(Messages.getString("ValidationError.5")); //$NON-NLS-1$
        }
    }

}

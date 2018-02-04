
package org.solmix.runtime.management.support;

import javax.management.JMException;
import javax.management.ObjectName;

import org.solmix.commons.util.ObjectUtils;
import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.Container;
import org.solmix.runtime.extension.ExtensionContainer;
import org.solmix.runtime.management.ManagedComponent;
import org.solmix.runtime.management.ManagementConstants;
import org.solmix.runtime.management.annotation.ManagedAttribute;
import org.solmix.runtime.management.annotation.ManagedOperation;
import org.solmix.runtime.management.annotation.ManagedResource;

@ManagedResource(componentName = "Container", description = "Responsible for managing containers.")
public class ManagedContainer implements ManagedComponent
{
    private static final String TYPE_VALUE = "Container";
    private static final String INSTANCE_ID = "managed.container.instance.id";
    private final Container container;

    public ManagedContainer(Container c)
    {
        container = c;
    }
    
    @ManagedOperation
    public void close(boolean wait){
        container.close(wait);
    }
    
    @ManagedOperation
    public  boolean hasExtensionByName(String name){
        return container.hasExtensionByName(name);
    }
    @ManagedOperation
    public String[] getMissingBeans(){
        if(container instanceof ExtensionContainer){
            return ((ExtensionContainer)container).getMissingBeans();
        }else{
            return ObjectUtils.EMPTY_STRING_ARRAY;
        }
    }
    
    @ManagedAttribute(description = "Container production", currencyTimeLimit = 60)
    public boolean isProduction() {
        return container.isProduction();
    }
    @ManagedAttribute(description = "Container Id")
    public String getId(){
        return container.getId();
    }

    @ManagedAttribute(description = "Container Status")
    public String getStatus(){
        return container.getStatus().name();
    }
    
    
    @Override
    public ObjectName getObjectName() throws JMException {
        String id = container.getId();
        StringBuilder buffer = new StringBuilder(ManagementConstants.DEFAULT_DOMAIN_NAME).append(':');
        buffer.append(ManagementConstants.TYPE_PROP).append('=').append(TYPE_VALUE).append(',');
        buffer.append(ManagementConstants.CONTAINER_ID_PROP).append('=').append(id).append(',');
//        buffer.append(PRODUCTION).append('=').append(container.isProduction()).append(',');
        // Added the instance id to make the ObjectName unique
        String instanceId = (String)container.getProperties().get(INSTANCE_ID);
        if (StringUtils.isEmpty(instanceId)) {
            instanceId = new StringBuilder().append(container.hashCode()).toString();
        }
        buffer.append(ManagementConstants.INSTANCE_ID_PROP).append('=').append(instanceId);
        

        return new ObjectName(buffer.toString());
    }

}


package org.solmix.runtime.management.support;

import java.lang.reflect.Method;

import javax.management.Descriptor;
import javax.management.modelmbean.ModelMBeanInfo;

import org.solmix.runtime.management.annotation.ManagedAttribute;
import org.solmix.runtime.management.annotation.ManagedNotification;
import org.solmix.runtime.management.annotation.ManagedNotifications;
import org.solmix.runtime.management.annotation.ManagedOperation;
import org.solmix.runtime.management.annotation.ManagedOperationParameter;
import org.solmix.runtime.management.annotation.ManagedOperationParameters;
import org.solmix.runtime.management.annotation.ManagedResource;

public class AssembleModelMBeanSupport
{

    private ModelMBeanInfos supporter = new ModelMBeanInfos();

    public ManagedResource getManagedResource(Class<?> clazz) {
        return clazz.getAnnotation(ManagedResource.class);
    }

    public ManagedAttribute getManagedAttribute(Method method) {
        return method.getAnnotation(ManagedAttribute.class);
    }

    public ManagedOperation getManagedOperation(Method method) {
        return method.getAnnotation(ManagedOperation.class);
    }

    public ManagedOperationParameter[] getManagedOperationParameters(Method method) {
        ManagedOperationParameters params = method.getAnnotation(ManagedOperationParameters.class);
        ManagedOperationParameter[] result = null;
        if (params == null) {
            result = new ManagedOperationParameter[0];
        } else {
            result = params.value();
        }
        return result;
    }

    public ManagedNotification[] getManagedNotifications(Class<?> clazz) {
        ManagedNotifications notificationsAnn = clazz.getAnnotation(ManagedNotifications.class);
        ManagedNotification[] result = null;
        if (null == notificationsAnn) {
            return new ManagedNotification[0];
        }
        result = notificationsAnn.value();
        return result;
    }

    public String getAttributeName(String methodName) {
        if (methodName.indexOf("set") == 0) {
            return methodName.substring(3);
        }
        if (methodName.indexOf("get") == 0) {
            return methodName.substring(3);
        }
        if (methodName.indexOf("is") == 0) {
            return methodName.substring(2);
        }
        return null;
    }

    public static boolean checkMethod(Method[] methods, String methodName) {
        boolean result = false;
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().compareTo(methodName) == 0) {
                result = true;
                break;
            }
        }
        return result;
    }

    public static String getAttributeType(Method[] methods, String attributeName) {
        String result = null;
        String searchMethod = "get" + attributeName;
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().compareTo(searchMethod) == 0) {
                result = methods[i].getReturnType().getName();
                break;
            }
        }
        // check it is "is " attribute
        if (null == result) {
            searchMethod = "is" + attributeName;
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().compareTo(searchMethod) == 0) {
                    result = methods[i].getReturnType().getName();
                    break;
                }
            }
        }
        return result;
    }

    class ManagedAttributeInfo
    {

        String fname;

        String ftype;

        String description;

        boolean read;

        boolean write;

        boolean is;
    };

    // get the attribut information for the method
    public ManagedAttributeInfo getAttributInfo(Method[] methods, String attributName, String attributType, ManagedAttribute managedAttribute) {
        ManagedAttributeInfo mai = new ManagedAttributeInfo();
        mai.fname = attributName;
        mai.ftype = attributType;
        mai.description = managedAttribute.description();
        mai.is = checkMethod(methods, "is" + attributName);
        mai.write = checkMethod(methods, "set" + attributName);

        if (mai.is) {
            mai.read = true;
        } else {
            mai.read = checkMethod(methods, "get" + attributName);
        }

        return mai;

    }

    Method findMethodByName(Method methods[], String methodName) {
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().compareTo(methodName) == 0) {
                return methods[i];
            } else {
                continue;
            }

        }
        return null;

    }

    void addAttributeOperation(Method method) {
        Descriptor operationDescriptor = supporter.buildAttributeOperationDescriptor(method.getName());

        Class<?>[] types = method.getParameterTypes();

        String[] paramTypes = new String[types.length];
        String[] paramNames = new String[types.length];
        String[] paramDescs = new String[types.length];

        for (int j = 0; j < types.length; j++) {
            paramTypes[j] = types[j].getName();
            paramDescs[j] = "";
            paramNames[j] = types[j].getName();
        }

        supporter.addModelMBeanMethod(method.getName(), paramTypes, paramNames, paramDescs, "", method.getReturnType().getName(),
            operationDescriptor);
    }

    public ModelMBeanInfo getModelMbeanInfo(Class<?> clazz) {
        supporter.clear();
        ManagedResource mr = getManagedResource(clazz);
        if (mr == null) {
            return null;
        }
        Descriptor mbeanDescriptor = supporter.buildMBeanDescriptor(mr);

        ManagedNotification[] mns = getManagedNotifications(clazz);
        for (int k = 0; k < mns.length; k++) {
            supporter.addModelMBeanNotification(mns[k].notificationTypes(), mns[k].name(), mns[k].description(), null);
        }

        Method[] methods = clazz.getDeclaredMethods();

        for (int i = 0; i < methods.length; i++) {
            ManagedAttribute ma = getManagedAttribute(methods[i]);
            if (ma != null) {
                String attributeName = getAttributeName(methods[i].getName());
                if (!supporter.checkAttribute(attributeName)) {
                    String attributeType = getAttributeType(methods, attributeName);
                    ManagedAttributeInfo mai = getAttributInfo(methods, attributeName, attributeType, ma);
                    Descriptor attributeDescriptor = supporter.buildAttributeDescriptor(ma, attributeName, mai.is, mai.read, mai.write);

                    supporter.addModelMBeanAttribute(mai.fname, mai.ftype, mai.read, mai.write, mai.is, mai.description, attributeDescriptor);

                    Method method;
                    if (mai.read) {
                        if (mai.is) {
                            method = findMethodByName(methods, "is" + attributeName);
                        } else {
                            method = findMethodByName(methods, "get" + attributeName);
                        }
                        addAttributeOperation(method);
                    }
                    if (mai.write) {
                        method = findMethodByName(methods, "set" + attributeName);
                        addAttributeOperation(method);
                    }
                }

            } else {
                ManagedOperation mo = getManagedOperation(methods[i]);

                if (mo != null) {
                    Class<?>[] types = methods[i].getParameterTypes();
                    ManagedOperationParameter[] mop = getManagedOperationParameters(methods[i]);
                    String[] paramTypes = new String[types.length];
                    String[] paramNames = new String[types.length];
                    String[] paramDescs = new String[types.length];

                    for (int j = 0; j < types.length; j++) {
                        paramTypes[j] = types[j].getName();
                        if (j < mop.length) {
                            paramDescs[j] = mop[j].description();
                            paramNames[j] = mop[j].name();
                        } else {
                            paramDescs[j] = "";
                            paramNames[j] = types[j].getName();
                        }
                    }
                    Descriptor operationDescriptor = supporter.buildOperationDescriptor(mo, methods[i].getName());
                    supporter.addModelMBeanMethod(methods[i].getName(), paramTypes, paramNames, paramDescs, mo.description(),
                        methods[i].getReturnType().getName(), operationDescriptor);
                }
            }

        }
        return supporter.buildModelMBeanInfo(mbeanDescriptor);
    }
}

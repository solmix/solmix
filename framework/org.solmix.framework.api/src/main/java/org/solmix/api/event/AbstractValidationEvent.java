/*
 * ========THE SOLMIX PROJECT=====================================
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/ 
 * or see the FSF site: http://www.fsf.org. 
 */

package org.solmix.api.event;

import java.util.Hashtable;
import java.util.Map;

import org.solmix.SlxConstants;
import org.solmix.api.criterion.ErrorMessage;
import org.solmix.api.criterion.ValidationEventLocator;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2011-10-5
 */

public abstract class AbstractValidationEvent implements IValidationEvent, java.io.Serializable
{

    public static final String ERROR_MESSAGE = "errorMessage";

    public static final String OUT_TYPE = "outType";

    public static final String EXCEPTION = "exception";

    public static final String STATUS = "status";

    public static final String NAME = "name";

    public static final String LEVEL = "level";

    protected Throwable exception;

    protected Status status;

    protected Level severity;

    protected OutType outType;

    protected String name;

    ErrorMessage errorMessage;

    org.solmix.api.criterion.ValidationEventLocator locator;

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.IValidationEvent#getErrorMessage()
     */
    @Override
    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.IValidationEvent#getStuts()
     */
    @Override
    public Status getStuts() {
        return status;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.IValidationEvent#setStatus(org.solmix.api.event.IValidationEvent.Status)
     */
    @Override
    public void setStatus(Status value) {
        this.status = value;

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.IValidationEvent#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.IValidationEvent#getException()
     */
    @Override
    public Throwable getException() {
        return exception;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.IValidationEvent#getOutType()
     */
    @Override
    public OutType getOutType() {
        return outType;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.IValidationEvent#setOutType(org.solmix.api.event.IValidationEvent.OutType)
     */
    @Override
    public void setOutType(OutType outType) {
        this.outType = outType;

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.IValidationEvent#getLevel()
     */
    @Override
    public Level getLevel() {
        return severity;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.IValidationEvent#setLevel(org.solmix.api.event.IValidationEvent.Level)
     */
    @Override
    public void setLevel(Level _severity) {
        if (_severity != Level.DEBUG && _severity != Level.WARNING && _severity != Level.ERROR) {
            throw new IllegalArgumentException("bad severity data");
        }

        this.severity = _severity;

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.IValidationEvent#setErrorMessage(org.solmix.api.criterion.ErrorMessage)
     */
    @Override
    public void setErrorMessage(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.IValidationEvent#setException(java.lang.Throwable)
     */
    @Override
    public void setException(Throwable exception) {
        this.exception = exception;

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.IValidationEvent#setLocator(org.solmix.api.criterion.ValidationEventLocator)
     */
    @Override
    public void setLocator(ValidationEventLocator locator) {
        this.locator = locator;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.IEvent#getProperties()
     */
    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new Hashtable<String, Object>();
        String topic = SlxConstants.VALIDATION_TOPIC_PREFIX;
        Level level = this.getLevel();
        switch (level) {
            case DEBUG:
                topic = topic + "DEBUG";
                break;
            case WARNING:
                topic = topic + "WARNING";
                break;
            case ERROR:
                topic = topic + "ERROR";
                break;
            default:
                topic = topic + "DEFAULT";

        }
        properties.put("topic", topic);
        if (this.getErrorMessage() != null)
            properties.put(ERROR_MESSAGE, this.getErrorMessage());
        if (this.getOutType() != null)
            properties.put(OUT_TYPE, this.getOutType().value());
        if (this.getException() != null)
            properties.put(EXCEPTION, this.getException());
        if (this.getStuts() != null)
            properties.put(STATUS, this.getStuts());
        if (this.getName() != null)
            properties.put(NAME, this.getName());
        if (this.getLevel() != null)
            properties.put(LEVEL, this.getLevel());
        return properties;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.IEvent#getProperty(java.lang.String)
     */
    @Override
    public Object getProperty(String name) {
        return this.getProperties().get(name);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.IEvent#getTopic()
     */
    @Override
    public String getTopic() {
        return (String) getProperty("topic");
    }

}

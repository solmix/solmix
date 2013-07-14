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
package org.solmix.fmk.event;

import org.solmix.api.criterion.ErrorMessage;
import org.solmix.api.criterion.ValidationEventLocator;
import org.solmix.api.event.AbstractValidationEvent;


/**
 * 
 * @author solomon
 * @version 110035  2011-10-5
 */

public class DSValidationEvent extends AbstractValidationEvent
{

    public DSValidationEvent()
    {

    }
    public DSValidationEvent( Level severity, String msg )
    {
       this( severity, msg, null, null, null );

    }

    public DSValidationEvent( Level severity, ErrorMessage msg )
    {
       this( severity, msg, null, null );
    }

    public DSValidationEvent( Level severity, String msg, Throwable e, ValidationEventLocator locator, String sugest )
    {
       setLevel( severity );
       setErrorMessage( new ErrorMessage( msg, sugest ) );
       setLocator( locator );
       setException( e );
    }

    public DSValidationEvent( Level severity, ErrorMessage msg, Throwable e )
    {
       this( severity, msg, e, null );
    }

    public DSValidationEvent( Level severity, ErrorMessage msg, Throwable e, ValidationEventLocator locator )
    {
       setLevel( severity );
       setErrorMessage( msg );
       setLocator( locator );
       setException( e );
    }

    public DSValidationEvent( Level severity, String msg, Throwable e )
    {
       this( severity, msg, e, null );
    }

    public DSValidationEvent( Level severity, String msg, Throwable e, ValidationEventLocator locator )
    {
       this( severity, msg, e, locator, null );
    }
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.IValidationEvent#setName(java.lang.String)
     */
    @Override
    public void setName(String targetName) {
        this.name="<DataSource>-"+targetName;

    }

}

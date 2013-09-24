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

import org.solmix.api.criterion.ErrorMessage;
import org.solmix.api.criterion.ValidationEventLocator;


/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035  2011-10-5
 */

public interface IValidationEvent extends IEvent
{
    public enum Status
    {
       HANDLED( 0 ) , NO_HANDLED( 1 );

       private final int value;

       Status( int value )
       {
          this.value = value;
       }

       public int value()
       {
          return value;
       }
    }

    public enum Level
    {
       DEBUG( 0 ) , WARNING( 1 ) , ERROR( 2 ) , ;

       private final int value;

       Level( int value )
       {
          this.value = value;
       }

       public int value()
       {
          return value;
       }
    }

    public enum OutType
    {
       UN_SET( 0 ) , SERVER( 1 ) , CLIENT( 2 ) , ;

       private final int value;

       OutType( int value )
       {
          this.value = value;
       }

       public int value()
       {
          return value;
       }
    }

    ErrorMessage getErrorMessage();

    Status getStuts();

    void setStatus( Status value );

    /**
     * Set the name of this validation event.e.g.for datasource should be datasource name,for ds-field is field name.
     * 
     * @param targetName
     */
    void setName( String targetName );

    /**
     * Return the name of the validation event.
     * 
     * @return
     */
    String getName();

    public Throwable getException();

    /**
     * @return
     */
    OutType getOutType();

    /**
     * @param outType
     */
    void setOutType( OutType outType );

    /**
     * @return
     */
    Level getLevel();

    void setLevel( Level level );

    /**
     * @param errorMessage
     */
    void setErrorMessage( ErrorMessage errorMessage );

    /**
     * @param exception
     */
    void setException( Throwable exception );

    /**
     * @param locator
     */
    void setLocator( ValidationEventLocator locator );
}


package org.solmix.api.datasource;

import org.solmix.api.exception.SlxException;

/**
 * @author Administrator
 */
public interface IType
{

   String getName();

   Object create( Object obj, Object validationcontext ) throws SlxException;
}


package com.solmix.api.datasource;

import com.solmix.api.exception.SlxException;

/**
 * @author Administrator
 */
public interface IType
{

   String getName();

   Object create( Object obj, Object validationcontext ) throws SlxException;
}

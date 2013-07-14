
package org.solmix.fmk.js;

import java.util.Map;

import org.solmix.fmk.datasource.ValidationContext;

/**
 * @author solomon
 * @since 0.0.1
 * @version 110035 2010-12-21 solmix-ds
 */
public interface IBeanFilter
{

   Map filter( Object obj ) throws Exception;

   Map filter( Object obj, ValidationContext validationcontext ) throws Exception;
}

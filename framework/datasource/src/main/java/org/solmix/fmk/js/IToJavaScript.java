
package org.solmix.fmk.js;

import java.io.IOException;
import java.io.Writer;

/**
 * @deprecated Interface IToJavaScript is deprecated
 */

public interface IToJavaScript
{

   public abstract void toJavaScript( Writer writer ) throws UnconvertableException, IOException;
}

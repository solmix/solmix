
package org.solmix.fmk.velocity;

import java.util.Iterator;

public class MinimalEntrySet
{

   public MinimalEntrySet()
   {
   }

   public Iterator iterator()
   {
      return new Iterator() {

         public boolean hasNext()
         {
            return false;
         }

         public Object next()
         {
            return null;
         }

         public void remove()
         {
         }

      };
   }

   public boolean contains( Object o )
   {
      return false;
   }

   public boolean remove( Object o )
   {
      return false;
   }

   public int size()
   {
      return 0;
   }

   public void clear()
   {
   }
}

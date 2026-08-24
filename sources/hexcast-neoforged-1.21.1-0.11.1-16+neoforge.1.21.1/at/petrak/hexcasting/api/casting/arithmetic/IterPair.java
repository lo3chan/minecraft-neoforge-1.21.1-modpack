package at.petrak.hexcasting.api.casting.arithmetic;

import java.util.Iterator;

public record IterPair<T>(T left, T right) implements Iterable<T> {
   @Override
   public Iterator<T> iterator() {
      return new Iterator<T>() {
         int ix;

         @Override
         public boolean hasNext() {
            return this.ix < 2;
         }

         @Override
         public T next() {
            switch (this.ix++) {
               case 0:
                  return IterPair.this.left;
               case 1:
                  return IterPair.this.right;
               default:
                  return null;
            }
         }
      };
   }
}

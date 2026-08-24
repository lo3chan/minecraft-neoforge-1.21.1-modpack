package kroppeb.stareval.parser;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import kroppeb.stareval.exception.ParseException;

abstract class OpResolver<T> {
   abstract T resolve(StringReader var1) throws ParseException;

   static class Builder<T> {
      private final Map<String, T> map = new Object2ObjectOpenHashMap();

      public Builder() {
      }

      public void singleChar(T op) {
         this.multiChar("", op);
      }

      public void multiChar(String trailing, T op) {
         T previous = this.map.put(trailing, op);
         if (previous != null) {
            throw new RuntimeException("Tried to add multiple operators that map to the same string.");
         }
      }

      public OpResolver<T> build() {
         if (this.map.size() > 2) {
            throw new RuntimeException(
               "unimplemented: Cannot currently build an optimized operator resolver tree when more than two operators start with the same character"
            );
         } else {
            T singleChar = this.map.get("");
            if (singleChar != null) {
               if (this.map.size() == 1) {
                  return new OpResolver.SingleChar<>(singleChar);
               }

               for (Entry<String, T> subEntry : this.map.entrySet()) {
                  if (!"".equals(subEntry.getKey())) {
                     if (subEntry.getKey().length() != 1) {
                        throw new RuntimeException(
                           "unimplemented: Optimized operator resolver trees can currently only be built of operators that contain one or two characters."
                        );
                     }

                     return new OpResolver.SingleDualChar<>(singleChar, subEntry.getValue(), subEntry.getKey().charAt(0));
                  }
               }
            } else {
               if (this.map.size() > 1) {
                  throw new RuntimeException(
                     "unimplemented: Optimized operator resolver trees can currently only handle two operators starting with the same character if one operator is a single character"
                  );
               }

               Iterator var4 = this.map.entrySet().iterator();
               if (var4.hasNext()) {
                  Entry<String, T> subEntryx = (Entry<String, T>)var4.next();
                  if (subEntryx.getKey().length() != 1) {
                     throw new RuntimeException(
                        "unimplemented: Optimized operator resolver trees can currently only be built of operators that contain one or two characters."
                     );
                  }

                  return new OpResolver.DualChar<>(subEntryx.getValue(), subEntryx.getKey().charAt(0));
               }
            }

            if (this.map.isEmpty()) {
               throw new RuntimeException("Tried to build an operator resolver tree that contains no operators.");
            } else {
               throw new RuntimeException("This shouldn't be reachable");
            }
         }
      }
   }

   static class DualChar<T> extends OpResolver<T> {
      private final T op;
      private final char secondChar;

      DualChar(T op, char secondChar) {
         this.op = op;
         this.secondChar = secondChar;
      }

      @Override
      T resolve(StringReader input) throws ParseException {
         input.read(this.secondChar);
         return this.op;
      }
   }

   static class SingleChar<T> extends OpResolver<T> {
      private final T op;

      SingleChar(T op) {
         this.op = op;
      }

      @Override
      T resolve(StringReader input) {
         return this.op;
      }
   }

   static class SingleDualChar<T> extends OpResolver<T> {
      private final T singleCharOperator;
      private final T doubleCharOperator;
      private final char secondChar;

      SingleDualChar(T singleCharOperator, T doubleCharOperator, char secondChar) {
         this.singleCharOperator = singleCharOperator;
         this.doubleCharOperator = doubleCharOperator;
         this.secondChar = secondChar;
      }

      @Override
      T resolve(StringReader input) {
         return input.tryRead(this.secondChar) ? this.doubleCharOperator : this.singleCharOperator;
      }
   }
}

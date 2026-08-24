package de.markusbordihn.modsoptimizer.thirdparty.semver.semver.util;

import java.util.Arrays;

public class UnexpectedElementException extends RuntimeException {
   private final Object unexpected;
   private final int position;
   private final Stream.ElementType<?>[] expected;

   UnexpectedElementException(Object element, int position, Stream.ElementType<?>... expected) {
      this.unexpected = element;
      this.position = position;
      this.expected = expected;
   }

   public Object getUnexpectedElement() {
      return this.unexpected;
   }

   public int getPosition() {
      return this.position;
   }

   public Stream.ElementType<?>[] getExpectedElementTypes() {
      return this.expected;
   }

   @Override
   public String toString() {
      String message = String.format("Unexpected element '%s' at position '%d'", this.unexpected, this.position);
      if (this.expected.length > 0) {
         message = message + String.format(", expecting '%s'", Arrays.toString((Object[])this.expected));
      }

      return message;
   }
}

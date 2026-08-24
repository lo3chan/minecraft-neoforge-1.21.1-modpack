package io.github.maxencedc.sparsestructures;

public class IdBasedSalt {
   public static int getSalt(String id) {
      return Math.abs(id.hashCode()) % 2147483647;
   }
}

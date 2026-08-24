package me.lucko.spark.lib.adventure.identity;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class NilIdentity implements Identity {
   static final UUID NIL_UUID = new UUID(0L, 0L);
   static final Identity INSTANCE = new NilIdentity();

   @NotNull
   @Override
   public UUID uuid() {
      return NIL_UUID;
   }

   @Override
   public String toString() {
      return "Identity.nil()";
   }

   @Override
   public boolean equals(@Nullable final Object that) {
      return this == that;
   }

   @Override
   public int hashCode() {
      return 0;
   }
}

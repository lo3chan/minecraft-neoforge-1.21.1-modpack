package net.mehvahdjukaar.amendments.common;

import net.minecraft.util.StringRepresentable;

public interface IBellConnection {
   IBellConnection.Type amendments$getConnection();

   void amendments$setConnected(IBellConnection.Type var1);

   public static enum Type implements StringRepresentable {
      NONE,
      CHAIN,
      ROPE;

      public boolean isRope() {
         return this == ROPE;
      }

      public boolean isEmpty() {
         return this == NONE;
      }

      public boolean isChain() {
         return this == CHAIN;
      }

      public String getSerializedName() {
         return switch (this) {
            case NONE -> "none";
            case CHAIN -> "chain";
            case ROPE -> "rope";
         };
      }
   }
}

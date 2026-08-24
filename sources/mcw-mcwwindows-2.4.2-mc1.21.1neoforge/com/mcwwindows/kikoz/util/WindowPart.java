package com.mcwwindows.kikoz.util;

import net.minecraft.util.StringRepresentable;

public enum WindowPart implements StringRepresentable {
   BASE("base"),
   TOP("top"),
   MIDDLE("middle"),
   BOTTOM("bottom");

   private final String name;

   private WindowPart(final String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public String getString() {
      return this.name;
   }

   public String getSerializedName() {
      return this.name;
   }
}

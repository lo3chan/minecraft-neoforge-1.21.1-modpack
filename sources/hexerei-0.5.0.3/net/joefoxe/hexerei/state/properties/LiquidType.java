package net.joefoxe.hexerei.state.properties;

import net.minecraft.util.StringRepresentable;

public enum LiquidType implements StringRepresentable {
   WATER("water"),
   LAVA("lava"),
   EMPTY("empty"),
   QUICKSILVER("quicksilver"),
   BLOOD("blood"),
   TALLOW("tallow"),
   MILK("milk");

   private final String name;

   private LiquidType(String name) {
      this.name = name;
   }

   @Override
   public String toString() {
      return this.name;
   }

   public String getString() {
      return this.name;
   }

   public String getSerializedName() {
      return this.name;
   }
}

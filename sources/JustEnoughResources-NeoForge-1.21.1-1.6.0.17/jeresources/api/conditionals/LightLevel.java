package jeresources.api.conditionals;

import net.minecraft.client.resources.language.I18n;

public class LightLevel {
   public static LightLevel any = new LightLevel(-1, LightLevel.Relative.above);
   public static LightLevel bat = new LightLevel(4);
   public static LightLevel hostile = new LightLevel(8);
   public static LightLevel blaze = new LightLevel(12);
   int lightLevel;
   LightLevel.Relative relative;

   LightLevel(int level, LightLevel.Relative relative) {
      this.lightLevel = level;
      this.relative = relative;
   }

   LightLevel(int level) {
      this(level, LightLevel.Relative.below);
   }

   @Override
   public String toString() {
      String base = I18n.get("jer.lightLevel", new Object[0]);
      return this.lightLevel < 0 ? base + ": " + I18n.get("jer.any", new Object[0]) : base + ": " + this.relative.toString() + " " + this.lightLevel;
   }

   public static enum Relative {
      above("Above"),
      below("Below");

      String text;

      private Relative(String string) {
         this.text = string;
      }

      @Override
      public String toString() {
         return this.text;
      }
   }
}

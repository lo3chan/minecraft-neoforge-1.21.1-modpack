package dev.latvian.mods.kubejs.color;

import net.minecraft.network.chat.TextColor;

public class SimpleColor implements KubeColor {
   public static final SimpleColor BLACK = new SimpleColor(-16777216);
   public static final SimpleColor WHITE = new SimpleColor(-1);
   private final int value;
   private TextColor textColor;

   public SimpleColor(int v) {
      this.value = 0xFF000000 | v;
   }

   @Override
   public int kjs$getARGB() {
      return this.value;
   }

   @Override
   public String kjs$toHexString() {
      return String.format("#%06X", this.kjs$getRGB());
   }

   @Override
   public String toString() {
      return this.kjs$toHexString();
   }

   @Override
   public TextColor kjs$createTextColor() {
      if (this.textColor == null) {
         this.textColor = TextColor.fromRgb(this.kjs$getRGB());
      }

      return this.textColor;
   }
}

package dev.latvian.mods.kubejs.color;

import net.minecraft.network.chat.TextColor;

public class SimpleColorWithAlpha implements KubeColor {
   private final int value;
   private TextColor textColor;

   public SimpleColorWithAlpha(int v) {
      this.value = v;
   }

   @Override
   public int kjs$getARGB() {
      return this.value;
   }

   @Override
   public TextColor kjs$createTextColor() {
      if (this.textColor == null) {
         this.textColor = TextColor.fromRgb(this.kjs$getRGB());
      }

      return this.textColor;
   }

   @Override
   public String toString() {
      return this.kjs$toHexString();
   }
}

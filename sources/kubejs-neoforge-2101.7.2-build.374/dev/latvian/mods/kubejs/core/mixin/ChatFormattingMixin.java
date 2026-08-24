package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.color.KubeColor;
import net.minecraft.ChatFormatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({ChatFormatting.class})
public abstract class ChatFormattingMixin implements KubeColor {
   @Shadow
   @Final
   private Integer color;

   @Override
   public int kjs$getARGB() {
      return this.color == null ? -16777216 : 0xFF000000 | this.color;
   }

   @Override
   public int kjs$getRGB() {
      return this.color == null ? 0 : this.color;
   }
}

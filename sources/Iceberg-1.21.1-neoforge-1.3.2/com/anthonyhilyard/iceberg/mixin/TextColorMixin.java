package com.anthonyhilyard.iceberg.mixin;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import net.minecraft.network.chat.TextColor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({TextColor.class})
public class TextColorMixin {
   @Shadow
   @Final
   @Mutable
   private int value;

   @Inject(
      method = {"<init>(ILjava/lang/String;)V"},
      at = {@At("TAIL")},
      require = 0
   )
   private void icebergConstructor1(int originalValue, String originalName, CallbackInfo info) {
      this.value = originalValue & -1;
   }

   @Inject(
      method = {"<init>(I)V"},
      at = {@At("TAIL")},
      require = 0
   )
   private void icebergConstructor2(int originalValue, CallbackInfo info) {
      this.value = originalValue & -1;
   }

   @Inject(
      method = {"parseColor(Ljava/lang/String;)Lcom/mojang/serialization/DataResult;"},
      at = {@At("HEAD")},
      cancellable = true,
      require = 0
   )
   private static void icebergParseColor(String colorString, CallbackInfoReturnable<DataResult<TextColor>> info) {
      if (colorString.startsWith("#")) {
         try {
            int i = Integer.parseUnsignedInt(colorString.substring(1).toLowerCase(), 16);
            if (Integer.compareUnsigned(i, 0) >= 0 && Integer.compareUnsigned(i, -1) <= 0) {
               info.setReturnValue(DataResult.success(TextColor.fromRgb(i), Lifecycle.stable()));
            } else {
               info.setReturnValue(DataResult.error(() -> "Color value out of range: " + colorString));
            }
         } catch (NumberFormatException var3) {
            info.setReturnValue(DataResult.error(() -> "Invalid color value: " + colorString));
         }
      }
   }
}

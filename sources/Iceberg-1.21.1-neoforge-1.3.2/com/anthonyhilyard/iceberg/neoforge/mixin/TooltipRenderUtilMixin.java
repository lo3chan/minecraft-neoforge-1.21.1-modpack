package com.anthonyhilyard.iceberg.neoforge.mixin;

import com.anthonyhilyard.iceberg.util.Tooltips;
import java.lang.reflect.Field;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({TooltipRenderUtil.class})
public class TooltipRenderUtilMixin {
   @Shadow
   @Final
   private static int BACKGROUND_COLOR;
   @Unique
   private static Field horizontalLineColorField = null;

   @Inject(
      method = {"renderTooltipBackground(Lnet/minecraft/client/gui/GuiGraphics;IIIIIIIII)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderHorizontalLine(Lnet/minecraft/client/gui/GuiGraphics;IIIII)V",
         shift = Shift.BEFORE,
         ordinal = 0,
         remap = false
      )}
   )
   private static void icebergRenderTooltipBackgroundOne(
      GuiGraphics graphics,
      int x,
      int y,
      int width,
      int height,
      int z,
      int backgroundTop,
      int backgroundBottom,
      int borderTop,
      int borderBottom,
      CallbackInfo info
   ) {
      if (horizontalLineColorField == null) {
         try {
            horizontalLineColorField = TooltipRenderUtil.class.getDeclaredField("horizontalLineColor");
            horizontalLineColorField.setAccessible(true);
         } catch (Exception var13) {
         }
      }

      try {
         horizontalLineColorField.set(null, Tooltips.currentColors.backgroundColorStart());
      } catch (Exception var12) {
      }
   }

   @Inject(
      method = {"renderTooltipBackground(Lnet/minecraft/client/gui/GuiGraphics;IIIIIIIII)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderHorizontalLine(Lnet/minecraft/client/gui/GuiGraphics;IIIII)V",
         shift = Shift.BEFORE,
         ordinal = 1,
         remap = false
      )}
   )
   private static void icebergRenderTooltipBackgroundTwo(
      GuiGraphics graphics,
      int x,
      int y,
      int width,
      int height,
      int z,
      int backgroundTop,
      int backgroundBottom,
      int borderTop,
      int borderBottom,
      CallbackInfo info
   ) {
      try {
         horizontalLineColorField.set(null, Tooltips.currentColors.backgroundColorEnd());
      } catch (Exception var12) {
      }
   }

   @Inject(
      method = {"renderRectangle(Lnet/minecraft/client/gui/GuiGraphics;IIIIIII)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void icebergRenderRectangle(GuiGraphics graphics, int x, int y, int width, int height, int z, int colorFrom, int colorTo, CallbackInfo info) {
      if (colorFrom == BACKGROUND_COLOR || colorTo == BACKGROUND_COLOR) {
         graphics.fillGradient(x, y, x + width, y + height, z, Tooltips.currentColors.backgroundColorStart(), Tooltips.currentColors.backgroundColorEnd());
         info.cancel();
      }
   }
}

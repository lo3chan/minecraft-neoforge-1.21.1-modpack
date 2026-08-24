package com.anthonyhilyard.iceberg.mixin;

import com.anthonyhilyard.iceberg.util.Tooltips;
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
   @Unique
   private static int horizontalLineColor;
   @Unique
   private static boolean renderingTop = true;
   @Shadow
   @Final
   private static int BACKGROUND_COLOR;
   @Shadow
   @Final
   private static int BORDER_COLOR_TOP;
   @Shadow
   @Final
   private static int BORDER_COLOR_BOTTOM;

   @Inject(
      method = {"renderFrameGradient(Lnet/minecraft/client/gui/GuiGraphics;IIIIIII)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderHorizontalLine(Lnet/minecraft/client/gui/GuiGraphics;IIIII)V",
         shift = Shift.BEFORE,
         ordinal = 0
      )}
   )
   private static void icebergRenderFrameGradientOne(
      GuiGraphics graphics, int x, int y, int width, int height, int z, int color1, int color2, CallbackInfo info
   ) {
      renderingTop = true;
      horizontalLineColor = Tooltips.currentColors.borderColorStart();
   }

   @Inject(
      method = {"renderFrameGradient(Lnet/minecraft/client/gui/GuiGraphics;IIIIIII)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderHorizontalLine(Lnet/minecraft/client/gui/GuiGraphics;IIIII)V",
         shift = Shift.BEFORE,
         ordinal = 1
      )}
   )
   private static void icebergRenderFrameGradientTwo(
      GuiGraphics graphics, int x, int y, int width, int height, int z, int color1, int color2, CallbackInfo info
   ) {
      renderingTop = false;
      horizontalLineColor = Tooltips.currentColors.borderColorEnd();
   }

   @Inject(
      method = {"renderHorizontalLine(Lnet/minecraft/client/gui/GuiGraphics;IIIII)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void icebergRenderHorizontalLine(GuiGraphics graphics, int x, int y, int width, int z, int color, CallbackInfo info) {
      if (color == BACKGROUND_COLOR || color == BORDER_COLOR_TOP || color == BORDER_COLOR_BOTTOM) {
         int renderColor = horizontalLineColor;
         graphics.fillGradient(x, y, x + width, y + 1, z, renderColor, renderColor);
         info.cancel();
      } else if (renderingTop) {
         Tooltips.currentColors = new Tooltips.TooltipColors(
            Tooltips.currentColors.backgroundColorStart(), Tooltips.currentColors.backgroundColorEnd(), color, Tooltips.currentColors.borderColorEnd()
         );
      } else {
         Tooltips.currentColors = new Tooltips.TooltipColors(
            Tooltips.currentColors.backgroundColorStart(), Tooltips.currentColors.backgroundColorEnd(), Tooltips.currentColors.borderColorStart(), color
         );
      }
   }

   @Inject(
      method = {"renderRectangle(Lnet/minecraft/client/gui/GuiGraphics;IIIIII)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void icebergRenderRectangle(GuiGraphics graphics, int x, int y, int width, int height, int z, int color, CallbackInfo info) {
      if (color != BACKGROUND_COLOR) {
         Tooltips.currentColors = new Tooltips.TooltipColors(color, color, Tooltips.currentColors.borderColorStart(), Tooltips.currentColors.borderColorEnd());
      } else {
         graphics.fillGradient(x, y, x + width, y + height, z, Tooltips.currentColors.backgroundColorStart(), Tooltips.currentColors.backgroundColorEnd());
         info.cancel();
      }
   }

   @Inject(
      method = {"renderVerticalLine(Lnet/minecraft/client/gui/GuiGraphics;IIIII)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void icebergRenderVerticalLine(GuiGraphics graphics, int x, int y, int height, int z, int color, CallbackInfo info) {
      if (color != BACKGROUND_COLOR) {
         Tooltips.currentColors = new Tooltips.TooltipColors(color, color, Tooltips.currentColors.borderColorStart(), Tooltips.currentColors.borderColorEnd());
      } else {
         graphics.fillGradient(x, y, x + 1, y + height, z, Tooltips.currentColors.backgroundColorStart(), Tooltips.currentColors.backgroundColorEnd());
         info.cancel();
      }
   }

   @Inject(
      method = {"renderVerticalLineGradient(Lnet/minecraft/client/gui/GuiGraphics;IIIIII)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void icebergRenderVerticalLineGradient(GuiGraphics graphics, int x, int y, int height, int z, int startColor, int endColor, CallbackInfo info) {
      if (startColor == BORDER_COLOR_TOP && endColor == BORDER_COLOR_BOTTOM) {
         graphics.fillGradient(x, y, x + 1, y + height, z, Tooltips.currentColors.borderColorStart(), Tooltips.currentColors.borderColorEnd());
         info.cancel();
      } else if (startColor != BACKGROUND_COLOR && endColor != BACKGROUND_COLOR) {
         Tooltips.currentColors = new Tooltips.TooltipColors(
            Tooltips.currentColors.backgroundColorStart(), Tooltips.currentColors.backgroundColorEnd(), startColor, endColor
         );
      }
   }
}

package com.anthonyhilyard.iceberg.mixin;

import com.anthonyhilyard.iceberg.component.IExtendedText;
import com.anthonyhilyard.iceberg.util.Tooltips;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ClientTextTooltip.class})
public class ClientTextTooltipMixin implements IExtendedText {
   @Unique
   Font currentFont;
   @Unique
   IExtendedText.TextAlignment textAlignment = IExtendedText.TextAlignment.LEFT;
   @Unique
   int leftPadding = 0;
   @Unique
   int rightPadding = 0;
   @Unique
   int topPadding = 0;
   @Unique
   int bottomPadding = 0;

   @Override
   public void setAlignment(IExtendedText.TextAlignment alignment) {
      this.textAlignment = alignment;
   }

   @Override
   public void setPadding(int left, int right, int top, int bottom) {
      this.leftPadding = left;
      this.rightPadding = right;
      this.topPadding = top;
      this.bottomPadding = bottom;
   }

   @Override
   public IExtendedText.TextAlignment getAlignment() {
      return this.textAlignment;
   }

   @Override
   public int getLeftPadding() {
      return this.leftPadding;
   }

   @Override
   public int getRightPadding() {
      return this.rightPadding;
   }

   @Override
   public int getTopPadding() {
      return this.topPadding;
   }

   @Override
   public int getBottomPadding() {
      return this.bottomPadding;
   }

   @ModifyVariable(
      method = {"renderText(Lnet/minecraft/client/gui/Font;IILorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)V"},
      at = @At("LOAD"),
      argsOnly = true,
      index = 1
   )
   private Font getFont(Font font) {
      this.currentFont = font;
      return font;
   }

   @ModifyVariable(
      method = {"renderText(Lnet/minecraft/client/gui/Font;IILorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)V"},
      at = @At("LOAD"),
      argsOnly = true,
      index = 2
   )
   private int modifyHorizontalOffset(int xOriginal) {
      if (this.currentFont == null) {
         return xOriginal;
      } else {
         int tooltipWidth = Tooltips.getCurrentRect().getWidth();
         return xOriginal
            + Tooltips.getTitleOffset(
               tooltipWidth, this.currentFont.width(((ClientTextTooltip)this).text), this.leftPadding, this.rightPadding, this.textAlignment
            );
      }
   }

   @ModifyVariable(
      method = {"renderText(Lnet/minecraft/client/gui/Font;IILorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)V"},
      at = @At("LOAD"),
      argsOnly = true,
      index = 3
   )
   private int modifyVerticalOffset(int yOriginal) {
      return this.currentFont == null ? yOriginal : yOriginal + this.topPadding;
   }

   @Inject(
      method = {"getWidth(Lnet/minecraft/client/gui/Font;)I"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void adjustWidth(Font font, CallbackInfoReturnable<Integer> info) {
      info.setReturnValue(Tooltips.getTitleWidth((ClientTextTooltip)this, font));
   }

   @Inject(
      method = {"getHeight()I"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void adjustHeight(CallbackInfoReturnable<Integer> info) {
      int defaultHeight = 10;
      int result = this.topPadding + defaultHeight + this.bottomPadding;
      info.setReturnValue(result);
   }
}

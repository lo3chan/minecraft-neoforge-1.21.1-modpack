package net.raphimc.immediatelyfast.injection.mixins.hud_batching.compat;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.world.item.ItemStack;
import net.raphimc.immediatelyfast.feature.batching.HudBatchingBufferSource;
import net.raphimc.immediatelyfast.feature.core.BatchableBufferSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiGraphics.class})
public abstract class MixinDrawContext {
   @Shadow
   public BufferSource bufferSource;
   @Shadow
   @Final
   private PoseStack pose;

   @Shadow
   public abstract void flush();

   @Shadow
   protected abstract void flushIfManaged();

   @WrapMethod(
      method = {"renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"}
   )
   private void renderItemDecorations(Font textRenderer, ItemStack stack, int x, int y, String countOverride, Operation<Void> original) {
      if (this.bufferSource instanceof HudBatchingBufferSource hudBatchingBufferSource) {
         hudBatchingBufferSource.setRenderingItemDecorations(true);
      }

      try {
         original.call(new Object[]{textRenderer, stack, x, y, countOverride});
      } finally {
         if (this.bufferSource instanceof HudBatchingBufferSource hudBatchingBufferSource) {
            hudBatchingBufferSource.setRenderingItemDecorations(false);
         }
      }
   }

   @Inject(
      method = {"renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"},
      slice = {@Slice(
         from = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemCooldowns;getCooldownPercent(Lnet/minecraft/world/item/Item;F)F"
         )
      )},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/GuiGraphics;fill(Lnet/minecraft/client/renderer/RenderType;IIIII)V"
      )}
   )
   private void forceDraw(CallbackInfo ci) {
      if (this.bufferSource instanceof BatchableBufferSource) {
         this.flush();
      }
   }

   @Redirect(
      method = {"applyScissor"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/GuiGraphics;flushIfManaged()V"
      )
   )
   private void drawIfBatching(GuiGraphics instance) {
      if (this.bufferSource instanceof BatchableBufferSource) {
         this.flush();
      } else {
         this.flushIfManaged();
      }
   }

   @Inject(
      method = {"renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V",
         shift = Shift.AFTER
      )}
   )
   private void translateZForAllItemOverlays(CallbackInfo ci) {
      if (this.bufferSource instanceof BatchableBufferSource) {
         this.pose.translate(0.0F, 0.0F, 200.0F);
      }
   }

   @WrapWithCondition(
      method = {"renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"
      )}
   )
   private boolean translateZEarlier(PoseStack instance, float x, float y, float z) {
      return !(this.bufferSource instanceof BatchableBufferSource);
   }
}

package net.raphimc.immediatelyfast.injection.mixins.core.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.DisplayRenderer.TextDisplayRenderer;
import net.minecraft.world.entity.Display.TextDisplay;
import net.minecraft.world.entity.Display.TextDisplay.TextRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({TextDisplayRenderer.class})
public abstract class MixinTextDisplayEntityRenderer {
   @Inject(
      method = {"renderInner(Lnet/minecraft/world/entity/Display$TextDisplay;Lnet/minecraft/world/entity/Display$TextDisplay$TextRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Display$TextDisplay$CachedInfo;lines()Ljava/util/List;",
         ordinal = 1
      )}
   )
   private void drawBackgroundImmediately(
      TextDisplay textDisplayEntity, TextRenderState data, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, float f, CallbackInfo ci
   ) {
      if ((data.flags() & 2) != 0 && vertexConsumerProvider instanceof BufferSource immediate) {
         immediate.endBatch();
      }
   }
}

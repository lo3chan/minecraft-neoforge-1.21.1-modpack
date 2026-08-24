package net.raphimc.immediatelyfast.injection.mixins.hud_batching.consumer;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.FastColor.ARGB32;
import net.raphimc.immediatelyfast.feature.batching.BatchingRenderLayers;
import net.raphimc.immediatelyfast.feature.batching.BlendFuncDepthFuncState;
import net.raphimc.immediatelyfast.feature.batching.HudBatchingBufferSource;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {GuiGraphics.class},
   priority = 1500
)
public abstract class MixinDrawContext {
   @Shadow
   @Final
   private PoseStack pose;
   @Shadow
   @Final
   private Minecraft minecraft;
   @Shadow
   @Mutable
   public BufferSource bufferSource;

   @ModifyVariable(
      method = {"fill(Lnet/minecraft/client/renderer/RenderType;IIIIII)V"},
      at = @At("HEAD"),
      index = 6,
      argsOnly = true
   )
   private int mixColor(int color) {
      return this.bufferSource instanceof HudBatchingBufferSource ? this.immediatelyFast$mixWithShaderColor(color) : color;
   }

   @ModifyVariable(
      method = {"fillGradient(Lnet/minecraft/client/renderer/RenderType;IIIIIII)V"},
      at = @At("HEAD"),
      index = 5,
      argsOnly = true
   )
   private int mixStartColor(int color) {
      return this.bufferSource instanceof HudBatchingBufferSource ? this.immediatelyFast$mixWithShaderColor(color) : color;
   }

   @ModifyVariable(
      method = {"fillGradient(Lnet/minecraft/client/renderer/RenderType;IIIIIII)V"},
      at = @At("HEAD"),
      index = 6,
      argsOnly = true
   )
   private int mixEndColor(int color) {
      return this.bufferSource instanceof HudBatchingBufferSource ? this.immediatelyFast$mixWithShaderColor(color) : color;
   }

   @Inject(
      method = {"innerBlit(Lnet/minecraft/resources/ResourceLocation;IIIIIFFFF)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void drawTexturedQuadIntoBuffer(
      ResourceLocation texture, int x1, int x2, int y1, int y2, int z, float u1, float u2, float v1, float v2, CallbackInfo ci
   ) {
      if (this.bufferSource instanceof HudBatchingBufferSource) {
         ci.cancel();
         Matrix4f matrix = this.pose.last().pose();
         float[] shaderColor = RenderSystem.getShaderColor();
         int r = Mth.clamp((int)(shaderColor[0] * 255.0F), 0, 255);
         int g = Mth.clamp((int)(shaderColor[1] * 255.0F), 0, 255);
         int b = Mth.clamp((int)(shaderColor[2] * 255.0F), 0, 255);
         int a = Mth.clamp((int)(shaderColor[3] * 255.0F), 0, 255);
         if (r == 255 && g == 255 && b == 255 && a == 255) {
            VertexConsumer vertexConsumer = this.bufferSource
               .getBuffer(BatchingRenderLayers.TEXTURE.apply(this.minecraft.getTextureManager().getTexture(texture).getId(), BlendFuncDepthFuncState.current()));
            vertexConsumer.addVertex(matrix, x1, y2, z).setUv(u1, v2);
            vertexConsumer.addVertex(matrix, x2, y2, z).setUv(u2, v2);
            vertexConsumer.addVertex(matrix, x2, y1, z).setUv(u2, v1);
            vertexConsumer.addVertex(matrix, x1, y1, z).setUv(u1, v1);
         } else {
            VertexConsumer vertexConsumer = this.bufferSource
               .getBuffer(
                  BatchingRenderLayers.COLORED_TEXTURE.apply(this.minecraft.getTextureManager().getTexture(texture).getId(), BlendFuncDepthFuncState.current())
               );
            vertexConsumer.addVertex(matrix, x1, y2, z).setUv(u1, v2).setColor(r, g, b, a);
            vertexConsumer.addVertex(matrix, x2, y2, z).setUv(u2, v2).setColor(r, g, b, a);
            vertexConsumer.addVertex(matrix, x2, y1, z).setUv(u2, v1).setColor(r, g, b, a);
            vertexConsumer.addVertex(matrix, x1, y1, z).setUv(u1, v1).setColor(r, g, b, a);
         }
      }
   }

   @Inject(
      method = {"innerBlit(Lnet/minecraft/resources/ResourceLocation;IIIIIFFFFFFFF)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void drawTexturedQuadIntoBuffer(
      ResourceLocation texture,
      int x1,
      int x2,
      int y1,
      int y2,
      int z,
      float u1,
      float u2,
      float v1,
      float v2,
      float red,
      float green,
      float blue,
      float alpha,
      CallbackInfo ci
   ) {
      if (this.bufferSource instanceof HudBatchingBufferSource) {
         ci.cancel();
         Matrix4f matrix = this.pose.last().pose();
         int color = this.immediatelyFast$mixWithShaderColor(
            (int)(alpha * 255.0F) << 24 | (int)(red * 255.0F) << 16 | (int)(green * 255.0F) << 8 | (int)(blue * 255.0F)
         );
         RenderSystem.enableBlend();
         VertexConsumer vertexConsumer = this.bufferSource
            .getBuffer(
               BatchingRenderLayers.COLORED_TEXTURE.apply(this.minecraft.getTextureManager().getTexture(texture).getId(), BlendFuncDepthFuncState.current())
            );
         vertexConsumer.addVertex(matrix, x1, y2, z).setUv(u1, v2).setColor(color);
         vertexConsumer.addVertex(matrix, x2, y2, z).setUv(u2, v2).setColor(color);
         vertexConsumer.addVertex(matrix, x2, y1, z).setUv(u2, v1).setColor(color);
         vertexConsumer.addVertex(matrix, x1, y1, z).setUv(u1, v1).setColor(color);
         RenderSystem.disableBlend();
      }
   }

   @Inject(
      method = {"flushIfUnmanaged"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void dontTryDrawIfBatching(CallbackInfo ci) {
      if (this.bufferSource instanceof HudBatchingBufferSource) {
         ci.cancel();
      }
   }

   @WrapWithCondition(
      method = {"renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/GuiGraphics;flush()V"
      )}
   )
   private boolean dontDrawIfBatching(GuiGraphics instance) {
      return !(instance.bufferSource instanceof HudBatchingBufferSource);
   }

   @Unique
   private int immediatelyFast$mixWithShaderColor(int color) {
      float[] shaderColor = RenderSystem.getShaderColor();
      int argb = Mth.clamp((int)(shaderColor[3] * 255.0F), 0, 255) << 24
         | Mth.clamp((int)(shaderColor[0] * 255.0F), 0, 255) << 16
         | Mth.clamp((int)(shaderColor[1] * 255.0F), 0, 255) << 8
         | Mth.clamp((int)(shaderColor[2] * 255.0F), 0, 255);
      return ARGB32.multiply(color, argb);
   }
}

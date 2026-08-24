package net.irisshaders.batchedentityrendering.mixin;

import net.irisshaders.batchedentityrendering.impl.DrawCallTrackingRenderBuffers;
import net.irisshaders.batchedentityrendering.impl.FullyBufferedMultiBufferSource;
import net.irisshaders.batchedentityrendering.impl.Groupable;
import net.irisshaders.batchedentityrendering.impl.RenderBuffersExt;
import net.irisshaders.batchedentityrendering.impl.TransparencyType;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {LevelRenderer.class},
   priority = 999
)
public class MixinLevelRenderer {
   @Unique
   private static final String RENDER_ENTITY = "Lnet/minecraft/client/renderer/LevelRenderer;renderEntity(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V";
   @Shadow
   private RenderBuffers renderBuffers;
   @Unique
   private Groupable groupable;

   @Inject(
      method = {"renderLevel"},
      at = {@At("HEAD")}
   )
   private void batchedentityrendering$beginLevelRender(
      DeltaTracker deltaTracker,
      boolean bl,
      Camera camera,
      GameRenderer gameRenderer,
      LightTexture lightTexture,
      Matrix4f matrix4f,
      Matrix4f matrix4f2,
      CallbackInfo ci
   ) {
      if (this.renderBuffers instanceof DrawCallTrackingRenderBuffers) {
         ((DrawCallTrackingRenderBuffers)this.renderBuffers).resetDrawCounts();
      }

      ((RenderBuffersExt)this.renderBuffers).beginLevelRendering();
      MultiBufferSource provider = this.renderBuffers.bufferSource();
      if (provider instanceof Groupable) {
         this.groupable = (Groupable)provider;
      }
   }

   @Inject(
      method = {"renderLevel"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/LevelRenderer;renderEntity(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V"
      )}
   )
   private void batchedentityrendering$preRenderEntity(
      DeltaTracker deltaTracker,
      boolean bl,
      Camera camera,
      GameRenderer gameRenderer,
      LightTexture lightTexture,
      Matrix4f matrix4f,
      Matrix4f matrix4f2,
      CallbackInfo ci
   ) {
      if (this.groupable != null) {
         this.groupable.startGroup();
      }
   }

   @Inject(
      method = {"renderLevel"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/LevelRenderer;renderEntity(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V",
         shift = Shift.AFTER
      )}
   )
   private void batchedentityrendering$postRenderEntity(
      DeltaTracker deltaTracker,
      boolean bl,
      Camera camera,
      GameRenderer gameRenderer,
      LightTexture lightTexture,
      Matrix4f matrix4f,
      Matrix4f matrix4f2,
      CallbackInfo ci
   ) {
      if (this.groupable != null) {
         this.groupable.endGroup();
      }
   }

   @Inject(
      method = {"renderLevel"},
      at = {@At(
         value = "CONSTANT",
         args = {"stringValue=translucent"}
      )}
   )
   private void batchedentityrendering$beginTranslucents(
      DeltaTracker deltaTracker,
      boolean bl,
      Camera camera,
      GameRenderer gameRenderer,
      LightTexture lightTexture,
      Matrix4f matrix4f,
      Matrix4f matrix4f2,
      CallbackInfo ci
   ) {
      if (this.renderBuffers.bufferSource() instanceof FullyBufferedMultiBufferSource fullyBufferedMultiBufferSource) {
         fullyBufferedMultiBufferSource.readyUp();
      }

      if (WorldRenderingSettings.INSTANCE.shouldSeparateEntityDraws()) {
         Minecraft.getInstance().getProfiler().popPush("entity_draws_opaque");
         if (this.renderBuffers.bufferSource() instanceof FullyBufferedMultiBufferSource source) {
            source.endBatchWithType(TransparencyType.OPAQUE);
            source.endBatchWithType(TransparencyType.OPAQUE_DECAL);
            source.endBatchWithType(TransparencyType.WATER_MASK);
         } else {
            this.renderBuffers.bufferSource().endBatch();
         }
      } else {
         Minecraft.getInstance().getProfiler().popPush("entity_draws");
         this.renderBuffers.bufferSource().endBatch();
      }
   }

   @Inject(
      method = {"renderLevel"},
      at = {@At(
         value = "CONSTANT",
         args = {"stringValue=translucent"},
         shift = Shift.AFTER
      )}
   )
   private void batchedentityrendering$endTranslucents(
      DeltaTracker deltaTracker,
      boolean bl,
      Camera camera,
      GameRenderer gameRenderer,
      LightTexture lightTexture,
      Matrix4f matrix4f,
      Matrix4f matrix4f2,
      CallbackInfo ci
   ) {
      if (WorldRenderingSettings.INSTANCE.shouldSeparateEntityDraws()) {
         this.renderBuffers.bufferSource().endBatch();
      }
   }

   @Inject(
      method = {"renderLevel"},
      at = {@At("RETURN")}
   )
   private void batchedentityrendering$endLevelRender(
      DeltaTracker deltaTracker,
      boolean bl,
      Camera camera,
      GameRenderer gameRenderer,
      LightTexture lightTexture,
      Matrix4f matrix4f,
      Matrix4f matrix4f2,
      CallbackInfo ci
   ) {
      ((RenderBuffersExt)this.renderBuffers).endLevelRendering();
      this.groupable = null;
   }
}

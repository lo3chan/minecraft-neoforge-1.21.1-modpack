package org.dimdev.limlib.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.dimdev.limlib.api.LimLibRegistryKeys;
import org.dimdev.limlib.api.client.effect.EffectRenderers;
import org.dimdev.limlib.api.effects.LookupGrabber;
import org.dimdev.limlib.api.skybox.Skybox;
import org.dimdev.limlib.impl.bridge.IrisBridge;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {LevelRenderer.class},
   priority = 950
)
public abstract class WorldRendererBeforeMixin {
   @Inject(
      method = {"renderLevel"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/systems/RenderSystem;clear(IZ)V",
         ordinal = 0,
         shift = Shift.AFTER,
         remap = false
      )}
   )
   private void limlib$render$clear(
      DeltaTracker deltaTracker,
      boolean bl,
      Camera camera,
      GameRenderer gameRenderer,
      LightTexture lightTexture,
      Matrix4f positionMatrix,
      Matrix4f projectionMatrix,
      CallbackInfo ci
   ) {
      if (!IrisBridge.IRIS_LOADED || !IrisBridge.areShadersInUse()) {
         Minecraft client = Minecraft.getInstance();
         LookupGrabber.<Skybox>snatchFromLevel(client.level, LimLibRegistryKeys.SKYBOX).ifPresent(sky -> {
            EffectRenderers.SkyBoxRenderer<Skybox> renderer = EffectRenderers.get(sky);
            if (renderer != null) {
               PoseStack poseStack = new PoseStack();
               poseStack.mulPose(positionMatrix);
               renderer.renderSky(sky, (LevelRenderer)this, client, poseStack, projectionMatrix, deltaTracker.getGameTimeDeltaPartialTick(false));
            }
         });
      }
   }
}

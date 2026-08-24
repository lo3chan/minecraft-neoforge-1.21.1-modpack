package com.github.alexthe666.citadel.mixin.client;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.client.event.EventGetOutlineColor;
import com.github.alexthe666.citadel.client.shader.PostEffectRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LevelRenderer.class})
public class LevelRendererMixin {
   @Final
   @Shadow
   private Minecraft minecraft;

   @Inject(
      method = {"initOutline"},
      at = {@At("TAIL")}
   )
   private void citadel_initOutline(CallbackInfo ci) {
      PostEffectRegistry.onInitializeOutline();
   }

   @Inject(
      method = {"resize"},
      at = {@At("TAIL")}
   )
   private void citadel_resize(int x, int y, CallbackInfo ci) {
      PostEffectRegistry.resize(x, y);
   }

   @Inject(
      method = {"renderLevel"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/RenderBuffers;bufferSource()Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;",
         shift = Shift.BEFORE
      )}
   )
   private void citadel_renderLevel_beforeEntities(
      DeltaTracker deltaTracker,
      boolean renderBlockOutline,
      Camera camera,
      GameRenderer gameRenderer,
      LightTexture lightTexture,
      Matrix4f frustumMatrix,
      Matrix4f projectionMatrix,
      CallbackInfo ci
   ) {
      PostEffectRegistry.clearAndBindWrite(this.minecraft.getMainRenderTarget());
   }

   @Inject(
      method = {"renderLevel"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/OutlineBufferSource;endOutlineBatch()V",
         shift = Shift.BEFORE
      )}
   )
   private void citadel_renderLevel_process(
      DeltaTracker deltaTracker,
      boolean renderBlockOutline,
      Camera camera,
      GameRenderer gameRenderer,
      LightTexture lightTexture,
      Matrix4f frustumMatrix,
      Matrix4f projectionMatrix,
      CallbackInfo ci
   ) {
      PostEffectRegistry.processEffects(this.minecraft.getMainRenderTarget());
   }

   @Inject(
      method = {"renderLevel"},
      at = {@At("TAIL")}
   )
   private void citadel_renderLevel_end(
      DeltaTracker deltaTracker,
      boolean renderBlockOutline,
      Camera camera,
      GameRenderer gameRenderer,
      LightTexture lightTexture,
      Matrix4f frustumMatrix,
      Matrix4f projectionMatrix,
      CallbackInfo ci
   ) {
      PostEffectRegistry.blitEffects();
   }

   @Redirect(
      method = {"renderLevel"},
      remap = true,
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;getTeamColor()I"
      )
   )
   private int citadel_getTeamColor(Entity entity) {
      EventGetOutlineColor event = new EventGetOutlineColor(entity, entity.getTeamColor());
      NeoForge.EVENT_BUS.post(event);
      int color = entity.getTeamColor();
      if (event.getResult() == TriState.TRUE) {
         color = event.getColor();
      }

      return color;
   }

   @Redirect(
      method = {"renderSky"},
      remap = true,
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/multiplayer/ClientLevel;getTimeOfDay(F)F"
      ),
      expect = 2
   )
   private float citadel_getTimeOfDay(ClientLevel instance, float partialTicks) {
      float lerpBy = Citadel.PROXY.isGamePaused() ? 0.0F : partialTicks;
      float lerpedDayTime = ((float)instance.dimensionType().fixedTime().orElse(instance.dayTime()) + lerpBy) / 24000.0F;
      double d0 = Mth.frac(lerpedDayTime - 0.25);
      double d1 = 0.5 - Math.cos(d0 * 3.141592653589793) / 2.0;
      return (float)(d0 * 2.0 + d1) / 3.0F;
   }
}

package dev.tr7zw.notenoughanimations.mixins;

import dev.tr7zw.notenoughanimations.NEAnimationsLoader;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LevelRenderer.class})
public class LevelRendererMixin {
   @Inject(
      method = {"renderLevel(Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V"},
      at = {@At("HEAD")}
   )
   private void beforeRender(
      DeltaTracker deltaTracker,
      boolean bl,
      Camera camera,
      GameRenderer gameRenderer,
      LightTexture lightTexture,
      Matrix4f matrix4f,
      Matrix4f matrix4f2,
      CallbackInfo ci
   ) {
      float tickDelta = deltaTracker.getGameTimeDeltaPartialTick(false);
      NEAnimationsLoader.INSTANCE.playerTransformer.setDeltaTick(tickDelta);
   }
}

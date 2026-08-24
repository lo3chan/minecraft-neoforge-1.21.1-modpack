package net.irisshaders.iris.mixin;

import net.irisshaders.iris.Iris;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LevelRenderer.class})
public class MixinLevelRenderer_Sky {
   @Shadow
   @Final
   private Minecraft minecraft;

   @Inject(
      method = {"renderSky"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void preRenderSky(Matrix4f matrix4f, Matrix4f matrix4f2, float f, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
      if (Iris.getCurrentPack().isEmpty()) {
         Vec3 cameraPosition = camera.getPosition();
         Entity cameraEntity = camera.getEntity();
         boolean isSubmersed = camera.getFluidInCamera() != FogType.NONE;
         boolean blockSky = ((LevelRendererAccessor)Minecraft.getInstance().levelRenderer).invokeDoesMobEffectBlockSky(camera);
         boolean useThickFog = this.minecraft.level.effects().isFoggyAt(Mth.floor(cameraPosition.x()), Mth.floor(cameraPosition.y()))
            || this.minecraft.gui.getBossOverlay().shouldCreateWorldFog();
         if (isSubmersed || blockSky || useThickFog) {
            ci.cancel();
         }
      }
   }
}

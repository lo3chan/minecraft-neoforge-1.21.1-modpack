package net.diebuddies.mixins;

import net.diebuddies.minecraft.LevelRendererAccessor;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.render.MainRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LevelRenderer.class})
public class MixinLevelRenderer implements LevelRendererAccessor {
   @Shadow
   private ClientLevel level;
   @Unique
   private MainRenderer mainRenderer = new MainRenderer();
   @Unique
   private Matrix4f prjSnow = new Matrix4f();
   @Unique
   private Matrix4f viewSnow = new Matrix4f();

   @Inject(
      at = {@At("TAIL")},
      method = {"renderSectionLayer"}
   )
   private void renderClothOptifine(
      RenderType blockLayerIn, double xIn, double yIn, double zInm, Matrix4f viewMatrix, Matrix4f projectionMatrix, CallbackInfo ci
   ) {
      if (blockLayerIn == RenderType.translucent()) {
      }
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"renderSectionLayer"}
   )
   private void renderMain(RenderType blockLayerIn, double xIn, double yIn, double zInm, Matrix4f viewMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
      if (blockLayerIn == RenderType.cutout()) {
         this.mainRenderer.renderAll(this.level, blockLayerIn, xIn, yIn, zInm, viewMatrix, projectionMatrix);
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"renderSectionLayer"}
   )
   private void renderLiquid(RenderType blockLayerIn, double xIn, double yIn, double zInm, Matrix4f viewMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
      if (blockLayerIn == RenderType.translucent()) {
         this.mainRenderer.renderLiquid(this.level, blockLayerIn, xIn, yIn, zInm, viewMatrix, projectionMatrix);
         this.mainRenderer.renderCloth(this.level, blockLayerIn, xIn, yIn, zInm, viewMatrix, projectionMatrix);
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"renderLevel"}
   )
   public void getSnowWorldViewProjection(
      DeltaTracker deltaTracker,
      boolean bl,
      Camera camera,
      GameRenderer gameRenderer,
      LightTexture lightTexture,
      Matrix4f viewMatrix,
      Matrix4f projectionMatrix,
      CallbackInfo info
   ) {
      if (this.level != null) {
         PhysicsMod mod = PhysicsMod.getInstance(this.level);
         this.prjSnow.set(projectionMatrix);
         this.viewSnow.set(viewMatrix);
         this.prjSnow.mul(this.viewSnow, mod.getPhysicsWorld().getSnowWorld().viewProjection);
      }
   }

   @Override
   public MainRenderer getMainRenderer() {
      return this.mainRenderer;
   }
}

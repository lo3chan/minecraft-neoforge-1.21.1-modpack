package net.diebuddies.mixins.snow;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.snow.IChunk;
import net.diebuddies.physics.snow.SnowWorld;
import net.diebuddies.physics.snow.math.Ray;
import net.diebuddies.physics.snow.math.RayHit;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Camera.class})
public class MixinCamera {
   @Shadow
   private BlockGetter level;
   @Shadow
   private Vec3 position = Vec3.ZERO;
   @Shadow
   @Final
   private Vector3f forwards;
   @Unique
   private Vector3d rayStartPos = new Vector3d();
   @Unique
   private Vector3d rayDirection = new Vector3d();

   @Inject(
      at = {@At("RETURN")},
      method = {"getMaxZoom"},
      cancellable = true
   )
   private void getMaxZoom(float maxZoom, CallbackInfoReturnable<Float> info) {
      if (this.level instanceof ClientLevel clientLevel && ConfigClient.areSnowPhysicsEnabled()) {
         try {
            SnowWorld snowWorld = PhysicsMod.getInstance(clientLevel).getPhysicsWorld().getSnowWorld();
            double offset = -0.5;
            this.rayStartPos
               .set(
                  this.position.x * IChunk.CHUNK_MULTIPLE + offset,
                  this.position.y * IChunk.CHUNK_MULTIPLE + offset,
                  this.position.z * IChunk.CHUNK_MULTIPLE + offset
               );
            this.rayDirection.set(-this.forwards.x(), -this.forwards.y(), -this.forwards.z());
            Ray ray = new Ray(this.rayStartPos, this.rayDirection);
            RayHit rayHit = snowWorld.contouring
               .castFastLevelRay(ray, 6.0 * IChunk.CHUNK_MULTIPLE, 0.25 * IChunk.CHUNK_MULTIPLE, 0.01 * IChunk.CHUNK_MULTIPLE, 24 * IChunk.CHUNK_MULTIPLE);
            if (rayHit != null) {
               double distanceToHit = rayHit.point.distance(this.rayStartPos) / IChunk.CHUNK_MULTIPLE;
               double zoomAdjustment = ConfigClient.snowThickness * 0.5;
               if (ConfigClient.snowQuality == 0) {
                  zoomAdjustment = ConfigClient.snowThickness * 0.75;
               }

               if (ConfigClient.snowType == 1 || !ConfigClient.snowSmoothShading) {
                  zoomAdjustment = 0.0;
               }

               info.setReturnValue((float)Math.min((double)maxZoom, distanceToHit * (0.9 - zoomAdjustment)));
            }
         } catch (Exception var13) {
         }
      }
   }
}

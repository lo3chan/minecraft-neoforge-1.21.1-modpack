package cc.cosmetica.cosmetica.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class Sniper {
   private static LivingEntity target;
   private static final float MAX_SNIPE_DISTANCE = 64.0F;

   @Nullable
   public static LivingEntity getTarget() {
      return target;
   }

   public static void updateTargetPlayer(Minecraft minecraft, float yawProbably) {
      Entity camera = minecraft.getCameraEntity();
      target = null;
      if (camera != null && minecraft.level != null) {
         minecraft.getProfiler().push("snipe");
         double maxDist = 64.0;
         HitResult pickResult = camera.pick(64.0, yawProbably, false);
         Vec3 eyePosition = camera.getEyePosition(yawProbably);
         double maxDistSqr = 64.0;
         maxDistSqr *= maxDistSqr;
         if (pickResult != null) {
            maxDistSqr = pickResult.getLocation().distanceToSqr(eyePosition);
         }

         Vec3 view = camera.getViewVector(1.0F);
         Vec3 castTowards = eyePosition.add(view.x * 64.0, view.y * 64.0, view.z * 64.0);
         float inflation = 1.0F;
         AABB selectionBoundingBox = camera.getBoundingBox().expandTowards(view.scale(64.0)).inflate(1.0, 1.0, 1.0);
         EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
            camera, eyePosition, castTowards, selectionBoundingBox, e -> !e.isSpectator() && e.isPickable(), maxDistSqr
         );
         if (entityHitResult != null) {
            Entity hitEntity = entityHitResult.getEntity();
            Vec3 resultLocation = entityHitResult.getLocation();
            double distance = eyePosition.distanceToSqr(resultLocation);
            if ((distance < maxDistSqr || pickResult == null) && hitEntity instanceof LivingEntity) {
               target = (LivingEntity)hitEntity;
            }
         }

         minecraft.getProfiler().pop();
      }
   }
}

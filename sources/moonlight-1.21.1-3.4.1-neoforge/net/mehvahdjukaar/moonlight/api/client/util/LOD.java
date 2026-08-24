package net.mehvahdjukaar.moonlight.api.client.util;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public final class LOD {
   public static final int BUFFER = sq(4);
   public static final int VERY_NEAR_DIST = sq(16);
   public static final int NEAR_DIST = sq(32);
   public static final int NEAR_MED_DIST = sq(48);
   public static final int MEDIUM_DIST = sq(64);
   public static final int FAR_DIST = sq(96);
   private final Vec3 cameraPosition;
   private final Vec3 cameraDirection;
   private final Vec3 objCenter;
   private final double distSq;
   public static final LOD MAX = new LOD(0.0);
   private static final float DEFAULT_RADIUS = 1.45F;

   public static LOD at(BlockEntity be) {
      return at(be.getBlockPos());
   }

   public static LOD at(BlockPos objPos) {
      Minecraft mc = Minecraft.getInstance();
      return at(mc.getBlockEntityRenderDispatcher().camera, objPos.getCenter());
   }

   public static LOD at(Camera camera, BlockPos objPos) {
      return at(camera, objPos.getCenter());
   }

   public static LOD at(Camera camera, Vec3 objCenter) {
      return new LOD(camera, objCenter);
   }

   private LOD(Camera camera, Vec3 objCenter) {
      this.cameraPosition = camera.getPosition();
      this.cameraDirection = new Vec3(camera.getLookVector()).normalize();
      this.objCenter = objCenter;
      this.distSq = isScoping() ? 1.0 : this.cameraPosition.distanceToSqr(objCenter);
   }

   private LOD(double distSq) {
      this.cameraPosition = Vec3.ZERO;
      this.cameraDirection = Vec3.ZERO;
      this.objCenter = Vec3.ZERO;
      this.distSq = distSq;
   }

   public static boolean isScoping() {
      Minecraft mc = Minecraft.getInstance();
      LocalPlayer p = mc.player;
      return p != null && mc.options.getCameraType().isFirstPerson() && p.isScoping();
   }

   @Deprecated(
      forRemoval = true
   )
   public LOD(Camera camera, BlockPos pos) {
      this.cameraPosition = camera.getPosition();
      this.cameraDirection = new Vec3(camera.getLookVector()).normalize();
      this.objCenter = pos.getCenter();
      this.distSq = isScoping() ? 1.0 : this.cameraPosition.distanceToSqr(this.objCenter);
   }

   @Deprecated(
      forRemoval = true
   )
   public LOD(Vec3 cameraPos, BlockPos pos) {
      this.cameraPosition = cameraPos;
      this.cameraDirection = Vec3.ZERO;
      this.objCenter = pos.getCenter();
      this.distSq = isScoping() ? 1.0 : this.cameraPosition.distanceToSqr(this.objCenter);
   }

   public boolean isVeryNear() {
      return this.distSq <= VERY_NEAR_DIST;
   }

   public boolean isNear() {
      return this.distSq <= NEAR_DIST;
   }

   public boolean isNearMed() {
      return this.distSq <= NEAR_MED_DIST;
   }

   public boolean isMedium() {
      return this.distSq <= MEDIUM_DIST;
   }

   public boolean isFar() {
      return this.distSq <= FAR_DIST;
   }

   public boolean within(double maxDist) {
      return this.distSq <= maxDist * maxDist;
   }

   public boolean isPlaneCulled(Vec3 normalVec) {
      return this.isPlaneCulled(normalVec, null, 0.0F);
   }

   public boolean isPlaneCulled(Vec3 normalVec, Vec3 offset) {
      return this.isPlaneCulled(normalVec, offset, 0.0F);
   }

   public boolean isPlaneCulled(Direction facing, float offset, float radius, float cosTolerance) {
      Vector3f normal = facing.step();
      return this.isPlaneCulled(new Vec3(normal), offset, radius, cosTolerance);
   }

   public boolean isPlaneCulled(Direction facing, float offset, float cosTolerance) {
      return this.isPlaneCulled(facing, offset, 1.45F, cosTolerance);
   }

   public boolean isPlaneCulled(Vec3 planeNormal, float offset, float cosTolerance) {
      return this.isPlaneCulled(planeNormal, offset, 1.45F, cosTolerance);
   }

   public boolean isPlaneCulled(Vec3 planeNormal, float offset, float radius, float cosTolerance) {
      return this.isPlaneCulled(planeNormal, planeNormal.scale(offset), radius, cosTolerance);
   }

   public boolean isPlaneCulled(Direction facing, float offset) {
      return this.isPlaneCulled(facing, offset, 0.0F);
   }

   public boolean isPlaneCulled(Vec3 planeNormal, @Nullable Vec3 offset, float cosTolerance) {
      return this.isPlaneCulled(planeNormal, offset, 1.45F, cosTolerance);
   }

   public boolean isPlaneCulled(Vec3 planeNormal, @Nullable Vec3 offset, float discRadius, float cosTolerance) {
      Vec3 planePoint = offset == null ? this.objCenter : this.objCenter.add(offset);
      Vec3 camToPlane = planePoint.subtract(this.cameraPosition);
      double forwardDist = camToPlane.dot(this.cameraDirection);
      if (forwardDist <= -discRadius) {
         return true;
      } else {
         Vec3 toCam = this.cameraPosition.subtract(planePoint);
         double len2 = toCam.lengthSqr();
         if (len2 <= 1.0E-12) {
            return false;
         } else {
            double cosFacing = planeNormal.dot(toCam) / Math.sqrt(len2);
            if (cosFacing <= cosTolerance) {
               return true;
            } else {
               double discRadius2 = discRadius * discRadius;
               if (discRadius2 >= len2) {
                  return false;
               } else {
                  double cosTheta = Math.sqrt(1.0 - discRadius2 / len2);
                  return cosFacing <= cosTolerance - cosTheta;
               }
            }
         }
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public static boolean isOutOfFocus(Vec3 cameraPos, BlockPos pos, float blockYaw) {
      return isOutOfFocus(cameraPos, pos, blockYaw, 0.0F, Direction.UP, 0.0F);
   }

   @Deprecated(
      forRemoval = true
   )
   public static boolean isOutOfFocus(Vec3 cameraPos, BlockPos pos, float blockYaw, float degMargin, Direction dir, float offset) {
      float relAngle = getRelativeAngle(cameraPos, pos, dir, offset);
      return isOutOfFocus(relAngle, blockYaw, degMargin);
   }

   @Deprecated(
      forRemoval = true
   )
   public static boolean isOutOfFocus(float relativeAngle, float blockYaw, float degMargin) {
      return Mth.degreesDifference(relativeAngle, blockYaw - 90.0F) > -degMargin;
   }

   @Deprecated(
      forRemoval = true
   )
   public static float getRelativeAngle(Vec3 cameraPos, BlockPos pos) {
      return getRelativeAngle(cameraPos, pos, Direction.UP, 0.0F);
   }

   @Deprecated(
      forRemoval = true
   )
   public static float getRelativeAngle(Vec3 cameraPos, BlockPos pos, Direction dir, float offset) {
      return (float)(
         Mth.atan2(offset * dir.getStepX() + cameraPos.x - (pos.getX() + 0.5F), offset * dir.getStepZ() + cameraPos.z - (pos.getZ() + 0.5F))
            * 180.0
            / 3.141592653589793
      );
   }

   public static int sq(int v) {
      return v * v;
   }
}

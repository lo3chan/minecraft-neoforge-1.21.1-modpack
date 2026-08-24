package com.iafenvoy.origins.util.math;

import com.mojang.serialization.Codec;
import java.util.Locale;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Vector3f;

public enum Space implements StringRepresentable {
   WORLD(false, false, false, false),
   LOCAL(true, false, false, false),
   LOCAL_HORIZONTAL(true, false, true, false),
   LOCAL_HORIZONTAL_NORMALIZED(true, false, true, true),
   VELOCITY(true, true, false, false),
   VELOCITY_NORMALIZED(true, true, false, true),
   VELOCITY_HORIZONTAL(true, true, true, false),
   VELOCITY_HORIZONTAL_NORMALIZED(true, true, true, true);

   public static final Codec<Space> CODEC = StringRepresentable.fromValues(Space::values);
   private final boolean process;
   private final boolean velocity;
   private final boolean horizontal;
   private final boolean normalize;

   private Space(boolean process, boolean velocity, boolean horizontal, boolean normalize) {
      this.process = process;
      this.velocity = velocity;
      this.horizontal = horizontal;
      this.normalize = normalize;
   }

   public void toGlobal(Vector3f vector, Entity entity) {
      if (this.process) {
         Vec3 vec3 = this.velocity ? entity.getDeltaMovement() : entity.getLookAngle();
         if (this.horizontal) {
            vec3 = new Vec3(vec3.x(), 0.0, vec3.z());
         }

         transformVectorToBase(vec3, vector, entity.getYRot(), this.normalize);
      }
   }

   @NotNull
   public String getSerializedName() {
      return this.name().toLowerCase(Locale.ROOT);
   }

   public static void transformVectorToBase(Vec3 baseForwardVector, Vector3f vector, float baseYaw, boolean normalizeBase) {
      double baseScaleD = baseForwardVector.length();
      if (baseScaleD <= 0.007) {
         vector.zero();
      } else {
         float baseScale = (float)baseScaleD;
         Vec3 normalizedBase = baseForwardVector.normalize();
         Matrix3f transformMatrix = getBaseTransformMatrixFromNormalizedDirectionVector(normalizedBase, baseYaw);
         if (!normalizeBase) {
            transformMatrix.scale(baseScale, baseScale, baseScale);
         }

         vector.mulTranspose(transformMatrix);
      }
   }

   private static Matrix3f getBaseTransformMatrixFromNormalizedDirectionVector(Vec3 vector, float yaw) {
      double zX = 0.0;
      double zY = vector.y();
      double zZ = 0.0;
      double xX;
      double xZ;
      if (Math.abs(zY) != 1.0) {
         zX = vector.x();
         zZ = vector.z();
         xX = vector.z();
         xZ = -vector.x();
         float xFactor = (float)(1.0 / Math.sqrt(xX * xX + xZ * xZ));
         xX *= xFactor;
         xZ *= xFactor;
      } else {
         float trigonometricYaw = -yaw * 0.017453292F;
         xX = Mth.cos(trigonometricYaw);
         xZ = -Mth.sin(trigonometricYaw);
      }

      Matrix3f res = new Matrix3f();
      res.set(0, 0, (float)xX);
      res.set(1, 0, 0.0F);
      res.set(2, 0, (float)xZ);
      res.set(0, 1, (float)(zY * xZ));
      res.set(1, 1, (float)(zZ * xX - zX * xZ));
      res.set(2, 1, (float)(-zY * xX));
      res.set(0, 2, (float)zX);
      res.set(1, 2, (float)zY);
      res.set(2, 2, (float)zZ);
      return res;
   }
}

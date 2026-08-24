package com.seibel.distanthorizons.core.render;

public class CameraZoom {
   public static final CameraZoom NOT_ZOOMED = createNotZoomed();
   public double magnification;
   public double coneTanHalfAngle;
   public double lookDirectionX;
   public double lookDirectionZ;

   public static CameraZoom createNotZoomed() {
      return new CameraZoom(1.0, 0.0, 0.0, 0.0);
   }

   public CameraZoom(double magnification, double coneTanHalfAngle, double lookDirectionX, double lookDirectionZ) {
      this.magnification = magnification;
      this.coneTanHalfAngle = coneTanHalfAngle;
      this.lookDirectionX = lookDirectionX;
      this.lookDirectionZ = lookDirectionZ;
   }

   public void set(CameraZoom that) {
      this.set(that.magnification, that.coneTanHalfAngle, that.lookDirectionX, that.lookDirectionZ);
   }

   public void set(double magnification, double coneTanHalfAngle, double lookDirectionX, double lookDirectionZ) {
      this.magnification = magnification;
      this.coneTanHalfAngle = coneTanHalfAngle;
      this.lookDirectionX = lookDirectionX;
      this.lookDirectionZ = lookDirectionZ;
   }

   public boolean coneIntersectsCircle(double coneOriginX, double coneOriginZ, double circleCenterX, double circleCenterZ, double circleRadius) {
      double offsetX = circleCenterX - coneOriginX;
      double offsetZ = circleCenterZ - coneOriginZ;
      double distanceAlongLook = offsetX * this.lookDirectionX + offsetZ * this.lookDirectionZ;
      if (distanceAlongLook < -circleRadius) {
         return false;
      } else {
         double distanceAcrossLook = Math.abs(offsetX * this.lookDirectionZ - offsetZ * this.lookDirectionX);
         return distanceAcrossLook <= distanceAlongLook * this.coneTanHalfAngle + circleRadius;
      }
   }
}

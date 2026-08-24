package dev.tr7zw.waveycapes.versionless.util;

public interface CapePoint {
   float getLerpX(float var1);

   float getLerpY(float var1);

   float getLerpZ(float var1);

   default Vector3 getLerpedPos(float delta) {
      return new Vector3(this.getLerpX(delta), this.getLerpY(delta), this.getLerpZ(delta));
   }
}

package dev.tr7zw.waveycapes.versionless.util;

import lombok.Generated;

public class Vector4 {
   public float x;
   public float y;
   public float z;
   public float w;

   public Vector4 clone() {
      return new Vector4(this.x, this.y, this.z, this.w);
   }

   public Vector3 toVec3() {
      return new Vector3(this.x, this.y, this.z);
   }

   @Generated
   public Vector4() {
   }

   @Generated
   public Vector4(float x, float y, float z, float w) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.w = w;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof Vector4 other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (Float.compare(this.x, other.x) != 0) {
         return false;
      } else if (Float.compare(this.y, other.y) != 0) {
         return false;
      } else {
         return Float.compare(this.z, other.z) != 0 ? false : Float.compare(this.w, other.w) == 0;
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof Vector4;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + Float.floatToIntBits(this.x);
      result = result * 59 + Float.floatToIntBits(this.y);
      result = result * 59 + Float.floatToIntBits(this.z);
      return result * 59 + Float.floatToIntBits(this.w);
   }

   @Generated
   @Override
   public String toString() {
      return "Vector4(x=" + this.x + ", y=" + this.y + ", z=" + this.z + ", w=" + this.w + ")";
   }
}

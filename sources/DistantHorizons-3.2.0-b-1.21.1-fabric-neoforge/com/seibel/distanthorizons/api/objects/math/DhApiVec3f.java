package com.seibel.distanthorizons.api.objects.math;

import com.seibel.distanthorizons.api.interfaces.util.IDhApiCopyable;

public class DhApiVec3f implements IDhApiCopyable {
   public float x;
   public float y;
   public float z;

   public DhApiVec3f() {
      this.x = 0.0F;
      this.y = 0.0F;
      this.z = 0.0F;
   }

   public DhApiVec3f(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         DhApiVec3f Vec3f = (DhApiVec3f)obj;
         if (Float.compare(Vec3f.x, this.x) != 0) {
            return false;
         } else {
            return Float.compare(Vec3f.y, this.y) != 0 ? false : Float.compare(Vec3f.z, this.z) == 0;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int i = Float.floatToIntBits(this.x);
      i = 31 * i + Float.floatToIntBits(this.y);
      return 31 * i + Float.floatToIntBits(this.z);
   }

   @Override
   public String toString() {
      return "[" + this.x + ", " + this.y + ", " + this.z + "]";
   }

   public DhApiVec3f copy() {
      return new DhApiVec3f(this.x, this.y, this.z);
   }
}

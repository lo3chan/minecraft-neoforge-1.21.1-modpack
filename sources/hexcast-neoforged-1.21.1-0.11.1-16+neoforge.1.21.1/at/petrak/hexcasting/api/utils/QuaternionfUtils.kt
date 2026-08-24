package at.petrak.hexcasting.api.utils

import org.joml.Quaternionf
import org.joml.Quaternionfc
import org.joml.Vector3f

public object QuaternionfUtils {
   @JvmStatic
   public final val ONE: Quaternionf
      public final get() {
         return new Quaternionf(0.0F, 0.0F, 0.0F, 1.0F);
      }


   @JvmStatic
   public fun fromXYZDegrees(vector3f: Vector3f): Quaternionf {
      return fromXYZ((float)Math.toRadians((double)vector3f.x()), (float)Math.toRadians((double)vector3f.y()), (float)Math.toRadians((double)vector3f.z()));
   }

   @JvmStatic
   public fun fromXYZ(vector3f: Vector3f): Quaternionf {
      return fromXYZ(vector3f.x(), vector3f.y(), vector3f.z());
   }

   @JvmStatic
   public fun fromXYZ(f: Float, g: Float, h: Float): Quaternionf {
      val quaternion: Quaternionf = getONE();
      quaternion.mul((new Quaternionf((float)Math.sin((double)(f / 2.0F)), 0.0F, 0.0F, (float)Math.cos((double)(f / 2.0F)))) as Quaternionfc);
      quaternion.mul((new Quaternionf(0.0F, (float)Math.sin((double)(g / 2.0F)), 0.0F, (float)Math.cos((double)(g / 2.0F)))) as Quaternionfc);
      quaternion.mul((new Quaternionf(0.0F, 0.0F, (float)Math.sin((double)(h / 2.0F)), (float)Math.cos((double)(h / 2.0F)))) as Quaternionfc);
      return quaternion;
   }
}

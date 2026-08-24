package at.petrak.hexcasting.api.utils

public object MathUtils {
   @JvmStatic
   public fun clamp(long: Long, min: Long, max: Long): Long {
      return if (var0 <= min) min else (if (var0 >= max) max else var0);
   }
}

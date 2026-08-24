package at.petrak.hexcasting.api.casting.math

import kotlin.enums.EnumEntries

public enum class HexAngle {
   FORWARD,
   RIGHT,
   RIGHT_BACK,
   BACK,
   LEFT_BACK,
   LEFT
   public fun rotatedBy(a: HexAngle): HexAngle {
      return values()[(this.ordinal() + a.ordinal()) % values().length];
   }

   public operator fun times(a: HexAngle): HexAngle {
      return this.rotatedBy(a);
   }

   @JvmStatic
   fun getEntries(): EnumEntries<HexAngle> {
      return $ENTRIES;
   }
}

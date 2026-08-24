package at.petrak.hexcasting.api.casting.math

import at.petrak.hexcasting.api.utils.HexUtils
import kotlin.enums.EnumEntries

public enum class HexDir {
   NORTH_EAST,
   EAST,
   SOUTH_EAST,
   SOUTH_WEST,
   WEST,
   NORTH_WEST   @JvmStatic
   public HexDir.Companion Companion = new HexDir.Companion(null);

   public fun rotatedBy(a: HexAngle): HexDir {
      val var10000: Array<HexDir> = values();
      val var2: Int = this.ordinal() + a.ordinal();
      val var3: Int = values().length;
      return var10000[var2 % var3 + (var3 and ((var2 % var3 xor var3) and (var2 % var3 or -(var2 % var3))) shr 31)];
   }

   public operator fun times(a: HexAngle): HexDir {
      return this.rotatedBy(a);
   }

   public fun angleFrom(other: HexDir): HexAngle {
      val var10000: Array<HexAngle> = HexAngle.values();
      val var2: Int = this.ordinal() - other.ordinal();
      val var3: Int = HexAngle.values().length;
      return var10000[var2 % var3 + (var3 and ((var2 % var3 xor var3) and (var2 % var3 or -(var2 % var3))) shr 31)];
   }

   public operator fun minus(other: HexDir): HexAngle {
      return this.angleFrom(other);
   }

   public fun asDelta(): HexCoord {
      var var10000: HexCoord;
      switch (HexDir.WhenMappings.$EnumSwitchMapping$0[this.ordinal()]) {
         case 1:
            var10000 = new HexCoord(1, -1);
            break;
         case 2:
            var10000 = new HexCoord(1, 0);
            break;
         case 3:
            var10000 = new HexCoord(0, 1);
            break;
         case 4:
            var10000 = new HexCoord(-1, 1);
            break;
         case 5:
            var10000 = new HexCoord(-1, 0);
            break;
         case 6:
            var10000 = new HexCoord(0, -1);
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      return var10000;
   }

   @JvmStatic
   fun getEntries(): EnumEntries<HexDir> {
      return $ENTRIES;
   }

   public companion object {
      public fun fromString(key: String): HexDir {
         return HexUtils.getSafe(HexDir.values(), key, HexDir.WEST);
      }
   }
}

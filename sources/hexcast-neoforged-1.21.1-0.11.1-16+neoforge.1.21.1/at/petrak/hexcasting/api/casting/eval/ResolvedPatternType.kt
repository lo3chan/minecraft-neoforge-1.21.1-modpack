package at.petrak.hexcasting.api.casting.eval

import at.petrak.hexcasting.api.utils.HexUtils
import kotlin.enums.EnumEntries

public enum class ResolvedPatternType(color: Int, fadeColor: Int, success: Boolean) {
   UNRESOLVED(8355711, 13421772, false),
   EVALUATED(7570910, 16698342, true),
   ESCAPED(14535795, 16775909, true),
   UNDONE(11692907, 13412494, true),
   ERRORED(14574178, 16762784, false),
   INVALID(11692907, 13412494, false)
   public final val color: Int
   public final val fadeColor: Int
   public final val success: Boolean
   @JvmStatic
   public ResolvedPatternType.Companion Companion = new ResolvedPatternType.Companion(null);

   init {
      this.color = color;
      this.fadeColor = fadeColor;
      this.success = success;
   }

   @JvmStatic
   fun getEntries(): EnumEntries<ResolvedPatternType> {
      return $ENTRIES;
   }

   public companion object {
      public fun fromString(key: String): ResolvedPatternType {
         return HexUtils.getSafe$default(ResolvedPatternType.values(), key, null, 2, null) as ResolvedPatternType;
      }
   }
}

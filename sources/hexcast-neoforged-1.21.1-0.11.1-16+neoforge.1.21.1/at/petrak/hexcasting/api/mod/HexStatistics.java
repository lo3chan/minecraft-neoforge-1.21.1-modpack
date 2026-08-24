package at.petrak.hexcasting.api.mod;

import at.petrak.hexcasting.api.HexAPI;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

public class HexStatistics {
   public static final ResourceLocation MEDIA_USED = makeCustomStat("media_used", mediamount -> StatFormatter.DEFAULT.format((int)(mediamount / 10000L)));
   public static final ResourceLocation MEDIA_OVERCAST = makeCustomStat(
      "media_overcast", mediamount -> StatFormatter.DEFAULT.format((int)(mediamount / 10000L))
   );
   public static final ResourceLocation PATTERNS_DRAWN = makeCustomStat("patterns_drawn", StatFormatter.DEFAULT);
   public static final ResourceLocation SPELLS_CAST = makeCustomStat("spells_cast", StatFormatter.DEFAULT);

   public static void register() {
   }

   private static ResourceLocation makeCustomStat(String key, StatFormatter formatter) {
      ResourceLocation resourcelocation = HexAPI.modLoc(key);
      Registry.register(BuiltInRegistries.CUSTOM_STAT, key, resourcelocation);
      Stats.CUSTOM.get(resourcelocation, formatter);
      return resourcelocation;
   }
}

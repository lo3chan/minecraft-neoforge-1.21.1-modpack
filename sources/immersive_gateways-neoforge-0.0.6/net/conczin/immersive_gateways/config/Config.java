package net.conczin.immersive_gateways.config;

import java.util.Map;

public final class Config extends JsonConfig {
   private static final Config INSTANCE = loadOrCreate(new Config(), Config.class);
   public int minDistance = 1024;
   public int maxDistance = 8096;
   public boolean onlyPlayersCanTeleport = true;
   public Map<String, Integer> colors = Map.ofEntries(
      Map.entry("minecraft:plains", 8368696),
      Map.entry("minecraft:desert", 16247203),
      Map.entry("minecraft:forest", 5869585),
      Map.entry("minecraft:taiga", 5073227),
      Map.entry("minecraft:swamp", 7188501),
      Map.entry("minecraft:jungle", 3371810),
      Map.entry("minecraft:savanna", 14188339),
      Map.entry("minecraft:badlands", 12217644),
      Map.entry("minecraft:snowy_tundra", 10526975),
      Map.entry("minecraft:mountains", 5204046),
      Map.entry("minecraft:beach", 16247203),
      Map.entry("minecraft:ocean", 4882687),
      Map.entry("minecraft:river", 6085589),
      Map.entry("minecraft:nether_wastes", 9314336),
      Map.entry("minecraft:the_end", 9079516),
      Map.entry("minecraft:mushroom_fields", 8336979),
      Map.entry("minecraft:dark_forest", 660993),
      Map.entry("minecraft:birch_forest", 5139259),
      Map.entry("minecraft:snowy_mountains", 10526975),
      Map.entry("minecraft:flower_forest", 15892389),
      Map.entry("minecraft:lukewarm_ocean", 6724056),
      Map.entry("minecraft:cold_ocean", 5801146),
      Map.entry("minecraft:deep_ocean", 2372989),
      Map.entry("minecraft:wooded_badlands_plateau", 12217644),
      Map.entry("minecraft:sunflower_plains", 16445005),
      Map.entry("minecraft:the_void", 3091015),
      Map.entry("minecraft:snowy_plains", 15200511),
      Map.entry("minecraft:ice_spikes", 10409215),
      Map.entry("minecraft:mangrove_swamp", 3116870),
      Map.entry("minecraft:pale_garden", 15925201),
      Map.entry("minecraft:old_growth_birch_forest", 7323514),
      Map.entry("minecraft:old_growth_pine_taiga", 2378290),
      Map.entry("minecraft:old_growth_spruce_taiga", 2046506),
      Map.entry("minecraft:snowy_taiga", 12968919),
      Map.entry("minecraft:savanna_plateau", 15771980),
      Map.entry("minecraft:windswept_hills", 8827553),
      Map.entry("minecraft:windswept_gravelly_hills", 11053234),
      Map.entry("minecraft:windswept_forest", 3757868),
      Map.entry("minecraft:windswept_savanna", 14919732),
      Map.entry("minecraft:sparse_jungle", 5809982),
      Map.entry("minecraft:bamboo_jungle", 3003516),
      Map.entry("minecraft:eroded_badlands", 15239482),
      Map.entry("minecraft:wooded_badlands", 13924404),
      Map.entry("minecraft:meadow", 8381326),
      Map.entry("minecraft:cherry_grove", 16752335),
      Map.entry("minecraft:grove", 12644056),
      Map.entry("minecraft:snowy_slopes", 16055295),
      Map.entry("minecraft:frozen_peaks", 13038335),
      Map.entry("minecraft:jagged_peaks", 10406376),
      Map.entry("minecraft:stony_peaks", 12502481),
      Map.entry("minecraft:frozen_river", 8179967),
      Map.entry("minecraft:snowy_beach", 16381406),
      Map.entry("minecraft:stony_shore", 9146262),
      Map.entry("minecraft:warm_ocean", 3071200),
      Map.entry("minecraft:deep_lukewarm_ocean", 5150412),
      Map.entry("minecraft:deep_cold_ocean", 3895205),
      Map.entry("minecraft:frozen_ocean", 9095423),
      Map.entry("minecraft:deep_frozen_ocean", 5670097),
      Map.entry("minecraft:dripstone_caves", 12618846),
      Map.entry("minecraft:lush_caves", 3988587),
      Map.entry("minecraft:deep_dark", 860208),
      Map.entry("minecraft:warped_forest", 2154691),
      Map.entry("minecraft:crimson_forest", 14104132),
      Map.entry("minecraft:soul_sand_valley", 10127227),
      Map.entry("minecraft:basalt_deltas", 6184550),
      Map.entry("minecraft:end_highlands", 13948159),
      Map.entry("minecraft:end_midlands", 12632306),
      Map.entry("minecraft:small_end_islands", 11184862),
      Map.entry("minecraft:end_barrens", 9276868)
   );

   public Config() {
      super("immersive_gateways");
   }

   public static Config getInstance() {
      return INSTANCE;
   }

   @Override
   int getVersion() {
      return 0;
   }
}

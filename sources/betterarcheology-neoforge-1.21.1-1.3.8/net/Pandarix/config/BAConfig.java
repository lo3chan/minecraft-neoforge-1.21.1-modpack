package net.Pandarix.config;

import com.teamresourceful.resourcefulconfig.api.annotations.Comment;
import com.teamresourceful.resourcefulconfig.api.annotations.Config;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo.Provider;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption.Range;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption.Separator;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption.Slider;
import com.teamresourceful.resourcefulconfig.api.types.options.EntryType;

@Provider(BAConfigInfoProvider.class)
@Config("betterarcheology")
public final class BAConfig {
   @Separator("Misc.")
   @ConfigEntry(
      id = "rustyBombTerrainDamage",
      type = EntryType.BOOLEAN,
      translation = "config.betterarcheology.rustyBombTerrainDamage"
   )
   public static boolean rustyBombTerrainDamage = true;
   @Separator("Brushes")
   @Slider
   @Range(
      min = 1.0,
      max = 10.0
   )
   @Comment("Time between brush uses in ticks. Lower values are faster. The vanilla brush has a speed of 10.")
   @ConfigEntry(
      id = "ironBrushTickRate",
      type = EntryType.INTEGER,
      translation = "config.betterarcheology.ironBrushTickRate"
   )
   public static int ironBrushTickRate = 7;
   @Slider
   @Range(
      min = 1.0,
      max = 10.0
   )
   @ConfigEntry(
      id = "diamondBrushTickRate",
      type = EntryType.INTEGER,
      translation = "config.betterarcheology.diamondBrushTickRate"
   )
   public static int diamondBrushTickRate = 5;
   @Slider
   @Range(
      min = 1.0,
      max = 10.0
   )
   @ConfigEntry(
      id = "netheriteBrushTickRate",
      type = EntryType.INTEGER,
      translation = "config.betterarcheology.netheriteBrushTickRate"
   )
   public static int netheriteBrushTickRate = 3;
   @Separator("Artifacts")
   @ConfigEntry(
      id = "artifactsEnabled",
      type = EntryType.BOOLEAN,
      translation = "config.betterarcheology.artifactsEnabled"
   )
   public static boolean artifactsEnabled = true;
   @ConfigEntry(
      id = "penetratingStrikeEnabled",
      type = EntryType.BOOLEAN,
      translation = "config.betterarcheology.penetratingStrikeEnabled"
   )
   public static boolean penetratingStrikeEnabled = true;
   @ConfigEntry(
      id = "penetratingStrikeIgnorance",
      type = EntryType.DOUBLE,
      translation = "config.betterarcheology.penetratingStrikeIgnorance"
   )
   @Slider
   @Range(
      min = 0.0,
      max = 1.0
   )
   @Comment("Set to % of damage-reduction from Protection Enchantments that should be ignored.")
   public static double penetratingStrikeIgnorance = 0.33;
   @ConfigEntry(
      id = "soaringWindsEnabled",
      type = EntryType.BOOLEAN,
      translation = "config.betterarcheology.soaringWindsEnabled"
   )
   public static boolean soaringWindsEnabled = true;
   @ConfigEntry(
      id = "soaringWindsBoost",
      type = EntryType.DOUBLE,
      translation = "config.betterarcheology.soaringWindsBoost"
   )
   @Slider
   @Range(
      min = 0.1,
      max = 3.0
   )
   public static double soaringWindsBoost = 0.75;
   @ConfigEntry(
      id = "tunnelingEnabled",
      type = EntryType.BOOLEAN,
      translation = "config.betterarcheology.tunnelingEnabled"
   )
   public static boolean tunnelingEnabled = true;
   @ConfigEntry(
      id = "tunnelingEffectiveTool",
      type = EntryType.BOOLEAN,
      translation = "config.betterarcheology.tunnelingEffectiveTool"
   )
   @Comment("Only mines the Block below if the Tool used is effective for the Block mined (e.g. Shovel on Stone).")
   public static boolean tunnelingEffectiveTool = true;
   @ConfigEntry(
      id = "tunnelingTolerance",
      type = EntryType.DOUBLE,
      translation = "config.betterarcheology.tunnelingTolerance"
   )
   @Slider
   @Range(
      min = 0.0,
      max = 10.0
   )
   @Comment("The difference of hardness between the two blocks to break that is allowed. Per default, this prevents e.g. mining Obsidian below when mining stone, but allows for ores below to be mined. For reference: Stone has 1.5.")
   public static double tunnelingTolerance = 3.75;
   @Separator("Totems")
   @ConfigEntry(
      id = "totemsEnabled",
      type = EntryType.BOOLEAN,
      translation = "config.betterarcheology.totemsEnabled"
   )
   public static boolean totemsEnabled = true;
   @ConfigEntry(
      id = "radianceTotemEnabled",
      type = EntryType.BOOLEAN,
      translation = "config.betterarcheology.radianceTotemEnabled"
   )
   public static boolean radianceTotemEnabled = true;
   @ConfigEntry(
      id = "radianceTotemDamageEnabled",
      type = EntryType.BOOLEAN,
      translation = "config.betterarcheology.radianceTotemDamageEnabled"
   )
   public static boolean radianceTotemDamageEnabled = true;
   @ConfigEntry(
      id = "radianceTotemDamage",
      type = EntryType.INTEGER,
      translation = "config.betterarcheology.radianceTotemDamage"
   )
   @Range(
      min = 1.0,
      max = 40.0
   )
   @Comment("Sets the damage in 1/2 hearts that will be dealt to hostile mobs when a damage tick occurs.")
   public static int radianceTotemDamage = 4;
   @ConfigEntry(
      id = "radianceTotemDamageTickAverage",
      type = EntryType.INTEGER,
      translation = "config.betterarcheology.radianceTotemDamageTickAverage"
   )
   @Range(
      min = 1.0,
      max = 60.0
   )
   @Comment("Sets the average time between damage ticks of the Radiance Totem in seconds. The totem will still damage mobs randomly, but the average time between damage ticks will be this value.")
   public static int radianceTotemDamageTickAverage = 3;
   @ConfigEntry(
      id = "radianceTotemRadius",
      type = EntryType.INTEGER,
      translation = "config.betterarcheology.radianceTotemRadius"
   )
   @Range(
      min = 1.0,
      max = 90.0
   )
   public static int radianceTotemRadius = 10;
   @ConfigEntry(
      id = "torrentTotemEnabled",
      type = EntryType.BOOLEAN,
      translation = "config.betterarcheology.torrentTotemEnabled"
   )
   public static boolean torrentTotemEnabled = true;
   @ConfigEntry(
      id = "torrentTotemBoost",
      type = EntryType.DOUBLE,
      translation = "config.betterarcheology.torrentTotemBoost"
   )
   @Slider
   @Range(
      min = 0.1,
      max = 3.0
   )
   public static double torrentTotemBoost = 1.0;
   @ConfigEntry(
      id = "torrentTotemUpwardsBoost",
      type = EntryType.BOOLEAN,
      translation = "config.betterarcheology.torrentTotemUpwardsBoost"
   )
   @Comment("Lets the Totem of Torrents dash up and down as well, following where the player aims. Disable to restrict the dash to the horizontal plane.")
   public static boolean torrentTotemUpwardsBoost = true;
   @ConfigEntry(
      id = "soulTotemEnabled",
      type = EntryType.BOOLEAN,
      translation = "config.betterarcheology.soulTotemEnabled"
   )
   public static boolean soulTotemEnabled = true;
   @ConfigEntry(
      id = "growthTotemEnabled",
      type = EntryType.BOOLEAN,
      translation = "config.betterarcheology.growthTotemEnabled"
   )
   public static boolean growthTotemEnabled = true;
   @ConfigEntry(
      id = "growthTotemGrowRadius",
      type = EntryType.INTEGER,
      translation = "config.betterarcheology.growthTotemGrowRadius"
   )
   @Range(
      min = 1.0,
      max = 50.0
   )
   public static int growthTotemGrowRadius = 5;
   @ConfigEntry(
      id = "growthTotemGrowChance",
      type = EntryType.INTEGER,
      translation = "config.betterarcheology.growthTotemGrowChance"
   )
   @Range(
      min = 1.0,
      max = 100.0
   )
   @Comment("The growth totem uses the randomTick to determine when it should grow crops. This value determines the chance in % that a random tick actually grows crops to potentially decrease its yield. For example, a 20% chance bonemeals a crop ~10.5 times an hour")
   public static int growthTotemGrowChance = 20;
   @Separator("Fossils")
   @ConfigEntry(
      id = "fossilEffectsEnabled",
      type = EntryType.BOOLEAN,
      translation = "config.betterarcheology.fossilEffectsEnabled"
   )
   public static boolean fossilEffectsEnabled = true;
   @ConfigEntry(
      id = "fossilFleeRange",
      type = EntryType.INTEGER,
      translation = "config.betterarcheology.fossilFleeRange"
   )
   @Range(
      min = 10.0,
      max = 50.0
   )
   public static int fossilFleeRange = 20;
   @ConfigEntry(
      id = "chickenFossilEffectsEnabled",
      type = EntryType.BOOLEAN,
      translation = "config.betterarcheology.chickenFossilEffectsEnabled"
   )
   public static boolean chickenFossilEffectsEnabled = true;
   @ConfigEntry(
      id = "ocelotFossilEffectsEnabled",
      type = EntryType.BOOLEAN,
      translation = "config.betterarcheology.ocelotFossilEffectsEnabled"
   )
   public static boolean ocelotFossilEffectsEnabled = true;
   @ConfigEntry(
      id = "wolfFossilEffectsEnabled",
      type = EntryType.BOOLEAN,
      translation = "config.betterarcheology.wolfFossilEffectsEnabled"
   )
   public static boolean wolfFossilEffectsEnabled = true;
   @ConfigEntry(
      id = "guardianFossilEffectsEnabled",
      type = EntryType.BOOLEAN,
      translation = "config.betterarcheology.guardianFossilEffectsEnabled"
   )
   public static boolean guardianFossilEffectsEnabled = true;
}

package net.joefoxe.hexerei.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.config.ModConfigEvent.Loading;
import net.neoforged.fml.event.config.ModConfigEvent.Reloading;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

@EventBusSubscriber(
   modid = "hexerei",
   bus = Bus.MOD
)
public class HexConfig {
   public static ModConfigSpec COMMON_CONFIG;
   public static ModConfigSpec CLIENT_CONFIG;
   public static BooleanValue JARS_ONLY_HOLD_HERBS;
   public static ConfigValue<Integer> SAGE_BURNING_PLATE_RANGE;
   public static ConfigValue<Integer> CROW_PICKPOCKET_COOLDOWN;
   public static ConfigValue<Integer> BROOM_BRUSH_DURABILITY;
   public static ConfigValue<Integer> HERB_ENHANCED_BRUSH_DURABILITY;
   public static ConfigValue<Integer> MOON_DUST_BRUSH_DURABILITY;
   public static ConfigValue<Integer> THRUSTER_BRUSH_DURABILITY;
   public static ConfigValue<Integer> BROOM_WATERPROOF_TIP_DURABILITY;
   public static ConfigValue<Integer> BROOM_NETHERITE_TIP_DURABILITY;
   public static ConfigValue<Integer> SAGE_BUNDLE_DURATION;
   public static ConfigValue<List<? extends String>> COFFER_BLACKLIST;
   public static ConfigValue<Integer> WILLOW_SWAMP_RARITY;
   public static ConfigValue<Boolean> DYNAMIC_LIGHT_TOGGLE;
   public static ConfigValue<Boolean> BOOK_SHADERS_TOGGLE;
   private static ConfigValue<List<? extends String>> ENTITY_LIGHT_CONFIG;
   private static ConfigValue<List<? extends String>> ITEM_LIGHT_CONFIG;
   public static Map<ResourceLocation, Integer> ENTITY_LIGHT_MAP = new HashMap<>();
   public static Map<ResourceLocation, Integer> ITEM_LIGHTMAP = new HashMap<>();
   public static ConfigValue<List<? extends String>> FONT_LIST;

   @SubscribeEvent
   public static void onLoad(Loading configEvent) {
      if (configEvent.getConfig().getSpec() == CLIENT_CONFIG) {
         resetLightMaps();
      }
   }

   @SubscribeEvent
   public static void onReload(Reloading configEvent) {
      if (configEvent.getConfig().getSpec() == CLIENT_CONFIG) {
         resetLightMaps();
      }
   }

   public static void resetLightMaps() {
      ENTITY_LIGHT_MAP = new HashMap<>();
      ITEM_LIGHTMAP = new HashMap<>();

      for (Entry<String, Integer> entry : HexConfig.ConfigUtil.parseMapConfig(ENTITY_LIGHT_CONFIG).entrySet()) {
         ENTITY_LIGHT_MAP.put(ResourceLocation.parse(entry.getKey()), entry.getValue());
      }

      for (Entry<String, Integer> entry : HexConfig.ConfigUtil.parseMapConfig(ITEM_LIGHT_CONFIG).entrySet()) {
         ITEM_LIGHTMAP.put(ResourceLocation.parse(entry.getKey()), entry.getValue());
      }
   }

   public static Map<String, Integer> getDefaultEntityLight() {
      Map<String, Integer> map = new HashMap<>();
      map.put("minecraft:blaze", 10);
      map.put("minecraft:spectral_arrow", 8);
      map.put("minecraft:magma_cube", 8);
      return map;
   }

   public static Map<String, Integer> getDefaultItemLight() {
      Map<String, Integer> map = new HashMap<>();
      map.put("minecraft:glowstone", 15);
      map.put("minecraft:torch", 14);
      map.put("hexerei:moon_dust", 8);
      map.put("minecraft:glowstone_dust", 8);
      map.put("minecraft:redstone_torch", 10);
      map.put("minecraft:soul_torch", 10);
      map.put("minecraft:blaze_rod", 10);
      map.put("minecraft:glow_berries", 8);
      map.put("minecraft:lava_bucket", 15);
      map.put("minecraft:lantern", 14);
      map.put("minecraft:soul_lantern", 12);
      map.put("minecraft:shroomlight", 10);
      map.put("minecraft:glow_ink_sac", 10);
      map.put("minecraft:nether_star", 14);
      map.put("minecraft:ochre_froglight", 15);
      map.put("minecraft:pearlescent_froglight", 15);
      map.put("minecraft:verdant_froglight", 15);
      return map;
   }

   public static String an(String s) {
      return ResourceLocation.fromNamespaceAndPath("hexerei", s).toString();
   }

   static {
      Builder builder = new Builder();
      builder.push("Settings");
      builder.pop();
      builder.push("Herb Jar Settings");
      JARS_ONLY_HOLD_HERBS = builder.comment("Disabling allows jars to hold any item")
         .translation("hexerei.config.jars_only_hold_herbs")
         .define("jars_only_hold_herbs", true);
      builder.pop();
      builder.push("Sage Burning Plate Settings");
      SAGE_BURNING_PLATE_RANGE = builder.comment("Range of the Sage Burning Plate, setting to 0 will disable completely")
         .translation("hexerei.config.spawn_disable_range")
         .define("spawn_disable_range", 48);
      builder.pop();
      builder.push("Crow Pickpocket Cooldown");
      CROW_PICKPOCKET_COOLDOWN = builder.comment("time (in ticks) for crow being able to pickpocket again (base 1 minute 30 seconds)")
         .translation("hexerei.config.crow_pickpocket_cooldown")
         .define("crow_pickpocket_cooldown", 1800);
      builder.pop();
      builder.push("Broom Brush Durability");
      BROOM_BRUSH_DURABILITY = builder.comment("broom brush durability")
         .translation("hexerei.config.broom_brush_durability")
         .define("broom_brush_durability", 100);
      builder.pop();
      builder.push("Herb Enhanced Brush Durability");
      HERB_ENHANCED_BRUSH_DURABILITY = builder.comment("Herb Enhanced brush durability")
         .translation("hexerei.config.herb_enhanced_brush_durability")
         .define("herb_enhanced_brush_durability", 200);
      builder.pop();
      builder.push("Moon Dust Brush Durability");
      MOON_DUST_BRUSH_DURABILITY = builder.comment("Moon Dust brush durability")
         .translation("hexerei.config.moon_dust_brush_durability")
         .define("moon_dust_brush_durability", 200);
      builder.pop();
      builder.push("Thruster Brush Durability");
      THRUSTER_BRUSH_DURABILITY = builder.comment("thruster brush durability")
         .translation("hexerei.config.thruster_brush_durability")
         .define("thruster_brush_durability", 400);
      builder.pop();
      builder.push("Broom Waterproof Tip Durability");
      BROOM_WATERPROOF_TIP_DURABILITY = builder.comment("Broom Waterproof Tip Durability")
         .translation("hexerei.config.broom_waterproof_tip_durability")
         .define("broom_waterproof_tip_durability", 800);
      builder.pop();
      builder.push("Broom Netherite Tip Durability");
      BROOM_NETHERITE_TIP_DURABILITY = builder.comment("Broom Netherite Tip Durability")
         .translation("hexerei.config.broom_netherite_tip_durability")
         .define("broom_netherite_tip_durability", 200);
      builder.pop();
      builder.push("Sage Bundle Durability");
      SAGE_BUNDLE_DURATION = builder.comment("time (in ticks) for the sage bundle to burn out (default 3600 - 1 hour)")
         .translation("hexerei.config.sage_bundle_durability")
         .define("sage_bundle_durability", 3600);
      builder.pop();
      builder.push("Coffer Item Blacklist");
      ArrayList<String> list = new ArrayList<>();
      list.add("minecraft:shulker_box");

      for (DyeColor color : DyeColor.values()) {
         String str = "minecraft:" + color.getName() + "_shulker_box";
         list.add(str);
      }

      list.add("hexerei:coffer");
      list.add("hexerei:entangled_coffer");
      COFFER_BLACKLIST = builder.comment("blacklists items from being placed inside of coffers")
         .translation("hexerei.config.coffer_blacklist")
         .defineList("coffer_blacklist", list, o -> true);
      builder.pop();
      builder.push("Biome Generation");
      WILLOW_SWAMP_RARITY = builder.comment("rarity of the willow swamp biome, 0 to disable").defineInRange("willow_swamp_rarity", 2, 0, 2147483647);
      builder.pop();
      COMMON_CONFIG = builder.build();
      builder = new Builder();
      builder.push("Settings");
      builder.pop();
      builder.push("List of Extra Fonts");
      FONT_LIST = builder.comment("list of fonts that can be used, mainly for the book of shadows")
         .translation("hexerei.config.font_list")
         .defineList(
            "font_list",
            List.of("minecraft:default", "hexerei:fancy", "hexerei:bloody", "hexerei:earth", "hexerei:seattle", "hexerei:medieval", "hexerei:augusta"),
            o -> true
         );
      builder.pop();
      BOOK_SHADERS_TOGGLE = builder.comment("Special book shaders toggle").define("book_shaders_toggle", true);
      DYNAMIC_LIGHT_TOGGLE = builder.comment("Dynamic light toggle").define("dynamic_light_toggle", false);
      ENTITY_LIGHT_CONFIG = builder.comment(new String[]{"Light level an entity should emit when dynamic lights are on", "Example entry: minecraft:blaze=15"})
         .defineList("entity_lights", HexConfig.ConfigUtil.writeConfig(getDefaultEntityLight()), HexConfig.ConfigUtil::validateMap);
      ITEM_LIGHT_CONFIG = builder.comment(
            new String[]{"Light level an item should emit when held when dynamic lights are on", "Example entry: minecraft:stick=15"}
         )
         .defineList("item_lights", HexConfig.ConfigUtil.writeConfig(getDefaultItemLight()), HexConfig.ConfigUtil::validateMap);
      CLIENT_CONFIG = builder.build();
   }

   public static class ConfigUtil {
      public static final Pattern STRING_INT_MAP = Pattern.compile("([^/=]+)=(\\p{Digit}+)");

      public static Map<String, Integer> parseMapConfig(ConfigValue<List<? extends String>> configValue) {
         return ((List)configValue.get())
            .stream()
            .map(STRING_INT_MAP::matcher)
            .filter(Matcher::matches)
            .collect(Collectors.toMap(m -> m.group(1), m -> Integer.valueOf(m.group(2))));
      }

      public static List<String> writeConfig(Map<String, Integer> map) {
         return map.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue().toString()).collect(Collectors.toList());
      }

      public static boolean validateMap(Object rawConfig) {
         return rawConfig instanceof CharSequence raw ? STRING_INT_MAP.matcher(raw).matches() : false;
      }
   }
}

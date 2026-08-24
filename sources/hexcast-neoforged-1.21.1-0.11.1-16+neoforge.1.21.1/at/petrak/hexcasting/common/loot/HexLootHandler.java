package at.petrak.hexcasting.common.loot;

import at.petrak.hexcasting.api.HexAPI;
import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public class HexLootHandler {
   public static final ImmutableList<HexLootHandler.ScrollInjection> DEFAULT_SCROLL_INJECTS = ImmutableList.of(
      new HexLootHandler.ScrollInjection(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/simple_dungeon"), 1),
      new HexLootHandler.ScrollInjection(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/abandoned_mineshaft"), 1),
      new HexLootHandler.ScrollInjection(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/bastion_other"), 1),
      new HexLootHandler.ScrollInjection(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/nether_bridge"), 1),
      new HexLootHandler.ScrollInjection(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/jungle_temple"), 2),
      new HexLootHandler.ScrollInjection(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/desert_pyramid"), 2),
      new HexLootHandler.ScrollInjection(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/village/village_cartographer"), 2),
      new HexLootHandler.ScrollInjection(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/shipwreck_map"), 3),
      new HexLootHandler.ScrollInjection(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/bastion_treasure"), 3),
      new HexLootHandler.ScrollInjection(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/end_city_treasure"), 3),
      new HexLootHandler.ScrollInjection(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/ancient_city"), 4),
      new HexLootHandler.ScrollInjection(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/pillager_outpost"), 4),
      new HexLootHandler.ScrollInjection[]{
         new HexLootHandler.ScrollInjection(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/woodland_mansion"), 5),
         new HexLootHandler.ScrollInjection(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/stronghold_library"), 5)
      }
   );
   public static final ImmutableList<ResourceLocation> DEFAULT_LORE_INJECTS = ImmutableList.of(
      ResourceLocation.fromNamespaceAndPath("minecraft", "chests/simple_dungeon"),
      ResourceLocation.fromNamespaceAndPath("minecraft", "chests/abandoned_mineshaft"),
      ResourceLocation.fromNamespaceAndPath("minecraft", "chests/pillager_outpost"),
      ResourceLocation.fromNamespaceAndPath("minecraft", "chests/woodland_mansion"),
      ResourceLocation.fromNamespaceAndPath("minecraft", "chests/stronghold_library"),
      ResourceLocation.fromNamespaceAndPath("minecraft", "chests/village/village_desert_house"),
      ResourceLocation.fromNamespaceAndPath("minecraft", "chests/village/village_plains_house"),
      ResourceLocation.fromNamespaceAndPath("minecraft", "chests/village/village_savanna_house"),
      ResourceLocation.fromNamespaceAndPath("minecraft", "chests/village/village_snowy_house"),
      ResourceLocation.fromNamespaceAndPath("minecraft", "chests/village/village_taiga_house")
   );
   public static final double DEFAULT_SHARD_MODIFICATION = -0.5;
   public static final double DEFAULT_LORE_CHANCE = 0.4;
   public static final ResourceLocation TABLE_INJECT_AMETHYST_CLUSTER = HexAPI.modLoc("inject/amethyst_cluster");

   public static int getScrollCount(int range, RandomSource random) {
      return Math.max(random.nextIntBetweenInclusive(-range, range), 0);
   }

   public record ScrollInjection(ResourceLocation injectee, int countRange) {
   }
}

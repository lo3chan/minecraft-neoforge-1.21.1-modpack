package vectorwing.farmersdelight.common.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Block> CAMPFIRE_SIGNAL_SMOKE = modBlockTag("campfire_signal_smoke");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Block> COMPOST_ACTIVATORS = modBlockTag("compost_activators");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Block> DROPS_CAKE_SLICE = modBlockTag("drops_cake_slice");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Block> HEAT_CONDUCTORS = modBlockTag("heat_conductors");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Block> HEAT_SOURCES = modBlockTag("heat_sources");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Block> TRAY_HEAT_SOURCES = modBlockTag("tray_heat_sources");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Block> MUSHROOM_COLONY_GROWABLE_ON = modBlockTag("mushroom_colony_growable_on");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Block> MINEABLE_WITH_KNIFE = modBlockTag("mineable/knife");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Block> TERRAIN = modBlockTag("terrain");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Block> STRAW_BLOCKS = modBlockTag("straw_blocks");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Block> WILD_CROPS = modBlockTag("wild_crops");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Block> CABINETS = modBlockTag("cabinets");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Block> WOODEN_CABINETS = modBlockTag("cabinets/wooden");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Block> MUSHROOM_COLONIES = modBlockTag("mushroom_colonies");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Block> ROPES = modBlockTag("ropes");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Block> UNAFFECTED_BY_RICH_SOIL = modBlockTag("unaffected_by_rich_soil");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Item> MEALS = modItemTag("meals");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Item> DRINKS = modItemTag("drinks");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Item> FEASTS = modItemTag("feasts");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Item> WILD_CROPS_ITEM = modItemTag("wild_crops");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Item> STRAW_HARVESTERS = modItemTag("straw_harvesters");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Item> KNIVES = modItemTag("tools/knives");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Item> CANVAS_SIGNS = modItemTag("canvas_signs");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Item> HANGING_CANVAS_SIGNS = modItemTag("hanging_canvas_signs");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Item> WOODEN_CABINET_ITEMS = modItemTag("cabinets/wooden");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Item> CABINET_ITEMS = modItemTag("cabinets");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Item> MUSHROOM_COLONY_ITEMS = modItemTag("mushroom_colonies");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Item> SERVING_CONTAINERS = modItemTag("serving_containers");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<Item> FLAT_ON_CUTTING_BOARD = modItemTag("flat_on_cutting_board");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<EntityType<?>> DOG_FOOD_USERS = modEntityTag("dog_food_users");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<EntityType<?>> HORSE_FEED_USERS = modEntityTag("horse_feed_users");
   @Deprecated(
      forRemoval = true
   )
   public static final TagKey<EntityType<?>> HORSE_FEED_TEMPTED = modEntityTag("horse_feed_tempted");

   private static TagKey<Item> modItemTag(String path) {
      return ItemTags.create(ResourceLocation.fromNamespaceAndPath("farmersdelight", path));
   }

   private static TagKey<Block> modBlockTag(String path) {
      return BlockTags.create(ResourceLocation.fromNamespaceAndPath("farmersdelight", path));
   }

   private static TagKey<EntityType<?>> modEntityTag(String path) {
      return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("farmersdelight", path));
   }

   public static class Blocks {
      public static final TagKey<Block> CAMPFIRE_SIGNAL_SMOKE = ModTags.modBlockTag("campfire_signal_smoke");
      public static final TagKey<Block> COMPOST_ACTIVATORS = ModTags.modBlockTag("compost_activators");
      public static final TagKey<Block> DROPS_CAKE_SLICE = ModTags.modBlockTag("drops_cake_slice");
      public static final TagKey<Block> HEAT_CONDUCTORS = ModTags.modBlockTag("heat_conductors");
      public static final TagKey<Block> HEAT_SOURCES = ModTags.modBlockTag("heat_sources");
      public static final TagKey<Block> TRAY_HEAT_SOURCES = ModTags.modBlockTag("tray_heat_sources");
      public static final TagKey<Block> MUSHROOM_COLONY_GROWABLE_ON = ModTags.modBlockTag("mushroom_colony_growable_on");
      public static final TagKey<Block> PLANTED_FROM_BELOW = ModTags.modBlockTag("planted_from_below");
      public static final TagKey<Block> FEASTS = ModTags.modBlockTag("feasts");
      public static final TagKey<Block> PIES = ModTags.modBlockTag("pies");
      public static final TagKey<Block> STRAW_BLOCKS = ModTags.modBlockTag("straw_blocks");
      public static final TagKey<Block> TERRAIN = ModTags.modBlockTag("terrain");
      public static final TagKey<Block> UNAFFECTED_BY_RICH_SOIL = ModTags.modBlockTag("unaffected_by_rich_soil");
      public static final TagKey<Block> CABINETS = ModTags.modBlockTag("cabinets");
      public static final TagKey<Block> CABINETS_WOODEN = ModTags.modBlockTag("cabinets/wooden");
      public static final TagKey<Block> MUSHROOM_COLONIES = ModTags.modBlockTag("mushroom_colonies");
      public static final TagKey<Block> MINEABLE_WITH_KNIFE = ModTags.modBlockTag("mineable/knife");
      public static final TagKey<Block> ROPES = ModTags.modBlockTag("ropes");
      public static final TagKey<Block> WILD_CROPS = ModTags.modBlockTag("wild_crops");
   }

   public static class EntityTypes {
      public static final TagKey<EntityType<?>> DOG_FOOD_USERS = ModTags.modEntityTag("dog_food_users");
      public static final TagKey<EntityType<?>> HORSE_FEED_USERS = ModTags.modEntityTag("horse_feed_users");
      public static final TagKey<EntityType<?>> HORSE_FEED_TEMPTED = ModTags.modEntityTag("horse_feed_tempted");
   }

   public static class Items {
      public static final TagKey<Item> KNIFE_ENCHANTABLE = ModTags.modItemTag("enchantable/knife");
      public static final TagKey<Item> SNACKS = ModTags.modItemTag("snacks");
      public static final TagKey<Item> MEALS = ModTags.modItemTag("meals");
      public static final TagKey<Item> DRINKS = ModTags.modItemTag("drinks");
      public static final TagKey<Item> SWEETS = ModTags.modItemTag("sweets");
      public static final TagKey<Item> FEASTS = ModTags.modItemTag("feasts");
      public static final TagKey<Item> PIES = ModTags.modItemTag("pies");
      public static final TagKey<Item> FLAT_ON_CUTTING_BOARD = ModTags.modItemTag("flat_on_cutting_board");
      public static final TagKey<Item> SERVING_CONTAINERS = ModTags.modItemTag("serving_containers");
      public static final TagKey<Item> STRAW_HARVESTERS = ModTags.modItemTag("straw_harvesters");
      public static final TagKey<Item> CABINETS = ModTags.modItemTag("cabinets");
      public static final TagKey<Item> CABINETS_WOODEN = ModTags.modItemTag("cabinets/wooden");
      public static final TagKey<Item> CANVAS_SIGNS = ModTags.modItemTag("canvas_signs");
      public static final TagKey<Item> HANGING_CANVAS_SIGNS = ModTags.modItemTag("hanging_canvas_signs");
      public static final TagKey<Item> KNIVES = ModTags.modItemTag("tools/knives");
      public static final TagKey<Item> MUSHROOM_COLONIES = ModTags.modItemTag("mushroom_colonies");
      public static final TagKey<Item> WILD_CROPS = ModTags.modItemTag("wild_crops");
   }
}

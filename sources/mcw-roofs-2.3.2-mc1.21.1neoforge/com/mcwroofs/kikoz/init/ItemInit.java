package com.mcwroofs.kikoz.init;

import com.mcwroofs.kikoz.objects.items.Hammer;
import com.mcwroofs.kikoz.objects.items.RoofItem;
import com.mcwroofs.kikoz.util.FuelItemBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;

public class ItemInit {
   public static final Items ITEMS = DeferredRegister.createItems("mcwroofs");
   public static final DeferredItem<Item> ROOFING_HAMMER = ITEMS.register("roofing_hammer", () -> new Hammer(null));
   public static final DeferredItem<Item> RAIN_GUTTER = ITEMS.register("rain_gutter", () -> new RoofItem(null));
   public static final DeferredItem<Item> OAK_ROOF = ITEMS.register("oak_roof", () -> new FuelItemBlock((Block)BlockInit.OAK_ROOF.get(), new Properties()));
   public static final DeferredItem<Item> OAK_ATTIC_ROOF = ITEMS.register(
      "oak_attic_roof", () -> new FuelItemBlock((Block)BlockInit.OAK_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_TOP_ROOF = ITEMS.register(
      "oak_top_roof", () -> new FuelItemBlock((Block)BlockInit.OAK_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_LOWER_ROOF = ITEMS.register(
      "oak_lower_roof", () -> new FuelItemBlock((Block)BlockInit.OAK_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_STEEP_ROOF = ITEMS.register(
      "oak_steep_roof", () -> new FuelItemBlock((Block)BlockInit.OAK_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_UPPER_LOWER_ROOF = ITEMS.register(
      "oak_upper_lower_roof", () -> new FuelItemBlock((Block)BlockInit.OAK_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_UPPER_STEEP_ROOF = ITEMS.register(
      "oak_upper_steep_roof", () -> new FuelItemBlock((Block)BlockInit.OAK_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_ROOF = ITEMS.register(
      "spruce_roof", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_ATTIC_ROOF = ITEMS.register(
      "spruce_attic_roof", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_TOP_ROOF = ITEMS.register(
      "spruce_top_roof", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_LOWER_ROOF = ITEMS.register(
      "spruce_lower_roof", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_STEEP_ROOF = ITEMS.register(
      "spruce_steep_roof", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_UPPER_LOWER_ROOF = ITEMS.register(
      "spruce_upper_lower_roof", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_UPPER_STEEP_ROOF = ITEMS.register(
      "spruce_upper_steep_roof", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_ROOF = ITEMS.register(
      "birch_roof", () -> new FuelItemBlock((Block)BlockInit.BIRCH_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_ATTIC_ROOF = ITEMS.register(
      "birch_attic_roof", () -> new FuelItemBlock((Block)BlockInit.BIRCH_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_TOP_ROOF = ITEMS.register(
      "birch_top_roof", () -> new FuelItemBlock((Block)BlockInit.BIRCH_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_LOWER_ROOF = ITEMS.register(
      "birch_lower_roof", () -> new FuelItemBlock((Block)BlockInit.BIRCH_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_STEEP_ROOF = ITEMS.register(
      "birch_steep_roof", () -> new FuelItemBlock((Block)BlockInit.BIRCH_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_UPPER_LOWER_ROOF = ITEMS.register(
      "birch_upper_lower_roof", () -> new FuelItemBlock((Block)BlockInit.BIRCH_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_UPPER_STEEP_ROOF = ITEMS.register(
      "birch_upper_steep_roof", () -> new FuelItemBlock((Block)BlockInit.BIRCH_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_ROOF = ITEMS.register(
      "jungle_roof", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_ATTIC_ROOF = ITEMS.register(
      "jungle_attic_roof", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_TOP_ROOF = ITEMS.register(
      "jungle_top_roof", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_LOWER_ROOF = ITEMS.register(
      "jungle_lower_roof", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_STEEP_ROOF = ITEMS.register(
      "jungle_steep_roof", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_UPPER_LOWER_ROOF = ITEMS.register(
      "jungle_upper_lower_roof", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_UPPER_STEEP_ROOF = ITEMS.register(
      "jungle_upper_steep_roof", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_ROOF = ITEMS.register(
      "acacia_roof", () -> new FuelItemBlock((Block)BlockInit.ACACIA_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_ATTIC_ROOF = ITEMS.register(
      "acacia_attic_roof", () -> new FuelItemBlock((Block)BlockInit.ACACIA_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_TOP_ROOF = ITEMS.register(
      "acacia_top_roof", () -> new FuelItemBlock((Block)BlockInit.ACACIA_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_LOWER_ROOF = ITEMS.register(
      "acacia_lower_roof", () -> new FuelItemBlock((Block)BlockInit.ACACIA_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_STEEP_ROOF = ITEMS.register(
      "acacia_steep_roof", () -> new FuelItemBlock((Block)BlockInit.ACACIA_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_UPPER_LOWER_ROOF = ITEMS.register(
      "acacia_upper_lower_roof", () -> new FuelItemBlock((Block)BlockInit.ACACIA_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_UPPER_STEEP_ROOF = ITEMS.register(
      "acacia_upper_steep_roof", () -> new FuelItemBlock((Block)BlockInit.ACACIA_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_ROOF = ITEMS.register(
      "dark_oak_roof", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_ATTIC_ROOF = ITEMS.register(
      "dark_oak_attic_roof", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_TOP_ROOF = ITEMS.register(
      "dark_oak_top_roof", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_LOWER_ROOF = ITEMS.register(
      "dark_oak_lower_roof", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_STEEP_ROOF = ITEMS.register(
      "dark_oak_steep_roof", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_UPPER_LOWER_ROOF = ITEMS.register(
      "dark_oak_upper_lower_roof", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_UPPER_STEEP_ROOF = ITEMS.register(
      "dark_oak_upper_steep_roof", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_ROOF = ITEMS.register(
      "crimson_roof", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_ATTIC_ROOF = ITEMS.register(
      "crimson_attic_roof", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_TOP_ROOF = ITEMS.register(
      "crimson_top_roof", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_LOWER_ROOF = ITEMS.register(
      "crimson_lower_roof", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_STEEP_ROOF = ITEMS.register(
      "crimson_steep_roof", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_UPPER_LOWER_ROOF = ITEMS.register(
      "crimson_upper_lower_roof", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_UPPER_STEEP_ROOF = ITEMS.register(
      "crimson_upper_steep_roof", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_ROOF = ITEMS.register(
      "warped_roof", () -> new FuelItemBlock((Block)BlockInit.WARPED_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_ATTIC_ROOF = ITEMS.register(
      "warped_attic_roof", () -> new FuelItemBlock((Block)BlockInit.WARPED_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_TOP_ROOF = ITEMS.register(
      "warped_top_roof", () -> new FuelItemBlock((Block)BlockInit.WARPED_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_LOWER_ROOF = ITEMS.register(
      "warped_lower_roof", () -> new FuelItemBlock((Block)BlockInit.WARPED_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_STEEP_ROOF = ITEMS.register(
      "warped_steep_roof", () -> new FuelItemBlock((Block)BlockInit.WARPED_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_UPPER_LOWER_ROOF = ITEMS.register(
      "warped_upper_lower_roof", () -> new FuelItemBlock((Block)BlockInit.WARPED_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_UPPER_STEEP_ROOF = ITEMS.register(
      "warped_upper_steep_roof", () -> new FuelItemBlock((Block)BlockInit.WARPED_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_ROOF = ITEMS.register(
      "mangrove_roof", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_ATTIC_ROOF = ITEMS.register(
      "mangrove_attic_roof", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_TOP_ROOF = ITEMS.register(
      "mangrove_top_roof", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_LOWER_ROOF = ITEMS.register(
      "mangrove_lower_roof", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_STEEP_ROOF = ITEMS.register(
      "mangrove_steep_roof", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_UPPER_LOWER_ROOF = ITEMS.register(
      "mangrove_upper_lower_roof", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_UPPER_STEEP_ROOF = ITEMS.register(
      "mangrove_upper_steep_roof", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_PLANKS_ROOF = ITEMS.register(
      "oak_planks_roof", () -> new FuelItemBlock((Block)BlockInit.OAK_PLANKS_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_PLANKS_ATTIC_ROOF = ITEMS.register(
      "oak_planks_attic_roof", () -> new FuelItemBlock((Block)BlockInit.OAK_PLANKS_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_PLANKS_TOP_ROOF = ITEMS.register(
      "oak_planks_top_roof", () -> new FuelItemBlock((Block)BlockInit.OAK_PLANKS_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_PLANKS_LOWER_ROOF = ITEMS.register(
      "oak_planks_lower_roof", () -> new FuelItemBlock((Block)BlockInit.OAK_PLANKS_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_PLANKS_STEEP_ROOF = ITEMS.register(
      "oak_planks_steep_roof", () -> new FuelItemBlock((Block)BlockInit.OAK_PLANKS_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_PLANKS_UPPER_LOWER_ROOF = ITEMS.register(
      "oak_planks_upper_lower_roof", () -> new FuelItemBlock((Block)BlockInit.OAK_PLANKS_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_PLANKS_UPPER_STEEP_ROOF = ITEMS.register(
      "oak_planks_upper_steep_roof", () -> new FuelItemBlock((Block)BlockInit.OAK_PLANKS_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_PLANKS_ROOF = ITEMS.register(
      "spruce_planks_roof", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_PLANKS_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_PLANKS_ATTIC_ROOF = ITEMS.register(
      "spruce_planks_attic_roof", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_PLANKS_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_PLANKS_TOP_ROOF = ITEMS.register(
      "spruce_planks_top_roof", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_PLANKS_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_PLANKS_LOWER_ROOF = ITEMS.register(
      "spruce_planks_lower_roof", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_PLANKS_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_PLANKS_STEEP_ROOF = ITEMS.register(
      "spruce_planks_steep_roof", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_PLANKS_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_PLANKS_UPPER_LOWER_ROOF = ITEMS.register(
      "spruce_planks_upper_lower_roof", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_PLANKS_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_PLANKS_UPPER_STEEP_ROOF = ITEMS.register(
      "spruce_planks_upper_steep_roof", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_PLANKS_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_PLANKS_ROOF = ITEMS.register(
      "birch_planks_roof", () -> new FuelItemBlock((Block)BlockInit.BIRCH_PLANKS_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_PLANKS_ATTIC_ROOF = ITEMS.register(
      "birch_planks_attic_roof", () -> new FuelItemBlock((Block)BlockInit.BIRCH_PLANKS_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_PLANKS_TOP_ROOF = ITEMS.register(
      "birch_planks_top_roof", () -> new FuelItemBlock((Block)BlockInit.BIRCH_PLANKS_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_PLANKS_LOWER_ROOF = ITEMS.register(
      "birch_planks_lower_roof", () -> new FuelItemBlock((Block)BlockInit.BIRCH_PLANKS_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_PLANKS_STEEP_ROOF = ITEMS.register(
      "birch_planks_steep_roof", () -> new FuelItemBlock((Block)BlockInit.BIRCH_PLANKS_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_PLANKS_UPPER_LOWER_ROOF = ITEMS.register(
      "birch_planks_upper_lower_roof", () -> new FuelItemBlock((Block)BlockInit.BIRCH_PLANKS_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_PLANKS_UPPER_STEEP_ROOF = ITEMS.register(
      "birch_planks_upper_steep_roof", () -> new FuelItemBlock((Block)BlockInit.BIRCH_PLANKS_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_PLANKS_ROOF = ITEMS.register(
      "jungle_planks_roof", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_PLANKS_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_PLANKS_ATTIC_ROOF = ITEMS.register(
      "jungle_planks_attic_roof", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_PLANKS_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_PLANKS_TOP_ROOF = ITEMS.register(
      "jungle_planks_top_roof", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_PLANKS_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_PLANKS_LOWER_ROOF = ITEMS.register(
      "jungle_planks_lower_roof", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_PLANKS_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_PLANKS_STEEP_ROOF = ITEMS.register(
      "jungle_planks_steep_roof", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_PLANKS_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_PLANKS_UPPER_LOWER_ROOF = ITEMS.register(
      "jungle_planks_upper_lower_roof", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_PLANKS_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_PLANKS_UPPER_STEEP_ROOF = ITEMS.register(
      "jungle_planks_upper_steep_roof", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_PLANKS_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_PLANKS_ROOF = ITEMS.register(
      "acacia_planks_roof", () -> new FuelItemBlock((Block)BlockInit.ACACIA_PLANKS_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_PLANKS_ATTIC_ROOF = ITEMS.register(
      "acacia_planks_attic_roof", () -> new FuelItemBlock((Block)BlockInit.ACACIA_PLANKS_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_PLANKS_TOP_ROOF = ITEMS.register(
      "acacia_planks_top_roof", () -> new FuelItemBlock((Block)BlockInit.ACACIA_PLANKS_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_PLANKS_LOWER_ROOF = ITEMS.register(
      "acacia_planks_lower_roof", () -> new FuelItemBlock((Block)BlockInit.ACACIA_PLANKS_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_PLANKS_STEEP_ROOF = ITEMS.register(
      "acacia_planks_steep_roof", () -> new FuelItemBlock((Block)BlockInit.ACACIA_PLANKS_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_PLANKS_UPPER_LOWER_ROOF = ITEMS.register(
      "acacia_planks_upper_lower_roof", () -> new FuelItemBlock((Block)BlockInit.ACACIA_PLANKS_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_PLANKS_UPPER_STEEP_ROOF = ITEMS.register(
      "acacia_planks_upper_steep_roof", () -> new FuelItemBlock((Block)BlockInit.ACACIA_PLANKS_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_PLANKS_ROOF = ITEMS.register(
      "dark_oak_planks_roof", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_PLANKS_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_PLANKS_ATTIC_ROOF = ITEMS.register(
      "dark_oak_planks_attic_roof", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_PLANKS_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_PLANKS_TOP_ROOF = ITEMS.register(
      "dark_oak_planks_top_roof", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_PLANKS_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_PLANKS_LOWER_ROOF = ITEMS.register(
      "dark_oak_planks_lower_roof", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_PLANKS_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_PLANKS_STEEP_ROOF = ITEMS.register(
      "dark_oak_planks_steep_roof", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_PLANKS_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_PLANKS_UPPER_LOWER_ROOF = ITEMS.register(
      "dark_oak_planks_upper_lower_roof", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_PLANKS_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_PLANKS_UPPER_STEEP_ROOF = ITEMS.register(
      "dark_oak_planks_upper_steep_roof", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_PLANKS_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_PLANKS_ROOF = ITEMS.register(
      "crimson_planks_roof", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_PLANKS_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_PLANKS_ATTIC_ROOF = ITEMS.register(
      "crimson_planks_attic_roof", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_PLANKS_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_PLANKS_TOP_ROOF = ITEMS.register(
      "crimson_planks_top_roof", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_PLANKS_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_PLANKS_LOWER_ROOF = ITEMS.register(
      "crimson_planks_lower_roof", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_PLANKS_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_PLANKS_STEEP_ROOF = ITEMS.register(
      "crimson_planks_steep_roof", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_PLANKS_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_PLANKS_UPPER_LOWER_ROOF = ITEMS.register(
      "crimson_planks_upper_lower_roof", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_PLANKS_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_PLANKS_UPPER_STEEP_ROOF = ITEMS.register(
      "crimson_planks_upper_steep_roof", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_PLANKS_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_PLANKS_ROOF = ITEMS.register(
      "warped_planks_roof", () -> new FuelItemBlock((Block)BlockInit.WARPED_PLANKS_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_PLANKS_ATTIC_ROOF = ITEMS.register(
      "warped_planks_attic_roof", () -> new FuelItemBlock((Block)BlockInit.WARPED_PLANKS_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_PLANKS_TOP_ROOF = ITEMS.register(
      "warped_planks_top_roof", () -> new FuelItemBlock((Block)BlockInit.WARPED_PLANKS_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_PLANKS_LOWER_ROOF = ITEMS.register(
      "warped_planks_lower_roof", () -> new FuelItemBlock((Block)BlockInit.WARPED_PLANKS_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_PLANKS_STEEP_ROOF = ITEMS.register(
      "warped_planks_steep_roof", () -> new FuelItemBlock((Block)BlockInit.WARPED_PLANKS_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_PLANKS_UPPER_LOWER_ROOF = ITEMS.register(
      "warped_planks_upper_lower_roof", () -> new FuelItemBlock((Block)BlockInit.WARPED_PLANKS_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_PLANKS_UPPER_STEEP_ROOF = ITEMS.register(
      "warped_planks_upper_steep_roof", () -> new FuelItemBlock((Block)BlockInit.WARPED_PLANKS_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_PLANKS_ROOF = ITEMS.register(
      "mangrove_planks_roof", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_PLANKS_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_PLANKS_ATTIC_ROOF = ITEMS.register(
      "mangrove_planks_attic_roof", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_PLANKS_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_PLANKS_TOP_ROOF = ITEMS.register(
      "mangrove_planks_top_roof", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_PLANKS_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_PLANKS_LOWER_ROOF = ITEMS.register(
      "mangrove_planks_lower_roof", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_PLANKS_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_PLANKS_STEEP_ROOF = ITEMS.register(
      "mangrove_planks_steep_roof", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_PLANKS_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_PLANKS_UPPER_LOWER_ROOF = ITEMS.register(
      "mangrove_planks_upper_lower_roof", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_PLANKS_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_PLANKS_UPPER_STEEP_ROOF = ITEMS.register(
      "mangrove_planks_upper_steep_roof", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_PLANKS_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WHITE_TERRACOTTA_ROOF = ITEMS.register(
      "white_terracotta_roof", () -> new BlockItem((Block)BlockInit.WHITE_TERRACOTTA_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WHITE_TERRACOTTA_ATTIC_ROOF = ITEMS.register(
      "white_terracotta_attic_roof", () -> new BlockItem((Block)BlockInit.WHITE_TERRACOTTA_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WHITE_TERRACOTTA_TOP_ROOF = ITEMS.register(
      "white_terracotta_top_roof", () -> new BlockItem((Block)BlockInit.WHITE_TERRACOTTA_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WHITE_TERRACOTTA_LOWER_ROOF = ITEMS.register(
      "white_terracotta_lower_roof", () -> new BlockItem((Block)BlockInit.WHITE_TERRACOTTA_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WHITE_TERRACOTTA_STEEP_ROOF = ITEMS.register(
      "white_terracotta_steep_roof", () -> new BlockItem((Block)BlockInit.WHITE_TERRACOTTA_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WHITE_TERRACOTTA_UPPER_LOWER_ROOF = ITEMS.register(
      "white_terracotta_upper_lower_roof", () -> new BlockItem((Block)BlockInit.WHITE_TERRACOTTA_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WHITE_TERRACOTTA_UPPER_STEEP_ROOF = ITEMS.register(
      "white_terracotta_upper_steep_roof", () -> new BlockItem((Block)BlockInit.WHITE_TERRACOTTA_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_TERRACOTTA_ROOF = ITEMS.register(
      "light_gray_terracotta_roof", () -> new BlockItem((Block)BlockInit.LIGHT_GRAY_TERRACOTTA_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_TERRACOTTA_ATTIC_ROOF = ITEMS.register(
      "light_gray_terracotta_attic_roof", () -> new BlockItem((Block)BlockInit.LIGHT_GRAY_TERRACOTTA_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_TERRACOTTA_TOP_ROOF = ITEMS.register(
      "light_gray_terracotta_top_roof", () -> new BlockItem((Block)BlockInit.LIGHT_GRAY_TERRACOTTA_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_TERRACOTTA_LOWER_ROOF = ITEMS.register(
      "light_gray_terracotta_lower_roof", () -> new BlockItem((Block)BlockInit.LIGHT_GRAY_TERRACOTTA_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_TERRACOTTA_STEEP_ROOF = ITEMS.register(
      "light_gray_terracotta_steep_roof", () -> new BlockItem((Block)BlockInit.LIGHT_GRAY_TERRACOTTA_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_TERRACOTTA_UPPER_LOWER_ROOF = ITEMS.register(
      "light_gray_terracotta_upper_lower_roof", () -> new BlockItem((Block)BlockInit.LIGHT_GRAY_TERRACOTTA_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_TERRACOTTA_UPPER_STEEP_ROOF = ITEMS.register(
      "light_gray_terracotta_upper_steep_roof", () -> new BlockItem((Block)BlockInit.LIGHT_GRAY_TERRACOTTA_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_TERRACOTTA_ROOF = ITEMS.register(
      "gray_terracotta_roof", () -> new BlockItem((Block)BlockInit.GRAY_TERRACOTTA_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_TERRACOTTA_ATTIC_ROOF = ITEMS.register(
      "gray_terracotta_attic_roof", () -> new BlockItem((Block)BlockInit.GRAY_TERRACOTTA_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_TERRACOTTA_TOP_ROOF = ITEMS.register(
      "gray_terracotta_top_roof", () -> new BlockItem((Block)BlockInit.GRAY_TERRACOTTA_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_TERRACOTTA_LOWER_ROOF = ITEMS.register(
      "gray_terracotta_lower_roof", () -> new BlockItem((Block)BlockInit.GRAY_TERRACOTTA_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_TERRACOTTA_STEEP_ROOF = ITEMS.register(
      "gray_terracotta_steep_roof", () -> new BlockItem((Block)BlockInit.GRAY_TERRACOTTA_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_TERRACOTTA_UPPER_LOWER_ROOF = ITEMS.register(
      "gray_terracotta_upper_lower_roof", () -> new BlockItem((Block)BlockInit.GRAY_TERRACOTTA_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_TERRACOTTA_UPPER_STEEP_ROOF = ITEMS.register(
      "gray_terracotta_upper_steep_roof", () -> new BlockItem((Block)BlockInit.GRAY_TERRACOTTA_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACK_TERRACOTTA_ROOF = ITEMS.register(
      "black_terracotta_roof", () -> new BlockItem((Block)BlockInit.BLACK_TERRACOTTA_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACK_TERRACOTTA_ATTIC_ROOF = ITEMS.register(
      "black_terracotta_attic_roof", () -> new BlockItem((Block)BlockInit.BLACK_TERRACOTTA_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACK_TERRACOTTA_TOP_ROOF = ITEMS.register(
      "black_terracotta_top_roof", () -> new BlockItem((Block)BlockInit.BLACK_TERRACOTTA_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACK_TERRACOTTA_LOWER_ROOF = ITEMS.register(
      "black_terracotta_lower_roof", () -> new BlockItem((Block)BlockInit.BLACK_TERRACOTTA_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACK_TERRACOTTA_STEEP_ROOF = ITEMS.register(
      "black_terracotta_steep_roof", () -> new BlockItem((Block)BlockInit.BLACK_TERRACOTTA_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACK_TERRACOTTA_UPPER_LOWER_ROOF = ITEMS.register(
      "black_terracotta_upper_lower_roof", () -> new BlockItem((Block)BlockInit.BLACK_TERRACOTTA_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACK_TERRACOTTA_UPPER_STEEP_ROOF = ITEMS.register(
      "black_terracotta_upper_steep_roof", () -> new BlockItem((Block)BlockInit.BLACK_TERRACOTTA_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLUE_TERRACOTTA_ROOF = ITEMS.register(
      "blue_terracotta_roof", () -> new BlockItem((Block)BlockInit.BLUE_TERRACOTTA_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLUE_TERRACOTTA_ATTIC_ROOF = ITEMS.register(
      "blue_terracotta_attic_roof", () -> new BlockItem((Block)BlockInit.BLUE_TERRACOTTA_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLUE_TERRACOTTA_TOP_ROOF = ITEMS.register(
      "blue_terracotta_top_roof", () -> new BlockItem((Block)BlockInit.BLUE_TERRACOTTA_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLUE_TERRACOTTA_LOWER_ROOF = ITEMS.register(
      "blue_terracotta_lower_roof", () -> new BlockItem((Block)BlockInit.BLUE_TERRACOTTA_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLUE_TERRACOTTA_STEEP_ROOF = ITEMS.register(
      "blue_terracotta_steep_roof", () -> new BlockItem((Block)BlockInit.BLUE_TERRACOTTA_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLUE_TERRACOTTA_UPPER_LOWER_ROOF = ITEMS.register(
      "blue_terracotta_upper_lower_roof", () -> new BlockItem((Block)BlockInit.BLUE_TERRACOTTA_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLUE_TERRACOTTA_UPPER_STEEP_ROOF = ITEMS.register(
      "blue_terracotta_upper_steep_roof", () -> new BlockItem((Block)BlockInit.BLUE_TERRACOTTA_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_BLUE_TERRACOTTA_ROOF = ITEMS.register(
      "light_blue_terracotta_roof", () -> new BlockItem((Block)BlockInit.LIGHT_BLUE_TERRACOTTA_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_BLUE_TERRACOTTA_ATTIC_ROOF = ITEMS.register(
      "light_blue_terracotta_attic_roof", () -> new BlockItem((Block)BlockInit.LIGHT_BLUE_TERRACOTTA_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_BLUE_TERRACOTTA_TOP_ROOF = ITEMS.register(
      "light_blue_terracotta_top_roof", () -> new BlockItem((Block)BlockInit.LIGHT_BLUE_TERRACOTTA_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_BLUE_TERRACOTTA_LOWER_ROOF = ITEMS.register(
      "light_blue_terracotta_lower_roof", () -> new BlockItem((Block)BlockInit.LIGHT_BLUE_TERRACOTTA_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_BLUE_TERRACOTTA_STEEP_ROOF = ITEMS.register(
      "light_blue_terracotta_steep_roof", () -> new BlockItem((Block)BlockInit.LIGHT_BLUE_TERRACOTTA_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_BLUE_TERRACOTTA_UPPER_LOWER_ROOF = ITEMS.register(
      "light_blue_terracotta_upper_lower_roof", () -> new BlockItem((Block)BlockInit.LIGHT_BLUE_TERRACOTTA_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_BLUE_TERRACOTTA_UPPER_STEEP_ROOF = ITEMS.register(
      "light_blue_terracotta_upper_steep_roof", () -> new BlockItem((Block)BlockInit.LIGHT_BLUE_TERRACOTTA_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CYAN_TERRACOTTA_ROOF = ITEMS.register(
      "cyan_terracotta_roof", () -> new BlockItem((Block)BlockInit.CYAN_TERRACOTTA_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CYAN_TERRACOTTA_ATTIC_ROOF = ITEMS.register(
      "cyan_terracotta_attic_roof", () -> new BlockItem((Block)BlockInit.CYAN_TERRACOTTA_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CYAN_TERRACOTTA_TOP_ROOF = ITEMS.register(
      "cyan_terracotta_top_roof", () -> new BlockItem((Block)BlockInit.CYAN_TERRACOTTA_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CYAN_TERRACOTTA_LOWER_ROOF = ITEMS.register(
      "cyan_terracotta_lower_roof", () -> new BlockItem((Block)BlockInit.CYAN_TERRACOTTA_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CYAN_TERRACOTTA_STEEP_ROOF = ITEMS.register(
      "cyan_terracotta_steep_roof", () -> new BlockItem((Block)BlockInit.CYAN_TERRACOTTA_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CYAN_TERRACOTTA_UPPER_LOWER_ROOF = ITEMS.register(
      "cyan_terracotta_upper_lower_roof", () -> new BlockItem((Block)BlockInit.CYAN_TERRACOTTA_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CYAN_TERRACOTTA_UPPER_STEEP_ROOF = ITEMS.register(
      "cyan_terracotta_upper_steep_roof", () -> new BlockItem((Block)BlockInit.CYAN_TERRACOTTA_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIME_TERRACOTTA_ROOF = ITEMS.register(
      "lime_terracotta_roof", () -> new BlockItem((Block)BlockInit.LIME_TERRACOTTA_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIME_TERRACOTTA_ATTIC_ROOF = ITEMS.register(
      "lime_terracotta_attic_roof", () -> new BlockItem((Block)BlockInit.LIME_TERRACOTTA_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIME_TERRACOTTA_TOP_ROOF = ITEMS.register(
      "lime_terracotta_top_roof", () -> new BlockItem((Block)BlockInit.LIME_TERRACOTTA_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIME_TERRACOTTA_LOWER_ROOF = ITEMS.register(
      "lime_terracotta_lower_roof", () -> new BlockItem((Block)BlockInit.LIME_TERRACOTTA_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIME_TERRACOTTA_STEEP_ROOF = ITEMS.register(
      "lime_terracotta_steep_roof", () -> new BlockItem((Block)BlockInit.LIME_TERRACOTTA_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIME_TERRACOTTA_UPPER_LOWER_ROOF = ITEMS.register(
      "lime_terracotta_upper_lower_roof", () -> new BlockItem((Block)BlockInit.LIME_TERRACOTTA_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIME_TERRACOTTA_UPPER_STEEP_ROOF = ITEMS.register(
      "lime_terracotta_upper_steep_roof", () -> new BlockItem((Block)BlockInit.LIME_TERRACOTTA_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GREEN_TERRACOTTA_ROOF = ITEMS.register(
      "green_terracotta_roof", () -> new BlockItem((Block)BlockInit.GREEN_TERRACOTTA_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GREEN_TERRACOTTA_ATTIC_ROOF = ITEMS.register(
      "green_terracotta_attic_roof", () -> new BlockItem((Block)BlockInit.GREEN_TERRACOTTA_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GREEN_TERRACOTTA_TOP_ROOF = ITEMS.register(
      "green_terracotta_top_roof", () -> new BlockItem((Block)BlockInit.GREEN_TERRACOTTA_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GREEN_TERRACOTTA_LOWER_ROOF = ITEMS.register(
      "green_terracotta_lower_roof", () -> new BlockItem((Block)BlockInit.GREEN_TERRACOTTA_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GREEN_TERRACOTTA_STEEP_ROOF = ITEMS.register(
      "green_terracotta_steep_roof", () -> new BlockItem((Block)BlockInit.GREEN_TERRACOTTA_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GREEN_TERRACOTTA_UPPER_LOWER_ROOF = ITEMS.register(
      "green_terracotta_upper_lower_roof", () -> new BlockItem((Block)BlockInit.GREEN_TERRACOTTA_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GREEN_TERRACOTTA_UPPER_STEEP_ROOF = ITEMS.register(
      "green_terracotta_upper_steep_roof", () -> new BlockItem((Block)BlockInit.GREEN_TERRACOTTA_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> YELLOW_TERRACOTTA_ROOF = ITEMS.register(
      "yellow_terracotta_roof", () -> new BlockItem((Block)BlockInit.YELLOW_TERRACOTTA_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> YELLOW_TERRACOTTA_ATTIC_ROOF = ITEMS.register(
      "yellow_terracotta_attic_roof", () -> new BlockItem((Block)BlockInit.YELLOW_TERRACOTTA_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> YELLOW_TERRACOTTA_TOP_ROOF = ITEMS.register(
      "yellow_terracotta_top_roof", () -> new BlockItem((Block)BlockInit.YELLOW_TERRACOTTA_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> YELLOW_TERRACOTTA_LOWER_ROOF = ITEMS.register(
      "yellow_terracotta_lower_roof", () -> new BlockItem((Block)BlockInit.YELLOW_TERRACOTTA_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> YELLOW_TERRACOTTA_STEEP_ROOF = ITEMS.register(
      "yellow_terracotta_steep_roof", () -> new BlockItem((Block)BlockInit.YELLOW_TERRACOTTA_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> YELLOW_TERRACOTTA_UPPER_LOWER_ROOF = ITEMS.register(
      "yellow_terracotta_upper_lower_roof", () -> new BlockItem((Block)BlockInit.YELLOW_TERRACOTTA_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> YELLOW_TERRACOTTA_UPPER_STEEP_ROOF = ITEMS.register(
      "yellow_terracotta_upper_steep_roof", () -> new BlockItem((Block)BlockInit.YELLOW_TERRACOTTA_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BROWN_TERRACOTTA_ROOF = ITEMS.register(
      "brown_terracotta_roof", () -> new BlockItem((Block)BlockInit.BROWN_TERRACOTTA_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BROWN_TERRACOTTA_ATTIC_ROOF = ITEMS.register(
      "brown_terracotta_attic_roof", () -> new BlockItem((Block)BlockInit.BROWN_TERRACOTTA_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BROWN_TERRACOTTA_TOP_ROOF = ITEMS.register(
      "brown_terracotta_top_roof", () -> new BlockItem((Block)BlockInit.BROWN_TERRACOTTA_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BROWN_TERRACOTTA_LOWER_ROOF = ITEMS.register(
      "brown_terracotta_lower_roof", () -> new BlockItem((Block)BlockInit.BROWN_TERRACOTTA_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BROWN_TERRACOTTA_STEEP_ROOF = ITEMS.register(
      "brown_terracotta_steep_roof", () -> new BlockItem((Block)BlockInit.BROWN_TERRACOTTA_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BROWN_TERRACOTTA_UPPER_LOWER_ROOF = ITEMS.register(
      "brown_terracotta_upper_lower_roof", () -> new BlockItem((Block)BlockInit.BROWN_TERRACOTTA_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BROWN_TERRACOTTA_UPPER_STEEP_ROOF = ITEMS.register(
      "brown_terracotta_upper_steep_roof", () -> new BlockItem((Block)BlockInit.BROWN_TERRACOTTA_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ORANGE_TERRACOTTA_ROOF = ITEMS.register(
      "orange_terracotta_roof", () -> new BlockItem((Block)BlockInit.ORANGE_TERRACOTTA_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ORANGE_TERRACOTTA_ATTIC_ROOF = ITEMS.register(
      "orange_terracotta_attic_roof", () -> new BlockItem((Block)BlockInit.ORANGE_TERRACOTTA_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ORANGE_TERRACOTTA_TOP_ROOF = ITEMS.register(
      "orange_terracotta_top_roof", () -> new BlockItem((Block)BlockInit.ORANGE_TERRACOTTA_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ORANGE_TERRACOTTA_LOWER_ROOF = ITEMS.register(
      "orange_terracotta_lower_roof", () -> new BlockItem((Block)BlockInit.ORANGE_TERRACOTTA_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ORANGE_TERRACOTTA_STEEP_ROOF = ITEMS.register(
      "orange_terracotta_steep_roof", () -> new BlockItem((Block)BlockInit.ORANGE_TERRACOTTA_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ORANGE_TERRACOTTA_UPPER_LOWER_ROOF = ITEMS.register(
      "orange_terracotta_upper_lower_roof", () -> new BlockItem((Block)BlockInit.ORANGE_TERRACOTTA_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ORANGE_TERRACOTTA_UPPER_STEEP_ROOF = ITEMS.register(
      "orange_terracotta_upper_steep_roof", () -> new BlockItem((Block)BlockInit.ORANGE_TERRACOTTA_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_TERRACOTTA_ROOF = ITEMS.register(
      "red_terracotta_roof", () -> new BlockItem((Block)BlockInit.RED_TERRACOTTA_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_TERRACOTTA_ATTIC_ROOF = ITEMS.register(
      "red_terracotta_attic_roof", () -> new BlockItem((Block)BlockInit.RED_TERRACOTTA_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_TERRACOTTA_TOP_ROOF = ITEMS.register(
      "red_terracotta_top_roof", () -> new BlockItem((Block)BlockInit.RED_TERRACOTTA_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_TERRACOTTA_LOWER_ROOF = ITEMS.register(
      "red_terracotta_lower_roof", () -> new BlockItem((Block)BlockInit.RED_TERRACOTTA_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_TERRACOTTA_STEEP_ROOF = ITEMS.register(
      "red_terracotta_steep_roof", () -> new BlockItem((Block)BlockInit.RED_TERRACOTTA_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_TERRACOTTA_UPPER_LOWER_ROOF = ITEMS.register(
      "red_terracotta_upper_lower_roof", () -> new BlockItem((Block)BlockInit.RED_TERRACOTTA_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_TERRACOTTA_UPPER_STEEP_ROOF = ITEMS.register(
      "red_terracotta_upper_steep_roof", () -> new BlockItem((Block)BlockInit.RED_TERRACOTTA_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MAGENTA_TERRACOTTA_ROOF = ITEMS.register(
      "magenta_terracotta_roof", () -> new BlockItem((Block)BlockInit.MAGENTA_TERRACOTTA_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MAGENTA_TERRACOTTA_ATTIC_ROOF = ITEMS.register(
      "magenta_terracotta_attic_roof", () -> new BlockItem((Block)BlockInit.MAGENTA_TERRACOTTA_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MAGENTA_TERRACOTTA_TOP_ROOF = ITEMS.register(
      "magenta_terracotta_top_roof", () -> new BlockItem((Block)BlockInit.MAGENTA_TERRACOTTA_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MAGENTA_TERRACOTTA_LOWER_ROOF = ITEMS.register(
      "magenta_terracotta_lower_roof", () -> new BlockItem((Block)BlockInit.MAGENTA_TERRACOTTA_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MAGENTA_TERRACOTTA_STEEP_ROOF = ITEMS.register(
      "magenta_terracotta_steep_roof", () -> new BlockItem((Block)BlockInit.MAGENTA_TERRACOTTA_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MAGENTA_TERRACOTTA_UPPER_LOWER_ROOF = ITEMS.register(
      "magenta_terracotta_upper_lower_roof", () -> new BlockItem((Block)BlockInit.MAGENTA_TERRACOTTA_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MAGENTA_TERRACOTTA_UPPER_STEEP_ROOF = ITEMS.register(
      "magenta_terracotta_upper_steep_roof", () -> new BlockItem((Block)BlockInit.MAGENTA_TERRACOTTA_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PINK_TERRACOTTA_ROOF = ITEMS.register(
      "pink_terracotta_roof", () -> new BlockItem((Block)BlockInit.PINK_TERRACOTTA_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PINK_TERRACOTTA_ATTIC_ROOF = ITEMS.register(
      "pink_terracotta_attic_roof", () -> new BlockItem((Block)BlockInit.PINK_TERRACOTTA_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PINK_TERRACOTTA_TOP_ROOF = ITEMS.register(
      "pink_terracotta_top_roof", () -> new BlockItem((Block)BlockInit.PINK_TERRACOTTA_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PINK_TERRACOTTA_LOWER_ROOF = ITEMS.register(
      "pink_terracotta_lower_roof", () -> new BlockItem((Block)BlockInit.PINK_TERRACOTTA_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PINK_TERRACOTTA_STEEP_ROOF = ITEMS.register(
      "pink_terracotta_steep_roof", () -> new BlockItem((Block)BlockInit.PINK_TERRACOTTA_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PINK_TERRACOTTA_UPPER_LOWER_ROOF = ITEMS.register(
      "pink_terracotta_upper_lower_roof", () -> new BlockItem((Block)BlockInit.PINK_TERRACOTTA_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PINK_TERRACOTTA_UPPER_STEEP_ROOF = ITEMS.register(
      "pink_terracotta_upper_steep_roof", () -> new BlockItem((Block)BlockInit.PINK_TERRACOTTA_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PURPLE_TERRACOTTA_ROOF = ITEMS.register(
      "purple_terracotta_roof", () -> new BlockItem((Block)BlockInit.PURPLE_TERRACOTTA_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PURPLE_TERRACOTTA_ATTIC_ROOF = ITEMS.register(
      "purple_terracotta_attic_roof", () -> new BlockItem((Block)BlockInit.PURPLE_TERRACOTTA_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PURPLE_TERRACOTTA_TOP_ROOF = ITEMS.register(
      "purple_terracotta_top_roof", () -> new BlockItem((Block)BlockInit.PURPLE_TERRACOTTA_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PURPLE_TERRACOTTA_LOWER_ROOF = ITEMS.register(
      "purple_terracotta_lower_roof", () -> new BlockItem((Block)BlockInit.PURPLE_TERRACOTTA_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PURPLE_TERRACOTTA_STEEP_ROOF = ITEMS.register(
      "purple_terracotta_steep_roof", () -> new BlockItem((Block)BlockInit.PURPLE_TERRACOTTA_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PURPLE_TERRACOTTA_UPPER_LOWER_ROOF = ITEMS.register(
      "purple_terracotta_upper_lower_roof", () -> new BlockItem((Block)BlockInit.PURPLE_TERRACOTTA_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PURPLE_TERRACOTTA_UPPER_STEEP_ROOF = ITEMS.register(
      "purple_terracotta_upper_steep_roof", () -> new BlockItem((Block)BlockInit.PURPLE_TERRACOTTA_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WHITE_CONCRETE_ROOF = ITEMS.register(
      "white_concrete_roof", () -> new BlockItem((Block)BlockInit.WHITE_CONCRETE_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WHITE_CONCRETE_ATTIC_ROOF = ITEMS.register(
      "white_concrete_attic_roof", () -> new BlockItem((Block)BlockInit.WHITE_CONCRETE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WHITE_CONCRETE_TOP_ROOF = ITEMS.register(
      "white_concrete_top_roof", () -> new BlockItem((Block)BlockInit.WHITE_CONCRETE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WHITE_CONCRETE_LOWER_ROOF = ITEMS.register(
      "white_concrete_lower_roof", () -> new BlockItem((Block)BlockInit.WHITE_CONCRETE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WHITE_CONCRETE_STEEP_ROOF = ITEMS.register(
      "white_concrete_steep_roof", () -> new BlockItem((Block)BlockInit.WHITE_CONCRETE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WHITE_CONCRETE_UPPER_LOWER_ROOF = ITEMS.register(
      "white_concrete_upper_lower_roof", () -> new BlockItem((Block)BlockInit.WHITE_CONCRETE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WHITE_CONCRETE_UPPER_STEEP_ROOF = ITEMS.register(
      "white_concrete_upper_steep_roof", () -> new BlockItem((Block)BlockInit.WHITE_CONCRETE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_CONCRETE_ROOF = ITEMS.register(
      "light_gray_concrete_roof", () -> new BlockItem((Block)BlockInit.LIGHT_GRAY_CONCRETE_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_CONCRETE_ATTIC_ROOF = ITEMS.register(
      "light_gray_concrete_attic_roof", () -> new BlockItem((Block)BlockInit.LIGHT_GRAY_CONCRETE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_CONCRETE_TOP_ROOF = ITEMS.register(
      "light_gray_concrete_top_roof", () -> new BlockItem((Block)BlockInit.LIGHT_GRAY_CONCRETE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_CONCRETE_LOWER_ROOF = ITEMS.register(
      "light_gray_concrete_lower_roof", () -> new BlockItem((Block)BlockInit.LIGHT_GRAY_CONCRETE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_CONCRETE_STEEP_ROOF = ITEMS.register(
      "light_gray_concrete_steep_roof", () -> new BlockItem((Block)BlockInit.LIGHT_GRAY_CONCRETE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_CONCRETE_UPPER_LOWER_ROOF = ITEMS.register(
      "light_gray_concrete_upper_lower_roof", () -> new BlockItem((Block)BlockInit.LIGHT_GRAY_CONCRETE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_CONCRETE_UPPER_STEEP_ROOF = ITEMS.register(
      "light_gray_concrete_upper_steep_roof", () -> new BlockItem((Block)BlockInit.LIGHT_GRAY_CONCRETE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_CONCRETE_ROOF = ITEMS.register(
      "gray_concrete_roof", () -> new BlockItem((Block)BlockInit.GRAY_CONCRETE_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_CONCRETE_ATTIC_ROOF = ITEMS.register(
      "gray_concrete_attic_roof", () -> new BlockItem((Block)BlockInit.GRAY_CONCRETE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_CONCRETE_TOP_ROOF = ITEMS.register(
      "gray_concrete_top_roof", () -> new BlockItem((Block)BlockInit.GRAY_CONCRETE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_CONCRETE_LOWER_ROOF = ITEMS.register(
      "gray_concrete_lower_roof", () -> new BlockItem((Block)BlockInit.GRAY_CONCRETE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_CONCRETE_STEEP_ROOF = ITEMS.register(
      "gray_concrete_steep_roof", () -> new BlockItem((Block)BlockInit.GRAY_CONCRETE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_CONCRETE_UPPER_LOWER_ROOF = ITEMS.register(
      "gray_concrete_upper_lower_roof", () -> new BlockItem((Block)BlockInit.GRAY_CONCRETE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_CONCRETE_UPPER_STEEP_ROOF = ITEMS.register(
      "gray_concrete_upper_steep_roof", () -> new BlockItem((Block)BlockInit.GRAY_CONCRETE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACK_CONCRETE_ROOF = ITEMS.register(
      "black_concrete_roof", () -> new BlockItem((Block)BlockInit.BLACK_CONCRETE_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACK_CONCRETE_ATTIC_ROOF = ITEMS.register(
      "black_concrete_attic_roof", () -> new BlockItem((Block)BlockInit.BLACK_CONCRETE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACK_CONCRETE_TOP_ROOF = ITEMS.register(
      "black_concrete_top_roof", () -> new BlockItem((Block)BlockInit.BLACK_CONCRETE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACK_CONCRETE_LOWER_ROOF = ITEMS.register(
      "black_concrete_lower_roof", () -> new BlockItem((Block)BlockInit.BLACK_CONCRETE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACK_CONCRETE_STEEP_ROOF = ITEMS.register(
      "black_concrete_steep_roof", () -> new BlockItem((Block)BlockInit.BLACK_CONCRETE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACK_CONCRETE_UPPER_LOWER_ROOF = ITEMS.register(
      "black_concrete_upper_lower_roof", () -> new BlockItem((Block)BlockInit.BLACK_CONCRETE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACK_CONCRETE_UPPER_STEEP_ROOF = ITEMS.register(
      "black_concrete_upper_steep_roof", () -> new BlockItem((Block)BlockInit.BLACK_CONCRETE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLUE_CONCRETE_ROOF = ITEMS.register(
      "blue_concrete_roof", () -> new BlockItem((Block)BlockInit.BLUE_CONCRETE_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLUE_CONCRETE_ATTIC_ROOF = ITEMS.register(
      "blue_concrete_attic_roof", () -> new BlockItem((Block)BlockInit.BLUE_CONCRETE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLUE_CONCRETE_TOP_ROOF = ITEMS.register(
      "blue_concrete_top_roof", () -> new BlockItem((Block)BlockInit.BLUE_CONCRETE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLUE_CONCRETE_LOWER_ROOF = ITEMS.register(
      "blue_concrete_lower_roof", () -> new BlockItem((Block)BlockInit.BLUE_CONCRETE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLUE_CONCRETE_STEEP_ROOF = ITEMS.register(
      "blue_concrete_steep_roof", () -> new BlockItem((Block)BlockInit.BLUE_CONCRETE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLUE_CONCRETE_UPPER_LOWER_ROOF = ITEMS.register(
      "blue_concrete_upper_lower_roof", () -> new BlockItem((Block)BlockInit.BLUE_CONCRETE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLUE_CONCRETE_UPPER_STEEP_ROOF = ITEMS.register(
      "blue_concrete_upper_steep_roof", () -> new BlockItem((Block)BlockInit.BLUE_CONCRETE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_BLUE_CONCRETE_ROOF = ITEMS.register(
      "light_blue_concrete_roof", () -> new BlockItem((Block)BlockInit.LIGHT_BLUE_CONCRETE_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_BLUE_CONCRETE_ATTIC_ROOF = ITEMS.register(
      "light_blue_concrete_attic_roof", () -> new BlockItem((Block)BlockInit.LIGHT_BLUE_CONCRETE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_BLUE_CONCRETE_TOP_ROOF = ITEMS.register(
      "light_blue_concrete_top_roof", () -> new BlockItem((Block)BlockInit.LIGHT_BLUE_CONCRETE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_BLUE_CONCRETE_LOWER_ROOF = ITEMS.register(
      "light_blue_concrete_lower_roof", () -> new BlockItem((Block)BlockInit.LIGHT_BLUE_CONCRETE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_BLUE_CONCRETE_STEEP_ROOF = ITEMS.register(
      "light_blue_concrete_steep_roof", () -> new BlockItem((Block)BlockInit.LIGHT_BLUE_CONCRETE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_BLUE_CONCRETE_UPPER_LOWER_ROOF = ITEMS.register(
      "light_blue_concrete_upper_lower_roof", () -> new BlockItem((Block)BlockInit.LIGHT_BLUE_CONCRETE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_BLUE_CONCRETE_UPPER_STEEP_ROOF = ITEMS.register(
      "light_blue_concrete_upper_steep_roof", () -> new BlockItem((Block)BlockInit.LIGHT_BLUE_CONCRETE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CYAN_CONCRETE_ROOF = ITEMS.register(
      "cyan_concrete_roof", () -> new BlockItem((Block)BlockInit.CYAN_CONCRETE_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CYAN_CONCRETE_ATTIC_ROOF = ITEMS.register(
      "cyan_concrete_attic_roof", () -> new BlockItem((Block)BlockInit.CYAN_CONCRETE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CYAN_CONCRETE_TOP_ROOF = ITEMS.register(
      "cyan_concrete_top_roof", () -> new BlockItem((Block)BlockInit.CYAN_CONCRETE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CYAN_CONCRETE_LOWER_ROOF = ITEMS.register(
      "cyan_concrete_lower_roof", () -> new BlockItem((Block)BlockInit.CYAN_CONCRETE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CYAN_CONCRETE_STEEP_ROOF = ITEMS.register(
      "cyan_concrete_steep_roof", () -> new BlockItem((Block)BlockInit.CYAN_CONCRETE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CYAN_CONCRETE_UPPER_LOWER_ROOF = ITEMS.register(
      "cyan_concrete_upper_lower_roof", () -> new BlockItem((Block)BlockInit.CYAN_CONCRETE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CYAN_CONCRETE_UPPER_STEEP_ROOF = ITEMS.register(
      "cyan_concrete_upper_steep_roof", () -> new BlockItem((Block)BlockInit.CYAN_CONCRETE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIME_CONCRETE_ROOF = ITEMS.register(
      "lime_concrete_roof", () -> new BlockItem((Block)BlockInit.LIME_CONCRETE_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIME_CONCRETE_ATTIC_ROOF = ITEMS.register(
      "lime_concrete_attic_roof", () -> new BlockItem((Block)BlockInit.LIME_CONCRETE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIME_CONCRETE_TOP_ROOF = ITEMS.register(
      "lime_concrete_top_roof", () -> new BlockItem((Block)BlockInit.LIME_CONCRETE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIME_CONCRETE_LOWER_ROOF = ITEMS.register(
      "lime_concrete_lower_roof", () -> new BlockItem((Block)BlockInit.LIME_CONCRETE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIME_CONCRETE_STEEP_ROOF = ITEMS.register(
      "lime_concrete_steep_roof", () -> new BlockItem((Block)BlockInit.LIME_CONCRETE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIME_CONCRETE_UPPER_LOWER_ROOF = ITEMS.register(
      "lime_concrete_upper_lower_roof", () -> new BlockItem((Block)BlockInit.LIME_CONCRETE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIME_CONCRETE_UPPER_STEEP_ROOF = ITEMS.register(
      "lime_concrete_upper_steep_roof", () -> new BlockItem((Block)BlockInit.LIME_CONCRETE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GREEN_CONCRETE_ROOF = ITEMS.register(
      "green_concrete_roof", () -> new BlockItem((Block)BlockInit.GREEN_CONCRETE_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GREEN_CONCRETE_ATTIC_ROOF = ITEMS.register(
      "green_concrete_attic_roof", () -> new BlockItem((Block)BlockInit.GREEN_CONCRETE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GREEN_CONCRETE_TOP_ROOF = ITEMS.register(
      "green_concrete_top_roof", () -> new BlockItem((Block)BlockInit.GREEN_CONCRETE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GREEN_CONCRETE_LOWER_ROOF = ITEMS.register(
      "green_concrete_lower_roof", () -> new BlockItem((Block)BlockInit.GREEN_CONCRETE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GREEN_CONCRETE_STEEP_ROOF = ITEMS.register(
      "green_concrete_steep_roof", () -> new BlockItem((Block)BlockInit.GREEN_CONCRETE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GREEN_CONCRETE_UPPER_LOWER_ROOF = ITEMS.register(
      "green_concrete_upper_lower_roof", () -> new BlockItem((Block)BlockInit.GREEN_CONCRETE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GREEN_CONCRETE_UPPER_STEEP_ROOF = ITEMS.register(
      "green_concrete_upper_steep_roof", () -> new BlockItem((Block)BlockInit.GREEN_CONCRETE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> YELLOW_CONCRETE_ROOF = ITEMS.register(
      "yellow_concrete_roof", () -> new BlockItem((Block)BlockInit.YELLOW_CONCRETE_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> YELLOW_CONCRETE_ATTIC_ROOF = ITEMS.register(
      "yellow_concrete_attic_roof", () -> new BlockItem((Block)BlockInit.YELLOW_CONCRETE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> YELLOW_CONCRETE_TOP_ROOF = ITEMS.register(
      "yellow_concrete_top_roof", () -> new BlockItem((Block)BlockInit.YELLOW_CONCRETE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> YELLOW_CONCRETE_LOWER_ROOF = ITEMS.register(
      "yellow_concrete_lower_roof", () -> new BlockItem((Block)BlockInit.YELLOW_CONCRETE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> YELLOW_CONCRETE_STEEP_ROOF = ITEMS.register(
      "yellow_concrete_steep_roof", () -> new BlockItem((Block)BlockInit.YELLOW_CONCRETE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> YELLOW_CONCRETE_UPPER_LOWER_ROOF = ITEMS.register(
      "yellow_concrete_upper_lower_roof", () -> new BlockItem((Block)BlockInit.YELLOW_CONCRETE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> YELLOW_CONCRETE_UPPER_STEEP_ROOF = ITEMS.register(
      "yellow_concrete_upper_steep_roof", () -> new BlockItem((Block)BlockInit.YELLOW_CONCRETE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BROWN_CONCRETE_ROOF = ITEMS.register(
      "brown_concrete_roof", () -> new BlockItem((Block)BlockInit.BROWN_CONCRETE_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BROWN_CONCRETE_ATTIC_ROOF = ITEMS.register(
      "brown_concrete_attic_roof", () -> new BlockItem((Block)BlockInit.BROWN_CONCRETE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BROWN_CONCRETE_TOP_ROOF = ITEMS.register(
      "brown_concrete_top_roof", () -> new BlockItem((Block)BlockInit.BROWN_CONCRETE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BROWN_CONCRETE_LOWER_ROOF = ITEMS.register(
      "brown_concrete_lower_roof", () -> new BlockItem((Block)BlockInit.BROWN_CONCRETE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BROWN_CONCRETE_STEEP_ROOF = ITEMS.register(
      "brown_concrete_steep_roof", () -> new BlockItem((Block)BlockInit.BROWN_CONCRETE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BROWN_CONCRETE_UPPER_LOWER_ROOF = ITEMS.register(
      "brown_concrete_upper_lower_roof", () -> new BlockItem((Block)BlockInit.BROWN_CONCRETE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BROWN_CONCRETE_UPPER_STEEP_ROOF = ITEMS.register(
      "brown_concrete_upper_steep_roof", () -> new BlockItem((Block)BlockInit.BROWN_CONCRETE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ORANGE_CONCRETE_ROOF = ITEMS.register(
      "orange_concrete_roof", () -> new BlockItem((Block)BlockInit.ORANGE_CONCRETE_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ORANGE_CONCRETE_ATTIC_ROOF = ITEMS.register(
      "orange_concrete_attic_roof", () -> new BlockItem((Block)BlockInit.ORANGE_CONCRETE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ORANGE_CONCRETE_TOP_ROOF = ITEMS.register(
      "orange_concrete_top_roof", () -> new BlockItem((Block)BlockInit.ORANGE_CONCRETE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ORANGE_CONCRETE_LOWER_ROOF = ITEMS.register(
      "orange_concrete_lower_roof", () -> new BlockItem((Block)BlockInit.ORANGE_CONCRETE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ORANGE_CONCRETE_STEEP_ROOF = ITEMS.register(
      "orange_concrete_steep_roof", () -> new BlockItem((Block)BlockInit.ORANGE_CONCRETE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ORANGE_CONCRETE_UPPER_LOWER_ROOF = ITEMS.register(
      "orange_concrete_upper_lower_roof", () -> new BlockItem((Block)BlockInit.ORANGE_CONCRETE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ORANGE_CONCRETE_UPPER_STEEP_ROOF = ITEMS.register(
      "orange_concrete_upper_steep_roof", () -> new BlockItem((Block)BlockInit.ORANGE_CONCRETE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_CONCRETE_ROOF = ITEMS.register(
      "red_concrete_roof", () -> new BlockItem((Block)BlockInit.RED_CONCRETE_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_CONCRETE_ATTIC_ROOF = ITEMS.register(
      "red_concrete_attic_roof", () -> new BlockItem((Block)BlockInit.RED_CONCRETE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_CONCRETE_TOP_ROOF = ITEMS.register(
      "red_concrete_top_roof", () -> new BlockItem((Block)BlockInit.RED_CONCRETE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_CONCRETE_LOWER_ROOF = ITEMS.register(
      "red_concrete_lower_roof", () -> new BlockItem((Block)BlockInit.RED_CONCRETE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_CONCRETE_STEEP_ROOF = ITEMS.register(
      "red_concrete_steep_roof", () -> new BlockItem((Block)BlockInit.RED_CONCRETE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_CONCRETE_UPPER_LOWER_ROOF = ITEMS.register(
      "red_concrete_upper_lower_roof", () -> new BlockItem((Block)BlockInit.RED_CONCRETE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_CONCRETE_UPPER_STEEP_ROOF = ITEMS.register(
      "red_concrete_upper_steep_roof", () -> new BlockItem((Block)BlockInit.RED_CONCRETE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MAGENTA_CONCRETE_ROOF = ITEMS.register(
      "magenta_concrete_roof", () -> new BlockItem((Block)BlockInit.MAGENTA_CONCRETE_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MAGENTA_CONCRETE_ATTIC_ROOF = ITEMS.register(
      "magenta_concrete_attic_roof", () -> new BlockItem((Block)BlockInit.MAGENTA_CONCRETE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MAGENTA_CONCRETE_TOP_ROOF = ITEMS.register(
      "magenta_concrete_top_roof", () -> new BlockItem((Block)BlockInit.MAGENTA_CONCRETE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MAGENTA_CONCRETE_LOWER_ROOF = ITEMS.register(
      "magenta_concrete_lower_roof", () -> new BlockItem((Block)BlockInit.MAGENTA_CONCRETE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MAGENTA_CONCRETE_STEEP_ROOF = ITEMS.register(
      "magenta_concrete_steep_roof", () -> new BlockItem((Block)BlockInit.MAGENTA_CONCRETE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MAGENTA_CONCRETE_UPPER_LOWER_ROOF = ITEMS.register(
      "magenta_concrete_upper_lower_roof", () -> new BlockItem((Block)BlockInit.MAGENTA_CONCRETE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MAGENTA_CONCRETE_UPPER_STEEP_ROOF = ITEMS.register(
      "magenta_concrete_upper_steep_roof", () -> new BlockItem((Block)BlockInit.MAGENTA_CONCRETE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PINK_CONCRETE_ROOF = ITEMS.register(
      "pink_concrete_roof", () -> new BlockItem((Block)BlockInit.PINK_CONCRETE_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PINK_CONCRETE_ATTIC_ROOF = ITEMS.register(
      "pink_concrete_attic_roof", () -> new BlockItem((Block)BlockInit.PINK_CONCRETE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PINK_CONCRETE_TOP_ROOF = ITEMS.register(
      "pink_concrete_top_roof", () -> new BlockItem((Block)BlockInit.PINK_CONCRETE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PINK_CONCRETE_LOWER_ROOF = ITEMS.register(
      "pink_concrete_lower_roof", () -> new BlockItem((Block)BlockInit.PINK_CONCRETE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PINK_CONCRETE_STEEP_ROOF = ITEMS.register(
      "pink_concrete_steep_roof", () -> new BlockItem((Block)BlockInit.PINK_CONCRETE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PINK_CONCRETE_UPPER_LOWER_ROOF = ITEMS.register(
      "pink_concrete_upper_lower_roof", () -> new BlockItem((Block)BlockInit.PINK_CONCRETE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PINK_CONCRETE_UPPER_STEEP_ROOF = ITEMS.register(
      "pink_concrete_upper_steep_roof", () -> new BlockItem((Block)BlockInit.PINK_CONCRETE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PURPLE_CONCRETE_ROOF = ITEMS.register(
      "purple_concrete_roof", () -> new BlockItem((Block)BlockInit.PURPLE_CONCRETE_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PURPLE_CONCRETE_ATTIC_ROOF = ITEMS.register(
      "purple_concrete_attic_roof", () -> new BlockItem((Block)BlockInit.PURPLE_CONCRETE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PURPLE_CONCRETE_TOP_ROOF = ITEMS.register(
      "purple_concrete_top_roof", () -> new BlockItem((Block)BlockInit.PURPLE_CONCRETE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PURPLE_CONCRETE_LOWER_ROOF = ITEMS.register(
      "purple_concrete_lower_roof", () -> new BlockItem((Block)BlockInit.PURPLE_CONCRETE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PURPLE_CONCRETE_STEEP_ROOF = ITEMS.register(
      "purple_concrete_steep_roof", () -> new BlockItem((Block)BlockInit.PURPLE_CONCRETE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PURPLE_CONCRETE_UPPER_LOWER_ROOF = ITEMS.register(
      "purple_concrete_upper_lower_roof", () -> new BlockItem((Block)BlockInit.PURPLE_CONCRETE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PURPLE_CONCRETE_UPPER_STEEP_ROOF = ITEMS.register(
      "purple_concrete_upper_steep_roof", () -> new BlockItem((Block)BlockInit.PURPLE_CONCRETE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WHITE_ROOF = ITEMS.register("white_roof", () -> new BlockItem((Block)BlockInit.WHITE_ROOF.get(), new Properties()));
   public static final DeferredItem<Item> WHITE_ATTIC_ROOF = ITEMS.register(
      "white_attic_roof", () -> new BlockItem((Block)BlockInit.WHITE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WHITE_TOP_ROOF = ITEMS.register(
      "white_top_roof", () -> new BlockItem((Block)BlockInit.WHITE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WHITE_LOWER_ROOF = ITEMS.register(
      "white_lower_roof", () -> new BlockItem((Block)BlockInit.WHITE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WHITE_STEEP_ROOF = ITEMS.register(
      "white_steep_roof", () -> new BlockItem((Block)BlockInit.WHITE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WHITE_UPPER_LOWER_ROOF = ITEMS.register(
      "white_upper_lower_roof", () -> new BlockItem((Block)BlockInit.WHITE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WHITE_UPPER_STEEP_ROOF = ITEMS.register(
      "white_upper_steep_roof", () -> new BlockItem((Block)BlockInit.WHITE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> WHITE_ROOF_BLOCK = ITEMS.register(
      "white_roof_block", () -> new BlockItem((Block)BlockInit.WHITE_ROOF_BLOCK.get(), new Properties())
   );
   public static final DeferredItem<Item> WHITE_ROOF_SLAB = ITEMS.register(
      "white_roof_slab", () -> new BlockItem((Block)BlockInit.WHITE_ROOF_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_ROOF = ITEMS.register(
      "light_gray_roof", () -> new BlockItem((Block)BlockInit.LIGHT_GRAY_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_ATTIC_ROOF = ITEMS.register(
      "light_gray_attic_roof", () -> new BlockItem((Block)BlockInit.LIGHT_GRAY_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_TOP_ROOF = ITEMS.register(
      "light_gray_top_roof", () -> new BlockItem((Block)BlockInit.LIGHT_GRAY_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_LOWER_ROOF = ITEMS.register(
      "light_gray_lower_roof", () -> new BlockItem((Block)BlockInit.LIGHT_GRAY_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_STEEP_ROOF = ITEMS.register(
      "light_gray_steep_roof", () -> new BlockItem((Block)BlockInit.LIGHT_GRAY_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_UPPER_LOWER_ROOF = ITEMS.register(
      "light_gray_upper_lower_roof", () -> new BlockItem((Block)BlockInit.LIGHT_GRAY_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_UPPER_STEEP_ROOF = ITEMS.register(
      "light_gray_upper_steep_roof", () -> new BlockItem((Block)BlockInit.LIGHT_GRAY_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_ROOF_BLOCK = ITEMS.register(
      "light_gray_roof_block", () -> new BlockItem((Block)BlockInit.LIGHT_GRAY_ROOF_BLOCK.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_ROOF_SLAB = ITEMS.register(
      "light_gray_roof_slab", () -> new BlockItem((Block)BlockInit.LIGHT_GRAY_ROOF_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_ROOF = ITEMS.register("gray_roof", () -> new BlockItem((Block)BlockInit.GRAY_ROOF.get(), new Properties()));
   public static final DeferredItem<Item> GRAY_ATTIC_ROOF = ITEMS.register(
      "gray_attic_roof", () -> new BlockItem((Block)BlockInit.GRAY_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_TOP_ROOF = ITEMS.register(
      "gray_top_roof", () -> new BlockItem((Block)BlockInit.GRAY_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_LOWER_ROOF = ITEMS.register(
      "gray_lower_roof", () -> new BlockItem((Block)BlockInit.GRAY_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_STEEP_ROOF = ITEMS.register(
      "gray_steep_roof", () -> new BlockItem((Block)BlockInit.GRAY_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_UPPER_LOWER_ROOF = ITEMS.register(
      "gray_upper_lower_roof", () -> new BlockItem((Block)BlockInit.GRAY_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_UPPER_STEEP_ROOF = ITEMS.register(
      "gray_upper_steep_roof", () -> new BlockItem((Block)BlockInit.GRAY_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_ROOF_BLOCK = ITEMS.register(
      "gray_roof_block", () -> new BlockItem((Block)BlockInit.GRAY_ROOF_BLOCK.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_ROOF_SLAB = ITEMS.register(
      "gray_roof_slab", () -> new BlockItem((Block)BlockInit.GRAY_ROOF_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACK_ROOF = ITEMS.register("black_roof", () -> new BlockItem((Block)BlockInit.BLACK_ROOF.get(), new Properties()));
   public static final DeferredItem<Item> BLACK_ATTIC_ROOF = ITEMS.register(
      "black_attic_roof", () -> new BlockItem((Block)BlockInit.BLACK_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACK_TOP_ROOF = ITEMS.register(
      "black_top_roof", () -> new BlockItem((Block)BlockInit.BLACK_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACK_LOWER_ROOF = ITEMS.register(
      "black_lower_roof", () -> new BlockItem((Block)BlockInit.BLACK_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACK_STEEP_ROOF = ITEMS.register(
      "black_steep_roof", () -> new BlockItem((Block)BlockInit.BLACK_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACK_UPPER_LOWER_ROOF = ITEMS.register(
      "black_upper_lower_roof", () -> new BlockItem((Block)BlockInit.BLACK_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACK_UPPER_STEEP_ROOF = ITEMS.register(
      "black_upper_steep_roof", () -> new BlockItem((Block)BlockInit.BLACK_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACK_ROOF_BLOCK = ITEMS.register(
      "black_roof_block", () -> new BlockItem((Block)BlockInit.BLACK_ROOF_BLOCK.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACK_ROOF_SLAB = ITEMS.register(
      "black_roof_slab", () -> new BlockItem((Block)BlockInit.BLACK_ROOF_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> BASE_ROOF = ITEMS.register("base_roof", () -> new BlockItem((Block)BlockInit.BASE_ROOF.get(), new Properties()));
   public static final DeferredItem<Item> BASE_ATTIC_ROOF = ITEMS.register(
      "base_attic_roof", () -> new BlockItem((Block)BlockInit.BASE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BASE_TOP_ROOF = ITEMS.register(
      "base_top_roof", () -> new BlockItem((Block)BlockInit.BASE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BASE_LOWER_ROOF = ITEMS.register(
      "base_lower_roof", () -> new BlockItem((Block)BlockInit.BASE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BASE_STEEP_ROOF = ITEMS.register(
      "base_steep_roof", () -> new BlockItem((Block)BlockInit.BASE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BASE_UPPER_LOWER_ROOF = ITEMS.register(
      "base_upper_lower_roof", () -> new BlockItem((Block)BlockInit.BASE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BASE_UPPER_STEEP_ROOF = ITEMS.register(
      "base_upper_steep_roof", () -> new BlockItem((Block)BlockInit.BASE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BASE_ROOF_BLOCK = ITEMS.register(
      "base_roof_block", () -> new BlockItem((Block)BlockInit.BASE_ROOF_BLOCK.get(), new Properties())
   );
   public static final DeferredItem<Item> BASE_ROOF_SLAB = ITEMS.register(
      "base_roof_slab", () -> new BlockItem((Block)BlockInit.BASE_ROOF_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_ROOF = ITEMS.register("stone_roof", () -> new BlockItem((Block)BlockInit.STONE_ROOF.get(), new Properties()));
   public static final DeferredItem<Item> STONE_ATTIC_ROOF = ITEMS.register(
      "stone_attic_roof", () -> new BlockItem((Block)BlockInit.STONE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_TOP_ROOF = ITEMS.register(
      "stone_top_roof", () -> new BlockItem((Block)BlockInit.STONE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_LOWER_ROOF = ITEMS.register(
      "stone_lower_roof", () -> new BlockItem((Block)BlockInit.STONE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_STEEP_ROOF = ITEMS.register(
      "stone_steep_roof", () -> new BlockItem((Block)BlockInit.STONE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_UPPER_LOWER_ROOF = ITEMS.register(
      "stone_upper_lower_roof", () -> new BlockItem((Block)BlockInit.STONE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_UPPER_STEEP_ROOF = ITEMS.register(
      "stone_upper_steep_roof", () -> new BlockItem((Block)BlockInit.STONE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_ROOF = ITEMS.register(
      "granite_roof", () -> new BlockItem((Block)BlockInit.GRANITE_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_ATTIC_ROOF = ITEMS.register(
      "granite_attic_roof", () -> new BlockItem((Block)BlockInit.GRANITE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_TOP_ROOF = ITEMS.register(
      "granite_top_roof", () -> new BlockItem((Block)BlockInit.GRANITE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_LOWER_ROOF = ITEMS.register(
      "granite_lower_roof", () -> new BlockItem((Block)BlockInit.GRANITE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_STEEP_ROOF = ITEMS.register(
      "granite_steep_roof", () -> new BlockItem((Block)BlockInit.GRANITE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_UPPER_LOWER_ROOF = ITEMS.register(
      "granite_upper_lower_roof", () -> new BlockItem((Block)BlockInit.GRANITE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_UPPER_STEEP_ROOF = ITEMS.register(
      "granite_upper_steep_roof", () -> new BlockItem((Block)BlockInit.GRANITE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_ROOF = ITEMS.register(
      "diorite_roof", () -> new BlockItem((Block)BlockInit.DIORITE_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_ATTIC_ROOF = ITEMS.register(
      "diorite_attic_roof", () -> new BlockItem((Block)BlockInit.DIORITE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_TOP_ROOF = ITEMS.register(
      "diorite_top_roof", () -> new BlockItem((Block)BlockInit.DIORITE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_LOWER_ROOF = ITEMS.register(
      "diorite_lower_roof", () -> new BlockItem((Block)BlockInit.DIORITE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_STEEP_ROOF = ITEMS.register(
      "diorite_steep_roof", () -> new BlockItem((Block)BlockInit.DIORITE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_UPPER_LOWER_ROOF = ITEMS.register(
      "diorite_upper_lower_roof", () -> new BlockItem((Block)BlockInit.DIORITE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_UPPER_STEEP_ROOF = ITEMS.register(
      "diorite_upper_steep_roof", () -> new BlockItem((Block)BlockInit.DIORITE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_ROOF = ITEMS.register(
      "andesite_roof", () -> new BlockItem((Block)BlockInit.ANDESITE_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_ATTIC_ROOF = ITEMS.register(
      "andesite_attic_roof", () -> new BlockItem((Block)BlockInit.ANDESITE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_TOP_ROOF = ITEMS.register(
      "andesite_top_roof", () -> new BlockItem((Block)BlockInit.ANDESITE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_LOWER_ROOF = ITEMS.register(
      "andesite_lower_roof", () -> new BlockItem((Block)BlockInit.ANDESITE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_STEEP_ROOF = ITEMS.register(
      "andesite_steep_roof", () -> new BlockItem((Block)BlockInit.ANDESITE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_UPPER_LOWER_ROOF = ITEMS.register(
      "andesite_upper_lower_roof", () -> new BlockItem((Block)BlockInit.ANDESITE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_UPPER_STEEP_ROOF = ITEMS.register(
      "andesite_upper_steep_roof", () -> new BlockItem((Block)BlockInit.ANDESITE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLESTONE_ROOF = ITEMS.register(
      "cobblestone_roof", () -> new BlockItem((Block)BlockInit.COBBLESTONE_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLESTONE_ATTIC_ROOF = ITEMS.register(
      "cobblestone_attic_roof", () -> new BlockItem((Block)BlockInit.COBBLESTONE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLESTONE_TOP_ROOF = ITEMS.register(
      "cobblestone_top_roof", () -> new BlockItem((Block)BlockInit.COBBLESTONE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLESTONE_LOWER_ROOF = ITEMS.register(
      "cobblestone_lower_roof", () -> new BlockItem((Block)BlockInit.COBBLESTONE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLESTONE_STEEP_ROOF = ITEMS.register(
      "cobblestone_steep_roof", () -> new BlockItem((Block)BlockInit.COBBLESTONE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLESTONE_UPPER_LOWER_ROOF = ITEMS.register(
      "cobblestone_upper_lower_roof", () -> new BlockItem((Block)BlockInit.COBBLESTONE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLESTONE_UPPER_STEEP_ROOF = ITEMS.register(
      "cobblestone_upper_steep_roof", () -> new BlockItem((Block)BlockInit.COBBLESTONE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_ROOF = ITEMS.register(
      "sandstone_roof", () -> new BlockItem((Block)BlockInit.SANDSTONE_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_ATTIC_ROOF = ITEMS.register(
      "sandstone_attic_roof", () -> new BlockItem((Block)BlockInit.SANDSTONE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_TOP_ROOF = ITEMS.register(
      "sandstone_top_roof", () -> new BlockItem((Block)BlockInit.SANDSTONE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_LOWER_ROOF = ITEMS.register(
      "sandstone_lower_roof", () -> new BlockItem((Block)BlockInit.SANDSTONE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_STEEP_ROOF = ITEMS.register(
      "sandstone_steep_roof", () -> new BlockItem((Block)BlockInit.SANDSTONE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_UPPER_LOWER_ROOF = ITEMS.register(
      "sandstone_upper_lower_roof", () -> new BlockItem((Block)BlockInit.SANDSTONE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_UPPER_STEEP_ROOF = ITEMS.register(
      "sandstone_upper_steep_roof", () -> new BlockItem((Block)BlockInit.SANDSTONE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_ROOF = ITEMS.register(
      "red_sandstone_roof", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_ATTIC_ROOF = ITEMS.register(
      "red_sandstone_attic_roof", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_TOP_ROOF = ITEMS.register(
      "red_sandstone_top_roof", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_LOWER_ROOF = ITEMS.register(
      "red_sandstone_lower_roof", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_STEEP_ROOF = ITEMS.register(
      "red_sandstone_steep_roof", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_UPPER_LOWER_ROOF = ITEMS.register(
      "red_sandstone_upper_lower_roof", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_UPPER_STEEP_ROOF = ITEMS.register(
      "red_sandstone_upper_steep_roof", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICKS_ROOF = ITEMS.register("bricks_roof", () -> new BlockItem((Block)BlockInit.BRICKS_ROOF.get(), new Properties()));
   public static final DeferredItem<Item> BRICKS_ATTIC_ROOF = ITEMS.register(
      "bricks_attic_roof", () -> new BlockItem((Block)BlockInit.BRICKS_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICKS_TOP_ROOF = ITEMS.register(
      "bricks_top_roof", () -> new BlockItem((Block)BlockInit.BRICKS_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICKS_LOWER_ROOF = ITEMS.register(
      "bricks_lower_roof", () -> new BlockItem((Block)BlockInit.BRICKS_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICKS_STEEP_ROOF = ITEMS.register(
      "bricks_steep_roof", () -> new BlockItem((Block)BlockInit.BRICKS_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICKS_UPPER_LOWER_ROOF = ITEMS.register(
      "bricks_upper_lower_roof", () -> new BlockItem((Block)BlockInit.BRICKS_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICKS_UPPER_STEEP_ROOF = ITEMS.register(
      "bricks_upper_steep_roof", () -> new BlockItem((Block)BlockInit.BRICKS_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_ROOF = ITEMS.register(
      "blackstone_roof", () -> new BlockItem((Block)BlockInit.BLACKSTONE_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_ATTIC_ROOF = ITEMS.register(
      "blackstone_attic_roof", () -> new BlockItem((Block)BlockInit.BLACKSTONE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_TOP_ROOF = ITEMS.register(
      "blackstone_top_roof", () -> new BlockItem((Block)BlockInit.BLACKSTONE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_LOWER_ROOF = ITEMS.register(
      "blackstone_lower_roof", () -> new BlockItem((Block)BlockInit.BLACKSTONE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_STEEP_ROOF = ITEMS.register(
      "blackstone_steep_roof", () -> new BlockItem((Block)BlockInit.BLACKSTONE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_UPPER_LOWER_ROOF = ITEMS.register(
      "blackstone_upper_lower_roof", () -> new BlockItem((Block)BlockInit.BLACKSTONE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_UPPER_STEEP_ROOF = ITEMS.register(
      "blackstone_upper_steep_roof", () -> new BlockItem((Block)BlockInit.BLACKSTONE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_ROOF = ITEMS.register(
      "deepslate_roof", () -> new BlockItem((Block)BlockInit.DEEPSLATE_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_ATTIC_ROOF = ITEMS.register(
      "deepslate_attic_roof", () -> new BlockItem((Block)BlockInit.DEEPSLATE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_TOP_ROOF = ITEMS.register(
      "deepslate_top_roof", () -> new BlockItem((Block)BlockInit.DEEPSLATE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_LOWER_ROOF = ITEMS.register(
      "deepslate_lower_roof", () -> new BlockItem((Block)BlockInit.DEEPSLATE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_STEEP_ROOF = ITEMS.register(
      "deepslate_steep_roof", () -> new BlockItem((Block)BlockInit.DEEPSLATE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_UPPER_LOWER_ROOF = ITEMS.register(
      "deepslate_upper_lower_roof", () -> new BlockItem((Block)BlockInit.DEEPSLATE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_UPPER_STEEP_ROOF = ITEMS.register(
      "deepslate_upper_steep_roof", () -> new BlockItem((Block)BlockInit.DEEPSLATE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_ROOF = ITEMS.register(
      "mud_brick_roof", () -> new BlockItem((Block)BlockInit.MUD_BRICK_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_ATTIC_ROOF = ITEMS.register(
      "mud_brick_attic_roof", () -> new BlockItem((Block)BlockInit.MUD_BRICK_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_TOP_ROOF = ITEMS.register(
      "mud_brick_top_roof", () -> new BlockItem((Block)BlockInit.MUD_BRICK_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_LOWER_ROOF = ITEMS.register(
      "mud_brick_lower_roof", () -> new BlockItem((Block)BlockInit.MUD_BRICK_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_STEEP_ROOF = ITEMS.register(
      "mud_brick_steep_roof", () -> new BlockItem((Block)BlockInit.MUD_BRICK_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_UPPER_LOWER_ROOF = ITEMS.register(
      "mud_brick_upper_lower_roof", () -> new BlockItem((Block)BlockInit.MUD_BRICK_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_UPPER_STEEP_ROOF = ITEMS.register(
      "mud_brick_upper_steep_roof", () -> new BlockItem((Block)BlockInit.MUD_BRICK_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> THATCH_ROOF = ITEMS.register(
      "thatch_roof", () -> new FuelItemBlock((Block)BlockInit.THATCH_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> THATCH_ATTIC_ROOF = ITEMS.register(
      "thatch_attic_roof", () -> new FuelItemBlock((Block)BlockInit.THATCH_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> THATCH_TOP_ROOF = ITEMS.register(
      "thatch_top_roof", () -> new FuelItemBlock((Block)BlockInit.THATCH_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> THATCH_LOWER_ROOF = ITEMS.register(
      "thatch_lower_roof", () -> new FuelItemBlock((Block)BlockInit.THATCH_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> THATCH_STEEP_ROOF = ITEMS.register(
      "thatch_steep_roof", () -> new FuelItemBlock((Block)BlockInit.THATCH_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> THATCH_UPPER_LOWER_ROOF = ITEMS.register(
      "thatch_upper_lower_roof", () -> new FuelItemBlock((Block)BlockInit.THATCH_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> THATCH_UPPER_STEEP_ROOF = ITEMS.register(
      "thatch_upper_steep_roof", () -> new FuelItemBlock((Block)BlockInit.THATCH_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PRISMARINE_BRICK_ROOF = ITEMS.register(
      "prismarine_brick_roof", () -> new BlockItem((Block)BlockInit.PRISMARINE_BRICK_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PRISMARINE_BRICK_ATTIC_ROOF = ITEMS.register(
      "prismarine_brick_attic_roof", () -> new BlockItem((Block)BlockInit.PRISMARINE_BRICK_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PRISMARINE_BRICK_TOP_ROOF = ITEMS.register(
      "prismarine_brick_top_roof", () -> new BlockItem((Block)BlockInit.PRISMARINE_BRICK_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PRISMARINE_BRICK_LOWER_ROOF = ITEMS.register(
      "prismarine_brick_lower_roof", () -> new BlockItem((Block)BlockInit.PRISMARINE_BRICK_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PRISMARINE_BRICK_STEEP_ROOF = ITEMS.register(
      "prismarine_brick_steep_roof", () -> new BlockItem((Block)BlockInit.PRISMARINE_BRICK_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PRISMARINE_BRICK_UPPER_LOWER_ROOF = ITEMS.register(
      "prismarine_brick_upper_lower_roof", () -> new BlockItem((Block)BlockInit.PRISMARINE_BRICK_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> PRISMARINE_BRICK_UPPER_STEEP_ROOF = ITEMS.register(
      "prismarine_brick_upper_steep_roof", () -> new BlockItem((Block)BlockInit.PRISMARINE_BRICK_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_ROOF = ITEMS.register(
      "dark_prismarine_roof", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_ATTIC_ROOF = ITEMS.register(
      "dark_prismarine_attic_roof", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_TOP_ROOF = ITEMS.register(
      "dark_prismarine_top_roof", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_LOWER_ROOF = ITEMS.register(
      "dark_prismarine_lower_roof", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_STEEP_ROOF = ITEMS.register(
      "dark_prismarine_steep_roof", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_UPPER_LOWER_ROOF = ITEMS.register(
      "dark_prismarine_upper_lower_roof", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_UPPER_STEEP_ROOF = ITEMS.register(
      "dark_prismarine_upper_steep_roof", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACK_STRIPED_AWNING = ITEMS.register(
      "black_striped_awning", () -> new FuelItemBlock((Block)BlockInit.BLACK_STRIPED_AWNING.get(), new Properties())
   );
   public static final DeferredItem<Item> BLUE_STRIPED_AWNING = ITEMS.register(
      "blue_striped_awning", () -> new FuelItemBlock((Block)BlockInit.BLUE_STRIPED_AWNING.get(), new Properties())
   );
   public static final DeferredItem<Item> BROWN_STRIPED_AWNING = ITEMS.register(
      "brown_striped_awning", () -> new FuelItemBlock((Block)BlockInit.BROWN_STRIPED_AWNING.get(), new Properties())
   );
   public static final DeferredItem<Item> CYAN_STRIPED_AWNING = ITEMS.register(
      "cyan_striped_awning", () -> new FuelItemBlock((Block)BlockInit.CYAN_STRIPED_AWNING.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_STRIPED_AWNING = ITEMS.register(
      "gray_striped_awning", () -> new FuelItemBlock((Block)BlockInit.GRAY_STRIPED_AWNING.get(), new Properties())
   );
   public static final DeferredItem<Item> GREEN_STRIPED_AWNING = ITEMS.register(
      "green_striped_awning", () -> new FuelItemBlock((Block)BlockInit.GREEN_STRIPED_AWNING.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_BLUE_STRIPED_AWNING = ITEMS.register(
      "light_blue_striped_awning", () -> new FuelItemBlock((Block)BlockInit.LIGHT_BLUE_STRIPED_AWNING.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_STRIPED_AWNING = ITEMS.register(
      "light_gray_striped_awning", () -> new FuelItemBlock((Block)BlockInit.LIGHT_GRAY_STRIPED_AWNING.get(), new Properties())
   );
   public static final DeferredItem<Item> LIME_STRIPED_AWNING = ITEMS.register(
      "lime_striped_awning", () -> new FuelItemBlock((Block)BlockInit.LIME_STRIPED_AWNING.get(), new Properties())
   );
   public static final DeferredItem<Item> MAGENTA_STRIPED_AWNING = ITEMS.register(
      "magenta_striped_awning", () -> new FuelItemBlock((Block)BlockInit.MAGENTA_STRIPED_AWNING.get(), new Properties())
   );
   public static final DeferredItem<Item> ORANGE_STRIPED_AWNING = ITEMS.register(
      "orange_striped_awning", () -> new FuelItemBlock((Block)BlockInit.ORANGE_STRIPED_AWNING.get(), new Properties())
   );
   public static final DeferredItem<Item> PINK_STRIPED_AWNING = ITEMS.register(
      "pink_striped_awning", () -> new FuelItemBlock((Block)BlockInit.PINK_STRIPED_AWNING.get(), new Properties())
   );
   public static final DeferredItem<Item> PURPLE_STRIPED_AWNING = ITEMS.register(
      "purple_striped_awning", () -> new FuelItemBlock((Block)BlockInit.PURPLE_STRIPED_AWNING.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_STRIPED_AWNING = ITEMS.register(
      "red_striped_awning", () -> new FuelItemBlock((Block)BlockInit.RED_STRIPED_AWNING.get(), new Properties())
   );
   public static final DeferredItem<Item> YELLOW_STRIPED_AWNING = ITEMS.register(
      "yellow_striped_awning", () -> new FuelItemBlock((Block)BlockInit.YELLOW_STRIPED_AWNING.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_BASE_YELLOW = ITEMS.register(
      "gutter_base_yellow", () -> new BlockItem((Block)BlockInit.GUTTER_BASE_YELLOW.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_BASE_ORANGE = ITEMS.register(
      "gutter_base_orange", () -> new BlockItem((Block)BlockInit.GUTTER_BASE_ORANGE.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_BASE_RED = ITEMS.register(
      "gutter_base_red", () -> new BlockItem((Block)BlockInit.GUTTER_BASE_RED.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_BASE_PINK = ITEMS.register(
      "gutter_base_pink", () -> new BlockItem((Block)BlockInit.GUTTER_BASE_PINK.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_BASE_MAGENTA = ITEMS.register(
      "gutter_base_magenta", () -> new BlockItem((Block)BlockInit.GUTTER_BASE_MAGENTA.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_BASE_PURPLE = ITEMS.register(
      "gutter_base_purple", () -> new BlockItem((Block)BlockInit.GUTTER_BASE_PURPLE.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_BASE_LIGHT_BLUE = ITEMS.register(
      "gutter_base_light_blue", () -> new BlockItem((Block)BlockInit.GUTTER_BASE_LIGHT_BLUE.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_BASE_BLUE = ITEMS.register(
      "gutter_base_blue", () -> new BlockItem((Block)BlockInit.GUTTER_BASE_BLUE.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_BASE_CYAN = ITEMS.register(
      "gutter_base_cyan", () -> new BlockItem((Block)BlockInit.GUTTER_BASE_CYAN.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_BASE_LIME = ITEMS.register(
      "gutter_base_lime", () -> new BlockItem((Block)BlockInit.GUTTER_BASE_LIME.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_BASE_GREEN = ITEMS.register(
      "gutter_base_green", () -> new BlockItem((Block)BlockInit.GUTTER_BASE_GREEN.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_BASE_BROWN = ITEMS.register(
      "gutter_base_brown", () -> new BlockItem((Block)BlockInit.GUTTER_BASE_BROWN.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_BASE = ITEMS.register("gutter_base", () -> new BlockItem((Block)BlockInit.GUTTER_BASE.get(), new Properties()));
   public static final DeferredItem<Item> GUTTER_BASE_BLACK = ITEMS.register(
      "gutter_base_black", () -> new BlockItem((Block)BlockInit.GUTTER_BASE_BLACK.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_BASE_GRAY = ITEMS.register(
      "gutter_base_gray", () -> new BlockItem((Block)BlockInit.GUTTER_BASE_GRAY.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_BASE_LIGHT_GRAY = ITEMS.register(
      "gutter_base_light_gray", () -> new BlockItem((Block)BlockInit.GUTTER_BASE_LIGHT_GRAY.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_BASE_WHITE = ITEMS.register(
      "gutter_base_white", () -> new BlockItem((Block)BlockInit.GUTTER_BASE_WHITE.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_MIDDLE_YELLOW = ITEMS.register(
      "gutter_middle_yellow", () -> new BlockItem((Block)BlockInit.GUTTER_MIDDLE_YELLOW.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_MIDDLE_ORANGE = ITEMS.register(
      "gutter_middle_orange", () -> new BlockItem((Block)BlockInit.GUTTER_MIDDLE_ORANGE.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_MIDDLE_RED = ITEMS.register(
      "gutter_middle_red", () -> new BlockItem((Block)BlockInit.GUTTER_MIDDLE_RED.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_MIDDLE_PINK = ITEMS.register(
      "gutter_middle_pink", () -> new BlockItem((Block)BlockInit.GUTTER_MIDDLE_PINK.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_MIDDLE_MAGENTA = ITEMS.register(
      "gutter_middle_magenta", () -> new BlockItem((Block)BlockInit.GUTTER_MIDDLE_MAGENTA.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_MIDDLE_PURPLE = ITEMS.register(
      "gutter_middle_purple", () -> new BlockItem((Block)BlockInit.GUTTER_MIDDLE_PURPLE.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_MIDDLE_LIGHT_BLUE = ITEMS.register(
      "gutter_middle_light_blue", () -> new BlockItem((Block)BlockInit.GUTTER_MIDDLE_LIGHT_BLUE.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_MIDDLE_BLUE = ITEMS.register(
      "gutter_middle_blue", () -> new BlockItem((Block)BlockInit.GUTTER_MIDDLE_BLUE.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_MIDDLE_CYAN = ITEMS.register(
      "gutter_middle_cyan", () -> new BlockItem((Block)BlockInit.GUTTER_MIDDLE_CYAN.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_MIDDLE_LIME = ITEMS.register(
      "gutter_middle_lime", () -> new BlockItem((Block)BlockInit.GUTTER_MIDDLE_LIME.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_MIDDLE_GREEN = ITEMS.register(
      "gutter_middle_green", () -> new BlockItem((Block)BlockInit.GUTTER_MIDDLE_GREEN.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_MIDDLE_BROWN = ITEMS.register(
      "gutter_middle_brown", () -> new BlockItem((Block)BlockInit.GUTTER_MIDDLE_BROWN.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_MIDDLE = ITEMS.register(
      "gutter_middle", () -> new BlockItem((Block)BlockInit.GUTTER_MIDDLE.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_MIDDLE_BLACK = ITEMS.register(
      "gutter_middle_black", () -> new BlockItem((Block)BlockInit.GUTTER_MIDDLE_BLACK.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_MIDDLE_GRAY = ITEMS.register(
      "gutter_middle_gray", () -> new BlockItem((Block)BlockInit.GUTTER_MIDDLE_GRAY.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_MIDDLE_LIGHT_GRAY = ITEMS.register(
      "gutter_middle_light_gray", () -> new BlockItem((Block)BlockInit.GUTTER_MIDDLE_LIGHT_GRAY.get(), new Properties())
   );
   public static final DeferredItem<Item> GUTTER_MIDDLE_WHITE = ITEMS.register(
      "gutter_middle_white", () -> new BlockItem((Block)BlockInit.GUTTER_MIDDLE_WHITE.get(), new Properties())
   );
   public static final DeferredItem<Item> NETHER_BRICKS_ROOF = ITEMS.register(
      "nether_bricks_roof", () -> new BlockItem((Block)BlockInit.NETHER_BRICKS_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> NETHER_BRICKS_ATTIC_ROOF = ITEMS.register(
      "nether_bricks_attic_roof", () -> new BlockItem((Block)BlockInit.NETHER_BRICKS_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> NETHER_BRICKS_TOP_ROOF = ITEMS.register(
      "nether_bricks_top_roof", () -> new BlockItem((Block)BlockInit.NETHER_BRICKS_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> NETHER_BRICKS_LOWER_ROOF = ITEMS.register(
      "nether_bricks_lower_roof", () -> new BlockItem((Block)BlockInit.NETHER_BRICKS_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> NETHER_BRICKS_STEEP_ROOF = ITEMS.register(
      "nether_bricks_steep_roof", () -> new BlockItem((Block)BlockInit.NETHER_BRICKS_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> NETHER_BRICKS_UPPER_LOWER_ROOF = ITEMS.register(
      "nether_bricks_upper_lower_roof", () -> new BlockItem((Block)BlockInit.NETHER_BRICKS_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> NETHER_BRICKS_UPPER_STEEP_ROOF = ITEMS.register(
      "nether_bricks_upper_steep_roof", () -> new BlockItem((Block)BlockInit.NETHER_BRICKS_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_NETHER_BRICKS_ROOF = ITEMS.register(
      "red_nether_bricks_roof", () -> new BlockItem((Block)BlockInit.RED_NETHER_BRICKS_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_NETHER_BRICKS_ATTIC_ROOF = ITEMS.register(
      "red_nether_bricks_attic_roof", () -> new BlockItem((Block)BlockInit.RED_NETHER_BRICKS_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_NETHER_BRICKS_TOP_ROOF = ITEMS.register(
      "red_nether_bricks_top_roof", () -> new BlockItem((Block)BlockInit.RED_NETHER_BRICKS_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_NETHER_BRICKS_LOWER_ROOF = ITEMS.register(
      "red_nether_bricks_lower_roof", () -> new BlockItem((Block)BlockInit.RED_NETHER_BRICKS_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_NETHER_BRICKS_STEEP_ROOF = ITEMS.register(
      "red_nether_bricks_steep_roof", () -> new BlockItem((Block)BlockInit.RED_NETHER_BRICKS_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_NETHER_BRICKS_UPPER_LOWER_ROOF = ITEMS.register(
      "red_nether_bricks_upper_lower_roof", () -> new BlockItem((Block)BlockInit.RED_NETHER_BRICKS_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_NETHER_BRICKS_UPPER_STEEP_ROOF = ITEMS.register(
      "red_nether_bricks_upper_steep_roof", () -> new BlockItem((Block)BlockInit.RED_NETHER_BRICKS_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_ROOF = ITEMS.register("cherry_roof", () -> new BlockItem((Block)BlockInit.CHERRY_ROOF.get(), new Properties()));
   public static final DeferredItem<Item> CHERRY_ATTIC_ROOF = ITEMS.register(
      "cherry_attic_roof", () -> new BlockItem((Block)BlockInit.CHERRY_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_TOP_ROOF = ITEMS.register(
      "cherry_top_roof", () -> new BlockItem((Block)BlockInit.CHERRY_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_LOWER_ROOF = ITEMS.register(
      "cherry_lower_roof", () -> new BlockItem((Block)BlockInit.CHERRY_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_STEEP_ROOF = ITEMS.register(
      "cherry_steep_roof", () -> new BlockItem((Block)BlockInit.CHERRY_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_UPPER_LOWER_ROOF = ITEMS.register(
      "cherry_upper_lower_roof", () -> new BlockItem((Block)BlockInit.CHERRY_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_UPPER_STEEP_ROOF = ITEMS.register(
      "cherry_upper_steep_roof", () -> new BlockItem((Block)BlockInit.CHERRY_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_PLANKS_ROOF = ITEMS.register(
      "cherry_planks_roof", () -> new BlockItem((Block)BlockInit.CHERRY_PLANKS_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_PLANKS_ATTIC_ROOF = ITEMS.register(
      "cherry_planks_attic_roof", () -> new BlockItem((Block)BlockInit.CHERRY_PLANKS_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_PLANKS_TOP_ROOF = ITEMS.register(
      "cherry_planks_top_roof", () -> new BlockItem((Block)BlockInit.CHERRY_PLANKS_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_PLANKS_LOWER_ROOF = ITEMS.register(
      "cherry_planks_lower_roof", () -> new BlockItem((Block)BlockInit.CHERRY_PLANKS_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_PLANKS_STEEP_ROOF = ITEMS.register(
      "cherry_planks_steep_roof", () -> new BlockItem((Block)BlockInit.CHERRY_PLANKS_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_PLANKS_UPPER_LOWER_ROOF = ITEMS.register(
      "cherry_planks_upper_lower_roof", () -> new BlockItem((Block)BlockInit.CHERRY_PLANKS_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_PLANKS_UPPER_STEEP_ROOF = ITEMS.register(
      "cherry_planks_upper_steep_roof", () -> new BlockItem((Block)BlockInit.CHERRY_PLANKS_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_ROOF = ITEMS.register("bamboo_roof", () -> new BlockItem((Block)BlockInit.BAMBOO_ROOF.get(), new Properties()));
   public static final DeferredItem<Item> BAMBOO_ATTIC_ROOF = ITEMS.register(
      "bamboo_attic_roof", () -> new BlockItem((Block)BlockInit.BAMBOO_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_TOP_ROOF = ITEMS.register(
      "bamboo_top_roof", () -> new BlockItem((Block)BlockInit.BAMBOO_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_LOWER_ROOF = ITEMS.register(
      "bamboo_lower_roof", () -> new BlockItem((Block)BlockInit.BAMBOO_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_STEEP_ROOF = ITEMS.register(
      "bamboo_steep_roof", () -> new BlockItem((Block)BlockInit.BAMBOO_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_UPPER_LOWER_ROOF = ITEMS.register(
      "bamboo_upper_lower_roof", () -> new BlockItem((Block)BlockInit.BAMBOO_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_UPPER_STEEP_ROOF = ITEMS.register(
      "bamboo_upper_steep_roof", () -> new BlockItem((Block)BlockInit.BAMBOO_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_PLANKS_ROOF = ITEMS.register(
      "bamboo_planks_roof", () -> new BlockItem((Block)BlockInit.BAMBOO_PLANKS_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_PLANKS_ATTIC_ROOF = ITEMS.register(
      "bamboo_planks_attic_roof", () -> new BlockItem((Block)BlockInit.BAMBOO_PLANKS_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_PLANKS_TOP_ROOF = ITEMS.register(
      "bamboo_planks_top_roof", () -> new BlockItem((Block)BlockInit.BAMBOO_PLANKS_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_PLANKS_LOWER_ROOF = ITEMS.register(
      "bamboo_planks_lower_roof", () -> new BlockItem((Block)BlockInit.BAMBOO_PLANKS_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_PLANKS_STEEP_ROOF = ITEMS.register(
      "bamboo_planks_steep_roof", () -> new BlockItem((Block)BlockInit.BAMBOO_PLANKS_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_PLANKS_UPPER_LOWER_ROOF = ITEMS.register(
      "bamboo_planks_upper_lower_roof", () -> new BlockItem((Block)BlockInit.BAMBOO_PLANKS_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_PLANKS_UPPER_STEEP_ROOF = ITEMS.register(
      "bamboo_planks_upper_steep_roof", () -> new BlockItem((Block)BlockInit.BAMBOO_PLANKS_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_MOSAIC_ROOF = ITEMS.register(
      "bamboo_mosaic_roof", () -> new BlockItem((Block)BlockInit.BAMBOO_MOSAIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_MOSAIC_ATTIC_ROOF = ITEMS.register(
      "bamboo_mosaic_attic_roof", () -> new BlockItem((Block)BlockInit.BAMBOO_MOSAIC_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_MOSAIC_TOP_ROOF = ITEMS.register(
      "bamboo_mosaic_top_roof", () -> new BlockItem((Block)BlockInit.BAMBOO_MOSAIC_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_MOSAIC_LOWER_ROOF = ITEMS.register(
      "bamboo_mosaic_lower_roof", () -> new BlockItem((Block)BlockInit.BAMBOO_MOSAIC_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_MOSAIC_STEEP_ROOF = ITEMS.register(
      "bamboo_mosaic_steep_roof", () -> new BlockItem((Block)BlockInit.BAMBOO_MOSAIC_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_MOSAIC_UPPER_LOWER_ROOF = ITEMS.register(
      "bamboo_mosaic_upper_lower_roof", () -> new BlockItem((Block)BlockInit.BAMBOO_MOSAIC_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_MOSAIC_UPPER_STEEP_ROOF = ITEMS.register(
      "bamboo_mosaic_upper_steep_roof", () -> new BlockItem((Block)BlockInit.BAMBOO_MOSAIC_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_BRICKS_ROOF = ITEMS.register(
      "stone_bricks_roof", () -> new BlockItem((Block)BlockInit.STONE_BRICKS_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_BRICKS_ATTIC_ROOF = ITEMS.register(
      "stone_bricks_attic_roof", () -> new BlockItem((Block)BlockInit.STONE_BRICKS_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_BRICKS_TOP_ROOF = ITEMS.register(
      "stone_bricks_top_roof", () -> new BlockItem((Block)BlockInit.STONE_BRICKS_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_BRICKS_LOWER_ROOF = ITEMS.register(
      "stone_bricks_lower_roof", () -> new BlockItem((Block)BlockInit.STONE_BRICKS_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_BRICKS_STEEP_ROOF = ITEMS.register(
      "stone_bricks_steep_roof", () -> new BlockItem((Block)BlockInit.STONE_BRICKS_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_BRICKS_UPPER_LOWER_ROOF = ITEMS.register(
      "stone_bricks_upper_lower_roof", () -> new BlockItem((Block)BlockInit.STONE_BRICKS_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_BRICKS_UPPER_STEEP_ROOF = ITEMS.register(
      "stone_bricks_upper_steep_roof", () -> new BlockItem((Block)BlockInit.STONE_BRICKS_UPPER_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRASS_ROOF = ITEMS.register("grass_roof", () -> new BlockItem((Block)BlockInit.GRASS_ROOF.get(), new Properties()));
   public static final DeferredItem<Item> GRASS_ATTIC_ROOF = ITEMS.register(
      "grass_attic_roof", () -> new BlockItem((Block)BlockInit.GRASS_ATTIC_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRASS_TOP_ROOF = ITEMS.register(
      "grass_top_roof", () -> new BlockItem((Block)BlockInit.GRASS_TOP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRASS_LOWER_ROOF = ITEMS.register(
      "grass_lower_roof", () -> new BlockItem((Block)BlockInit.GRASS_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRASS_STEEP_ROOF = ITEMS.register(
      "grass_steep_roof", () -> new BlockItem((Block)BlockInit.GRASS_STEEP_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRASS_UPPER_LOWER_ROOF = ITEMS.register(
      "grass_upper_lower_roof", () -> new BlockItem((Block)BlockInit.GRASS_UPPER_LOWER_ROOF.get(), new Properties())
   );
   public static final DeferredItem<Item> GRASS_UPPER_STEEP_ROOF = ITEMS.register(
      "grass_upper_steep_roof", () -> new BlockItem((Block)BlockInit.GRASS_UPPER_STEEP_ROOF.get(), new Properties())
   );
}

package com.mcwwindows.kikoz.init;

import com.mcwwindows.kikoz.util.FuelItemBlock;
import com.mcwwindows.kikoz.util.Hammer;
import com.mcwwindows.kikoz.util.Key;
import com.mcwwindows.kikoz.util.ToolTip;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;

public class ItemInit {
   public static final Items ITEMS = DeferredRegister.createItems("mcwwindows");
   public static final DeferredItem<Item> WINDOW_BASE = ITEMS.register("window_base", () -> new ToolTip(new Properties()));
   public static final DeferredItem<Item> WINDOW_HALF_BAR_BASE = ITEMS.register("window_half_bar_base", () -> new ToolTip(new Properties()));
   public static final DeferredItem<Item> WINDOW_CENTRE_BAR_BASE = ITEMS.register("window_centre_bar_base", () -> new ToolTip(new Properties()));
   public static final DeferredItem<Item> HAMMER = ITEMS.register("hammer", () -> new Hammer(new Properties()));
   public static final DeferredItem<Item> KEY = ITEMS.register("key", () -> new Key(new Properties()));
   public static final DeferredItem<Item> OAK_WINDOW = ITEMS.register(
      "oak_window", () -> new FuelItemBlock((Block)BlockInit.OAK_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_WINDOW2 = ITEMS.register(
      "oak_window2", () -> new FuelItemBlock((Block)BlockInit.OAK_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_FOUR_WINDOW = ITEMS.register(
      "oak_four_window", () -> new FuelItemBlock((Block)BlockInit.OAK_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_LOG_WINDOW = ITEMS.register(
      "stripped_oak_log_window", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_OAK_LOG_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_LOG_WINDOW2 = ITEMS.register(
      "stripped_oak_log_window2", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_OAK_LOG_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_LOG_FOUR_WINDOW = ITEMS.register(
      "stripped_oak_log_four_window", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_OAK_LOG_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_PLANK_WINDOW = ITEMS.register(
      "oak_plank_window", () -> new FuelItemBlock((Block)BlockInit.OAK_PLANK_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_PLANK_WINDOW2 = ITEMS.register(
      "oak_plank_window2", () -> new FuelItemBlock((Block)BlockInit.OAK_PLANK_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_PLANK_FOUR_WINDOW = ITEMS.register(
      "oak_plank_four_window", () -> new FuelItemBlock((Block)BlockInit.OAK_PLANK_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_WINDOW = ITEMS.register(
      "spruce_window", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_WINDOW2 = ITEMS.register(
      "spruce_window2", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_FOUR_WINDOW = ITEMS.register(
      "spruce_four_window", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_LOG_WINDOW = ITEMS.register(
      "stripped_spruce_log_window", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_SPRUCE_LOG_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_LOG_WINDOW2 = ITEMS.register(
      "stripped_spruce_log_window2", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_SPRUCE_LOG_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_LOG_FOUR_WINDOW = ITEMS.register(
      "stripped_spruce_log_four_window", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_SPRUCE_LOG_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_PLANK_WINDOW = ITEMS.register(
      "spruce_plank_window", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_PLANK_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_PLANK_WINDOW2 = ITEMS.register(
      "spruce_plank_window2", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_PLANK_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_PLANK_FOUR_WINDOW = ITEMS.register(
      "spruce_plank_four_window", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_PLANK_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_WINDOW = ITEMS.register(
      "birch_window", () -> new FuelItemBlock((Block)BlockInit.BIRCH_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_WINDOW2 = ITEMS.register(
      "birch_window2", () -> new FuelItemBlock((Block)BlockInit.BIRCH_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_FOUR_WINDOW = ITEMS.register(
      "birch_four_window", () -> new FuelItemBlock((Block)BlockInit.BIRCH_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_LOG_WINDOW = ITEMS.register(
      "stripped_birch_log_window", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_BIRCH_LOG_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_LOG_WINDOW2 = ITEMS.register(
      "stripped_birch_log_window2", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_BIRCH_LOG_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_LOG_FOUR_WINDOW = ITEMS.register(
      "stripped_birch_log_four_window", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_BIRCH_LOG_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_PLANK_WINDOW = ITEMS.register(
      "birch_plank_window", () -> new FuelItemBlock((Block)BlockInit.BIRCH_PLANK_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_PLANK_WINDOW2 = ITEMS.register(
      "birch_plank_window2", () -> new FuelItemBlock((Block)BlockInit.BIRCH_PLANK_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_PLANK_FOUR_WINDOW = ITEMS.register(
      "birch_plank_four_window", () -> new FuelItemBlock((Block)BlockInit.BIRCH_PLANK_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_WINDOW = ITEMS.register(
      "jungle_window", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_WINDOW2 = ITEMS.register(
      "jungle_window2", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_FOUR_WINDOW = ITEMS.register(
      "jungle_four_window", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_LOG_WINDOW = ITEMS.register(
      "stripped_jungle_log_window", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_JUNGLE_LOG_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_LOG_WINDOW2 = ITEMS.register(
      "stripped_jungle_log_window2", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_JUNGLE_LOG_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_LOG_FOUR_WINDOW = ITEMS.register(
      "stripped_jungle_log_four_window", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_JUNGLE_LOG_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_PLANK_WINDOW = ITEMS.register(
      "jungle_plank_window", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_PLANK_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_PLANK_WINDOW2 = ITEMS.register(
      "jungle_plank_window2", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_PLANK_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_PLANK_FOUR_WINDOW = ITEMS.register(
      "jungle_plank_four_window", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_PLANK_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_WINDOW = ITEMS.register(
      "acacia_window", () -> new FuelItemBlock((Block)BlockInit.ACACIA_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_WINDOW2 = ITEMS.register(
      "acacia_window2", () -> new FuelItemBlock((Block)BlockInit.ACACIA_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_FOUR_WINDOW = ITEMS.register(
      "acacia_four_window", () -> new FuelItemBlock((Block)BlockInit.ACACIA_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_LOG_WINDOW = ITEMS.register(
      "stripped_acacia_log_window", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_ACACIA_LOG_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_LOG_WINDOW2 = ITEMS.register(
      "stripped_acacia_log_window2", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_ACACIA_LOG_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_LOG_FOUR_WINDOW = ITEMS.register(
      "stripped_acacia_log_four_window", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_ACACIA_LOG_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_PLANK_WINDOW = ITEMS.register(
      "acacia_plank_window", () -> new FuelItemBlock((Block)BlockInit.ACACIA_PLANK_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_PLANK_WINDOW2 = ITEMS.register(
      "acacia_plank_window2", () -> new FuelItemBlock((Block)BlockInit.ACACIA_PLANK_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_PLANK_FOUR_WINDOW = ITEMS.register(
      "acacia_plank_four_window", () -> new FuelItemBlock((Block)BlockInit.ACACIA_PLANK_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_WINDOW = ITEMS.register(
      "dark_oak_window", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_WINDOW2 = ITEMS.register(
      "dark_oak_window2", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_FOUR_WINDOW = ITEMS.register(
      "dark_oak_four_window", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_LOG_WINDOW = ITEMS.register(
      "stripped_dark_oak_log_window", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_LOG_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_LOG_WINDOW2 = ITEMS.register(
      "stripped_dark_oak_log_window2", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_LOG_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_LOG_FOUR_WINDOW = ITEMS.register(
      "stripped_dark_oak_log_four_window", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_LOG_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_PLANK_WINDOW = ITEMS.register(
      "dark_oak_plank_window", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_PLANK_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_PLANK_WINDOW2 = ITEMS.register(
      "dark_oak_plank_window2", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_PLANK_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_PLANK_FOUR_WINDOW = ITEMS.register(
      "dark_oak_plank_four_window", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_PLANK_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_WINDOW = ITEMS.register(
      "mangrove_window", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_WINDOW2 = ITEMS.register(
      "mangrove_window2", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_FOUR_WINDOW = ITEMS.register(
      "mangrove_four_window", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_LOG_WINDOW = ITEMS.register(
      "stripped_mangrove_log_window", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_MANGROVE_LOG_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_LOG_WINDOW2 = ITEMS.register(
      "stripped_mangrove_log_window2", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_MANGROVE_LOG_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_LOG_FOUR_WINDOW = ITEMS.register(
      "stripped_mangrove_log_four_window", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_MANGROVE_LOG_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_PLANK_WINDOW = ITEMS.register(
      "mangrove_plank_window", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_PLANK_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_PLANK_WINDOW2 = ITEMS.register(
      "mangrove_plank_window2", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_PLANK_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_PLANK_FOUR_WINDOW = ITEMS.register(
      "mangrove_plank_four_window", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_PLANK_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_STEM_WINDOW = ITEMS.register(
      "crimson_stem_window", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_STEM_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_STEM_WINDOW2 = ITEMS.register(
      "crimson_stem_window2", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_STEM_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_STEM_FOUR_WINDOW = ITEMS.register(
      "crimson_stem_four_window", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_STEM_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_STEM_WINDOW = ITEMS.register(
      "stripped_crimson_stem_window", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_CRIMSON_STEM_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_STEM_WINDOW2 = ITEMS.register(
      "stripped_crimson_stem_window2", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_CRIMSON_STEM_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_STEM_FOUR_WINDOW = ITEMS.register(
      "stripped_crimson_stem_four_window", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_CRIMSON_STEM_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_PLANKS_WINDOW = ITEMS.register(
      "crimson_planks_window", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_PLANKS_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_PLANKS_WINDOW2 = ITEMS.register(
      "crimson_planks_window2", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_PLANKS_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_PLANKS_FOUR_WINDOW = ITEMS.register(
      "crimson_planks_four_window", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_PLANKS_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_STEM_WINDOW = ITEMS.register(
      "warped_stem_window", () -> new FuelItemBlock((Block)BlockInit.WARPED_STEM_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_STEM_WINDOW2 = ITEMS.register(
      "warped_stem_window2", () -> new FuelItemBlock((Block)BlockInit.WARPED_STEM_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_STEM_FOUR_WINDOW = ITEMS.register(
      "warped_stem_four_window", () -> new FuelItemBlock((Block)BlockInit.WARPED_STEM_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_STEM_WINDOW = ITEMS.register(
      "stripped_warped_stem_window", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_WARPED_STEM_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_STEM_WINDOW2 = ITEMS.register(
      "stripped_warped_stem_window2", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_WARPED_STEM_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_STEM_FOUR_WINDOW = ITEMS.register(
      "stripped_warped_stem_four_window", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_WARPED_STEM_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_PLANKS_WINDOW = ITEMS.register(
      "warped_planks_window", () -> new FuelItemBlock((Block)BlockInit.WARPED_PLANKS_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_PLANKS_WINDOW2 = ITEMS.register(
      "warped_planks_window2", () -> new FuelItemBlock((Block)BlockInit.WARPED_PLANKS_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_PLANKS_FOUR_WINDOW = ITEMS.register(
      "warped_planks_four_window", () -> new FuelItemBlock((Block)BlockInit.WARPED_PLANKS_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_WINDOW = ITEMS.register(
      "andesite_window", () -> new BlockItem((Block)BlockInit.ANDESITE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_WINDOW2 = ITEMS.register(
      "andesite_window2", () -> new BlockItem((Block)BlockInit.ANDESITE_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_FOUR_WINDOW = ITEMS.register(
      "andesite_four_window", () -> new BlockItem((Block)BlockInit.ANDESITE_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_WINDOW = ITEMS.register(
      "diorite_window", () -> new BlockItem((Block)BlockInit.DIORITE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_WINDOW2 = ITEMS.register(
      "diorite_window2", () -> new BlockItem((Block)BlockInit.DIORITE_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_FOUR_WINDOW = ITEMS.register(
      "diorite_four_window", () -> new BlockItem((Block)BlockInit.DIORITE_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_WINDOW = ITEMS.register(
      "granite_window", () -> new BlockItem((Block)BlockInit.GRANITE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_WINDOW2 = ITEMS.register(
      "granite_window2", () -> new BlockItem((Block)BlockInit.GRANITE_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_FOUR_WINDOW = ITEMS.register(
      "granite_four_window", () -> new BlockItem((Block)BlockInit.GRANITE_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_WINDOW = ITEMS.register(
      "stone_window", () -> new BlockItem((Block)BlockInit.STONE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_WINDOW2 = ITEMS.register(
      "stone_window2", () -> new BlockItem((Block)BlockInit.STONE_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_FOUR_WINDOW = ITEMS.register(
      "stone_four_window", () -> new BlockItem((Block)BlockInit.STONE_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_WINDOW = ITEMS.register(
      "blackstone_window", () -> new BlockItem((Block)BlockInit.BLACKSTONE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_WINDOW2 = ITEMS.register(
      "blackstone_window2", () -> new BlockItem((Block)BlockInit.BLACKSTONE_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_FOUR_WINDOW = ITEMS.register(
      "blackstone_four_window", () -> new BlockItem((Block)BlockInit.BLACKSTONE_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> PRISMARINE_WINDOW = ITEMS.register(
      "prismarine_window", () -> new BlockItem((Block)BlockInit.PRISMARINE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> PRISMARINE_WINDOW2 = ITEMS.register(
      "prismarine_window2", () -> new BlockItem((Block)BlockInit.PRISMARINE_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> PRISMARINE_FOUR_WINDOW = ITEMS.register(
      "prismarine_four_window", () -> new BlockItem((Block)BlockInit.PRISMARINE_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_WINDOW = ITEMS.register(
      "dark_prismarine_window", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_WINDOW2 = ITEMS.register(
      "dark_prismarine_window2", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_FOUR_WINDOW = ITEMS.register(
      "dark_prismarine_four_window", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_LOG_PARAPET = ITEMS.register(
      "oak_log_parapet", () -> new FuelItemBlock((Block)BlockInit.OAK_LOG_PARAPET.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_LOG_PARAPET = ITEMS.register(
      "spruce_log_parapet", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_LOG_PARAPET.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_LOG_PARAPET = ITEMS.register(
      "birch_log_parapet", () -> new FuelItemBlock((Block)BlockInit.BIRCH_LOG_PARAPET.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_LOG_PARAPET = ITEMS.register(
      "jungle_log_parapet", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_LOG_PARAPET.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_LOG_PARAPET = ITEMS.register(
      "acacia_log_parapet", () -> new FuelItemBlock((Block)BlockInit.ACACIA_LOG_PARAPET.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_LOG_PARAPET = ITEMS.register(
      "dark_oak_log_parapet", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_LOG_PARAPET.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_STEM_PARAPET = ITEMS.register(
      "crimson_stem_parapet", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_STEM_PARAPET.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_STEM_PARAPET = ITEMS.register(
      "warped_stem_parapet", () -> new FuelItemBlock((Block)BlockInit.WARPED_STEM_PARAPET.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_LOG_PARAPET = ITEMS.register(
      "mangrove_log_parapet", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_LOG_PARAPET.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_PLANK_PARAPET = ITEMS.register(
      "oak_plank_parapet", () -> new FuelItemBlock((Block)BlockInit.OAK_PLANK_PARAPET.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_PLANK_PARAPET = ITEMS.register(
      "spruce_plank_parapet", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_PLANK_PARAPET.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_PLANK_PARAPET = ITEMS.register(
      "birch_plank_parapet", () -> new FuelItemBlock((Block)BlockInit.BIRCH_PLANK_PARAPET.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_PLANK_PARAPET = ITEMS.register(
      "jungle_plank_parapet", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_PLANK_PARAPET.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_PLANK_PARAPET = ITEMS.register(
      "acacia_plank_parapet", () -> new FuelItemBlock((Block)BlockInit.ACACIA_PLANK_PARAPET.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_PLANK_PARAPET = ITEMS.register(
      "dark_oak_plank_parapet", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_PLANK_PARAPET.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_PLANK_PARAPET = ITEMS.register(
      "crimson_plank_parapet", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_PLANK_PARAPET.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_PLANK_PARAPET = ITEMS.register(
      "warped_plank_parapet", () -> new FuelItemBlock((Block)BlockInit.WARPED_PLANK_PARAPET.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_PLANK_PARAPET = ITEMS.register(
      "mangrove_plank_parapet", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_PLANK_PARAPET.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_PARAPET = ITEMS.register(
      "andesite_parapet", () -> new BlockItem((Block)BlockInit.ANDESITE_PARAPET.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_PARAPET = ITEMS.register(
      "diorite_parapet", () -> new BlockItem((Block)BlockInit.DIORITE_PARAPET.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_PARAPET = ITEMS.register(
      "granite_parapet", () -> new BlockItem((Block)BlockInit.GRANITE_PARAPET.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_PARAPET = ITEMS.register(
      "blackstone_parapet", () -> new BlockItem((Block)BlockInit.BLACKSTONE_PARAPET.get(), new Properties())
   );
   public static final DeferredItem<Item> PRISMARINE_PARAPET = ITEMS.register(
      "prismarine_parapet", () -> new BlockItem((Block)BlockInit.PRISMARINE_PARAPET.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_PARAPET = ITEMS.register(
      "dark_prismarine_parapet", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_PARAPET.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_BRICK_GOTHIC = ITEMS.register(
      "stone_brick_gothic", () -> new BlockItem((Block)BlockInit.STONE_BRICK_GOTHIC.get(), new Properties())
   );
   public static final DeferredItem<Item> END_BRICK_GOTHIC = ITEMS.register(
      "end_brick_gothic", () -> new BlockItem((Block)BlockInit.END_BRICK_GOTHIC.get(), new Properties())
   );
   public static final DeferredItem<Item> NETHER_BRICK_GOTHIC = ITEMS.register(
      "nether_brick_gothic", () -> new BlockItem((Block)BlockInit.NETHER_BRICK_GOTHIC.get(), new Properties())
   );
   public static final DeferredItem<Item> PRISMARINE_BRICK_GOTHIC = ITEMS.register(
      "prismarine_brick_gothic", () -> new BlockItem((Block)BlockInit.PRISMARINE_BRICK_GOTHIC.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_GOTHIC = ITEMS.register(
      "mud_brick_gothic", () -> new BlockItem((Block)BlockInit.MUD_BRICK_GOTHIC.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_BRICK_GOTHIC = ITEMS.register(
      "dark_prismarine_brick_gothic", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_BRICK_GOTHIC.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_BRICK_GOTHIC = ITEMS.register(
      "blackstone_brick_gothic", () -> new BlockItem((Block)BlockInit.BLACKSTONE_BRICK_GOTHIC.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_BRICK_ARROW_SLIT = ITEMS.register(
      "stone_brick_arrow_slit", () -> new BlockItem((Block)BlockInit.STONE_BRICK_ARROW_SLIT.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLESTONE_ARROW_SLIT = ITEMS.register(
      "cobblestone_arrow_slit", () -> new BlockItem((Block)BlockInit.COBBLESTONE_ARROW_SLIT.get(), new Properties())
   );
   public static final DeferredItem<Item> NETHER_BRICK_ARROW_SLIT = ITEMS.register(
      "nether_brick_arrow_slit", () -> new BlockItem((Block)BlockInit.NETHER_BRICK_ARROW_SLIT.get(), new Properties())
   );
   public static final DeferredItem<Item> ENDER_BRICK_ARROW_SLIT = ITEMS.register(
      "ender_brick_arrow_slit", () -> new BlockItem((Block)BlockInit.ENDER_BRICK_ARROW_SLIT.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_ARROW_SLIT = ITEMS.register(
      "mud_brick_arrow_slit", () -> new BlockItem((Block)BlockInit.MUD_BRICK_ARROW_SLIT.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_BRICK_ARROW_SLIT = ITEMS.register(
      "blackstone_brick_arrow_slit", () -> new BlockItem((Block)BlockInit.BLACKSTONE_BRICK_ARROW_SLIT.get(), new Properties())
   );
   public static final DeferredItem<Item> PRISMARINE_BRICK_ARROW_SLIT = ITEMS.register(
      "prismarine_brick_arrow_slit", () -> new BlockItem((Block)BlockInit.PRISMARINE_BRICK_ARROW_SLIT.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_BRICK_ARROW_SLIT = ITEMS.register(
      "dark_prismarine_brick_arrow_slit", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_BRICK_ARROW_SLIT.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_BLINDS = ITEMS.register(
      "oak_blinds", () -> new FuelItemBlock((Block)BlockInit.OAK_BLINDS.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_BLINDS = ITEMS.register(
      "spruce_blinds", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_BLINDS.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_BLINDS = ITEMS.register(
      "birch_blinds", () -> new FuelItemBlock((Block)BlockInit.BIRCH_BLINDS.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_BLINDS = ITEMS.register(
      "jungle_blinds", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_BLINDS.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_BLINDS = ITEMS.register(
      "acacia_blinds", () -> new FuelItemBlock((Block)BlockInit.ACACIA_BLINDS.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_BLINDS = ITEMS.register(
      "dark_oak_blinds", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_BLINDS.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_BLINDS = ITEMS.register(
      "crimson_blinds", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_BLINDS.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_BLINDS = ITEMS.register(
      "warped_blinds", () -> new FuelItemBlock((Block)BlockInit.WARPED_BLINDS.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_BLINDS = ITEMS.register(
      "mangrove_blinds", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_BLINDS.get(), new Properties())
   );
   public static final DeferredItem<Item> WHITE_MOSAIC_GLASS = ITEMS.register(
      "white_mosaic_glass", () -> new BlockItem((Block)BlockInit.WHITE_MOSAIC_GLASS.get(), new Properties())
   );
   public static final DeferredItem<Item> ORANGE_MOSAIC_GLASS = ITEMS.register(
      "orange_mosaic_glass", () -> new BlockItem((Block)BlockInit.ORANGE_MOSAIC_GLASS.get(), new Properties())
   );
   public static final DeferredItem<Item> MAGENTA_MOSAIC_GLASS = ITEMS.register(
      "magenta_mosaic_glass", () -> new BlockItem((Block)BlockInit.MAGENTA_MOSAIC_GLASS.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_BLUE_MOSAIC_GLASS = ITEMS.register(
      "light_blue_mosaic_glass", () -> new BlockItem((Block)BlockInit.LIGHT_BLUE_MOSAIC_GLASS.get(), new Properties())
   );
   public static final DeferredItem<Item> YELLOW_MOSAIC_GLASS = ITEMS.register(
      "yellow_mosaic_glass", () -> new BlockItem((Block)BlockInit.YELLOW_MOSAIC_GLASS.get(), new Properties())
   );
   public static final DeferredItem<Item> LIME_MOSAIC_GLASS = ITEMS.register(
      "lime_mosaic_glass", () -> new BlockItem((Block)BlockInit.LIME_MOSAIC_GLASS.get(), new Properties())
   );
   public static final DeferredItem<Item> PINK_MOSAIC_GLASS = ITEMS.register(
      "pink_mosaic_glass", () -> new BlockItem((Block)BlockInit.PINK_MOSAIC_GLASS.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_MOSAIC_GLASS = ITEMS.register(
      "gray_mosaic_glass", () -> new BlockItem((Block)BlockInit.GRAY_MOSAIC_GLASS.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_MOSAIC_GLASS = ITEMS.register(
      "light_gray_mosaic_glass", () -> new BlockItem((Block)BlockInit.LIGHT_GRAY_MOSAIC_GLASS.get(), new Properties())
   );
   public static final DeferredItem<Item> CYAN_MOSAIC_GLASS = ITEMS.register(
      "cyan_mosaic_glass", () -> new BlockItem((Block)BlockInit.CYAN_MOSAIC_GLASS.get(), new Properties())
   );
   public static final DeferredItem<Item> PURPLE_MOSAIC_GLASS = ITEMS.register(
      "purple_mosaic_glass", () -> new BlockItem((Block)BlockInit.PURPLE_MOSAIC_GLASS.get(), new Properties())
   );
   public static final DeferredItem<Item> BLUE_MOSAIC_GLASS = ITEMS.register(
      "blue_mosaic_glass", () -> new BlockItem((Block)BlockInit.BLUE_MOSAIC_GLASS.get(), new Properties())
   );
   public static final DeferredItem<Item> BROWN_MOSAIC_GLASS = ITEMS.register(
      "brown_mosaic_glass", () -> new BlockItem((Block)BlockInit.BROWN_MOSAIC_GLASS.get(), new Properties())
   );
   public static final DeferredItem<Item> GREEN_MOSAIC_GLASS = ITEMS.register(
      "green_mosaic_glass", () -> new BlockItem((Block)BlockInit.GREEN_MOSAIC_GLASS.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_MOSAIC_GLASS = ITEMS.register(
      "red_mosaic_glass", () -> new BlockItem((Block)BlockInit.RED_MOSAIC_GLASS.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACK_MOSAIC_GLASS = ITEMS.register(
      "black_mosaic_glass", () -> new BlockItem((Block)BlockInit.BLACK_MOSAIC_GLASS.get(), new Properties())
   );
   public static final DeferredItem<Item> WHITE_MOSAIC_GLASS_PANE = ITEMS.register(
      "white_mosaic_glass_pane", () -> new BlockItem((Block)BlockInit.WHITE_MOSAIC_GLASS_PANE.get(), new Properties())
   );
   public static final DeferredItem<Item> ORANGE_MOSAIC_GLASS_PANE = ITEMS.register(
      "orange_mosaic_glass_pane", () -> new BlockItem((Block)BlockInit.ORANGE_MOSAIC_GLASS_PANE.get(), new Properties())
   );
   public static final DeferredItem<Item> MAGENTA_MOSAIC_GLASS_PANE = ITEMS.register(
      "magenta_mosaic_glass_pane", () -> new BlockItem((Block)BlockInit.MAGENTA_MOSAIC_GLASS_PANE.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_BLUE_MOSAIC_GLASS_PANE = ITEMS.register(
      "light_blue_mosaic_glass_pane", () -> new BlockItem((Block)BlockInit.LIGHT_BLUE_MOSAIC_GLASS_PANE.get(), new Properties())
   );
   public static final DeferredItem<Item> YELLOW_MOSAIC_GLASS_PANE = ITEMS.register(
      "yellow_mosaic_glass_pane", () -> new BlockItem((Block)BlockInit.YELLOW_MOSAIC_GLASS_PANE.get(), new Properties())
   );
   public static final DeferredItem<Item> LIME_MOSAIC_GLASS_PANE = ITEMS.register(
      "lime_mosaic_glass_pane", () -> new BlockItem((Block)BlockInit.LIME_MOSAIC_GLASS_PANE.get(), new Properties())
   );
   public static final DeferredItem<Item> PINK_MOSAIC_GLASS_PANE = ITEMS.register(
      "pink_mosaic_glass_pane", () -> new BlockItem((Block)BlockInit.PINK_MOSAIC_GLASS_PANE.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_MOSAIC_GLASS_PANE = ITEMS.register(
      "gray_mosaic_glass_pane", () -> new BlockItem((Block)BlockInit.GRAY_MOSAIC_GLASS_PANE.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_MOSAIC_GLASS_PANE = ITEMS.register(
      "light_gray_mosaic_glass_pane", () -> new BlockItem((Block)BlockInit.LIGHT_GRAY_MOSAIC_GLASS_PANE.get(), new Properties())
   );
   public static final DeferredItem<Item> CYAN_MOSAIC_GLASS_PANE = ITEMS.register(
      "cyan_mosaic_glass_pane", () -> new BlockItem((Block)BlockInit.CYAN_MOSAIC_GLASS_PANE.get(), new Properties())
   );
   public static final DeferredItem<Item> PURPLE_MOSAIC_GLASS_PANE = ITEMS.register(
      "purple_mosaic_glass_pane", () -> new BlockItem((Block)BlockInit.PURPLE_MOSAIC_GLASS_PANE.get(), new Properties())
   );
   public static final DeferredItem<Item> BLUE_MOSAIC_GLASS_PANE = ITEMS.register(
      "blue_mosaic_glass_pane", () -> new BlockItem((Block)BlockInit.BLUE_MOSAIC_GLASS_PANE.get(), new Properties())
   );
   public static final DeferredItem<Item> BROWN_MOSAIC_GLASS_PANE = ITEMS.register(
      "brown_mosaic_glass_pane", () -> new BlockItem((Block)BlockInit.BROWN_MOSAIC_GLASS_PANE.get(), new Properties())
   );
   public static final DeferredItem<Item> GREEN_MOSAIC_GLASS_PANE = ITEMS.register(
      "green_mosaic_glass_pane", () -> new BlockItem((Block)BlockInit.GREEN_MOSAIC_GLASS_PANE.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_MOSAIC_GLASS_PANE = ITEMS.register(
      "red_mosaic_glass_pane", () -> new BlockItem((Block)BlockInit.RED_MOSAIC_GLASS_PANE.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACK_MOSAIC_GLASS_PANE = ITEMS.register(
      "black_mosaic_glass_pane", () -> new BlockItem((Block)BlockInit.BLACK_MOSAIC_GLASS_PANE.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_SHUTTER = ITEMS.register(
      "oak_shutter", () -> new FuelItemBlock((Block)BlockInit.OAK_SHUTTER.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_SHUTTER = ITEMS.register(
      "spruce_shutter", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_SHUTTER.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_SHUTTER = ITEMS.register(
      "birch_shutter", () -> new FuelItemBlock((Block)BlockInit.BIRCH_SHUTTER.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_SHUTTER = ITEMS.register(
      "jungle_shutter", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_SHUTTER.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_SHUTTER = ITEMS.register(
      "acacia_shutter", () -> new FuelItemBlock((Block)BlockInit.ACACIA_SHUTTER.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_SHUTTER = ITEMS.register(
      "dark_oak_shutter", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_SHUTTER.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_SHUTTER = ITEMS.register(
      "crimson_shutter", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_SHUTTER.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_SHUTTER = ITEMS.register(
      "warped_shutter", () -> new FuelItemBlock((Block)BlockInit.WARPED_SHUTTER.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_SHUTTER = ITEMS.register(
      "mangrove_shutter", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_SHUTTER.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_SHUTTER = ITEMS.register(
      "bamboo_shutter", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_SHUTTER.get(), new Properties())
   );
   public static final DeferredItem<Item> IRON_SHUTTER = ITEMS.register(
      "iron_shutter", () -> new BlockItem((Block)BlockInit.IRON_SHUTTER.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_LOUVERED_SHUTTER = ITEMS.register(
      "oak_louvered_shutter", () -> new FuelItemBlock((Block)BlockInit.OAK_LOUVERED_SHUTTER.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_LOUVERED_SHUTTER = ITEMS.register(
      "spruce_louvered_shutter", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_LOUVERED_SHUTTER.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_LOUVERED_SHUTTER = ITEMS.register(
      "birch_louvered_shutter", () -> new FuelItemBlock((Block)BlockInit.BIRCH_LOUVERED_SHUTTER.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_LOUVERED_SHUTTER = ITEMS.register(
      "jungle_louvered_shutter", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_LOUVERED_SHUTTER.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_LOUVERED_SHUTTER = ITEMS.register(
      "acacia_louvered_shutter", () -> new FuelItemBlock((Block)BlockInit.ACACIA_LOUVERED_SHUTTER.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_LOUVERED_SHUTTER = ITEMS.register(
      "dark_oak_louvered_shutter", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_LOUVERED_SHUTTER.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_LOUVERED_SHUTTER = ITEMS.register(
      "crimson_louvered_shutter", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_LOUVERED_SHUTTER.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_LOUVERED_SHUTTER = ITEMS.register(
      "warped_louvered_shutter", () -> new FuelItemBlock((Block)BlockInit.WARPED_LOUVERED_SHUTTER.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_LOUVERED_SHUTTER = ITEMS.register(
      "mangrove_louvered_shutter", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_LOUVERED_SHUTTER.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_LOUVERED_SHUTTER = ITEMS.register(
      "diorite_louvered_shutter", () -> new BlockItem((Block)BlockInit.DIORITE_LOUVERED_SHUTTER.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_LOUVERED_SHUTTER = ITEMS.register(
      "andesite_louvered_shutter", () -> new BlockItem((Block)BlockInit.ANDESITE_LOUVERED_SHUTTER.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_LOUVERED_SHUTTER = ITEMS.register(
      "granite_louvered_shutter", () -> new BlockItem((Block)BlockInit.GRANITE_LOUVERED_SHUTTER.get(), new Properties())
   );
   public static final DeferredItem<Item> WHITE_CURTAIN = ITEMS.register(
      "white_curtain", () -> new FuelItemBlock((Block)BlockInit.WHITE_CURTAIN.get(), new Properties())
   );
   public static final DeferredItem<Item> ORANGE_CURTAIN = ITEMS.register(
      "orange_curtain", () -> new FuelItemBlock((Block)BlockInit.ORANGE_CURTAIN.get(), new Properties())
   );
   public static final DeferredItem<Item> MAGENTA_CURTAIN = ITEMS.register(
      "magenta_curtain", () -> new FuelItemBlock((Block)BlockInit.MAGENTA_CURTAIN.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_BLUE_CURTAIN = ITEMS.register(
      "light_blue_curtain", () -> new FuelItemBlock((Block)BlockInit.LIGHT_BLUE_CURTAIN.get(), new Properties())
   );
   public static final DeferredItem<Item> YELLOW_CURTAIN = ITEMS.register(
      "yellow_curtain", () -> new FuelItemBlock((Block)BlockInit.YELLOW_CURTAIN.get(), new Properties())
   );
   public static final DeferredItem<Item> LIME_CURTAIN = ITEMS.register(
      "lime_curtain", () -> new FuelItemBlock((Block)BlockInit.LIME_CURTAIN.get(), new Properties())
   );
   public static final DeferredItem<Item> PINK_CURTAIN = ITEMS.register(
      "pink_curtain", () -> new FuelItemBlock((Block)BlockInit.PINK_CURTAIN.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_CURTAIN = ITEMS.register(
      "gray_curtain", () -> new FuelItemBlock((Block)BlockInit.GRAY_CURTAIN.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_CURTAIN = ITEMS.register(
      "light_gray_curtain", () -> new FuelItemBlock((Block)BlockInit.LIGHT_GRAY_CURTAIN.get(), new Properties())
   );
   public static final DeferredItem<Item> CYAN_CURTAIN = ITEMS.register(
      "cyan_curtain", () -> new FuelItemBlock((Block)BlockInit.CYAN_CURTAIN.get(), new Properties())
   );
   public static final DeferredItem<Item> PURPLE_CURTAIN = ITEMS.register(
      "purple_curtain", () -> new FuelItemBlock((Block)BlockInit.PURPLE_CURTAIN.get(), new Properties())
   );
   public static final DeferredItem<Item> BLUE_CURTAIN = ITEMS.register(
      "blue_curtain", () -> new FuelItemBlock((Block)BlockInit.BLUE_CURTAIN.get(), new Properties())
   );
   public static final DeferredItem<Item> BROWN_CURTAIN = ITEMS.register(
      "brown_curtain", () -> new FuelItemBlock((Block)BlockInit.BROWN_CURTAIN.get(), new Properties())
   );
   public static final DeferredItem<Item> GREEN_CURTAIN = ITEMS.register(
      "green_curtain", () -> new FuelItemBlock((Block)BlockInit.GREEN_CURTAIN.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_CURTAIN = ITEMS.register(
      "red_curtain", () -> new FuelItemBlock((Block)BlockInit.RED_CURTAIN.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACK_CURTAIN = ITEMS.register(
      "black_curtain", () -> new FuelItemBlock((Block)BlockInit.BLACK_CURTAIN.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_PANE_WINDOW = ITEMS.register(
      "oak_pane_window", () -> new FuelItemBlock((Block)BlockInit.OAK_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_OAK_PANE_WINDOW = ITEMS.register(
      "stripped_oak_pane_window", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_OAK_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_PLANK_PANE_WINDOW = ITEMS.register(
      "oak_plank_pane_window", () -> new FuelItemBlock((Block)BlockInit.OAK_PLANK_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_PANE_WINDOW = ITEMS.register(
      "birch_pane_window", () -> new FuelItemBlock((Block)BlockInit.BIRCH_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_BIRCH_PANE_WINDOW = ITEMS.register(
      "stripped_birch_pane_window", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_BIRCH_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_PLANK_PANE_WINDOW = ITEMS.register(
      "birch_plank_pane_window", () -> new FuelItemBlock((Block)BlockInit.BIRCH_PLANK_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_PANE_WINDOW = ITEMS.register(
      "spruce_pane_window", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_SPRUCE_PANE_WINDOW = ITEMS.register(
      "stripped_spruce_pane_window", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_SPRUCE_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_PLANK_PANE_WINDOW = ITEMS.register(
      "spruce_plank_pane_window", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_PLANK_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_PANE_WINDOW = ITEMS.register(
      "jungle_pane_window", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_JUNGLE_PANE_WINDOW = ITEMS.register(
      "stripped_jungle_pane_window", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_JUNGLE_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_PLANK_PANE_WINDOW = ITEMS.register(
      "jungle_plank_pane_window", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_PLANK_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_PANE_WINDOW = ITEMS.register(
      "acacia_pane_window", () -> new FuelItemBlock((Block)BlockInit.ACACIA_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_ACACIA_PANE_WINDOW = ITEMS.register(
      "stripped_acacia_pane_window", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_ACACIA_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_PLANK_PANE_WINDOW = ITEMS.register(
      "acacia_plank_pane_window", () -> new FuelItemBlock((Block)BlockInit.ACACIA_PLANK_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_PANE_WINDOW = ITEMS.register(
      "dark_oak_pane_window", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_DARK_OAK_PANE_WINDOW = ITEMS.register(
      "stripped_dark_oak_pane_window", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_DARK_OAK_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_PLANK_PANE_WINDOW = ITEMS.register(
      "dark_oak_plank_pane_window", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_PLANK_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_PANE_WINDOW = ITEMS.register(
      "crimson_pane_window", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CRIMSON_PANE_WINDOW = ITEMS.register(
      "stripped_crimson_pane_window", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_CRIMSON_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_PLANK_PANE_WINDOW = ITEMS.register(
      "crimson_plank_pane_window", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_PLANK_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_PANE_WINDOW = ITEMS.register(
      "warped_pane_window", () -> new FuelItemBlock((Block)BlockInit.WARPED_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_WARPED_PANE_WINDOW = ITEMS.register(
      "stripped_warped_pane_window", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_WARPED_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_PLANK_PANE_WINDOW = ITEMS.register(
      "warped_plank_pane_window", () -> new FuelItemBlock((Block)BlockInit.WARPED_PLANK_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_PANE_WINDOW = ITEMS.register(
      "mangrove_pane_window", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_MANGROVE_PANE_WINDOW = ITEMS.register(
      "stripped_mangrove_pane_window", () -> new FuelItemBlock((Block)BlockInit.STRIPPED_MANGROVE_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_PLANK_PANE_WINDOW = ITEMS.register(
      "mangrove_plank_pane_window", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_PLANK_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_PANE_WINDOW = ITEMS.register(
      "andesite_pane_window", () -> new BlockItem((Block)BlockInit.ANDESITE_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_PANE_WINDOW = ITEMS.register(
      "diorite_pane_window", () -> new BlockItem((Block)BlockInit.DIORITE_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_PANE_WINDOW = ITEMS.register(
      "granite_pane_window", () -> new BlockItem((Block)BlockInit.GRANITE_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_PANE_WINDOW = ITEMS.register(
      "stone_pane_window", () -> new BlockItem((Block)BlockInit.STONE_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_PANE_WINDOW = ITEMS.register(
      "blackstone_pane_window", () -> new BlockItem((Block)BlockInit.BLACKSTONE_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> PRISMARINE_PANE_WINDOW = ITEMS.register(
      "prismarine_pane_window", () -> new BlockItem((Block)BlockInit.PRISMARINE_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_PANE_WINDOW = ITEMS.register(
      "dark_prismarine_pane_window", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_WINDOW = ITEMS.register(
      "cherry_window", () -> new BlockItem((Block)BlockInit.CHERRY_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_WINDOW2 = ITEMS.register(
      "cherry_window2", () -> new BlockItem((Block)BlockInit.CHERRY_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_FOUR_WINDOW = ITEMS.register(
      "cherry_four_window", () -> new BlockItem((Block)BlockInit.CHERRY_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_LOG_WINDOW = ITEMS.register(
      "stripped_cherry_log_window", () -> new BlockItem((Block)BlockInit.STRIPPED_CHERRY_LOG_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_LOG_WINDOW2 = ITEMS.register(
      "stripped_cherry_log_window2", () -> new BlockItem((Block)BlockInit.STRIPPED_CHERRY_LOG_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_LOG_FOUR_WINDOW = ITEMS.register(
      "stripped_cherry_log_four_window", () -> new BlockItem((Block)BlockInit.STRIPPED_CHERRY_LOG_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_PLANK_WINDOW = ITEMS.register(
      "cherry_plank_window", () -> new BlockItem((Block)BlockInit.CHERRY_PLANK_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_PLANK_WINDOW2 = ITEMS.register(
      "cherry_plank_window2", () -> new BlockItem((Block)BlockInit.CHERRY_PLANK_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_PLANK_FOUR_WINDOW = ITEMS.register(
      "cherry_plank_four_window", () -> new BlockItem((Block)BlockInit.CHERRY_PLANK_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_LOG_PARAPET = ITEMS.register(
      "cherry_log_parapet", () -> new BlockItem((Block)BlockInit.CHERRY_LOG_PARAPET.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_PLANK_PARAPET = ITEMS.register(
      "cherry_plank_parapet", () -> new BlockItem((Block)BlockInit.CHERRY_PLANK_PARAPET.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_BLINDS = ITEMS.register(
      "cherry_blinds", () -> new BlockItem((Block)BlockInit.CHERRY_BLINDS.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_SHUTTER = ITEMS.register(
      "cherry_shutter", () -> new BlockItem((Block)BlockInit.CHERRY_SHUTTER.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_LOUVERED_SHUTTER = ITEMS.register(
      "cherry_louvered_shutter", () -> new BlockItem((Block)BlockInit.CHERRY_LOUVERED_SHUTTER.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_PANE_WINDOW = ITEMS.register(
      "cherry_pane_window", () -> new BlockItem((Block)BlockInit.CHERRY_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPPED_CHERRY_PANE_WINDOW = ITEMS.register(
      "stripped_cherry_pane_window", () -> new BlockItem((Block)BlockInit.STRIPPED_CHERRY_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_PLANK_PANE_WINDOW = ITEMS.register(
      "cherry_plank_pane_window", () -> new BlockItem((Block)BlockInit.CHERRY_PLANK_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> ONE_WAY_GLASS = ITEMS.register(
      "one_way_glass", () -> new BlockItem((Block)BlockInit.ONE_WAY_GLASS.get(), new Properties())
   );
   public static final DeferredItem<Item> ONE_WAY_GLASS_PANE = ITEMS.register(
      "one_way_glass_pane", () -> new BlockItem((Block)BlockInit.ONE_WAY_GLASS_PANE.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_WINDOW = ITEMS.register(
      "deepslate_window", () -> new BlockItem((Block)BlockInit.DEEPSLATE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_WINDOW2 = ITEMS.register(
      "deepslate_window2", () -> new BlockItem((Block)BlockInit.DEEPSLATE_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_FOUR_WINDOW = ITEMS.register(
      "deepslate_four_window", () -> new BlockItem((Block)BlockInit.DEEPSLATE_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_PANE_WINDOW = ITEMS.register(
      "deepslate_pane_window", () -> new BlockItem((Block)BlockInit.DEEPSLATE_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_WINDOW = ITEMS.register(
      "sandstone_window", () -> new BlockItem((Block)BlockInit.SANDSTONE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_WINDOW2 = ITEMS.register(
      "sandstone_window2", () -> new BlockItem((Block)BlockInit.SANDSTONE_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_FOUR_WINDOW = ITEMS.register(
      "sandstone_four_window", () -> new BlockItem((Block)BlockInit.SANDSTONE_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_PANE_WINDOW = ITEMS.register(
      "sandstone_pane_window", () -> new BlockItem((Block)BlockInit.SANDSTONE_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_WINDOW = ITEMS.register(
      "red_sandstone_window", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_WINDOW2 = ITEMS.register(
      "red_sandstone_window2", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_FOUR_WINDOW = ITEMS.register(
      "red_sandstone_four_window", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_PANE_WINDOW = ITEMS.register(
      "red_sandstone_pane_window", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICKS_WINDOW = ITEMS.register(
      "bricks_window", () -> new BlockItem((Block)BlockInit.BRICKS_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICKS_WINDOW2 = ITEMS.register(
      "bricks_window2", () -> new BlockItem((Block)BlockInit.BRICKS_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICKS_FOUR_WINDOW = ITEMS.register(
      "bricks_four_window", () -> new BlockItem((Block)BlockInit.BRICKS_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICKS_PANE_WINDOW = ITEMS.register(
      "bricks_pane_window", () -> new BlockItem((Block)BlockInit.BRICKS_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> QUARTZ_WINDOW = ITEMS.register(
      "quartz_window", () -> new BlockItem((Block)BlockInit.QUARTZ_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> QUARTZ_WINDOW2 = ITEMS.register(
      "quartz_window2", () -> new BlockItem((Block)BlockInit.QUARTZ_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> QUARTZ_FOUR_WINDOW = ITEMS.register(
      "quartz_four_window", () -> new BlockItem((Block)BlockInit.QUARTZ_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> QUARTZ_PANE_WINDOW = ITEMS.register(
      "quartz_pane_window", () -> new BlockItem((Block)BlockInit.QUARTZ_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> METAL_WINDOW = ITEMS.register(
      "metal_window", () -> new BlockItem((Block)BlockInit.METAL_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> METAL_WINDOW2 = ITEMS.register(
      "metal_window2", () -> new BlockItem((Block)BlockInit.METAL_WINDOW2.get(), new Properties())
   );
   public static final DeferredItem<Item> METAL_FOUR_WINDOW = ITEMS.register(
      "metal_four_window", () -> new BlockItem((Block)BlockInit.METAL_FOUR_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> METAL_PANE_WINDOW = ITEMS.register(
      "metal_pane_window", () -> new BlockItem((Block)BlockInit.METAL_PANE_WINDOW.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_CURTAIN_ROD = ITEMS.register(
      "oak_curtain_rod", () -> new BlockItem((Block)BlockInit.OAK_CURTAIN_ROD.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_CURTAIN_ROD = ITEMS.register(
      "spruce_curtain_rod", () -> new BlockItem((Block)BlockInit.SPRUCE_CURTAIN_ROD.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_CURTAIN_ROD = ITEMS.register(
      "birch_curtain_rod", () -> new BlockItem((Block)BlockInit.BIRCH_CURTAIN_ROD.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_CURTAIN_ROD = ITEMS.register(
      "jungle_curtain_rod", () -> new BlockItem((Block)BlockInit.JUNGLE_CURTAIN_ROD.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_CURTAIN_ROD = ITEMS.register(
      "acacia_curtain_rod", () -> new BlockItem((Block)BlockInit.ACACIA_CURTAIN_ROD.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_CURTAIN_ROD = ITEMS.register(
      "dark_oak_curtain_rod", () -> new BlockItem((Block)BlockInit.DARK_OAK_CURTAIN_ROD.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_CURTAIN_ROD = ITEMS.register(
      "crimson_curtain_rod", () -> new BlockItem((Block)BlockInit.CRIMSON_CURTAIN_ROD.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_CURTAIN_ROD = ITEMS.register(
      "warped_curtain_rod", () -> new BlockItem((Block)BlockInit.WARPED_CURTAIN_ROD.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_CURTAIN_ROD = ITEMS.register(
      "mangrove_curtain_rod", () -> new BlockItem((Block)BlockInit.MANGROVE_CURTAIN_ROD.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_CURTAIN_ROD = ITEMS.register(
      "cherry_curtain_rod", () -> new BlockItem((Block)BlockInit.CHERRY_CURTAIN_ROD.get(), new Properties())
   );
   public static final DeferredItem<Item> METAL_CURTAIN_ROD = ITEMS.register(
      "metal_curtain_rod", () -> new BlockItem((Block)BlockInit.METAL_CURTAIN_ROD.get(), new Properties())
   );
   public static final DeferredItem<Item> GOLDEN_CURTAIN_ROD = ITEMS.register(
      "golden_curtain_rod", () -> new BlockItem((Block)BlockInit.GOLDEN_CURTAIN_ROD.get(), new Properties())
   );
}

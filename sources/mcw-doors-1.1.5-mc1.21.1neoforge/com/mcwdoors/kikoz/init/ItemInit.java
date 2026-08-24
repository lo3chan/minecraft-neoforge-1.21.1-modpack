package com.mcwdoors.kikoz.init;

import com.mcwdoors.kikoz.util.FuelItemBlock;
import com.mcwdoors.kikoz.util.GarageToolTip;
import com.mcwdoors.kikoz.util.MetalToolTip;
import com.mcwdoors.kikoz.util.ToolTip;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;

public class ItemInit {
   public static final Items ITEMS = DeferredRegister.createItems("mcwdoors");
   public static final DeferredItem<Item> PRINT_OAK = ITEMS.register("print_oak", () -> new ToolTip(new Properties()));
   public static final DeferredItem<Item> PRINT_SPRUCE = ITEMS.register("print_spruce", () -> new ToolTip(new Properties()));
   public static final DeferredItem<Item> PRINT_BIRCH = ITEMS.register("print_birch", () -> new ToolTip(new Properties()));
   public static final DeferredItem<Item> PRINT_JUNGLE = ITEMS.register("print_jungle", () -> new ToolTip(new Properties()));
   public static final DeferredItem<Item> PRINT_ACACIA = ITEMS.register("print_acacia", () -> new ToolTip(new Properties()));
   public static final DeferredItem<Item> PRINT_DARK_OAK = ITEMS.register("print_dark_oak", () -> new ToolTip(new Properties()));
   public static final DeferredItem<Item> PRINT_MYSTIC = ITEMS.register("print_mystic", () -> new ToolTip(new Properties()));
   public static final DeferredItem<Item> PRINT_NETHER = ITEMS.register("print_nether", () -> new ToolTip(new Properties()));
   public static final DeferredItem<Item> PRINT_SWAMP = ITEMS.register("print_swamp", () -> new ToolTip(new Properties()));
   public static final DeferredItem<Item> PRINT_BAMBOO = ITEMS.register("print_bamboo", () -> new ToolTip(new Properties()));
   public static final DeferredItem<Item> PRINT_WAFFLE = ITEMS.register("print_waffle", () -> new ToolTip(new Properties()));
   public static final DeferredItem<Item> OAK_JAPANESE_DOOR = ITEMS.register(
      "oak_japanese_door", () -> new FuelItemBlock((Block)BlockInit.OAK_JAPANESE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_JAPANESE_DOOR = ITEMS.register(
      "spruce_japanese_door", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_JAPANESE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_JAPANESE_DOOR = ITEMS.register(
      "birch_japanese_door", () -> new FuelItemBlock((Block)BlockInit.BIRCH_JAPANESE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_JAPANESE_DOOR = ITEMS.register(
      "jungle_japanese_door", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_JAPANESE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_JAPANESE_DOOR = ITEMS.register(
      "acacia_japanese_door", () -> new FuelItemBlock((Block)BlockInit.ACACIA_JAPANESE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_JAPANESE_DOOR = ITEMS.register(
      "dark_oak_japanese_door", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_JAPANESE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_JAPANESE_DOOR = ITEMS.register(
      "crimson_japanese_door", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_JAPANESE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_JAPANESE_DOOR = ITEMS.register(
      "warped_japanese_door", () -> new FuelItemBlock((Block)BlockInit.WARPED_JAPANESE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_JAPANESE_DOOR = ITEMS.register(
      "mangrove_japanese_door", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_JAPANESE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_JAPANESE_DOOR = ITEMS.register(
      "bamboo_japanese_door", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_JAPANESE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_JAPANESE2_DOOR = ITEMS.register(
      "oak_japanese2_door", () -> new FuelItemBlock((Block)BlockInit.OAK_JAPANESE2_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_JAPANESE2_DOOR = ITEMS.register(
      "spruce_japanese2_door", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_JAPANESE2_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_JAPANESE2_DOOR = ITEMS.register(
      "birch_japanese2_door", () -> new FuelItemBlock((Block)BlockInit.BIRCH_JAPANESE2_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_JAPANESE2_DOOR = ITEMS.register(
      "jungle_japanese2_door", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_JAPANESE2_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_JAPANESE2_DOOR = ITEMS.register(
      "acacia_japanese2_door", () -> new FuelItemBlock((Block)BlockInit.ACACIA_JAPANESE2_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_JAPANESE2_DOOR = ITEMS.register(
      "dark_oak_japanese2_door", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_JAPANESE2_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_JAPANESE2_DOOR = ITEMS.register(
      "crimson_japanese2_door", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_JAPANESE2_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_JAPANESE2_DOOR = ITEMS.register(
      "warped_japanese2_door", () -> new FuelItemBlock((Block)BlockInit.WARPED_JAPANESE2_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_JAPANESE2_DOOR = ITEMS.register(
      "mangrove_japanese2_door", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_JAPANESE2_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_JAPANESE2_DOOR = ITEMS.register(
      "bamboo_japanese2_door", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_JAPANESE2_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_BARN_DOOR = ITEMS.register(
      "oak_barn_door", () -> new FuelItemBlock((Block)BlockInit.OAK_BARN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_BARN_DOOR = ITEMS.register(
      "spruce_barn_door", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_BARN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_BARN_DOOR = ITEMS.register(
      "birch_barn_door", () -> new FuelItemBlock((Block)BlockInit.BIRCH_BARN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_BARN_DOOR = ITEMS.register(
      "jungle_barn_door", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_BARN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_BARN_DOOR = ITEMS.register(
      "acacia_barn_door", () -> new FuelItemBlock((Block)BlockInit.ACACIA_BARN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_BARN_DOOR = ITEMS.register(
      "dark_oak_barn_door", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_BARN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_BARN_DOOR = ITEMS.register(
      "crimson_barn_door", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_BARN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_BARN_DOOR = ITEMS.register(
      "warped_barn_door", () -> new FuelItemBlock((Block)BlockInit.WARPED_BARN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_BARN_DOOR = ITEMS.register(
      "mangrove_barn_door", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_BARN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_BARN_DOOR = ITEMS.register(
      "bamboo_barn_door", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_BARN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_BARN_GLASS_DOOR = ITEMS.register(
      "oak_barn_glass_door", () -> new FuelItemBlock((Block)BlockInit.OAK_BARN_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_BARN_GLASS_DOOR = ITEMS.register(
      "spruce_barn_glass_door", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_BARN_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_BARN_GLASS_DOOR = ITEMS.register(
      "birch_barn_glass_door", () -> new FuelItemBlock((Block)BlockInit.BIRCH_BARN_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_BARN_GLASS_DOOR = ITEMS.register(
      "jungle_barn_glass_door", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_BARN_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_BARN_GLASS_DOOR = ITEMS.register(
      "acacia_barn_glass_door", () -> new FuelItemBlock((Block)BlockInit.ACACIA_BARN_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_BARN_GLASS_DOOR = ITEMS.register(
      "dark_oak_barn_glass_door", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_BARN_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_BARN_GLASS_DOOR = ITEMS.register(
      "crimson_barn_glass_door", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_BARN_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_BARN_GLASS_DOOR = ITEMS.register(
      "warped_barn_glass_door", () -> new FuelItemBlock((Block)BlockInit.WARPED_BARN_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_BARN_GLASS_DOOR = ITEMS.register(
      "mangrove_barn_glass_door", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_BARN_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_BARN_GLASS_DOOR = ITEMS.register(
      "bamboo_barn_glass_door", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_BARN_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_MODERN_DOOR = ITEMS.register(
      "oak_modern_door", () -> new FuelItemBlock((Block)BlockInit.OAK_MODERN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_MODERN_DOOR = ITEMS.register(
      "spruce_modern_door", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_MODERN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_MODERN_DOOR = ITEMS.register(
      "birch_modern_door", () -> new FuelItemBlock((Block)BlockInit.BIRCH_MODERN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_MODERN_DOOR = ITEMS.register(
      "jungle_modern_door", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_MODERN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_MODERN_DOOR = ITEMS.register(
      "acacia_modern_door", () -> new FuelItemBlock((Block)BlockInit.ACACIA_MODERN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_MODERN_DOOR = ITEMS.register(
      "dark_oak_modern_door", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_MODERN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_MODERN_DOOR = ITEMS.register(
      "crimson_modern_door", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_MODERN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_MODERN_DOOR = ITEMS.register(
      "warped_modern_door", () -> new FuelItemBlock((Block)BlockInit.WARPED_MODERN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_MODERN_DOOR = ITEMS.register(
      "mangrove_modern_door", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_MODERN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_MODERN_DOOR = ITEMS.register(
      "bamboo_modern_door", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_MODERN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_COTTAGE_DOOR = ITEMS.register(
      "oak_cottage_door", () -> new FuelItemBlock((Block)BlockInit.OAK_COTTAGE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_COTTAGE_DOOR = ITEMS.register(
      "birch_cottage_door", () -> new FuelItemBlock((Block)BlockInit.BIRCH_COTTAGE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_COTTAGE_DOOR = ITEMS.register(
      "jungle_cottage_door", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_COTTAGE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_COTTAGE_DOOR = ITEMS.register(
      "acacia_cottage_door", () -> new FuelItemBlock((Block)BlockInit.ACACIA_COTTAGE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_COTTAGE_DOOR = ITEMS.register(
      "dark_oak_cottage_door", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_COTTAGE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_COTTAGE_DOOR = ITEMS.register(
      "crimson_cottage_door", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_COTTAGE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_COTTAGE_DOOR = ITEMS.register(
      "warped_cottage_door", () -> new FuelItemBlock((Block)BlockInit.WARPED_COTTAGE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_COTTAGE_DOOR = ITEMS.register(
      "mangrove_cottage_door", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_COTTAGE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_COTTAGE_DOOR = ITEMS.register(
      "bamboo_cottage_door", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_COTTAGE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_CLASSIC_DOOR = ITEMS.register(
      "spruce_classic_door", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_CLASSIC_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_CLASSIC_DOOR = ITEMS.register(
      "birch_classic_door", () -> new FuelItemBlock((Block)BlockInit.BIRCH_CLASSIC_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_CLASSIC_DOOR = ITEMS.register(
      "jungle_classic_door", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_CLASSIC_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_CLASSIC_DOOR = ITEMS.register(
      "acacia_classic_door", () -> new FuelItemBlock((Block)BlockInit.ACACIA_CLASSIC_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_CLASSIC_DOOR = ITEMS.register(
      "dark_oak_classic_door", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_CLASSIC_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_CLASSIC_DOOR = ITEMS.register(
      "crimson_classic_door", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_CLASSIC_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_CLASSIC_DOOR = ITEMS.register(
      "warped_classic_door", () -> new FuelItemBlock((Block)BlockInit.WARPED_CLASSIC_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_CLASSIC_DOOR = ITEMS.register(
      "mangrove_classic_door", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_CLASSIC_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_CLASSIC_DOOR = ITEMS.register(
      "bamboo_classic_door", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_CLASSIC_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_BEACH_DOOR = ITEMS.register(
      "oak_beach_door", () -> new FuelItemBlock((Block)BlockInit.OAK_BEACH_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_BEACH_DOOR = ITEMS.register(
      "spruce_beach_door", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_BEACH_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_BEACH_DOOR = ITEMS.register(
      "birch_beach_door", () -> new FuelItemBlock((Block)BlockInit.BIRCH_BEACH_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_BEACH_DOOR = ITEMS.register(
      "acacia_beach_door", () -> new FuelItemBlock((Block)BlockInit.ACACIA_BEACH_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_BEACH_DOOR = ITEMS.register(
      "dark_oak_beach_door", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_BEACH_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_BEACH_DOOR = ITEMS.register(
      "crimson_beach_door", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_BEACH_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_BEACH_DOOR = ITEMS.register(
      "warped_beach_door", () -> new FuelItemBlock((Block)BlockInit.WARPED_BEACH_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_BEACH_DOOR = ITEMS.register(
      "mangrove_beach_door", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_BEACH_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_BEACH_DOOR = ITEMS.register(
      "bamboo_beach_door", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_BEACH_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_PAPER_DOOR = ITEMS.register(
      "oak_paper_door", () -> new FuelItemBlock((Block)BlockInit.OAK_PAPER_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_PAPER_DOOR = ITEMS.register(
      "spruce_paper_door", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_PAPER_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_PAPER_DOOR = ITEMS.register(
      "jungle_paper_door", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_PAPER_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_PAPER_DOOR = ITEMS.register(
      "acacia_paper_door", () -> new FuelItemBlock((Block)BlockInit.ACACIA_PAPER_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_PAPER_DOOR = ITEMS.register(
      "dark_oak_paper_door", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_PAPER_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_PAPER_DOOR = ITEMS.register(
      "crimson_paper_door", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_PAPER_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_PAPER_DOOR = ITEMS.register(
      "warped_paper_door", () -> new FuelItemBlock((Block)BlockInit.WARPED_PAPER_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_PAPER_DOOR = ITEMS.register(
      "mangrove_paper_door", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_PAPER_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_PAPER_DOOR = ITEMS.register(
      "bamboo_paper_door", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_PAPER_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_FOUR_PANEL_DOOR = ITEMS.register(
      "oak_four_panel_door", () -> new FuelItemBlock((Block)BlockInit.OAK_FOUR_PANEL_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_FOUR_PANEL_DOOR = ITEMS.register(
      "spruce_four_panel_door", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_FOUR_PANEL_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_FOUR_PANEL_DOOR = ITEMS.register(
      "birch_four_panel_door", () -> new FuelItemBlock((Block)BlockInit.BIRCH_FOUR_PANEL_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_FOUR_PANEL_DOOR = ITEMS.register(
      "jungle_four_panel_door", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_FOUR_PANEL_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_FOUR_PANEL_DOOR = ITEMS.register(
      "acacia_four_panel_door", () -> new FuelItemBlock((Block)BlockInit.ACACIA_FOUR_PANEL_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_FOUR_PANEL_DOOR = ITEMS.register(
      "crimson_four_panel_door", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_FOUR_PANEL_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_FOUR_PANEL_DOOR = ITEMS.register(
      "warped_four_panel_door", () -> new FuelItemBlock((Block)BlockInit.WARPED_FOUR_PANEL_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_FOUR_PANEL_DOOR = ITEMS.register(
      "mangrove_four_panel_door", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_FOUR_PANEL_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_FOUR_PANEL_DOOR = ITEMS.register(
      "bamboo_four_panel_door", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_FOUR_PANEL_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_TROPICAL_DOOR = ITEMS.register(
      "oak_tropical_door", () -> new FuelItemBlock((Block)BlockInit.OAK_TROPICAL_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_TROPICAL_DOOR = ITEMS.register(
      "spruce_tropical_door", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_TROPICAL_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_TROPICAL_DOOR = ITEMS.register(
      "birch_tropical_door", () -> new FuelItemBlock((Block)BlockInit.BIRCH_TROPICAL_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_TROPICAL_DOOR = ITEMS.register(
      "jungle_tropical_door", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_TROPICAL_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_TROPICAL_DOOR = ITEMS.register(
      "dark_oak_tropical_door", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_TROPICAL_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_TROPICAL_DOOR = ITEMS.register(
      "crimson_tropical_door", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_TROPICAL_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_TROPICAL_DOOR = ITEMS.register(
      "warped_tropical_door", () -> new FuelItemBlock((Block)BlockInit.WARPED_TROPICAL_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_TROPICAL_DOOR = ITEMS.register(
      "mangrove_tropical_door", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_TROPICAL_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_TROPICAL_DOOR = ITEMS.register(
      "bamboo_tropical_door", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_TROPICAL_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> METAL_DOOR = ITEMS.register("metal_door", () -> new MetalToolTip((Block)BlockInit.METAL_DOOR.get(), new Properties()));
   public static final DeferredItem<Item> METAL_WARNING_DOOR = ITEMS.register(
      "metal_warning_door", () -> new MetalToolTip((Block)BlockInit.METAL_WARNING_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> METAL_HOSPITAL_DOOR = ITEMS.register(
      "metal_hospital_door", () -> new MetalToolTip((Block)BlockInit.METAL_HOSPITAL_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> METAL_REINFORCED_DOOR = ITEMS.register(
      "metal_reinforced_door", () -> new MetalToolTip((Block)BlockInit.METAL_REINFORCED_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> METAL_WINDOWED_DOOR = ITEMS.register(
      "metal_windowed_door", () -> new MetalToolTip((Block)BlockInit.METAL_WINDOWED_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> JAIL_DOOR = ITEMS.register("jail_door", () -> new MetalToolTip((Block)BlockInit.JAIL_DOOR.get(), new Properties()));
   public static final DeferredItem<Item> OAK_GLASS_DOOR = ITEMS.register(
      "oak_glass_door", () -> new FuelItemBlock((Block)BlockInit.OAK_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_GLASS_DOOR = ITEMS.register(
      "spruce_glass_door", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_GLASS_DOOR = ITEMS.register(
      "birch_glass_door", () -> new FuelItemBlock((Block)BlockInit.BIRCH_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_GLASS_DOOR = ITEMS.register(
      "jungle_glass_door", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_GLASS_DOOR = ITEMS.register(
      "acacia_glass_door", () -> new FuelItemBlock((Block)BlockInit.ACACIA_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_GLASS_DOOR = ITEMS.register(
      "dark_oak_glass_door", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_GLASS_DOOR = ITEMS.register(
      "crimson_glass_door", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_GLASS_DOOR = ITEMS.register(
      "warped_glass_door", () -> new FuelItemBlock((Block)BlockInit.WARPED_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_GLASS_DOOR = ITEMS.register(
      "mangrove_glass_door", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_GLASS_DOOR = ITEMS.register(
      "bamboo_glass_door", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_STABLE_DOOR = ITEMS.register(
      "oak_stable_door", () -> new FuelItemBlock((Block)BlockInit.OAK_STABLE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_STABLE_DOOR = ITEMS.register(
      "spruce_stable_door", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_STABLE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_STABLE_DOOR = ITEMS.register(
      "birch_stable_door", () -> new FuelItemBlock((Block)BlockInit.BIRCH_STABLE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_STABLE_DOOR = ITEMS.register(
      "jungle_stable_door", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_STABLE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_STABLE_DOOR = ITEMS.register(
      "acacia_stable_door", () -> new FuelItemBlock((Block)BlockInit.ACACIA_STABLE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_STABLE_DOOR = ITEMS.register(
      "dark_oak_stable_door", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_STABLE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_STABLE_DOOR = ITEMS.register(
      "crimson_stable_door", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_STABLE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_STABLE_DOOR = ITEMS.register(
      "warped_stable_door", () -> new FuelItemBlock((Block)BlockInit.WARPED_STABLE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_STABLE_DOOR = ITEMS.register(
      "mangrove_stable_door", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_STABLE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_STABLE_DOOR = ITEMS.register(
      "bamboo_stable_door", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_STABLE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_STABLE_HEAD_DOOR = ITEMS.register(
      "oak_stable_head_door", () -> new FuelItemBlock((Block)BlockInit.OAK_STABLE_HEAD_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_STABLE_HEAD_DOOR = ITEMS.register(
      "spruce_stable_head_door", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_STABLE_HEAD_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_STABLE_HEAD_DOOR = ITEMS.register(
      "birch_stable_head_door", () -> new FuelItemBlock((Block)BlockInit.BIRCH_STABLE_HEAD_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_STABLE_HEAD_DOOR = ITEMS.register(
      "jungle_stable_head_door", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_STABLE_HEAD_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_STABLE_HEAD_DOOR = ITEMS.register(
      "acacia_stable_head_door", () -> new FuelItemBlock((Block)BlockInit.ACACIA_STABLE_HEAD_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_STABLE_HEAD_DOOR = ITEMS.register(
      "dark_oak_stable_head_door", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_STABLE_HEAD_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_STABLE_HEAD_DOOR = ITEMS.register(
      "crimson_stable_head_door", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_STABLE_HEAD_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_STABLE_HEAD_DOOR = ITEMS.register(
      "warped_stable_head_door", () -> new FuelItemBlock((Block)BlockInit.WARPED_STABLE_HEAD_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_STABLE_HEAD_DOOR = ITEMS.register(
      "mangrove_stable_head_door", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_STABLE_HEAD_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_STABLE_HEAD_DOOR = ITEMS.register(
      "bamboo_stable_head_door", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_STABLE_HEAD_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_WESTERN_DOOR = ITEMS.register(
      "oak_western_door", () -> new FuelItemBlock((Block)BlockInit.OAK_WESTERN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_WESTERN_DOOR = ITEMS.register(
      "spruce_western_door", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_WESTERN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_WESTERN_DOOR = ITEMS.register(
      "birch_western_door", () -> new FuelItemBlock((Block)BlockInit.BIRCH_WESTERN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_WESTERN_DOOR = ITEMS.register(
      "jungle_western_door", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_WESTERN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_WESTERN_DOOR = ITEMS.register(
      "acacia_western_door", () -> new FuelItemBlock((Block)BlockInit.ACACIA_WESTERN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_WESTERN_DOOR = ITEMS.register(
      "dark_oak_western_door", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_WESTERN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_WESTERN_DOOR = ITEMS.register(
      "crimson_western_door", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_WESTERN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_WESTERN_DOOR = ITEMS.register(
      "warped_western_door", () -> new FuelItemBlock((Block)BlockInit.WARPED_WESTERN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_WESTERN_DOOR = ITEMS.register(
      "mangrove_western_door", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_WESTERN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_WESTERN_DOOR = ITEMS.register(
      "bamboo_western_door", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_WESTERN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_MYSTIC_DOOR = ITEMS.register(
      "oak_mystic_door", () -> new FuelItemBlock((Block)BlockInit.OAK_MYSTIC_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_MYSTIC_DOOR = ITEMS.register(
      "spruce_mystic_door", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_MYSTIC_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_MYSTIC_DOOR = ITEMS.register(
      "birch_mystic_door", () -> new FuelItemBlock((Block)BlockInit.BIRCH_MYSTIC_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_MYSTIC_DOOR = ITEMS.register(
      "jungle_mystic_door", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_MYSTIC_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_MYSTIC_DOOR = ITEMS.register(
      "acacia_mystic_door", () -> new FuelItemBlock((Block)BlockInit.ACACIA_MYSTIC_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_MYSTIC_DOOR = ITEMS.register(
      "dark_oak_mystic_door", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_MYSTIC_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_MYSTIC_DOOR = ITEMS.register(
      "crimson_mystic_door", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_MYSTIC_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_MYSTIC_DOOR = ITEMS.register(
      "mangrove_mystic_door", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_MYSTIC_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_MYSTIC_DOOR = ITEMS.register(
      "bamboo_mystic_door", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_MYSTIC_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_NETHER_DOOR = ITEMS.register(
      "oak_nether_door", () -> new FuelItemBlock((Block)BlockInit.OAK_NETHER_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_NETHER_DOOR = ITEMS.register(
      "spruce_nether_door", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_NETHER_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_NETHER_DOOR = ITEMS.register(
      "birch_nether_door", () -> new FuelItemBlock((Block)BlockInit.BIRCH_NETHER_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_NETHER_DOOR = ITEMS.register(
      "jungle_nether_door", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_NETHER_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_NETHER_DOOR = ITEMS.register(
      "acacia_nether_door", () -> new FuelItemBlock((Block)BlockInit.ACACIA_NETHER_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_NETHER_DOOR = ITEMS.register(
      "dark_oak_nether_door", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_NETHER_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_NETHER_DOOR = ITEMS.register(
      "warped_nether_door", () -> new FuelItemBlock((Block)BlockInit.WARPED_NETHER_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_NETHER_DOOR = ITEMS.register(
      "mangrove_nether_door", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_NETHER_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_NETHER_DOOR = ITEMS.register(
      "bamboo_nether_door", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_NETHER_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_SWAMP_DOOR = ITEMS.register(
      "oak_swamp_door", () -> new FuelItemBlock((Block)BlockInit.OAK_SWAMP_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_SWAMP_DOOR = ITEMS.register(
      "spruce_swamp_door", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_SWAMP_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_SWAMP_DOOR = ITEMS.register(
      "birch_swamp_door", () -> new FuelItemBlock((Block)BlockInit.BIRCH_SWAMP_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_SWAMP_DOOR = ITEMS.register(
      "jungle_swamp_door", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_SWAMP_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_SWAMP_DOOR = ITEMS.register(
      "acacia_swamp_door", () -> new FuelItemBlock((Block)BlockInit.ACACIA_SWAMP_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_SWAMP_DOOR = ITEMS.register(
      "dark_oak_swamp_door", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_SWAMP_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_SWAMP_DOOR = ITEMS.register(
      "crimson_swamp_door", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_SWAMP_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_SWAMP_DOOR = ITEMS.register(
      "warped_swamp_door", () -> new FuelItemBlock((Block)BlockInit.WARPED_SWAMP_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_SWAMP_DOOR = ITEMS.register(
      "bamboo_swamp_door", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_SWAMP_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_BAMBOO_DOOR = ITEMS.register(
      "oak_bamboo_door", () -> new FuelItemBlock((Block)BlockInit.OAK_BAMBOO_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_BAMBOO_DOOR = ITEMS.register(
      "spruce_bamboo_door", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_BAMBOO_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_BAMBOO_DOOR = ITEMS.register(
      "birch_bamboo_door", () -> new FuelItemBlock((Block)BlockInit.BIRCH_BAMBOO_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_BAMBOO_DOOR = ITEMS.register(
      "jungle_bamboo_door", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_BAMBOO_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_BAMBOO_DOOR = ITEMS.register(
      "acacia_bamboo_door", () -> new FuelItemBlock((Block)BlockInit.ACACIA_BAMBOO_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_BAMBOO_DOOR = ITEMS.register(
      "dark_oak_bamboo_door", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_BAMBOO_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_BAMBOO_DOOR = ITEMS.register(
      "crimson_bamboo_door", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_BAMBOO_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_BAMBOO_DOOR = ITEMS.register(
      "warped_bamboo_door", () -> new FuelItemBlock((Block)BlockInit.WARPED_BAMBOO_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_BAMBOO_DOOR = ITEMS.register(
      "mangrove_bamboo_door", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_BAMBOO_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_BARK_GLASS_DOOR = ITEMS.register(
      "oak_bark_glass_door", () -> new FuelItemBlock((Block)BlockInit.OAK_BARK_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_BARK_GLASS_DOOR = ITEMS.register(
      "spruce_bark_glass_door", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_BARK_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_BARK_GLASS_DOOR = ITEMS.register(
      "birch_bark_glass_door", () -> new FuelItemBlock((Block)BlockInit.BIRCH_BARK_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_BARK_GLASS_DOOR = ITEMS.register(
      "jungle_bark_glass_door", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_BARK_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_BARK_GLASS_DOOR = ITEMS.register(
      "acacia_bark_glass_door", () -> new FuelItemBlock((Block)BlockInit.ACACIA_BARK_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_BARK_GLASS_DOOR = ITEMS.register(
      "dark_oak_bark_glass_door", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_BARK_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_STEM_GLASS_DOOR = ITEMS.register(
      "crimson_stem_glass_door", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_STEM_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_STEM_GLASS_DOOR = ITEMS.register(
      "warped_stem_glass_door", () -> new FuelItemBlock((Block)BlockInit.WARPED_STEM_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_BARK_GLASS_DOOR = ITEMS.register(
      "mangrove_bark_glass_door", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_BARK_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_BARK_GLASS_DOOR = ITEMS.register(
      "bamboo_bark_glass_door", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_BARK_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> GARAGE_WHITE_DOOR = ITEMS.register(
      "garage_white_door", () -> new GarageToolTip((Block)BlockInit.GARAGE_WHITE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> GARAGE_SILVER_DOOR = ITEMS.register(
      "garage_silver_door", () -> new GarageToolTip((Block)BlockInit.GARAGE_SILVER_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> GARAGE_GRAY_DOOR = ITEMS.register(
      "garage_gray_door", () -> new GarageToolTip((Block)BlockInit.GARAGE_GRAY_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> GARAGE_BLACK_DOOR = ITEMS.register(
      "garage_black_door", () -> new GarageToolTip((Block)BlockInit.GARAGE_BLACK_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> WOODEN_PORTCULLIS = ITEMS.register(
      "wooden_portcullis", () -> new GarageToolTip((Block)BlockInit.WOODEN_PORTCULLIS.get(), new Properties())
   );
   public static final DeferredItem<Item> IRON_PORTCULLIS = ITEMS.register(
      "iron_portcullis", () -> new GarageToolTip((Block)BlockInit.IRON_PORTCULLIS.get(), new Properties())
   );
   public static final DeferredItem<Item> STORE_DOOR = ITEMS.register("store_door", () -> new BlockItem((Block)BlockInit.STORE_DOOR.get(), new Properties()));
   public static final DeferredItem<Item> SLIDING_GLASS_DOOR = ITEMS.register(
      "sliding_glass_door", () -> new BlockItem((Block)BlockInit.SLIDING_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_WAFFLE_DOOR = ITEMS.register(
      "oak_waffle_door", () -> new FuelItemBlock((Block)BlockInit.OAK_WAFFLE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_WAFFLE_DOOR = ITEMS.register(
      "spruce_waffle_door", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_WAFFLE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_WAFFLE_DOOR = ITEMS.register(
      "birch_waffle_door", () -> new FuelItemBlock((Block)BlockInit.BIRCH_WAFFLE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_WAFFLE_DOOR = ITEMS.register(
      "jungle_waffle_door", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_WAFFLE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_WAFFLE_DOOR = ITEMS.register(
      "acacia_waffle_door", () -> new FuelItemBlock((Block)BlockInit.ACACIA_WAFFLE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_WAFFLE_DOOR = ITEMS.register(
      "dark_oak_waffle_door", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_WAFFLE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_WAFFLE_DOOR = ITEMS.register(
      "crimson_waffle_door", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_WAFFLE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_WAFFLE_DOOR = ITEMS.register(
      "warped_waffle_door", () -> new FuelItemBlock((Block)BlockInit.WARPED_WAFFLE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_WAFFLE_DOOR = ITEMS.register(
      "bamboo_waffle_door", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_WAFFLE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_WAFFLE_DOOR = ITEMS.register(
      "mangrove_waffle_door", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_WAFFLE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_JAPANESE_DOOR = ITEMS.register(
      "cherry_japanese_door", () -> new FuelItemBlock((Block)BlockInit.CHERRY_JAPANESE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_JAPANESE2_DOOR = ITEMS.register(
      "cherry_japanese2_door", () -> new FuelItemBlock((Block)BlockInit.CHERRY_JAPANESE2_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_BARN_DOOR = ITEMS.register(
      "cherry_barn_door", () -> new FuelItemBlock((Block)BlockInit.CHERRY_BARN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_BARN_GLASS_DOOR = ITEMS.register(
      "cherry_barn_glass_door", () -> new FuelItemBlock((Block)BlockInit.CHERRY_BARN_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_MODERN_DOOR = ITEMS.register(
      "cherry_modern_door", () -> new FuelItemBlock((Block)BlockInit.CHERRY_MODERN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_COTTAGE_DOOR = ITEMS.register(
      "cherry_cottage_door", () -> new FuelItemBlock((Block)BlockInit.CHERRY_COTTAGE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_CLASSIC_DOOR = ITEMS.register(
      "cherry_classic_door", () -> new FuelItemBlock((Block)BlockInit.CHERRY_CLASSIC_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_BEACH_DOOR = ITEMS.register(
      "cherry_beach_door", () -> new FuelItemBlock((Block)BlockInit.CHERRY_BEACH_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_PAPER_DOOR = ITEMS.register(
      "cherry_paper_door", () -> new FuelItemBlock((Block)BlockInit.CHERRY_PAPER_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_FOUR_PANEL_DOOR = ITEMS.register(
      "cherry_four_panel_door", () -> new FuelItemBlock((Block)BlockInit.CHERRY_FOUR_PANEL_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_TROPICAL_DOOR = ITEMS.register(
      "cherry_tropical_door", () -> new FuelItemBlock((Block)BlockInit.CHERRY_TROPICAL_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_GLASS_DOOR = ITEMS.register(
      "cherry_glass_door", () -> new FuelItemBlock((Block)BlockInit.CHERRY_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_STABLE_DOOR = ITEMS.register(
      "cherry_stable_door", () -> new FuelItemBlock((Block)BlockInit.CHERRY_STABLE_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_STABLE_HEAD_DOOR = ITEMS.register(
      "cherry_stable_head_door", () -> new FuelItemBlock((Block)BlockInit.CHERRY_STABLE_HEAD_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_WESTERN_DOOR = ITEMS.register(
      "cherry_western_door", () -> new FuelItemBlock((Block)BlockInit.CHERRY_WESTERN_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_MYSTIC_DOOR = ITEMS.register(
      "cherry_mystic_door", () -> new FuelItemBlock((Block)BlockInit.CHERRY_MYSTIC_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_NETHER_DOOR = ITEMS.register(
      "cherry_nether_door", () -> new FuelItemBlock((Block)BlockInit.CHERRY_NETHER_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_SWAMP_DOOR = ITEMS.register(
      "cherry_swamp_door", () -> new FuelItemBlock((Block)BlockInit.CHERRY_SWAMP_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_BAMBOO_DOOR = ITEMS.register(
      "cherry_bamboo_door", () -> new FuelItemBlock((Block)BlockInit.CHERRY_BAMBOO_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_BARK_GLASS_DOOR = ITEMS.register(
      "cherry_bark_glass_door", () -> new FuelItemBlock((Block)BlockInit.CHERRY_BARK_GLASS_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> PRINT_WHISPERING = ITEMS.register("print_whispering", () -> new ToolTip(new Properties()));
   public static final DeferredItem<Item> OAK_WHISPERING_DOOR = ITEMS.register(
      "oak_whispering_door", () -> new FuelItemBlock((Block)BlockInit.OAK_WHISPERING_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_WHISPERING_DOOR = ITEMS.register(
      "spruce_whispering_door", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_WHISPERING_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_WHISPERING_DOOR = ITEMS.register(
      "birch_whispering_door", () -> new FuelItemBlock((Block)BlockInit.BIRCH_WHISPERING_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_WHISPERING_DOOR = ITEMS.register(
      "jungle_whispering_door", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_WHISPERING_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_WHISPERING_DOOR = ITEMS.register(
      "acacia_whispering_door", () -> new FuelItemBlock((Block)BlockInit.ACACIA_WHISPERING_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_WHISPERING_DOOR = ITEMS.register(
      "dark_oak_whispering_door", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_WHISPERING_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_WHISPERING_DOOR = ITEMS.register(
      "crimson_whispering_door", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_WHISPERING_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_WHISPERING_DOOR = ITEMS.register(
      "warped_whispering_door", () -> new FuelItemBlock((Block)BlockInit.WARPED_WHISPERING_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_WHISPERING_DOOR = ITEMS.register(
      "bamboo_whispering_door", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_WHISPERING_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_WHISPERING_DOOR = ITEMS.register(
      "mangrove_whispering_door", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_WHISPERING_DOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_WHISPERING_DOOR = ITEMS.register(
      "cherry_whispering_door", () -> new FuelItemBlock((Block)BlockInit.CHERRY_WHISPERING_DOOR.get(), new Properties())
   );
}

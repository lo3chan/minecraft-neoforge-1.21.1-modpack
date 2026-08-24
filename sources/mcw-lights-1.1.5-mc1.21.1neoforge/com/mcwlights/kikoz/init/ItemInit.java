package com.mcwlights.kikoz.init;

import com.mcwlights.kikoz.util.DyeableInfo;
import com.mcwlights.kikoz.util.FuelItemBlock;
import com.mcwlights.kikoz.util.Stackable;
import com.mcwlights.kikoz.util.TikiTorchInfo;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;

public class ItemInit {
   public static final Items ITEMS = DeferredRegister.createItems("mcwlights");
   public static final DeferredItem<Item> WHITE_LAMP = ITEMS.register("white_lamp", () -> new Stackable((Block)BlockInit.WHITE_LAMP.get(), new Properties()));
   public static final DeferredItem<Item> ORANGE_LAMP = ITEMS.register("orange_lamp", () -> new Stackable((Block)BlockInit.ORANGE_LAMP.get(), new Properties()));
   public static final DeferredItem<Item> MAGENTA_LAMP = ITEMS.register(
      "magenta_lamp", () -> new Stackable((Block)BlockInit.MAGENTA_LAMP.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_BLUE_LAMP = ITEMS.register(
      "light_blue_lamp", () -> new Stackable((Block)BlockInit.LIGHT_BLUE_LAMP.get(), new Properties())
   );
   public static final DeferredItem<Item> YELLOW_LAMP = ITEMS.register("yellow_lamp", () -> new Stackable((Block)BlockInit.YELLOW_LAMP.get(), new Properties()));
   public static final DeferredItem<Item> LIME_LAMP = ITEMS.register("lime_lamp", () -> new Stackable((Block)BlockInit.LIME_LAMP.get(), new Properties()));
   public static final DeferredItem<Item> PINK_LAMP = ITEMS.register("pink_lamp", () -> new Stackable((Block)BlockInit.PINK_LAMP.get(), new Properties()));
   public static final DeferredItem<Item> GRAY_LAMP = ITEMS.register("gray_lamp", () -> new Stackable((Block)BlockInit.GRAY_LAMP.get(), new Properties()));
   public static final DeferredItem<Item> LIGHT_GRAY_LAMP = ITEMS.register(
      "light_gray_lamp", () -> new Stackable((Block)BlockInit.LIGHT_GRAY_LAMP.get(), new Properties())
   );
   public static final DeferredItem<Item> CYAN_LAMP = ITEMS.register("cyan_lamp", () -> new Stackable((Block)BlockInit.CYAN_LAMP.get(), new Properties()));
   public static final DeferredItem<Item> PURPLE_LAMP = ITEMS.register("purple_lamp", () -> new Stackable((Block)BlockInit.PURPLE_LAMP.get(), new Properties()));
   public static final DeferredItem<Item> BLUE_LAMP = ITEMS.register("blue_lamp", () -> new Stackable((Block)BlockInit.BLUE_LAMP.get(), new Properties()));
   public static final DeferredItem<Item> BROWN_LAMP = ITEMS.register("brown_lamp", () -> new Stackable((Block)BlockInit.BROWN_LAMP.get(), new Properties()));
   public static final DeferredItem<Item> GREEN_LAMP = ITEMS.register("green_lamp", () -> new Stackable((Block)BlockInit.GREEN_LAMP.get(), new Properties()));
   public static final DeferredItem<Item> RED_LAMP = ITEMS.register("red_lamp", () -> new Stackable((Block)BlockInit.RED_LAMP.get(), new Properties()));
   public static final DeferredItem<Item> BLACK_LAMP = ITEMS.register("black_lamp", () -> new Stackable((Block)BlockInit.BLACK_LAMP.get(), new Properties()));
   public static final DeferredItem<Item> OAK_TIKI_TORCH = ITEMS.register(
      "oak_tiki_torch", () -> new TikiTorchInfo((Block)BlockInit.OAK_TIKI_TORCH.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_TIKI_TORCH = ITEMS.register(
      "spruce_tiki_torch", () -> new TikiTorchInfo((Block)BlockInit.SPRUCE_TIKI_TORCH.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_TIKI_TORCH = ITEMS.register(
      "birch_tiki_torch", () -> new TikiTorchInfo((Block)BlockInit.BIRCH_TIKI_TORCH.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_TIKI_TORCH = ITEMS.register(
      "jungle_tiki_torch", () -> new TikiTorchInfo((Block)BlockInit.JUNGLE_TIKI_TORCH.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_TIKI_TORCH = ITEMS.register(
      "acacia_tiki_torch", () -> new TikiTorchInfo((Block)BlockInit.ACACIA_TIKI_TORCH.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_TIKI_TORCH = ITEMS.register(
      "dark_oak_tiki_torch", () -> new TikiTorchInfo((Block)BlockInit.DARK_OAK_TIKI_TORCH.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_TIKI_TORCH = ITEMS.register(
      "crimson_tiki_torch", () -> new TikiTorchInfo((Block)BlockInit.CRIMSON_TIKI_TORCH.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_TIKI_TORCH = ITEMS.register(
      "warped_tiki_torch", () -> new TikiTorchInfo((Block)BlockInit.WARPED_TIKI_TORCH.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_TIKI_TORCH = ITEMS.register(
      "mangrove_tiki_torch", () -> new TikiTorchInfo((Block)BlockInit.MANGROVE_TIKI_TORCH.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_TIKI_TORCH = ITEMS.register(
      "bamboo_tiki_torch", () -> new TikiTorchInfo((Block)BlockInit.BAMBOO_TIKI_TORCH.get(), new Properties())
   );
   public static final DeferredItem<Item> SOUL_OAK_TIKI_TORCH = ITEMS.register(
      "soul_oak_tiki_torch", () -> new TikiTorchInfo((Block)BlockInit.SOUL_OAK_TIKI_TORCH.get(), new Properties())
   );
   public static final DeferredItem<Item> SOUL_SPRUCE_TIKI_TORCH = ITEMS.register(
      "soul_spruce_tiki_torch", () -> new TikiTorchInfo((Block)BlockInit.SOUL_SPRUCE_TIKI_TORCH.get(), new Properties())
   );
   public static final DeferredItem<Item> SOUL_BIRCH_TIKI_TORCH = ITEMS.register(
      "soul_birch_tiki_torch", () -> new TikiTorchInfo((Block)BlockInit.SOUL_BIRCH_TIKI_TORCH.get(), new Properties())
   );
   public static final DeferredItem<Item> SOUL_JUNGLE_TIKI_TORCH = ITEMS.register(
      "soul_jungle_tiki_torch", () -> new TikiTorchInfo((Block)BlockInit.SOUL_JUNGLE_TIKI_TORCH.get(), new Properties())
   );
   public static final DeferredItem<Item> SOUL_ACACIA_TIKI_TORCH = ITEMS.register(
      "soul_acacia_tiki_torch", () -> new TikiTorchInfo((Block)BlockInit.SOUL_ACACIA_TIKI_TORCH.get(), new Properties())
   );
   public static final DeferredItem<Item> SOUL_DARK_OAK_TIKI_TORCH = ITEMS.register(
      "soul_dark_oak_tiki_torch", () -> new TikiTorchInfo((Block)BlockInit.SOUL_DARK_OAK_TIKI_TORCH.get(), new Properties())
   );
   public static final DeferredItem<Item> SOUL_CRIMSON_TIKI_TORCH = ITEMS.register(
      "soul_crimson_tiki_torch", () -> new TikiTorchInfo((Block)BlockInit.SOUL_CRIMSON_TIKI_TORCH.get(), new Properties())
   );
   public static final DeferredItem<Item> SOUL_WARPED_TIKI_TORCH = ITEMS.register(
      "soul_warped_tiki_torch", () -> new TikiTorchInfo((Block)BlockInit.SOUL_WARPED_TIKI_TORCH.get(), new Properties())
   );
   public static final DeferredItem<Item> SOUL_MANGROVE_TIKI_TORCH = ITEMS.register(
      "soul_mangrove_tiki_torch", () -> new TikiTorchInfo((Block)BlockInit.SOUL_MANGROVE_TIKI_TORCH.get(), new Properties())
   );
   public static final DeferredItem<Item> SOUL_BAMBOO_TIKI_TORCH = ITEMS.register(
      "soul_bamboo_tiki_torch", () -> new TikiTorchInfo((Block)BlockInit.SOUL_BAMBOO_TIKI_TORCH.get(), new Properties())
   );
   public static final DeferredItem<Item> WHITE_CEILING_LIGHT = ITEMS.register(
      "white_ceiling_light", () -> new Stackable((Block)BlockInit.WHITE_CEILING_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> ORANGE_CEILING_LIGHT = ITEMS.register(
      "orange_ceiling_light", () -> new Stackable((Block)BlockInit.ORANGE_CEILING_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> MAGENTA_CEILING_LIGHT = ITEMS.register(
      "magenta_ceiling_light", () -> new Stackable((Block)BlockInit.MAGENTA_CEILING_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_BLUE_CEILING_LIGHT = ITEMS.register(
      "light_blue_ceiling_light", () -> new Stackable((Block)BlockInit.LIGHT_BLUE_CEILING_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> YELLOW_CEILING_LIGHT = ITEMS.register(
      "yellow_ceiling_light", () -> new Stackable((Block)BlockInit.YELLOW_CEILING_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> LIME_CEILING_LIGHT = ITEMS.register(
      "lime_ceiling_light", () -> new Stackable((Block)BlockInit.LIME_CEILING_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> PINK_CEILING_LIGHT = ITEMS.register(
      "pink_ceiling_light", () -> new Stackable((Block)BlockInit.PINK_CEILING_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_CEILING_LIGHT = ITEMS.register(
      "gray_ceiling_light", () -> new Stackable((Block)BlockInit.GRAY_CEILING_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_CEILING_LIGHT = ITEMS.register(
      "light_gray_ceiling_light", () -> new Stackable((Block)BlockInit.LIGHT_GRAY_CEILING_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> CYAN_CEILING_LIGHT = ITEMS.register(
      "cyan_ceiling_light", () -> new Stackable((Block)BlockInit.CYAN_CEILING_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> PURPLE_CEILING_LIGHT = ITEMS.register(
      "purple_ceiling_light", () -> new Stackable((Block)BlockInit.PURPLE_CEILING_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> BLUE_CEILING_LIGHT = ITEMS.register(
      "blue_ceiling_light", () -> new Stackable((Block)BlockInit.BLUE_CEILING_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> BROWN_CEILING_LIGHT = ITEMS.register(
      "brown_ceiling_light", () -> new Stackable((Block)BlockInit.BROWN_CEILING_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> GREEN_CEILING_LIGHT = ITEMS.register(
      "green_ceiling_light", () -> new Stackable((Block)BlockInit.GREEN_CEILING_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_CEILING_LIGHT = ITEMS.register(
      "red_ceiling_light", () -> new Stackable((Block)BlockInit.RED_CEILING_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACK_CEILING_LIGHT = ITEMS.register(
      "black_ceiling_light", () -> new Stackable((Block)BlockInit.BLACK_CEILING_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> CLASSIC_STREET_LAMP = ITEMS.register(
      "classic_street_lamp", () -> new Stackable((Block)BlockInit.CLASSIC_STREET_LAMP.get(), new Properties())
   );
   public static final DeferredItem<Item> DOUBLE_STREET_LAMP = ITEMS.register(
      "double_street_lamp", () -> new Stackable((Block)BlockInit.DOUBLE_STREET_LAMP.get(), new Properties())
   );
   public static final DeferredItem<Item> SOUL_CLASSIC_STREET_LAMP = ITEMS.register(
      "soul_classic_street_lamp", () -> new Stackable((Block)BlockInit.SOUL_CLASSIC_STREET_LAMP.get(), new Properties())
   );
   public static final DeferredItem<Item> SOUL_DOUBLE_STREET_LAMP = ITEMS.register(
      "soul_double_street_lamp", () -> new Stackable((Block)BlockInit.SOUL_DOUBLE_STREET_LAMP.get(), new Properties())
   );
   public static final DeferredItem<Item> LAVA_LAMP = ITEMS.register("lava_lamp", () -> new BlockItem((Block)BlockInit.LAVA_LAMP.get(), new Properties()));
   public static final DeferredItem<Item> GARDEN_LIGHT = ITEMS.register(
      "garden_light", () -> new BlockItem((Block)BlockInit.GARDEN_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> WHITE_PAPER_LAMP = ITEMS.register(
      "white_paper_lamp", () -> new FuelItemBlock((Block)BlockInit.WHITE_PAPER_LAMP.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_GRAY_PAPER_LAMP = ITEMS.register(
      "light_gray_paper_lamp", () -> new FuelItemBlock((Block)BlockInit.LIGHT_GRAY_PAPER_LAMP.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAY_PAPER_LAMP = ITEMS.register(
      "gray_paper_lamp", () -> new FuelItemBlock((Block)BlockInit.GRAY_PAPER_LAMP.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACK_PAPER_LAMP = ITEMS.register(
      "black_paper_lamp", () -> new FuelItemBlock((Block)BlockInit.BLACK_PAPER_LAMP.get(), new Properties())
   );
   public static final DeferredItem<Item> BROWN_PAPER_LAMP = ITEMS.register(
      "brown_paper_lamp", () -> new FuelItemBlock((Block)BlockInit.BROWN_PAPER_LAMP.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_PAPER_LAMP = ITEMS.register(
      "red_paper_lamp", () -> new FuelItemBlock((Block)BlockInit.RED_PAPER_LAMP.get(), new Properties())
   );
   public static final DeferredItem<Item> ORANGE_PAPER_LAMP = ITEMS.register(
      "orange_paper_lamp", () -> new FuelItemBlock((Block)BlockInit.ORANGE_PAPER_LAMP.get(), new Properties())
   );
   public static final DeferredItem<Item> YELLOW_PAPER_LAMP = ITEMS.register(
      "yellow_paper_lamp", () -> new FuelItemBlock((Block)BlockInit.YELLOW_PAPER_LAMP.get(), new Properties())
   );
   public static final DeferredItem<Item> LIME_PAPER_LAMP = ITEMS.register(
      "lime_paper_lamp", () -> new FuelItemBlock((Block)BlockInit.LIME_PAPER_LAMP.get(), new Properties())
   );
   public static final DeferredItem<Item> GREEN_PAPER_LAMP = ITEMS.register(
      "green_paper_lamp", () -> new FuelItemBlock((Block)BlockInit.GREEN_PAPER_LAMP.get(), new Properties())
   );
   public static final DeferredItem<Item> CYAN_PAPER_LAMP = ITEMS.register(
      "cyan_paper_lamp", () -> new FuelItemBlock((Block)BlockInit.CYAN_PAPER_LAMP.get(), new Properties())
   );
   public static final DeferredItem<Item> LIGHT_BLUE_PAPER_LAMP = ITEMS.register(
      "light_blue_paper_lamp", () -> new FuelItemBlock((Block)BlockInit.LIGHT_BLUE_PAPER_LAMP.get(), new Properties())
   );
   public static final DeferredItem<Item> BLUE_PAPER_LAMP = ITEMS.register(
      "blue_paper_lamp", () -> new FuelItemBlock((Block)BlockInit.BLUE_PAPER_LAMP.get(), new Properties())
   );
   public static final DeferredItem<Item> PURPLE_PAPER_LAMP = ITEMS.register(
      "purple_paper_lamp", () -> new FuelItemBlock((Block)BlockInit.PURPLE_PAPER_LAMP.get(), new Properties())
   );
   public static final DeferredItem<Item> MAGENTA_PAPER_LAMP = ITEMS.register(
      "magenta_paper_lamp", () -> new FuelItemBlock((Block)BlockInit.MAGENTA_PAPER_LAMP.get(), new Properties())
   );
   public static final DeferredItem<Item> PINK_PAPER_LAMP = ITEMS.register(
      "pink_paper_lamp", () -> new FuelItemBlock((Block)BlockInit.PINK_PAPER_LAMP.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPED_LANTERN = ITEMS.register(
      "striped_lantern", () -> new BlockItem((Block)BlockInit.STRIPED_LANTERN.get(), new Properties())
   );
   public static final DeferredItem<Item> COVERED_LANTERN = ITEMS.register(
      "covered_lantern", () -> new BlockItem((Block)BlockInit.COVERED_LANTERN.get(), new Properties())
   );
   public static final DeferredItem<Item> CHAIN_LANTERN = ITEMS.register(
      "chain_lantern", () -> new BlockItem((Block)BlockInit.CHAIN_LANTERN.get(), new Properties())
   );
   public static final DeferredItem<Item> TAVERN_LANTERN = ITEMS.register(
      "tavern_lantern", () -> new BlockItem((Block)BlockInit.TAVERN_LANTERN.get(), new Properties())
   );
   public static final DeferredItem<Item> FESTIVE_LANTERN = ITEMS.register(
      "festive_lantern", () -> new BlockItem((Block)BlockInit.FESTIVE_LANTERN.get(), new Properties())
   );
   public static final DeferredItem<Item> CROSS_LANTERN = ITEMS.register(
      "cross_lantern", () -> new BlockItem((Block)BlockInit.CROSS_LANTERN.get(), new Properties())
   );
   public static final DeferredItem<Item> BELL_LANTERN = ITEMS.register(
      "bell_lantern", () -> new BlockItem((Block)BlockInit.BELL_LANTERN.get(), new Properties())
   );
   public static final DeferredItem<Item> WALL_LANTERN = ITEMS.register(
      "wall_lantern", () -> new BlockItem((Block)BlockInit.WALL_LANTERN.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPED_WALL_LANTERN = ITEMS.register(
      "striped_wall_lantern", () -> new BlockItem((Block)BlockInit.STRIPED_WALL_LANTERN.get(), new Properties())
   );
   public static final DeferredItem<Item> COVERED_WALL_LANTERN = ITEMS.register(
      "covered_wall_lantern", () -> new BlockItem((Block)BlockInit.COVERED_WALL_LANTERN.get(), new Properties())
   );
   public static final DeferredItem<Item> CHAIN_WALL_LANTERN = ITEMS.register(
      "chain_wall_lantern", () -> new BlockItem((Block)BlockInit.CHAIN_WALL_LANTERN.get(), new Properties())
   );
   public static final DeferredItem<Item> TAVERN_WALL_LANTERN = ITEMS.register(
      "tavern_wall_lantern", () -> new BlockItem((Block)BlockInit.TAVERN_WALL_LANTERN.get(), new Properties())
   );
   public static final DeferredItem<Item> FESTIVE_WALL_LANTERN = ITEMS.register(
      "festive_wall_lantern", () -> new BlockItem((Block)BlockInit.FESTIVE_WALL_LANTERN.get(), new Properties())
   );
   public static final DeferredItem<Item> CROSS_WALL_LANTERN = ITEMS.register(
      "cross_wall_lantern", () -> new BlockItem((Block)BlockInit.CROSS_WALL_LANTERN.get(), new Properties())
   );
   public static final DeferredItem<Item> BELL_WALL_LANTERN = ITEMS.register(
      "bell_wall_lantern", () -> new BlockItem((Block)BlockInit.BELL_WALL_LANTERN.get(), new Properties())
   );
   public static final DeferredItem<Item> WALL_LAMP = ITEMS.register("wall_lamp", () -> new DyeableInfo((Block)BlockInit.WALL_LAMP.get(), new Properties()));
   public static final DeferredItem<Item> SQUARE_WALL_LAMP = ITEMS.register(
      "square_wall_lamp", () -> new DyeableInfo((Block)BlockInit.SQUARE_WALL_LAMP.get(), new Properties())
   );
   public static final DeferredItem<Item> FRAMED_TORCH = ITEMS.register(
      "framed_torch", () -> new BlockItem((Block)BlockInit.FRAMED_TORCH.get(), new Properties())
   );
   public static final DeferredItem<Item> IRON_FRAMED_TORCH = ITEMS.register(
      "iron_framed_torch", () -> new BlockItem((Block)BlockInit.IRON_FRAMED_TORCH.get(), new Properties())
   );
   public static final DeferredItem<Item> REINFORCED_TORCH = ITEMS.register(
      "reinforced_torch", () -> new BlockItem((Block)BlockInit.REINFORCED_TORCH.get(), new Properties())
   );
   public static final DeferredItem<Item> RUSTIC_TORCH = ITEMS.register(
      "rustic_torch", () -> new BlockItem((Block)BlockInit.RUSTIC_TORCH.get(), new Properties())
   );
   public static final DeferredItem<Item> UPGRADED_TORCH = ITEMS.register(
      "upgraded_torch", () -> new BlockItem((Block)BlockInit.UPGRADED_TORCH.get(), new Properties())
   );
   public static final DeferredItem<Item> REDSTONE_LAMP_SLAB = ITEMS.register(
      "redstone_lamp_slab", () -> new BlockItem((Block)BlockInit.REDSTONE_LAMP_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> GLOWSTONE_SLAB = ITEMS.register(
      "glowstone_slab", () -> new BlockItem((Block)BlockInit.GLOWSTONE_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> SHROOMLIGHT_SLAB = ITEMS.register(
      "shroomlight_slab", () -> new BlockItem((Block)BlockInit.SHROOMLIGHT_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> SEA_LANTERN_SLAB = ITEMS.register(
      "sea_lantern_slab", () -> new BlockItem((Block)BlockInit.SEA_LANTERN_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_TIKI_TORCH = ITEMS.register(
      "cherry_tiki_torch", () -> new TikiTorchInfo((Block)BlockInit.CHERRY_TIKI_TORCH.get(), new Properties())
   );
   public static final DeferredItem<Item> SOUL_CHERRY_TIKI_TORCH = ITEMS.register(
      "soul_cherry_tiki_torch", () -> new TikiTorchInfo((Block)BlockInit.SOUL_CHERRY_TIKI_TORCH.get(), new Properties())
   );
   public static final DeferredItem<Item> OAK_CEILING_FAN_LIGHT = ITEMS.register(
      "oak_ceiling_fan_light", () -> new BlockItem((Block)BlockInit.OAK_CEILING_FAN_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_CEILING_FAN_LIGHT = ITEMS.register(
      "spruce_ceiling_fan_light", () -> new BlockItem((Block)BlockInit.SPRUCE_CEILING_FAN_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_CEILING_FAN_LIGHT = ITEMS.register(
      "birch_ceiling_fan_light", () -> new BlockItem((Block)BlockInit.BIRCH_CEILING_FAN_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_CEILING_FAN_LIGHT = ITEMS.register(
      "jungle_ceiling_fan_light", () -> new BlockItem((Block)BlockInit.JUNGLE_CEILING_FAN_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_CEILING_FAN_LIGHT = ITEMS.register(
      "acacia_ceiling_fan_light", () -> new BlockItem((Block)BlockInit.ACACIA_CEILING_FAN_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_CEILING_FAN_LIGHT = ITEMS.register(
      "dark_oak_ceiling_fan_light", () -> new BlockItem((Block)BlockInit.DARK_OAK_CEILING_FAN_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_CEILING_FAN_LIGHT = ITEMS.register(
      "crimson_ceiling_fan_light", () -> new BlockItem((Block)BlockInit.CRIMSON_CEILING_FAN_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_CEILING_FAN_LIGHT = ITEMS.register(
      "warped_ceiling_fan_light", () -> new BlockItem((Block)BlockInit.WARPED_CEILING_FAN_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_CEILING_FAN_LIGHT = ITEMS.register(
      "mangrove_ceiling_fan_light", () -> new BlockItem((Block)BlockInit.MANGROVE_CEILING_FAN_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_CEILING_FAN_LIGHT = ITEMS.register(
      "cherry_ceiling_fan_light", () -> new BlockItem((Block)BlockInit.CHERRY_CEILING_FAN_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> GOLDEN_LOW_CANDLE_HOLDER = ITEMS.register(
      "golden_low_candle_holder", () -> new BlockItem((Block)BlockInit.GOLDEN_LOW_CANDLE_HOLDER.get(), new Properties())
   );
   public static final DeferredItem<Item> GOLDEN_CANDLE_HOLDER = ITEMS.register(
      "golden_candle_holder", () -> new BlockItem((Block)BlockInit.GOLDEN_CANDLE_HOLDER.get(), new Properties())
   );
   public static final DeferredItem<Item> GOLDEN_WALL_CANDLE_HOLDER = ITEMS.register(
      "golden_wall_candle_holder", () -> new BlockItem((Block)BlockInit.GOLDEN_WALL_CANDLE_HOLDER.get(), new Properties())
   );
   public static final DeferredItem<Item> GOLDEN_DOUBLE_CANDLE_HOLDER = ITEMS.register(
      "golden_double_candle_holder", () -> new BlockItem((Block)BlockInit.GOLDEN_DOUBLE_CANDLE_HOLDER.get(), new Properties())
   );
   public static final DeferredItem<Item> GOLDEN_TRIPLE_CANDLE_HOLDER = ITEMS.register(
      "golden_triple_candle_holder", () -> new BlockItem((Block)BlockInit.GOLDEN_TRIPLE_CANDLE_HOLDER.get(), new Properties())
   );
   public static final DeferredItem<Item> GOLDEN_SMALL_CHANDELIER = ITEMS.register(
      "golden_small_chandelier", () -> new BlockItem((Block)BlockInit.GOLDEN_SMALL_CHANDELIER.get(), new Properties())
   );
   public static final DeferredItem<Item> GOLDEN_CHANDELIER = ITEMS.register(
      "golden_chandelier", () -> new BlockItem((Block)BlockInit.GOLDEN_CHANDELIER.get(), new Properties())
   );
   public static final DeferredItem<Item> COPPER_LOW_CANDLE_HOLDER = ITEMS.register(
      "copper_low_candle_holder", () -> new BlockItem((Block)BlockInit.COPPER_LOW_CANDLE_HOLDER.get(), new Properties())
   );
   public static final DeferredItem<Item> COPPER_CANDLE_HOLDER = ITEMS.register(
      "copper_candle_holder", () -> new BlockItem((Block)BlockInit.COPPER_CANDLE_HOLDER.get(), new Properties())
   );
   public static final DeferredItem<Item> COPPER_WALL_CANDLE_HOLDER = ITEMS.register(
      "copper_wall_candle_holder", () -> new BlockItem((Block)BlockInit.COPPER_WALL_CANDLE_HOLDER.get(), new Properties())
   );
   public static final DeferredItem<Item> COPPER_DOUBLE_CANDLE_HOLDER = ITEMS.register(
      "copper_double_candle_holder", () -> new BlockItem((Block)BlockInit.COPPER_DOUBLE_CANDLE_HOLDER.get(), new Properties())
   );
   public static final DeferredItem<Item> COPPER_TRIPLE_CANDLE_HOLDER = ITEMS.register(
      "copper_triple_candle_holder", () -> new BlockItem((Block)BlockInit.COPPER_TRIPLE_CANDLE_HOLDER.get(), new Properties())
   );
   public static final DeferredItem<Item> COPPER_SMALL_CHANDELIER = ITEMS.register(
      "copper_small_chandelier", () -> new BlockItem((Block)BlockInit.COPPER_SMALL_CHANDELIER.get(), new Properties())
   );
   public static final DeferredItem<Item> COPPER_CHANDELIER = ITEMS.register(
      "copper_chandelier", () -> new BlockItem((Block)BlockInit.COPPER_CHANDELIER.get(), new Properties())
   );
   public static final DeferredItem<Item> IRON_LOW_CANDLE_HOLDER = ITEMS.register(
      "iron_low_candle_holder", () -> new BlockItem((Block)BlockInit.IRON_LOW_CANDLE_HOLDER.get(), new Properties())
   );
   public static final DeferredItem<Item> IRON_CANDLE_HOLDER = ITEMS.register(
      "iron_candle_holder", () -> new BlockItem((Block)BlockInit.IRON_CANDLE_HOLDER.get(), new Properties())
   );
   public static final DeferredItem<Item> IRON_WALL_CANDLE_HOLDER = ITEMS.register(
      "iron_wall_candle_holder", () -> new BlockItem((Block)BlockInit.IRON_WALL_CANDLE_HOLDER.get(), new Properties())
   );
   public static final DeferredItem<Item> IRON_DOUBLE_CANDLE_HOLDER = ITEMS.register(
      "iron_double_candle_holder", () -> new BlockItem((Block)BlockInit.IRON_DOUBLE_CANDLE_HOLDER.get(), new Properties())
   );
   public static final DeferredItem<Item> IRON_TRIPLE_CANDLE_HOLDER = ITEMS.register(
      "iron_triple_candle_holder", () -> new BlockItem((Block)BlockInit.IRON_TRIPLE_CANDLE_HOLDER.get(), new Properties())
   );
   public static final DeferredItem<Item> IRON_SMALL_CHANDELIER = ITEMS.register(
      "iron_small_chandelier", () -> new BlockItem((Block)BlockInit.IRON_SMALL_CHANDELIER.get(), new Properties())
   );
   public static final DeferredItem<Item> IRON_CHANDELIER = ITEMS.register(
      "iron_chandelier", () -> new BlockItem((Block)BlockInit.IRON_CHANDELIER.get(), new Properties())
   );
   public static final DeferredItem<Item> GOLDEN_CHAIN = ITEMS.register(
      "golden_chain", () -> new BlockItem((Block)BlockInit.GOLDEN_CHAIN.get(), new Properties())
   );
   public static final DeferredItem<Item> COPPER_CHAIN = ITEMS.register(
      "copper_chain", () -> new BlockItem((Block)BlockInit.COPPER_CHAIN.get(), new Properties())
   );
   public static final DeferredItem<Item> THIN_GARDEN_LIGHT = ITEMS.register(
      "thin_garden_light", () -> new BlockItem((Block)BlockInit.THIN_GARDEN_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> TOWER_GARDEN_LIGHT = ITEMS.register(
      "tower_garden_light", () -> new BlockItem((Block)BlockInit.TOWER_GARDEN_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> COVERED_GARDEN_LIGHT = ITEMS.register(
      "covered_garden_light", () -> new BlockItem((Block)BlockInit.COVERED_GARDEN_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> ROUND_GARDEN_LIGHT = ITEMS.register(
      "round_garden_light", () -> new BlockItem((Block)BlockInit.ROUND_GARDEN_LIGHT.get(), new Properties())
   );
   public static final DeferredItem<Item> STRIPED_GARDEN_LIGHT = ITEMS.register(
      "striped_garden_light", () -> new BlockItem((Block)BlockInit.STRIPED_GARDEN_LIGHT.get(), new Properties())
   );
}

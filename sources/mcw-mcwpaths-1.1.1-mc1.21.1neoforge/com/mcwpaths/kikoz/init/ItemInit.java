package com.mcwpaths.kikoz.init;

import com.mcwpaths.kikoz.util.EngravedBlockTooltip;
import com.mcwpaths.kikoz.util.FlattenedBlockTooltip;
import com.mcwpaths.kikoz.util.FuelItemBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;

public class ItemInit {
   public static final Items ITEMS = DeferredRegister.createItems("mcwpaths");
   public static final DeferredItem<Item> OAK_PLANKS_PATH = ITEMS.register(
      "oak_planks_path", () -> new FuelItemBlock((Block)BlockInit.OAK_PLANKS_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> BIRCH_PLANKS_PATH = ITEMS.register(
      "birch_planks_path", () -> new FuelItemBlock((Block)BlockInit.BIRCH_PLANKS_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> SPRUCE_PLANKS_PATH = ITEMS.register(
      "spruce_planks_path", () -> new FuelItemBlock((Block)BlockInit.SPRUCE_PLANKS_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> JUNGLE_PLANKS_PATH = ITEMS.register(
      "jungle_planks_path", () -> new FuelItemBlock((Block)BlockInit.JUNGLE_PLANKS_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> ACACIA_PLANKS_PATH = ITEMS.register(
      "acacia_planks_path", () -> new FuelItemBlock((Block)BlockInit.ACACIA_PLANKS_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_OAK_PLANKS_PATH = ITEMS.register(
      "dark_oak_planks_path", () -> new FuelItemBlock((Block)BlockInit.DARK_OAK_PLANKS_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> CRIMSON_PLANKS_PATH = ITEMS.register(
      "crimson_planks_path", () -> new FuelItemBlock((Block)BlockInit.CRIMSON_PLANKS_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> WARPED_PLANKS_PATH = ITEMS.register(
      "warped_planks_path", () -> new FuelItemBlock((Block)BlockInit.WARPED_PLANKS_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> MANGROVE_PLANKS_PATH = ITEMS.register(
      "mangrove_planks_path", () -> new FuelItemBlock((Block)BlockInit.MANGROVE_PLANKS_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_DIAMOND_PAVING = ITEMS.register(
      "andesite_diamond_paving", () -> new BlockItem((Block)BlockInit.ANDESITE_DIAMOND_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_BASKET_WEAVE_PAVING = ITEMS.register(
      "andesite_basket_weave_paving", () -> new BlockItem((Block)BlockInit.ANDESITE_BASKET_WEAVE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_SQUARE_PAVING = ITEMS.register(
      "andesite_square_paving", () -> new BlockItem((Block)BlockInit.ANDESITE_SQUARE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_HONEYCOMB_PAVING = ITEMS.register(
      "andesite_honeycomb_paving", () -> new BlockItem((Block)BlockInit.ANDESITE_HONEYCOMB_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_CLOVER_PAVING = ITEMS.register(
      "andesite_clover_paving", () -> new BlockItem((Block)BlockInit.ANDESITE_CLOVER_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_DUMBLE_PAVING = ITEMS.register(
      "andesite_dumble_paving", () -> new BlockItem((Block)BlockInit.ANDESITE_DUMBLE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_DIAMOND_PAVING = ITEMS.register(
      "diorite_diamond_paving", () -> new BlockItem((Block)BlockInit.DIORITE_DIAMOND_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_BASKET_WEAVE_PAVING = ITEMS.register(
      "diorite_basket_weave_paving", () -> new BlockItem((Block)BlockInit.DIORITE_BASKET_WEAVE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_SQUARE_PAVING = ITEMS.register(
      "diorite_square_paving", () -> new BlockItem((Block)BlockInit.DIORITE_SQUARE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_HONEYCOMB_PAVING = ITEMS.register(
      "diorite_honeycomb_paving", () -> new BlockItem((Block)BlockInit.DIORITE_HONEYCOMB_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_CLOVER_PAVING = ITEMS.register(
      "diorite_clover_paving", () -> new BlockItem((Block)BlockInit.DIORITE_CLOVER_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_DUMBLE_PAVING = ITEMS.register(
      "diorite_dumble_paving", () -> new BlockItem((Block)BlockInit.DIORITE_DUMBLE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_DIAMOND_PAVING = ITEMS.register(
      "granite_diamond_paving", () -> new BlockItem((Block)BlockInit.GRANITE_DIAMOND_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_BASKET_WEAVE_PAVING = ITEMS.register(
      "granite_basket_weave_paving", () -> new BlockItem((Block)BlockInit.GRANITE_BASKET_WEAVE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_SQUARE_PAVING = ITEMS.register(
      "granite_square_paving", () -> new BlockItem((Block)BlockInit.GRANITE_SQUARE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_HONEYCOMB_PAVING = ITEMS.register(
      "granite_honeycomb_paving", () -> new BlockItem((Block)BlockInit.GRANITE_HONEYCOMB_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_CLOVER_PAVING = ITEMS.register(
      "granite_clover_paving", () -> new BlockItem((Block)BlockInit.GRANITE_CLOVER_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_DUMBLE_PAVING = ITEMS.register(
      "granite_dumble_paving", () -> new BlockItem((Block)BlockInit.GRANITE_DUMBLE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_DIAMOND_PAVING = ITEMS.register(
      "sandstone_diamond_paving", () -> new BlockItem((Block)BlockInit.SANDSTONE_DIAMOND_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_BASKET_WEAVE_PAVING = ITEMS.register(
      "sandstone_basket_weave_paving", () -> new BlockItem((Block)BlockInit.SANDSTONE_BASKET_WEAVE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_SQUARE_PAVING = ITEMS.register(
      "sandstone_square_paving", () -> new BlockItem((Block)BlockInit.SANDSTONE_SQUARE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_HONEYCOMB_PAVING = ITEMS.register(
      "sandstone_honeycomb_paving", () -> new BlockItem((Block)BlockInit.SANDSTONE_HONEYCOMB_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_CLOVER_PAVING = ITEMS.register(
      "sandstone_clover_paving", () -> new BlockItem((Block)BlockInit.SANDSTONE_CLOVER_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_DUMBLE_PAVING = ITEMS.register(
      "sandstone_dumble_paving", () -> new BlockItem((Block)BlockInit.SANDSTONE_DUMBLE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_DIAMOND_PAVING = ITEMS.register(
      "red_sandstone_diamond_paving", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_DIAMOND_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_BASKET_WEAVE_PAVING = ITEMS.register(
      "red_sandstone_basket_weave_paving", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_BASKET_WEAVE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_SQUARE_PAVING = ITEMS.register(
      "red_sandstone_square_paving", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_SQUARE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_HONEYCOMB_PAVING = ITEMS.register(
      "red_sandstone_honeycomb_paving", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_HONEYCOMB_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_CLOVER_PAVING = ITEMS.register(
      "red_sandstone_clover_paving", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_CLOVER_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_DUMBLE_PAVING = ITEMS.register(
      "red_sandstone_dumble_paving", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_DUMBLE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICK_DIAMOND_PAVING = ITEMS.register(
      "brick_diamond_paving", () -> new BlockItem((Block)BlockInit.BRICK_DIAMOND_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICK_BASKET_WEAVE_PAVING = ITEMS.register(
      "brick_basket_weave_paving", () -> new BlockItem((Block)BlockInit.BRICK_BASKET_WEAVE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICK_SQUARE_PAVING = ITEMS.register(
      "brick_square_paving", () -> new BlockItem((Block)BlockInit.BRICK_SQUARE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICK_HONEYCOMB_PAVING = ITEMS.register(
      "brick_honeycomb_paving", () -> new BlockItem((Block)BlockInit.BRICK_HONEYCOMB_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICK_CLOVER_PAVING = ITEMS.register(
      "brick_clover_paving", () -> new BlockItem((Block)BlockInit.BRICK_CLOVER_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICK_DUMBLE_PAVING = ITEMS.register(
      "brick_dumble_paving", () -> new BlockItem((Block)BlockInit.BRICK_DUMBLE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLESTONE_DIAMOND_PAVING = ITEMS.register(
      "cobblestone_diamond_paving", () -> new BlockItem((Block)BlockInit.COBBLESTONE_DIAMOND_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLESTONE_BASKET_WEAVE_PAVING = ITEMS.register(
      "cobblestone_basket_weave_paving", () -> new BlockItem((Block)BlockInit.COBBLESTONE_BASKET_WEAVE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLESTONE_SQUARE_PAVING = ITEMS.register(
      "cobblestone_square_paving", () -> new BlockItem((Block)BlockInit.COBBLESTONE_SQUARE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLESTONE_HONEYCOMB_PAVING = ITEMS.register(
      "cobblestone_honeycomb_paving", () -> new BlockItem((Block)BlockInit.COBBLESTONE_HONEYCOMB_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLESTONE_CLOVER_PAVING = ITEMS.register(
      "cobblestone_clover_paving", () -> new BlockItem((Block)BlockInit.COBBLESTONE_CLOVER_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLESTONE_DUMBLE_PAVING = ITEMS.register(
      "cobblestone_dumble_paving", () -> new BlockItem((Block)BlockInit.COBBLESTONE_DUMBLE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_COBBLESTONE_DIAMOND_PAVING = ITEMS.register(
      "mossy_cobblestone_diamond_paving", () -> new BlockItem((Block)BlockInit.MOSSY_COBBLESTONE_DIAMOND_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_COBBLESTONE_BASKET_WEAVE_PAVING = ITEMS.register(
      "mossy_cobblestone_basket_weave_paving", () -> new BlockItem((Block)BlockInit.MOSSY_COBBLESTONE_BASKET_WEAVE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_COBBLESTONE_SQUARE_PAVING = ITEMS.register(
      "mossy_cobblestone_square_paving", () -> new BlockItem((Block)BlockInit.MOSSY_COBBLESTONE_SQUARE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_COBBLESTONE_HONEYCOMB_PAVING = ITEMS.register(
      "mossy_cobblestone_honeycomb_paving", () -> new BlockItem((Block)BlockInit.MOSSY_COBBLESTONE_HONEYCOMB_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_COBBLESTONE_CLOVER_PAVING = ITEMS.register(
      "mossy_cobblestone_clover_paving", () -> new BlockItem((Block)BlockInit.MOSSY_COBBLESTONE_CLOVER_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_COBBLESTONE_DUMBLE_PAVING = ITEMS.register(
      "mossy_cobblestone_dumble_paving", () -> new BlockItem((Block)BlockInit.MOSSY_COBBLESTONE_DUMBLE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLED_DEEPSLATE_DIAMOND_PAVING = ITEMS.register(
      "cobbled_deepslate_diamond_paving", () -> new BlockItem((Block)BlockInit.COBBLED_DEEPSLATE_DIAMOND_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLED_DEEPSLATE_BASKET_WEAVE_PAVING = ITEMS.register(
      "cobbled_deepslate_basket_weave_paving", () -> new BlockItem((Block)BlockInit.COBBLED_DEEPSLATE_BASKET_WEAVE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLED_DEEPSLATE_SQUARE_PAVING = ITEMS.register(
      "cobbled_deepslate_square_paving", () -> new BlockItem((Block)BlockInit.COBBLED_DEEPSLATE_SQUARE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLED_DEEPSLATE_HONEYCOMB_PAVING = ITEMS.register(
      "cobbled_deepslate_honeycomb_paving", () -> new BlockItem((Block)BlockInit.COBBLED_DEEPSLATE_HONEYCOMB_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLED_DEEPSLATE_CLOVER_PAVING = ITEMS.register(
      "cobbled_deepslate_clover_paving", () -> new BlockItem((Block)BlockInit.COBBLED_DEEPSLATE_CLOVER_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLED_DEEPSLATE_DUMBLE_PAVING = ITEMS.register(
      "cobbled_deepslate_dumble_paving", () -> new BlockItem((Block)BlockInit.COBBLED_DEEPSLATE_DUMBLE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_DIAMOND_PAVING = ITEMS.register(
      "deepslate_diamond_paving", () -> new BlockItem((Block)BlockInit.DEEPSLATE_DIAMOND_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_BASKET_WEAVE_PAVING = ITEMS.register(
      "deepslate_basket_weave_paving", () -> new BlockItem((Block)BlockInit.DEEPSLATE_BASKET_WEAVE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_SQUARE_PAVING = ITEMS.register(
      "deepslate_square_paving", () -> new BlockItem((Block)BlockInit.DEEPSLATE_SQUARE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_HONEYCOMB_PAVING = ITEMS.register(
      "deepslate_honeycomb_paving", () -> new BlockItem((Block)BlockInit.DEEPSLATE_HONEYCOMB_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_CLOVER_PAVING = ITEMS.register(
      "deepslate_clover_paving", () -> new BlockItem((Block)BlockInit.DEEPSLATE_CLOVER_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_DUMBLE_PAVING = ITEMS.register(
      "deepslate_dumble_paving", () -> new BlockItem((Block)BlockInit.DEEPSLATE_DUMBLE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_DIAMOND_PAVING = ITEMS.register(
      "mud_brick_diamond_paving", () -> new BlockItem((Block)BlockInit.MUD_BRICK_DIAMOND_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_BASKET_WEAVE_PAVING = ITEMS.register(
      "mud_brick_basket_weave_paving", () -> new BlockItem((Block)BlockInit.MUD_BRICK_BASKET_WEAVE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_SQUARE_PAVING = ITEMS.register(
      "mud_brick_square_paving", () -> new BlockItem((Block)BlockInit.MUD_BRICK_SQUARE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_HONEYCOMB_PAVING = ITEMS.register(
      "mud_brick_honeycomb_paving", () -> new BlockItem((Block)BlockInit.MUD_BRICK_HONEYCOMB_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_CLOVER_PAVING = ITEMS.register(
      "mud_brick_clover_paving", () -> new BlockItem((Block)BlockInit.MUD_BRICK_CLOVER_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_DUMBLE_PAVING = ITEMS.register(
      "mud_brick_dumble_paving", () -> new BlockItem((Block)BlockInit.MUD_BRICK_DUMBLE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_DIAMOND_PAVING = ITEMS.register(
      "blackstone_diamond_paving", () -> new BlockItem((Block)BlockInit.BLACKSTONE_DIAMOND_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_BASKET_WEAVE_PAVING = ITEMS.register(
      "blackstone_basket_weave_paving", () -> new BlockItem((Block)BlockInit.BLACKSTONE_BASKET_WEAVE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_SQUARE_PAVING = ITEMS.register(
      "blackstone_square_paving", () -> new BlockItem((Block)BlockInit.BLACKSTONE_SQUARE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_HONEYCOMB_PAVING = ITEMS.register(
      "blackstone_honeycomb_paving", () -> new BlockItem((Block)BlockInit.BLACKSTONE_HONEYCOMB_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_CLOVER_PAVING = ITEMS.register(
      "blackstone_clover_paving", () -> new BlockItem((Block)BlockInit.BLACKSTONE_CLOVER_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_DUMBLE_PAVING = ITEMS.register(
      "blackstone_dumble_paving", () -> new BlockItem((Block)BlockInit.BLACKSTONE_DUMBLE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_DIAMOND_PAVING = ITEMS.register(
      "dark_prismarine_diamond_paving", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_DIAMOND_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_BASKET_WEAVE_PAVING = ITEMS.register(
      "dark_prismarine_basket_weave_paving", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_BASKET_WEAVE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_SQUARE_PAVING = ITEMS.register(
      "dark_prismarine_square_paving", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_SQUARE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_HONEYCOMB_PAVING = ITEMS.register(
      "dark_prismarine_honeycomb_paving", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_HONEYCOMB_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_CLOVER_PAVING = ITEMS.register(
      "dark_prismarine_clover_paving", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_CLOVER_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_DUMBLE_PAVING = ITEMS.register(
      "dark_prismarine_dumble_paving", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_DUMBLE_PAVING.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_RUNNING_BOND_PATH = ITEMS.register(
      "andesite_running_bond_path", () -> new BlockItem((Block)BlockInit.ANDESITE_RUNNING_BOND_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_RUNNING_BOND_SLAB = ITEMS.register(
      "andesite_running_bond_slab", () -> new BlockItem((Block)BlockInit.ANDESITE_RUNNING_BOND_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_RUNNING_BOND = ITEMS.register(
      "andesite_running_bond", () -> new EngravedBlockTooltip((Block)BlockInit.ANDESITE_RUNNING_BOND.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_RUNNING_BOND_PATH = ITEMS.register(
      "diorite_running_bond_path", () -> new BlockItem((Block)BlockInit.DIORITE_RUNNING_BOND_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_RUNNING_BOND_SLAB = ITEMS.register(
      "diorite_running_bond_slab", () -> new BlockItem((Block)BlockInit.DIORITE_RUNNING_BOND_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_RUNNING_BOND = ITEMS.register(
      "diorite_running_bond", () -> new EngravedBlockTooltip((Block)BlockInit.DIORITE_RUNNING_BOND.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_RUNNING_BOND_PATH = ITEMS.register(
      "granite_running_bond_path", () -> new BlockItem((Block)BlockInit.GRANITE_RUNNING_BOND_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_RUNNING_BOND_SLAB = ITEMS.register(
      "granite_running_bond_slab", () -> new BlockItem((Block)BlockInit.GRANITE_RUNNING_BOND_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_RUNNING_BOND = ITEMS.register(
      "granite_running_bond", () -> new EngravedBlockTooltip((Block)BlockInit.GRANITE_RUNNING_BOND.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_RUNNING_BOND_PATH = ITEMS.register(
      "sandstone_running_bond_path", () -> new BlockItem((Block)BlockInit.SANDSTONE_RUNNING_BOND_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_RUNNING_BOND_SLAB = ITEMS.register(
      "sandstone_running_bond_slab", () -> new BlockItem((Block)BlockInit.SANDSTONE_RUNNING_BOND_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_RUNNING_BOND = ITEMS.register(
      "sandstone_running_bond", () -> new EngravedBlockTooltip((Block)BlockInit.SANDSTONE_RUNNING_BOND.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_RUNNING_BOND_PATH = ITEMS.register(
      "red_sandstone_running_bond_path", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_RUNNING_BOND_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_RUNNING_BOND_SLAB = ITEMS.register(
      "red_sandstone_running_bond_slab", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_RUNNING_BOND_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_RUNNING_BOND = ITEMS.register(
      "red_sandstone_running_bond", () -> new EngravedBlockTooltip((Block)BlockInit.RED_SANDSTONE_RUNNING_BOND.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICK_RUNNING_BOND_PATH = ITEMS.register(
      "brick_running_bond_path", () -> new BlockItem((Block)BlockInit.BRICK_RUNNING_BOND_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICK_RUNNING_BOND_SLAB = ITEMS.register(
      "brick_running_bond_slab", () -> new BlockItem((Block)BlockInit.BRICK_RUNNING_BOND_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICK_RUNNING_BOND = ITEMS.register(
      "brick_running_bond", () -> new EngravedBlockTooltip((Block)BlockInit.BRICK_RUNNING_BOND.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_RUNNING_BOND_PATH = ITEMS.register(
      "stone_running_bond_path", () -> new BlockItem((Block)BlockInit.STONE_RUNNING_BOND_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_RUNNING_BOND_SLAB = ITEMS.register(
      "stone_running_bond_slab", () -> new BlockItem((Block)BlockInit.STONE_RUNNING_BOND_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_RUNNING_BOND = ITEMS.register(
      "stone_running_bond", () -> new EngravedBlockTooltip((Block)BlockInit.STONE_RUNNING_BOND.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_STONE_RUNNING_BOND_PATH = ITEMS.register(
      "mossy_stone_running_bond_path", () -> new BlockItem((Block)BlockInit.MOSSY_STONE_RUNNING_BOND_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_STONE_RUNNING_BOND_SLAB = ITEMS.register(
      "mossy_stone_running_bond_slab", () -> new BlockItem((Block)BlockInit.MOSSY_STONE_RUNNING_BOND_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_STONE_RUNNING_BOND = ITEMS.register(
      "mossy_stone_running_bond", () -> new EngravedBlockTooltip((Block)BlockInit.MOSSY_STONE_RUNNING_BOND.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLED_DEEPSLATE_RUNNING_BOND_PATH = ITEMS.register(
      "cobbled_deepslate_running_bond_path", () -> new BlockItem((Block)BlockInit.COBBLED_DEEPSLATE_RUNNING_BOND_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLED_DEEPSLATE_RUNNING_BOND_SLAB = ITEMS.register(
      "cobbled_deepslate_running_bond_slab", () -> new BlockItem((Block)BlockInit.COBBLED_DEEPSLATE_RUNNING_BOND_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLED_DEEPSLATE_RUNNING_BOND = ITEMS.register(
      "cobbled_deepslate_running_bond", () -> new EngravedBlockTooltip((Block)BlockInit.COBBLED_DEEPSLATE_RUNNING_BOND.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_RUNNING_BOND_PATH = ITEMS.register(
      "deepslate_running_bond_path", () -> new BlockItem((Block)BlockInit.DEEPSLATE_RUNNING_BOND_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_RUNNING_BOND_SLAB = ITEMS.register(
      "deepslate_running_bond_slab", () -> new BlockItem((Block)BlockInit.DEEPSLATE_RUNNING_BOND_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_RUNNING_BOND = ITEMS.register(
      "deepslate_running_bond", () -> new EngravedBlockTooltip((Block)BlockInit.DEEPSLATE_RUNNING_BOND.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_RUNNING_BOND_PATH = ITEMS.register(
      "mud_brick_running_bond_path", () -> new BlockItem((Block)BlockInit.MUD_BRICK_RUNNING_BOND_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_RUNNING_BOND_SLAB = ITEMS.register(
      "mud_brick_running_bond_slab", () -> new BlockItem((Block)BlockInit.MUD_BRICK_RUNNING_BOND_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_RUNNING_BOND = ITEMS.register(
      "mud_brick_running_bond", () -> new EngravedBlockTooltip((Block)BlockInit.MUD_BRICK_RUNNING_BOND.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_RUNNING_BOND_PATH = ITEMS.register(
      "blackstone_running_bond_path", () -> new BlockItem((Block)BlockInit.BLACKSTONE_RUNNING_BOND_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_RUNNING_BOND_SLAB = ITEMS.register(
      "blackstone_running_bond_slab", () -> new BlockItem((Block)BlockInit.BLACKSTONE_RUNNING_BOND_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_RUNNING_BOND = ITEMS.register(
      "blackstone_running_bond", () -> new EngravedBlockTooltip((Block)BlockInit.BLACKSTONE_RUNNING_BOND.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_RUNNING_BOND_PATH = ITEMS.register(
      "dark_prismarine_running_bond_path", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_RUNNING_BOND_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_RUNNING_BOND_SLAB = ITEMS.register(
      "dark_prismarine_running_bond_slab", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_RUNNING_BOND_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_RUNNING_BOND = ITEMS.register(
      "dark_prismarine_running_bond", () -> new EngravedBlockTooltip((Block)BlockInit.DARK_PRISMARINE_RUNNING_BOND.get(), new Properties())
   );
   public static final DeferredItem<Item> CHERRY_PLANKS_PATH = ITEMS.register(
      "cherry_planks_path", () -> new FuelItemBlock((Block)BlockInit.CHERRY_PLANKS_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> BAMBOO_PLANKS_PATH = ITEMS.register(
      "bamboo_planks_path", () -> new FuelItemBlock((Block)BlockInit.BAMBOO_PLANKS_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_STREWN_ROCKY_PATH = ITEMS.register(
      "andesite_strewn_rocky_path", () -> new BlockItem((Block)BlockInit.ANDESITE_STREWN_ROCKY_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_STREWN_ROCKY_PATH = ITEMS.register(
      "diorite_strewn_rocky_path", () -> new BlockItem((Block)BlockInit.DIORITE_STREWN_ROCKY_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_STREWN_ROCKY_PATH = ITEMS.register(
      "granite_strewn_rocky_path", () -> new BlockItem((Block)BlockInit.GRANITE_STREWN_ROCKY_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_STREWN_ROCKY_PATH = ITEMS.register(
      "sandstone_strewn_rocky_path", () -> new BlockItem((Block)BlockInit.SANDSTONE_STREWN_ROCKY_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_STREWN_ROCKY_PATH = ITEMS.register(
      "red_sandstone_strewn_rocky_path", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_STREWN_ROCKY_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICK_STREWN_ROCKY_PATH = ITEMS.register(
      "brick_strewn_rocky_path", () -> new BlockItem((Block)BlockInit.BRICK_STREWN_ROCKY_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_STREWN_ROCKY_PATH = ITEMS.register(
      "stone_strewn_rocky_path", () -> new BlockItem((Block)BlockInit.STONE_STREWN_ROCKY_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_STONE_STREWN_ROCKY_PATH = ITEMS.register(
      "mossy_stone_strewn_rocky_path", () -> new BlockItem((Block)BlockInit.MOSSY_STONE_STREWN_ROCKY_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLED_DEEPSLATE_STREWN_ROCKY_PATH = ITEMS.register(
      "cobbled_deepslate_strewn_rocky_path", () -> new BlockItem((Block)BlockInit.COBBLED_DEEPSLATE_STREWN_ROCKY_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_STREWN_ROCKY_PATH = ITEMS.register(
      "deepslate_strewn_rocky_path", () -> new BlockItem((Block)BlockInit.DEEPSLATE_STREWN_ROCKY_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_STREWN_ROCKY_PATH = ITEMS.register(
      "mud_brick_strewn_rocky_path", () -> new BlockItem((Block)BlockInit.MUD_BRICK_STREWN_ROCKY_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_STREWN_ROCKY_PATH = ITEMS.register(
      "blackstone_strewn_rocky_path", () -> new BlockItem((Block)BlockInit.BLACKSTONE_STREWN_ROCKY_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_STREWN_ROCKY_PATH = ITEMS.register(
      "dark_prismarine_strewn_rocky_path", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_STREWN_ROCKY_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_WINDMILL_WEAVE_PATH = ITEMS.register(
      "andesite_windmill_weave_path", () -> new BlockItem((Block)BlockInit.ANDESITE_WINDMILL_WEAVE_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_WINDMILL_WEAVE_SLAB = ITEMS.register(
      "andesite_windmill_weave_slab", () -> new BlockItem((Block)BlockInit.ANDESITE_WINDMILL_WEAVE_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_WINDMILL_WEAVE = ITEMS.register(
      "andesite_windmill_weave", () -> new EngravedBlockTooltip((Block)BlockInit.ANDESITE_WINDMILL_WEAVE.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_WINDMILL_WEAVE_PATH = ITEMS.register(
      "diorite_windmill_weave_path", () -> new BlockItem((Block)BlockInit.DIORITE_WINDMILL_WEAVE_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_WINDMILL_WEAVE_SLAB = ITEMS.register(
      "diorite_windmill_weave_slab", () -> new BlockItem((Block)BlockInit.DIORITE_WINDMILL_WEAVE_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_WINDMILL_WEAVE = ITEMS.register(
      "diorite_windmill_weave", () -> new EngravedBlockTooltip((Block)BlockInit.DIORITE_WINDMILL_WEAVE.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_WINDMILL_WEAVE_PATH = ITEMS.register(
      "granite_windmill_weave_path", () -> new BlockItem((Block)BlockInit.GRANITE_WINDMILL_WEAVE_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_WINDMILL_WEAVE_SLAB = ITEMS.register(
      "granite_windmill_weave_slab", () -> new BlockItem((Block)BlockInit.GRANITE_WINDMILL_WEAVE_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_WINDMILL_WEAVE = ITEMS.register(
      "granite_windmill_weave", () -> new EngravedBlockTooltip((Block)BlockInit.GRANITE_WINDMILL_WEAVE.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_WINDMILL_WEAVE_PATH = ITEMS.register(
      "sandstone_windmill_weave_path", () -> new BlockItem((Block)BlockInit.SANDSTONE_WINDMILL_WEAVE_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_WINDMILL_WEAVE_SLAB = ITEMS.register(
      "sandstone_windmill_weave_slab", () -> new BlockItem((Block)BlockInit.SANDSTONE_WINDMILL_WEAVE_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_WINDMILL_WEAVE = ITEMS.register(
      "sandstone_windmill_weave", () -> new EngravedBlockTooltip((Block)BlockInit.SANDSTONE_WINDMILL_WEAVE.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_WINDMILL_WEAVE_PATH = ITEMS.register(
      "red_sandstone_windmill_weave_path", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_WINDMILL_WEAVE_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_WINDMILL_WEAVE_SLAB = ITEMS.register(
      "red_sandstone_windmill_weave_slab", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_WINDMILL_WEAVE_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_WINDMILL_WEAVE = ITEMS.register(
      "red_sandstone_windmill_weave", () -> new EngravedBlockTooltip((Block)BlockInit.RED_SANDSTONE_WINDMILL_WEAVE.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICK_WINDMILL_WEAVE_PATH = ITEMS.register(
      "brick_windmill_weave_path", () -> new BlockItem((Block)BlockInit.BRICK_WINDMILL_WEAVE_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICK_WINDMILL_WEAVE_SLAB = ITEMS.register(
      "brick_windmill_weave_slab", () -> new BlockItem((Block)BlockInit.BRICK_WINDMILL_WEAVE_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICK_WINDMILL_WEAVE = ITEMS.register(
      "brick_windmill_weave", () -> new EngravedBlockTooltip((Block)BlockInit.BRICK_WINDMILL_WEAVE.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_WINDMILL_WEAVE_PATH = ITEMS.register(
      "stone_windmill_weave_path", () -> new BlockItem((Block)BlockInit.STONE_WINDMILL_WEAVE_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_WINDMILL_WEAVE_SLAB = ITEMS.register(
      "stone_windmill_weave_slab", () -> new BlockItem((Block)BlockInit.STONE_WINDMILL_WEAVE_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_WINDMILL_WEAVE = ITEMS.register(
      "stone_windmill_weave", () -> new EngravedBlockTooltip((Block)BlockInit.STONE_WINDMILL_WEAVE.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_STONE_WINDMILL_WEAVE_PATH = ITEMS.register(
      "mossy_stone_windmill_weave_path", () -> new BlockItem((Block)BlockInit.MOSSY_STONE_WINDMILL_WEAVE_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_STONE_WINDMILL_WEAVE_SLAB = ITEMS.register(
      "mossy_stone_windmill_weave_slab", () -> new BlockItem((Block)BlockInit.MOSSY_STONE_WINDMILL_WEAVE_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_STONE_WINDMILL_WEAVE = ITEMS.register(
      "mossy_stone_windmill_weave", () -> new EngravedBlockTooltip((Block)BlockInit.MOSSY_STONE_WINDMILL_WEAVE.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLED_DEEPSLATE_WINDMILL_WEAVE_PATH = ITEMS.register(
      "cobbled_deepslate_windmill_weave_path", () -> new BlockItem((Block)BlockInit.COBBLED_DEEPSLATE_WINDMILL_WEAVE_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLED_DEEPSLATE_WINDMILL_WEAVE_SLAB = ITEMS.register(
      "cobbled_deepslate_windmill_weave_slab", () -> new BlockItem((Block)BlockInit.COBBLED_DEEPSLATE_WINDMILL_WEAVE_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLED_DEEPSLATE_WINDMILL_WEAVE = ITEMS.register(
      "cobbled_deepslate_windmill_weave", () -> new EngravedBlockTooltip((Block)BlockInit.COBBLED_DEEPSLATE_WINDMILL_WEAVE.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_WINDMILL_WEAVE_PATH = ITEMS.register(
      "deepslate_windmill_weave_path", () -> new BlockItem((Block)BlockInit.DEEPSLATE_WINDMILL_WEAVE_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_WINDMILL_WEAVE_SLAB = ITEMS.register(
      "deepslate_windmill_weave_slab", () -> new BlockItem((Block)BlockInit.DEEPSLATE_WINDMILL_WEAVE_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_WINDMILL_WEAVE = ITEMS.register(
      "deepslate_windmill_weave", () -> new EngravedBlockTooltip((Block)BlockInit.DEEPSLATE_WINDMILL_WEAVE.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_WINDMILL_WEAVE_PATH = ITEMS.register(
      "mud_brick_windmill_weave_path", () -> new BlockItem((Block)BlockInit.MUD_BRICK_WINDMILL_WEAVE_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_WINDMILL_WEAVE_SLAB = ITEMS.register(
      "mud_brick_windmill_weave_slab", () -> new BlockItem((Block)BlockInit.MUD_BRICK_WINDMILL_WEAVE_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_WINDMILL_WEAVE = ITEMS.register(
      "mud_brick_windmill_weave", () -> new EngravedBlockTooltip((Block)BlockInit.MUD_BRICK_WINDMILL_WEAVE.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_WINDMILL_WEAVE_PATH = ITEMS.register(
      "blackstone_windmill_weave_path", () -> new BlockItem((Block)BlockInit.BLACKSTONE_WINDMILL_WEAVE_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_WINDMILL_WEAVE_SLAB = ITEMS.register(
      "blackstone_windmill_weave_slab", () -> new BlockItem((Block)BlockInit.BLACKSTONE_WINDMILL_WEAVE_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_WINDMILL_WEAVE = ITEMS.register(
      "blackstone_windmill_weave", () -> new EngravedBlockTooltip((Block)BlockInit.BLACKSTONE_WINDMILL_WEAVE.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_WINDMILL_WEAVE_PATH = ITEMS.register(
      "dark_prismarine_windmill_weave_path", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_WINDMILL_WEAVE_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_WINDMILL_WEAVE_SLAB = ITEMS.register(
      "dark_prismarine_windmill_weave_slab", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_WINDMILL_WEAVE_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_WINDMILL_WEAVE = ITEMS.register(
      "dark_prismarine_windmill_weave", () -> new EngravedBlockTooltip((Block)BlockInit.DARK_PRISMARINE_WINDMILL_WEAVE.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_FLAGSTONE_PATH = ITEMS.register(
      "andesite_flagstone_path", () -> new BlockItem((Block)BlockInit.ANDESITE_FLAGSTONE_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_FLAGSTONE_SLAB = ITEMS.register(
      "andesite_flagstone_slab", () -> new BlockItem((Block)BlockInit.ANDESITE_FLAGSTONE_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_FLAGSTONE = ITEMS.register(
      "andesite_flagstone", () -> new EngravedBlockTooltip((Block)BlockInit.ANDESITE_FLAGSTONE.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_FLAGSTONE_PATH = ITEMS.register(
      "diorite_flagstone_path", () -> new BlockItem((Block)BlockInit.DIORITE_FLAGSTONE_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_FLAGSTONE_SLAB = ITEMS.register(
      "diorite_flagstone_slab", () -> new BlockItem((Block)BlockInit.DIORITE_FLAGSTONE_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_FLAGSTONE = ITEMS.register(
      "diorite_flagstone", () -> new EngravedBlockTooltip((Block)BlockInit.DIORITE_FLAGSTONE.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_FLAGSTONE_PATH = ITEMS.register(
      "granite_flagstone_path", () -> new BlockItem((Block)BlockInit.GRANITE_FLAGSTONE_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_FLAGSTONE_SLAB = ITEMS.register(
      "granite_flagstone_slab", () -> new BlockItem((Block)BlockInit.GRANITE_FLAGSTONE_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_FLAGSTONE = ITEMS.register(
      "granite_flagstone", () -> new EngravedBlockTooltip((Block)BlockInit.GRANITE_FLAGSTONE.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_FLAGSTONE_PATH = ITEMS.register(
      "sandstone_flagstone_path", () -> new BlockItem((Block)BlockInit.SANDSTONE_FLAGSTONE_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_FLAGSTONE_SLAB = ITEMS.register(
      "sandstone_flagstone_slab", () -> new BlockItem((Block)BlockInit.SANDSTONE_FLAGSTONE_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_FLAGSTONE = ITEMS.register(
      "sandstone_flagstone", () -> new EngravedBlockTooltip((Block)BlockInit.SANDSTONE_FLAGSTONE.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_FLAGSTONE_PATH = ITEMS.register(
      "red_sandstone_flagstone_path", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_FLAGSTONE_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_FLAGSTONE_SLAB = ITEMS.register(
      "red_sandstone_flagstone_slab", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_FLAGSTONE_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_FLAGSTONE = ITEMS.register(
      "red_sandstone_flagstone", () -> new EngravedBlockTooltip((Block)BlockInit.RED_SANDSTONE_FLAGSTONE.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICK_FLAGSTONE_PATH = ITEMS.register(
      "brick_flagstone_path", () -> new BlockItem((Block)BlockInit.BRICK_FLAGSTONE_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICK_FLAGSTONE_SLAB = ITEMS.register(
      "brick_flagstone_slab", () -> new BlockItem((Block)BlockInit.BRICK_FLAGSTONE_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICK_FLAGSTONE = ITEMS.register(
      "brick_flagstone", () -> new EngravedBlockTooltip((Block)BlockInit.BRICK_FLAGSTONE.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_FLAGSTONE_PATH = ITEMS.register(
      "stone_flagstone_path", () -> new BlockItem((Block)BlockInit.STONE_FLAGSTONE_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_FLAGSTONE_SLAB = ITEMS.register(
      "stone_flagstone_slab", () -> new BlockItem((Block)BlockInit.STONE_FLAGSTONE_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_FLAGSTONE = ITEMS.register(
      "stone_flagstone", () -> new EngravedBlockTooltip((Block)BlockInit.STONE_FLAGSTONE.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_STONE_FLAGSTONE_PATH = ITEMS.register(
      "mossy_stone_flagstone_path", () -> new BlockItem((Block)BlockInit.MOSSY_STONE_FLAGSTONE_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_STONE_FLAGSTONE_SLAB = ITEMS.register(
      "mossy_stone_flagstone_slab", () -> new BlockItem((Block)BlockInit.MOSSY_STONE_FLAGSTONE_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_STONE_FLAGSTONE = ITEMS.register(
      "mossy_stone_flagstone", () -> new EngravedBlockTooltip((Block)BlockInit.MOSSY_STONE_FLAGSTONE.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLED_DEEPSLATE_FLAGSTONE_PATH = ITEMS.register(
      "cobbled_deepslate_flagstone_path", () -> new BlockItem((Block)BlockInit.COBBLED_DEEPSLATE_FLAGSTONE_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLED_DEEPSLATE_FLAGSTONE_SLAB = ITEMS.register(
      "cobbled_deepslate_flagstone_slab", () -> new BlockItem((Block)BlockInit.COBBLED_DEEPSLATE_FLAGSTONE_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLED_DEEPSLATE_FLAGSTONE = ITEMS.register(
      "cobbled_deepslate_flagstone", () -> new EngravedBlockTooltip((Block)BlockInit.COBBLED_DEEPSLATE_FLAGSTONE.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_FLAGSTONE_PATH = ITEMS.register(
      "deepslate_flagstone_path", () -> new BlockItem((Block)BlockInit.DEEPSLATE_FLAGSTONE_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_FLAGSTONE_SLAB = ITEMS.register(
      "deepslate_flagstone_slab", () -> new BlockItem((Block)BlockInit.DEEPSLATE_FLAGSTONE_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_FLAGSTONE = ITEMS.register(
      "deepslate_flagstone", () -> new EngravedBlockTooltip((Block)BlockInit.DEEPSLATE_FLAGSTONE.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_FLAGSTONE_PATH = ITEMS.register(
      "mud_brick_flagstone_path", () -> new BlockItem((Block)BlockInit.MUD_BRICK_FLAGSTONE_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_FLAGSTONE_SLAB = ITEMS.register(
      "mud_brick_flagstone_slab", () -> new BlockItem((Block)BlockInit.MUD_BRICK_FLAGSTONE_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_FLAGSTONE = ITEMS.register(
      "mud_brick_flagstone", () -> new EngravedBlockTooltip((Block)BlockInit.MUD_BRICK_FLAGSTONE.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_FLAGSTONE_PATH = ITEMS.register(
      "blackstone_flagstone_path", () -> new BlockItem((Block)BlockInit.BLACKSTONE_FLAGSTONE_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_FLAGSTONE_SLAB = ITEMS.register(
      "blackstone_flagstone_slab", () -> new BlockItem((Block)BlockInit.BLACKSTONE_FLAGSTONE_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_FLAGSTONE = ITEMS.register(
      "blackstone_flagstone", () -> new EngravedBlockTooltip((Block)BlockInit.BLACKSTONE_FLAGSTONE.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_FLAGSTONE_PATH = ITEMS.register(
      "dark_prismarine_flagstone_path", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_FLAGSTONE_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_FLAGSTONE_SLAB = ITEMS.register(
      "dark_prismarine_flagstone_slab", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_FLAGSTONE_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_FLAGSTONE = ITEMS.register(
      "dark_prismarine_flagstone", () -> new EngravedBlockTooltip((Block)BlockInit.DARK_PRISMARINE_FLAGSTONE.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_CRYSTAL_FLOOR_PATH = ITEMS.register(
      "andesite_crystal_floor_path", () -> new BlockItem((Block)BlockInit.ANDESITE_CRYSTAL_FLOOR_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_CRYSTAL_FLOOR_SLAB = ITEMS.register(
      "andesite_crystal_floor_slab", () -> new BlockItem((Block)BlockInit.ANDESITE_CRYSTAL_FLOOR_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_CRYSTAL_FLOOR = ITEMS.register(
      "andesite_crystal_floor", () -> new EngravedBlockTooltip((Block)BlockInit.ANDESITE_CRYSTAL_FLOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_CRYSTAL_FLOOR_PATH = ITEMS.register(
      "diorite_crystal_floor_path", () -> new BlockItem((Block)BlockInit.DIORITE_CRYSTAL_FLOOR_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_CRYSTAL_FLOOR_SLAB = ITEMS.register(
      "diorite_crystal_floor_slab", () -> new BlockItem((Block)BlockInit.DIORITE_CRYSTAL_FLOOR_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_CRYSTAL_FLOOR = ITEMS.register(
      "diorite_crystal_floor", () -> new EngravedBlockTooltip((Block)BlockInit.DIORITE_CRYSTAL_FLOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_CRYSTAL_FLOOR_PATH = ITEMS.register(
      "granite_crystal_floor_path", () -> new BlockItem((Block)BlockInit.GRANITE_CRYSTAL_FLOOR_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_CRYSTAL_FLOOR_SLAB = ITEMS.register(
      "granite_crystal_floor_slab", () -> new BlockItem((Block)BlockInit.GRANITE_CRYSTAL_FLOOR_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_CRYSTAL_FLOOR = ITEMS.register(
      "granite_crystal_floor", () -> new EngravedBlockTooltip((Block)BlockInit.GRANITE_CRYSTAL_FLOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_CRYSTAL_FLOOR_PATH = ITEMS.register(
      "sandstone_crystal_floor_path", () -> new BlockItem((Block)BlockInit.SANDSTONE_CRYSTAL_FLOOR_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_CRYSTAL_FLOOR_SLAB = ITEMS.register(
      "sandstone_crystal_floor_slab", () -> new BlockItem((Block)BlockInit.SANDSTONE_CRYSTAL_FLOOR_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_CRYSTAL_FLOOR = ITEMS.register(
      "sandstone_crystal_floor", () -> new EngravedBlockTooltip((Block)BlockInit.SANDSTONE_CRYSTAL_FLOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_CRYSTAL_FLOOR_PATH = ITEMS.register(
      "red_sandstone_crystal_floor_path", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_CRYSTAL_FLOOR_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_CRYSTAL_FLOOR_SLAB = ITEMS.register(
      "red_sandstone_crystal_floor_slab", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_CRYSTAL_FLOOR_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_CRYSTAL_FLOOR = ITEMS.register(
      "red_sandstone_crystal_floor", () -> new EngravedBlockTooltip((Block)BlockInit.RED_SANDSTONE_CRYSTAL_FLOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICK_CRYSTAL_FLOOR_PATH = ITEMS.register(
      "brick_crystal_floor_path", () -> new BlockItem((Block)BlockInit.BRICK_CRYSTAL_FLOOR_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICK_CRYSTAL_FLOOR_SLAB = ITEMS.register(
      "brick_crystal_floor_slab", () -> new BlockItem((Block)BlockInit.BRICK_CRYSTAL_FLOOR_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICK_CRYSTAL_FLOOR = ITEMS.register(
      "brick_crystal_floor", () -> new EngravedBlockTooltip((Block)BlockInit.BRICK_CRYSTAL_FLOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_CRYSTAL_FLOOR_PATH = ITEMS.register(
      "stone_crystal_floor_path", () -> new BlockItem((Block)BlockInit.STONE_CRYSTAL_FLOOR_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_CRYSTAL_FLOOR_SLAB = ITEMS.register(
      "stone_crystal_floor_slab", () -> new BlockItem((Block)BlockInit.STONE_CRYSTAL_FLOOR_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_CRYSTAL_FLOOR = ITEMS.register(
      "stone_crystal_floor", () -> new EngravedBlockTooltip((Block)BlockInit.STONE_CRYSTAL_FLOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_STONE_CRYSTAL_FLOOR_PATH = ITEMS.register(
      "mossy_stone_crystal_floor_path", () -> new BlockItem((Block)BlockInit.MOSSY_STONE_CRYSTAL_FLOOR_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_STONE_CRYSTAL_FLOOR_SLAB = ITEMS.register(
      "mossy_stone_crystal_floor_slab", () -> new BlockItem((Block)BlockInit.MOSSY_STONE_CRYSTAL_FLOOR_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_STONE_CRYSTAL_FLOOR = ITEMS.register(
      "mossy_stone_crystal_floor", () -> new EngravedBlockTooltip((Block)BlockInit.MOSSY_STONE_CRYSTAL_FLOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLED_DEEPSLATE_CRYSTAL_FLOOR_PATH = ITEMS.register(
      "cobbled_deepslate_crystal_floor_path", () -> new BlockItem((Block)BlockInit.COBBLED_DEEPSLATE_CRYSTAL_FLOOR_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLED_DEEPSLATE_CRYSTAL_FLOOR_SLAB = ITEMS.register(
      "cobbled_deepslate_crystal_floor_slab", () -> new BlockItem((Block)BlockInit.COBBLED_DEEPSLATE_CRYSTAL_FLOOR_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLED_DEEPSLATE_CRYSTAL_FLOOR = ITEMS.register(
      "cobbled_deepslate_crystal_floor", () -> new EngravedBlockTooltip((Block)BlockInit.COBBLED_DEEPSLATE_CRYSTAL_FLOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_CRYSTAL_FLOOR_PATH = ITEMS.register(
      "deepslate_crystal_floor_path", () -> new BlockItem((Block)BlockInit.DEEPSLATE_CRYSTAL_FLOOR_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_CRYSTAL_FLOOR_SLAB = ITEMS.register(
      "deepslate_crystal_floor_slab", () -> new BlockItem((Block)BlockInit.DEEPSLATE_CRYSTAL_FLOOR_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_CRYSTAL_FLOOR = ITEMS.register(
      "deepslate_crystal_floor", () -> new EngravedBlockTooltip((Block)BlockInit.DEEPSLATE_CRYSTAL_FLOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_CRYSTAL_FLOOR_PATH = ITEMS.register(
      "mud_brick_crystal_floor_path", () -> new BlockItem((Block)BlockInit.MUD_BRICK_CRYSTAL_FLOOR_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_CRYSTAL_FLOOR_SLAB = ITEMS.register(
      "mud_brick_crystal_floor_slab", () -> new BlockItem((Block)BlockInit.MUD_BRICK_CRYSTAL_FLOOR_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_CRYSTAL_FLOOR = ITEMS.register(
      "mud_brick_crystal_floor", () -> new EngravedBlockTooltip((Block)BlockInit.MUD_BRICK_CRYSTAL_FLOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_CRYSTAL_FLOOR_PATH = ITEMS.register(
      "blackstone_crystal_floor_path", () -> new BlockItem((Block)BlockInit.BLACKSTONE_CRYSTAL_FLOOR_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_CRYSTAL_FLOOR_SLAB = ITEMS.register(
      "blackstone_crystal_floor_slab", () -> new BlockItem((Block)BlockInit.BLACKSTONE_CRYSTAL_FLOOR_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_CRYSTAL_FLOOR = ITEMS.register(
      "blackstone_crystal_floor", () -> new EngravedBlockTooltip((Block)BlockInit.BLACKSTONE_CRYSTAL_FLOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_CRYSTAL_FLOOR_PATH = ITEMS.register(
      "dark_prismarine_crystal_floor_path", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_CRYSTAL_FLOOR_PATH.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_CRYSTAL_FLOOR_SLAB = ITEMS.register(
      "dark_prismarine_crystal_floor_slab", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_CRYSTAL_FLOOR_SLAB.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_CRYSTAL_FLOOR = ITEMS.register(
      "dark_prismarine_crystal_floor", () -> new EngravedBlockTooltip((Block)BlockInit.DARK_PRISMARINE_CRYSTAL_FLOOR.get(), new Properties())
   );
   public static final DeferredItem<Item> PODZOL_PATH_BLOCK = ITEMS.register(
      "podzol_path_block", () -> new FlattenedBlockTooltip((Block)BlockInit.PODZOL_PATH_BLOCK.get(), new Properties())
   );
   public static final DeferredItem<Item> DIRT_PATH_BLOCK = ITEMS.register(
      "dirt_path_block", () -> new FlattenedBlockTooltip((Block)BlockInit.DIRT_PATH_BLOCK.get(), new Properties())
   );
   public static final DeferredItem<Item> GRAVEL_PATH_BLOCK = ITEMS.register(
      "gravel_path_block", () -> new FlattenedBlockTooltip((Block)BlockInit.GRAVEL_PATH_BLOCK.get(), new Properties())
   );
   public static final DeferredItem<Item> SAND_PATH_BLOCK = ITEMS.register(
      "sand_path_block", () -> new FlattenedBlockTooltip((Block)BlockInit.SAND_PATH_BLOCK.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SAND_PATH_BLOCK = ITEMS.register(
      "red_sand_path_block", () -> new FlattenedBlockTooltip((Block)BlockInit.RED_SAND_PATH_BLOCK.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_RUNNING_BOND_STAIRS = ITEMS.register(
      "andesite_running_bond_stairs", () -> new BlockItem((Block)BlockInit.ANDESITE_RUNNING_BOND_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_RUNNING_BOND_STAIRS = ITEMS.register(
      "diorite_running_bond_stairs", () -> new BlockItem((Block)BlockInit.DIORITE_RUNNING_BOND_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_RUNNING_BOND_STAIRS = ITEMS.register(
      "granite_running_bond_stairs", () -> new BlockItem((Block)BlockInit.GRANITE_RUNNING_BOND_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_RUNNING_BOND_STAIRS = ITEMS.register(
      "sandstone_running_bond_stairs", () -> new BlockItem((Block)BlockInit.SANDSTONE_RUNNING_BOND_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_RUNNING_BOND_STAIRS = ITEMS.register(
      "red_sandstone_running_bond_stairs", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_RUNNING_BOND_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICK_RUNNING_BOND_STAIRS = ITEMS.register(
      "brick_running_bond_stairs", () -> new BlockItem((Block)BlockInit.BRICK_RUNNING_BOND_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_RUNNING_BOND_STAIRS = ITEMS.register(
      "stone_running_bond_stairs", () -> new BlockItem((Block)BlockInit.STONE_RUNNING_BOND_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_STONE_RUNNING_BOND_STAIRS = ITEMS.register(
      "mossy_stone_running_bond_stairs", () -> new BlockItem((Block)BlockInit.MOSSY_STONE_RUNNING_BOND_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLED_DEEPSLATE_RUNNING_BOND_STAIRS = ITEMS.register(
      "cobbled_deepslate_running_bond_stairs", () -> new BlockItem((Block)BlockInit.COBBLED_DEEPSLATE_RUNNING_BOND_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_RUNNING_BOND_STAIRS = ITEMS.register(
      "deepslate_running_bond_stairs", () -> new BlockItem((Block)BlockInit.DEEPSLATE_RUNNING_BOND_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_RUNNING_BOND_STAIRS = ITEMS.register(
      "mud_brick_running_bond_stairs", () -> new BlockItem((Block)BlockInit.MUD_BRICK_RUNNING_BOND_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_RUNNING_BOND_STAIRS = ITEMS.register(
      "blackstone_running_bond_stairs", () -> new BlockItem((Block)BlockInit.BLACKSTONE_RUNNING_BOND_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_RUNNING_BOND_STAIRS = ITEMS.register(
      "dark_prismarine_running_bond_stairs", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_RUNNING_BOND_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_WINDMILL_WEAVE_STAIRS = ITEMS.register(
      "andesite_windmill_weave_stairs", () -> new BlockItem((Block)BlockInit.ANDESITE_WINDMILL_WEAVE_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_WINDMILL_WEAVE_STAIRS = ITEMS.register(
      "diorite_windmill_weave_stairs", () -> new BlockItem((Block)BlockInit.DIORITE_WINDMILL_WEAVE_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_WINDMILL_WEAVE_STAIRS = ITEMS.register(
      "granite_windmill_weave_stairs", () -> new BlockItem((Block)BlockInit.GRANITE_WINDMILL_WEAVE_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_WINDMILL_WEAVE_STAIRS = ITEMS.register(
      "sandstone_windmill_weave_stairs", () -> new BlockItem((Block)BlockInit.SANDSTONE_WINDMILL_WEAVE_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_WINDMILL_WEAVE_STAIRS = ITEMS.register(
      "red_sandstone_windmill_weave_stairs", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_WINDMILL_WEAVE_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICK_WINDMILL_WEAVE_STAIRS = ITEMS.register(
      "brick_windmill_weave_stairs", () -> new BlockItem((Block)BlockInit.BRICK_WINDMILL_WEAVE_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_WINDMILL_WEAVE_STAIRS = ITEMS.register(
      "stone_windmill_weave_stairs", () -> new BlockItem((Block)BlockInit.STONE_WINDMILL_WEAVE_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_STONE_WINDMILL_WEAVE_STAIRS = ITEMS.register(
      "mossy_stone_windmill_weave_stairs", () -> new BlockItem((Block)BlockInit.MOSSY_STONE_WINDMILL_WEAVE_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLED_DEEPSLATE_WINDMILL_WEAVE_STAIRS = ITEMS.register(
      "cobbled_deepslate_windmill_weave_stairs", () -> new BlockItem((Block)BlockInit.COBBLED_DEEPSLATE_WINDMILL_WEAVE_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_WINDMILL_WEAVE_STAIRS = ITEMS.register(
      "deepslate_windmill_weave_stairs", () -> new BlockItem((Block)BlockInit.DEEPSLATE_WINDMILL_WEAVE_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_WINDMILL_WEAVE_STAIRS = ITEMS.register(
      "mud_brick_windmill_weave_stairs", () -> new BlockItem((Block)BlockInit.MUD_BRICK_WINDMILL_WEAVE_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_WINDMILL_WEAVE_STAIRS = ITEMS.register(
      "blackstone_windmill_weave_stairs", () -> new BlockItem((Block)BlockInit.BLACKSTONE_WINDMILL_WEAVE_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_WINDMILL_WEAVE_STAIRS = ITEMS.register(
      "dark_prismarine_windmill_weave_stairs", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_WINDMILL_WEAVE_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_FLAGSTONE_STAIRS = ITEMS.register(
      "andesite_flagstone_stairs", () -> new BlockItem((Block)BlockInit.ANDESITE_FLAGSTONE_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_FLAGSTONE_STAIRS = ITEMS.register(
      "diorite_flagstone_stairs", () -> new BlockItem((Block)BlockInit.DIORITE_FLAGSTONE_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_FLAGSTONE_STAIRS = ITEMS.register(
      "granite_flagstone_stairs", () -> new BlockItem((Block)BlockInit.GRANITE_FLAGSTONE_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_FLAGSTONE_STAIRS = ITEMS.register(
      "sandstone_flagstone_stairs", () -> new BlockItem((Block)BlockInit.SANDSTONE_FLAGSTONE_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_FLAGSTONE_STAIRS = ITEMS.register(
      "red_sandstone_flagstone_stairs", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_FLAGSTONE_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICK_FLAGSTONE_STAIRS = ITEMS.register(
      "brick_flagstone_stairs", () -> new BlockItem((Block)BlockInit.BRICK_FLAGSTONE_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_FLAGSTONE_STAIRS = ITEMS.register(
      "stone_flagstone_stairs", () -> new BlockItem((Block)BlockInit.STONE_FLAGSTONE_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_STONE_FLAGSTONE_STAIRS = ITEMS.register(
      "mossy_stone_flagstone_stairs", () -> new BlockItem((Block)BlockInit.MOSSY_STONE_FLAGSTONE_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLED_DEEPSLATE_FLAGSTONE_STAIRS = ITEMS.register(
      "cobbled_deepslate_flagstone_stairs", () -> new BlockItem((Block)BlockInit.COBBLED_DEEPSLATE_FLAGSTONE_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_FLAGSTONE_STAIRS = ITEMS.register(
      "deepslate_flagstone_stairs", () -> new BlockItem((Block)BlockInit.DEEPSLATE_FLAGSTONE_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_FLAGSTONE_STAIRS = ITEMS.register(
      "mud_brick_flagstone_stairs", () -> new BlockItem((Block)BlockInit.MUD_BRICK_FLAGSTONE_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_FLAGSTONE_STAIRS = ITEMS.register(
      "blackstone_flagstone_stairs", () -> new BlockItem((Block)BlockInit.BLACKSTONE_FLAGSTONE_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_FLAGSTONE_STAIRS = ITEMS.register(
      "dark_prismarine_flagstone_stairs", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_FLAGSTONE_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> ANDESITE_CRYSTAL_FLOOR_STAIRS = ITEMS.register(
      "andesite_crystal_floor_stairs", () -> new BlockItem((Block)BlockInit.ANDESITE_CRYSTAL_FLOOR_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> DIORITE_CRYSTAL_FLOOR_STAIRS = ITEMS.register(
      "diorite_crystal_floor_stairs", () -> new BlockItem((Block)BlockInit.DIORITE_CRYSTAL_FLOOR_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> GRANITE_CRYSTAL_FLOOR_STAIRS = ITEMS.register(
      "granite_crystal_floor_stairs", () -> new BlockItem((Block)BlockInit.GRANITE_CRYSTAL_FLOOR_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> SANDSTONE_CRYSTAL_FLOOR_STAIRS = ITEMS.register(
      "sandstone_crystal_floor_stairs", () -> new BlockItem((Block)BlockInit.SANDSTONE_CRYSTAL_FLOOR_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> RED_SANDSTONE_CRYSTAL_FLOOR_STAIRS = ITEMS.register(
      "red_sandstone_crystal_floor_stairs", () -> new BlockItem((Block)BlockInit.RED_SANDSTONE_CRYSTAL_FLOOR_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> BRICK_CRYSTAL_FLOOR_STAIRS = ITEMS.register(
      "brick_crystal_floor_stairs", () -> new BlockItem((Block)BlockInit.BRICK_CRYSTAL_FLOOR_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> STONE_CRYSTAL_FLOOR_STAIRS = ITEMS.register(
      "stone_crystal_floor_stairs", () -> new BlockItem((Block)BlockInit.STONE_CRYSTAL_FLOOR_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> MOSSY_STONE_CRYSTAL_FLOOR_STAIRS = ITEMS.register(
      "mossy_stone_crystal_floor_stairs", () -> new BlockItem((Block)BlockInit.MOSSY_STONE_CRYSTAL_FLOOR_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> COBBLED_DEEPSLATE_CRYSTAL_FLOOR_STAIRS = ITEMS.register(
      "cobbled_deepslate_crystal_floor_stairs", () -> new BlockItem((Block)BlockInit.COBBLED_DEEPSLATE_CRYSTAL_FLOOR_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> DEEPSLATE_CRYSTAL_FLOOR_STAIRS = ITEMS.register(
      "deepslate_crystal_floor_stairs", () -> new BlockItem((Block)BlockInit.DEEPSLATE_CRYSTAL_FLOOR_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> MUD_BRICK_CRYSTAL_FLOOR_STAIRS = ITEMS.register(
      "mud_brick_crystal_floor_stairs", () -> new BlockItem((Block)BlockInit.MUD_BRICK_CRYSTAL_FLOOR_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> BLACKSTONE_CRYSTAL_FLOOR_STAIRS = ITEMS.register(
      "blackstone_crystal_floor_stairs", () -> new BlockItem((Block)BlockInit.BLACKSTONE_CRYSTAL_FLOOR_STAIRS.get(), new Properties())
   );
   public static final DeferredItem<Item> DARK_PRISMARINE_CRYSTAL_FLOOR_STAIRS = ITEMS.register(
      "dark_prismarine_crystal_floor_stairs", () -> new BlockItem((Block)BlockInit.DARK_PRISMARINE_CRYSTAL_FLOOR_STAIRS.get(), new Properties())
   );
}

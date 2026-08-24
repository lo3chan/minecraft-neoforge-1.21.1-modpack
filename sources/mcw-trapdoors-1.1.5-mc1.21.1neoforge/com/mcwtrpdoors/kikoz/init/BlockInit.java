package com.mcwtrpdoors.kikoz.init;

import com.mcwtrpdoors.kikoz.objects.NonCullTrapdoor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Blocks;

public class BlockInit {
   public static final Blocks BLOCKS = DeferredRegister.createBlocks("mcwtrpdoors");
   public static final DeferredBlock<Block> OAK_BARN_TRAPDOOR = BLOCKS.register(
      "oak_barn_trapdoor", () -> new TrapDoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> OAK_BARRED_TRAPDOOR = BLOCKS.register(
      "oak_barred_trapdoor", () -> new TrapDoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> OAK_BEACH_TRAPDOOR = BLOCKS.register(
      "oak_beach_trapdoor", () -> new TrapDoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> OAK_COTTAGE_TRAPDOOR = BLOCKS.register(
      "oak_cottage_trapdoor", () -> new TrapDoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> OAK_FOUR_PANEL_TRAPDOOR = BLOCKS.register(
      "oak_four_panel_trapdoor", () -> new TrapDoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> OAK_GLASS_TRAPDOOR = BLOCKS.register(
      "oak_glass_trapdoor", () -> new TrapDoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> OAK_MYSTIC_TRAPDOOR = BLOCKS.register(
      "oak_mystic_trapdoor", () -> new TrapDoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> OAK_PAPER_TRAPDOOR = BLOCKS.register(
      "oak_paper_trapdoor", () -> new TrapDoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> OAK_TROPICAL_TRAPDOOR = BLOCKS.register(
      "oak_tropical_trapdoor", () -> new TrapDoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> OAK_SWAMP_TRAPDOOR = BLOCKS.register(
      "oak_swamp_trapdoor", () -> new TrapDoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> OAK_BAMBOO_TRAPDOOR = BLOCKS.register(
      "oak_bamboo_trapdoor", () -> new TrapDoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_BARN_TRAPDOOR = BLOCKS.register(
      "spruce_barn_trapdoor", () -> new TrapDoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_BARRED_TRAPDOOR = BLOCKS.register(
      "spruce_barred_trapdoor", () -> new TrapDoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_BEACH_TRAPDOOR = BLOCKS.register(
      "spruce_beach_trapdoor", () -> new TrapDoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_CLASSIC_TRAPDOOR = BLOCKS.register(
      "spruce_classic_trapdoor", () -> new TrapDoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_FOUR_PANEL_TRAPDOOR = BLOCKS.register(
      "spruce_four_panel_trapdoor", () -> new TrapDoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_GLASS_TRAPDOOR = BLOCKS.register(
      "spruce_glass_trapdoor", () -> new TrapDoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_MYSTIC_TRAPDOOR = BLOCKS.register(
      "spruce_mystic_trapdoor", () -> new TrapDoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_PAPER_TRAPDOOR = BLOCKS.register(
      "spruce_paper_trapdoor", () -> new TrapDoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_TROPICAL_TRAPDOOR = BLOCKS.register(
      "spruce_tropical_trapdoor", () -> new TrapDoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_SWAMP_TRAPDOOR = BLOCKS.register(
      "spruce_swamp_trapdoor", () -> new TrapDoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_BAMBOO_TRAPDOOR = BLOCKS.register(
      "spruce_bamboo_trapdoor", () -> new TrapDoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BIRCH_BARN_TRAPDOOR = BLOCKS.register(
      "birch_barn_trapdoor", () -> new TrapDoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BIRCH_BARRED_TRAPDOOR = BLOCKS.register(
      "birch_barred_trapdoor", () -> new TrapDoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BIRCH_BEACH_TRAPDOOR = BLOCKS.register(
      "birch_beach_trapdoor", () -> new TrapDoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BIRCH_CLASSIC_TRAPDOOR = BLOCKS.register(
      "birch_classic_trapdoor", () -> new TrapDoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BIRCH_COTTAGE_TRAPDOOR = BLOCKS.register(
      "birch_cottage_trapdoor", () -> new TrapDoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BIRCH_FOUR_PANEL_TRAPDOOR = BLOCKS.register(
      "birch_four_panel_trapdoor", () -> new TrapDoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BIRCH_GLASS_TRAPDOOR = BLOCKS.register(
      "birch_glass_trapdoor", () -> new TrapDoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BIRCH_MYSTIC_TRAPDOOR = BLOCKS.register(
      "birch_mystic_trapdoor", () -> new TrapDoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BIRCH_TROPICAL_TRAPDOOR = BLOCKS.register(
      "birch_tropical_trapdoor", () -> new TrapDoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BIRCH_SWAMP_TRAPDOOR = BLOCKS.register(
      "birch_swamp_trapdoor", () -> new TrapDoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BIRCH_BAMBOO_TRAPDOOR = BLOCKS.register(
      "birch_bamboo_trapdoor", () -> new TrapDoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_TRAPDOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_BARN_TRAPDOOR = BLOCKS.register(
      "jungle_barn_trapdoor", () -> new TrapDoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_BARRED_TRAPDOOR = BLOCKS.register(
      "jungle_barred_trapdoor", () -> new TrapDoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_CLASSIC_TRAPDOOR = BLOCKS.register(
      "jungle_classic_trapdoor", () -> new TrapDoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_COTTAGE_TRAPDOOR = BLOCKS.register(
      "jungle_cottage_trapdoor", () -> new TrapDoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_FOUR_PANEL_TRAPDOOR = BLOCKS.register(
      "jungle_four_panel_trapdoor", () -> new TrapDoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_GLASS_TRAPDOOR = BLOCKS.register(
      "jungle_glass_trapdoor", () -> new TrapDoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_MYSTIC_TRAPDOOR = BLOCKS.register(
      "jungle_mystic_trapdoor", () -> new TrapDoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_PAPER_TRAPDOOR = BLOCKS.register(
      "jungle_paper_trapdoor", () -> new TrapDoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_TROPICAL_TRAPDOOR = BLOCKS.register(
      "jungle_tropical_trapdoor", () -> new TrapDoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_SWAMP_TRAPDOOR = BLOCKS.register(
      "jungle_swamp_trapdoor", () -> new TrapDoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_BAMBOO_TRAPDOOR = BLOCKS.register(
      "jungle_bamboo_trapdoor", () -> new TrapDoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> ACACIA_BARN_TRAPDOOR = BLOCKS.register(
      "acacia_barn_trapdoor", () -> new TrapDoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_TRAPDOOR))
   );
   public static final DeferredBlock<Block> ACACIA_BARRED_TRAPDOOR = BLOCKS.register(
      "acacia_barred_trapdoor", () -> new TrapDoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_TRAPDOOR))
   );
   public static final DeferredBlock<Block> ACACIA_BEACH_TRAPDOOR = BLOCKS.register(
      "acacia_beach_trapdoor", () -> new TrapDoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_TRAPDOOR))
   );
   public static final DeferredBlock<Block> ACACIA_CLASSIC_TRAPDOOR = BLOCKS.register(
      "acacia_classic_trapdoor", () -> new TrapDoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_TRAPDOOR))
   );
   public static final DeferredBlock<Block> ACACIA_COTTAGE_TRAPDOOR = BLOCKS.register(
      "acacia_cottage_trapdoor", () -> new TrapDoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_TRAPDOOR))
   );
   public static final DeferredBlock<Block> ACACIA_FOUR_PANEL_TRAPDOOR = BLOCKS.register(
      "acacia_four_panel_trapdoor", () -> new TrapDoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_TRAPDOOR))
   );
   public static final DeferredBlock<Block> ACACIA_GLASS_TRAPDOOR = BLOCKS.register(
      "acacia_glass_trapdoor", () -> new TrapDoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_TRAPDOOR))
   );
   public static final DeferredBlock<Block> ACACIA_MYSTIC_TRAPDOOR = BLOCKS.register(
      "acacia_mystic_trapdoor", () -> new TrapDoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_TRAPDOOR))
   );
   public static final DeferredBlock<Block> ACACIA_PAPER_TRAPDOOR = BLOCKS.register(
      "acacia_paper_trapdoor", () -> new TrapDoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_TRAPDOOR))
   );
   public static final DeferredBlock<Block> ACACIA_SWAMP_TRAPDOOR = BLOCKS.register(
      "acacia_swamp_trapdoor", () -> new TrapDoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_TRAPDOOR))
   );
   public static final DeferredBlock<Block> ACACIA_BAMBOO_TRAPDOOR = BLOCKS.register(
      "acacia_bamboo_trapdoor", () -> new TrapDoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_TRAPDOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_BARN_TRAPDOOR = BLOCKS.register(
      "dark_oak_barn_trapdoor", () -> new TrapDoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_BARRED_TRAPDOOR = BLOCKS.register(
      "dark_oak_barred_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_BEACH_TRAPDOOR = BLOCKS.register(
      "dark_oak_beach_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_CLASSIC_TRAPDOOR = BLOCKS.register(
      "dark_oak_classic_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_COTTAGE_TRAPDOOR = BLOCKS.register(
      "dark_oak_cottage_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_GLASS_TRAPDOOR = BLOCKS.register(
      "dark_oak_glass_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_MYSTIC_TRAPDOOR = BLOCKS.register(
      "dark_oak_mystic_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_PAPER_TRAPDOOR = BLOCKS.register(
      "dark_oak_paper_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_TROPICAL_TRAPDOOR = BLOCKS.register(
      "dark_oak_tropical_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_SWAMP_TRAPDOOR = BLOCKS.register(
      "dark_oak_swamp_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_BAMBOO_TRAPDOOR = BLOCKS.register(
      "dark_oak_bamboo_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_BARN_TRAPDOOR = BLOCKS.register(
      "crimson_barn_trapdoor", () -> new TrapDoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_BEACH_TRAPDOOR = BLOCKS.register(
      "crimson_beach_trapdoor", () -> new TrapDoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_CLASSIC_TRAPDOOR = BLOCKS.register(
      "crimson_classic_trapdoor", () -> new TrapDoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_COTTAGE_TRAPDOOR = BLOCKS.register(
      "crimson_cottage_trapdoor", () -> new TrapDoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_FOUR_PANEL_TRAPDOOR = BLOCKS.register(
      "crimson_four_panel_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_GLASS_TRAPDOOR = BLOCKS.register(
      "crimson_glass_trapdoor", () -> new TrapDoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_MYSTIC_TRAPDOOR = BLOCKS.register(
      "crimson_mystic_trapdoor", () -> new TrapDoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_PAPER_TRAPDOOR = BLOCKS.register(
      "crimson_paper_trapdoor", () -> new TrapDoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_TROPICAL_TRAPDOOR = BLOCKS.register(
      "crimson_tropical_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_SWAMP_TRAPDOOR = BLOCKS.register(
      "crimson_swamp_trapdoor", () -> new TrapDoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_BAMBOO_TRAPDOOR = BLOCKS.register(
      "crimson_bamboo_trapdoor", () -> new TrapDoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_TRAPDOOR))
   );
   public static final DeferredBlock<Block> WARPED_BARN_TRAPDOOR = BLOCKS.register(
      "warped_barn_trapdoor", () -> new TrapDoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_TRAPDOOR))
   );
   public static final DeferredBlock<Block> WARPED_BARRED_TRAPDOOR = BLOCKS.register(
      "warped_barred_trapdoor", () -> new TrapDoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_TRAPDOOR))
   );
   public static final DeferredBlock<Block> WARPED_BEACH_TRAPDOOR = BLOCKS.register(
      "warped_beach_trapdoor", () -> new TrapDoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_TRAPDOOR))
   );
   public static final DeferredBlock<Block> WARPED_CLASSIC_TRAPDOOR = BLOCKS.register(
      "warped_classic_trapdoor", () -> new TrapDoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_TRAPDOOR))
   );
   public static final DeferredBlock<Block> WARPED_COTTAGE_TRAPDOOR = BLOCKS.register(
      "warped_cottage_trapdoor", () -> new TrapDoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_TRAPDOOR))
   );
   public static final DeferredBlock<Block> WARPED_FOUR_PANEL_TRAPDOOR = BLOCKS.register(
      "warped_four_panel_trapdoor", () -> new TrapDoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_TRAPDOOR))
   );
   public static final DeferredBlock<Block> WARPED_GLASS_TRAPDOOR = BLOCKS.register(
      "warped_glass_trapdoor", () -> new TrapDoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_TRAPDOOR))
   );
   public static final DeferredBlock<Block> WARPED_PAPER_TRAPDOOR = BLOCKS.register(
      "warped_paper_trapdoor", () -> new TrapDoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_TRAPDOOR))
   );
   public static final DeferredBlock<Block> WARPED_TROPICAL_TRAPDOOR = BLOCKS.register(
      "warped_tropical_trapdoor", () -> new TrapDoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_TRAPDOOR))
   );
   public static final DeferredBlock<Block> WARPED_SWAMP_TRAPDOOR = BLOCKS.register(
      "warped_swamp_trapdoor", () -> new TrapDoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_TRAPDOOR))
   );
   public static final DeferredBlock<Block> WARPED_BAMBOO_TRAPDOOR = BLOCKS.register(
      "warped_bamboo_trapdoor", () -> new TrapDoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_TRAPDOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_BARN_TRAPDOOR = BLOCKS.register(
      "mangrove_barn_trapdoor", () -> new TrapDoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_BARRED_TRAPDOOR = BLOCKS.register(
      "mangrove_barred_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_BEACH_TRAPDOOR = BLOCKS.register(
      "mangrove_beach_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_CLASSIC_TRAPDOOR = BLOCKS.register(
      "mangrove_classic_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_COTTAGE_TRAPDOOR = BLOCKS.register(
      "mangrove_cottage_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_FOUR_PANEL_TRAPDOOR = BLOCKS.register(
      "mangrove_four_panel_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_GLASS_TRAPDOOR = BLOCKS.register(
      "mangrove_glass_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_PAPER_TRAPDOOR = BLOCKS.register(
      "mangrove_paper_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_TROPICAL_TRAPDOOR = BLOCKS.register(
      "mangrove_tropical_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_MYSTIC_TRAPDOOR = BLOCKS.register(
      "mangrove_mystic_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_BAMBOO_TRAPDOOR = BLOCKS.register(
      "mangrove_bamboo_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> OAK_BARK_TRAPDOOR = BLOCKS.register(
      "oak_bark_trapdoor", () -> new NonCullTrapdoor(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_BARK_TRAPDOOR = BLOCKS.register(
      "spruce_bark_trapdoor", () -> new NonCullTrapdoor(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BIRCH_BARK_TRAPDOOR = BLOCKS.register(
      "birch_bark_trapdoor", () -> new NonCullTrapdoor(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_TRAPDOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_BARK_TRAPDOOR = BLOCKS.register(
      "jungle_bark_trapdoor", () -> new NonCullTrapdoor(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> ACACIA_BARK_TRAPDOOR = BLOCKS.register(
      "acacia_bark_trapdoor", () -> new NonCullTrapdoor(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_TRAPDOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_BARK_TRAPDOOR = BLOCKS.register(
      "dark_oak_bark_trapdoor",
      () -> new NonCullTrapdoor(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_BARK_TRAPDOOR = BLOCKS.register(
      "crimson_bark_trapdoor", () -> new NonCullTrapdoor(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_TRAPDOOR))
   );
   public static final DeferredBlock<Block> WARPED_BARK_TRAPDOOR = BLOCKS.register(
      "warped_bark_trapdoor", () -> new NonCullTrapdoor(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_TRAPDOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_BARK_TRAPDOOR = BLOCKS.register(
      "mangrove_bark_trapdoor",
      () -> new NonCullTrapdoor(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_BARN_TRAPDOOR = BLOCKS.register(
      "bamboo_barn_trapdoor", () -> new TrapDoorBlock(BlockSetType.BAMBOO, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_BARRED_TRAPDOOR = BLOCKS.register(
      "bamboo_barred_trapdoor", () -> new TrapDoorBlock(BlockSetType.BAMBOO, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_BEACH_TRAPDOOR = BLOCKS.register(
      "bamboo_beach_trapdoor", () -> new TrapDoorBlock(BlockSetType.BAMBOO, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_CLASSIC_TRAPDOOR = BLOCKS.register(
      "bamboo_classic_trapdoor", () -> new TrapDoorBlock(BlockSetType.BAMBOO, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_COTTAGE_TRAPDOOR = BLOCKS.register(
      "bamboo_cottage_trapdoor", () -> new TrapDoorBlock(BlockSetType.BAMBOO, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_FOUR_PANEL_TRAPDOOR = BLOCKS.register(
      "bamboo_four_panel_trapdoor", () -> new TrapDoorBlock(BlockSetType.BAMBOO, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_GLASS_TRAPDOOR = BLOCKS.register(
      "bamboo_glass_trapdoor", () -> new TrapDoorBlock(BlockSetType.BAMBOO, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_MYSTIC_TRAPDOOR = BLOCKS.register(
      "bamboo_mystic_trapdoor", () -> new TrapDoorBlock(BlockSetType.BAMBOO, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_PAPER_TRAPDOOR = BLOCKS.register(
      "bamboo_paper_trapdoor", () -> new TrapDoorBlock(BlockSetType.BAMBOO, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_TROPICAL_TRAPDOOR = BLOCKS.register(
      "bamboo_tropical_trapdoor", () -> new TrapDoorBlock(BlockSetType.BAMBOO, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_SWAMP_TRAPDOOR = BLOCKS.register(
      "bamboo_swamp_trapdoor", () -> new TrapDoorBlock(BlockSetType.BAMBOO, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_TRAPDOOR))
   );
   public static final DeferredBlock<Block> OAK_RANCH_TRAPDOOR = BLOCKS.register(
      "oak_ranch_trapdoor", () -> new NonCullTrapdoor(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_RANCH_TRAPDOOR = BLOCKS.register(
      "spruce_ranch_trapdoor", () -> new NonCullTrapdoor(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BIRCH_RANCH_TRAPDOOR = BLOCKS.register(
      "birch_ranch_trapdoor", () -> new NonCullTrapdoor(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_TRAPDOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_RANCH_TRAPDOOR = BLOCKS.register(
      "jungle_ranch_trapdoor", () -> new NonCullTrapdoor(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> ACACIA_RANCH_TRAPDOOR = BLOCKS.register(
      "acacia_ranch_trapdoor", () -> new NonCullTrapdoor(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_TRAPDOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_RANCH_TRAPDOOR = BLOCKS.register(
      "dark_oak_ranch_trapdoor",
      () -> new NonCullTrapdoor(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_RANCH_TRAPDOOR = BLOCKS.register(
      "crimson_ranch_trapdoor", () -> new NonCullTrapdoor(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_TRAPDOOR))
   );
   public static final DeferredBlock<Block> WARPED_RANCH_TRAPDOOR = BLOCKS.register(
      "warped_ranch_trapdoor", () -> new NonCullTrapdoor(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_TRAPDOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_RANCH_TRAPDOOR = BLOCKS.register(
      "mangrove_ranch_trapdoor",
      () -> new NonCullTrapdoor(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_TRAPDOOR = BLOCKS.register(
      "bamboo_trapdoor", () -> new NonCullTrapdoor(BlockSetType.BAMBOO, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_TRAPDOOR))
   );
   public static final DeferredBlock<Block> METAL_TRAPDOOR = BLOCKS.register(
      "metal_trapdoor", () -> new TrapDoorBlock(BlockSetType.IRON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.IRON_TRAPDOOR))
   );
   public static final DeferredBlock<Block> METAL_WARNING_TRAPDOOR = BLOCKS.register(
      "metal_warning_trapdoor", () -> new TrapDoorBlock(BlockSetType.IRON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.IRON_TRAPDOOR))
   );
   public static final DeferredBlock<Block> METAL_FULL_TRAPDOOR = BLOCKS.register(
      "metal_full_trapdoor", () -> new TrapDoorBlock(BlockSetType.IRON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.IRON_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CHERRY_BARN_TRAPDOOR = BLOCKS.register(
      "cherry_barn_trapdoor", () -> new TrapDoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CHERRY_BARRED_TRAPDOOR = BLOCKS.register(
      "cherry_barred_trapdoor", () -> new TrapDoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CHERRY_BEACH_TRAPDOOR = BLOCKS.register(
      "cherry_beach_trapdoor", () -> new TrapDoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CHERRY_CLASSIC_TRAPDOOR = BLOCKS.register(
      "cherry_classic_trapdoor", () -> new TrapDoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CHERRY_COTTAGE_TRAPDOOR = BLOCKS.register(
      "cherry_cottage_trapdoor", () -> new TrapDoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CHERRY_FOUR_PANEL_TRAPDOOR = BLOCKS.register(
      "cherry_four_panel_trapdoor", () -> new TrapDoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CHERRY_GLASS_TRAPDOOR = BLOCKS.register(
      "cherry_glass_trapdoor", () -> new TrapDoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CHERRY_PAPER_TRAPDOOR = BLOCKS.register(
      "cherry_paper_trapdoor", () -> new TrapDoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CHERRY_TROPICAL_TRAPDOOR = BLOCKS.register(
      "cherry_tropical_trapdoor", () -> new TrapDoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CHERRY_MYSTIC_TRAPDOOR = BLOCKS.register(
      "cherry_mystic_trapdoor", () -> new TrapDoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CHERRY_BAMBOO_TRAPDOOR = BLOCKS.register(
      "cherry_bamboo_trapdoor", () -> new TrapDoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CHERRY_SWAMP_TRAPDOOR = BLOCKS.register(
      "cherry_swamp_trapdoor", () -> new TrapDoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CHERRY_RANCH_TRAPDOOR = BLOCKS.register(
      "cherry_ranch_trapdoor", () -> new NonCullTrapdoor(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CHERRY_BARK_TRAPDOOR = BLOCKS.register(
      "cherry_bark_trapdoor", () -> new NonCullTrapdoor(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_TRAPDOOR))
   );
   public static final DeferredBlock<Block> OAK_BLOSSOM_TRAPDOOR = BLOCKS.register(
      "oak_blossom_trapdoor", () -> new TrapDoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_BLOSSOM_TRAPDOOR = BLOCKS.register(
      "spruce_blossom_trapdoor", () -> new TrapDoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BIRCH_BLOSSOM_TRAPDOOR = BLOCKS.register(
      "birch_blossom_trapdoor", () -> new TrapDoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_TRAPDOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_BLOSSOM_TRAPDOOR = BLOCKS.register(
      "jungle_blossom_trapdoor", () -> new TrapDoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> ACACIA_BLOSSOM_TRAPDOOR = BLOCKS.register(
      "acacia_blossom_trapdoor", () -> new TrapDoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_TRAPDOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_BLOSSOM_TRAPDOOR = BLOCKS.register(
      "dark_oak_blossom_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_BLOSSOM_TRAPDOOR = BLOCKS.register(
      "mangrove_blossom_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> WARPED_BLOSSOM_TRAPDOOR = BLOCKS.register(
      "warped_blossom_trapdoor", () -> new TrapDoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_BLOSSOM_TRAPDOOR = BLOCKS.register(
      "crimson_blossom_trapdoor", () -> new TrapDoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_BLOSSOM_TRAPDOOR = BLOCKS.register(
      "bamboo_blossom_trapdoor", () -> new TrapDoorBlock(BlockSetType.BAMBOO, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_TRAPDOOR))
   );
   public static final DeferredBlock<Block> OAK_BARREL_TRAPDOOR = BLOCKS.register(
      "oak_barrel_trapdoor", () -> new TrapDoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_BARREL_TRAPDOOR = BLOCKS.register(
      "spruce_barrel_trapdoor", () -> new TrapDoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BIRCH_BARREL_TRAPDOOR = BLOCKS.register(
      "birch_barrel_trapdoor", () -> new TrapDoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_TRAPDOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_BARREL_TRAPDOOR = BLOCKS.register(
      "jungle_barrel_trapdoor", () -> new TrapDoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> ACACIA_BARREL_TRAPDOOR = BLOCKS.register(
      "acacia_barrel_trapdoor", () -> new TrapDoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_TRAPDOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_BARREL_TRAPDOOR = BLOCKS.register(
      "dark_oak_barrel_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_BARREL_TRAPDOOR = BLOCKS.register(
      "mangrove_barrel_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> WARPED_BARREL_TRAPDOOR = BLOCKS.register(
      "warped_barrel_trapdoor", () -> new TrapDoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_BARREL_TRAPDOOR = BLOCKS.register(
      "crimson_barrel_trapdoor", () -> new TrapDoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_BARREL_TRAPDOOR = BLOCKS.register(
      "bamboo_barrel_trapdoor", () -> new TrapDoorBlock(BlockSetType.BAMBOO, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CHERRY_BARREL_TRAPDOOR = BLOCKS.register(
      "cherry_barrel_trapdoor", () -> new TrapDoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_BARK_TRAPDOOR = BLOCKS.register(
      "bamboo_bark_trapdoor", () -> new NonCullTrapdoor(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> OAK_WHISPERING_TRAPDOOR = BLOCKS.register(
      "oak_whispering_trapdoor", () -> new TrapDoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_WHISPERING_TRAPDOOR = BLOCKS.register(
      "spruce_whispering_trapdoor", () -> new TrapDoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BIRCH_WHISPERING_TRAPDOOR = BLOCKS.register(
      "birch_whispering_trapdoor", () -> new TrapDoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_TRAPDOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_WHISPERING_TRAPDOOR = BLOCKS.register(
      "jungle_whispering_trapdoor", () -> new TrapDoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> ACACIA_WHISPERING_TRAPDOOR = BLOCKS.register(
      "acacia_whispering_trapdoor", () -> new TrapDoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_TRAPDOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_WHISPERING_TRAPDOOR = BLOCKS.register(
      "dark_oak_whispering_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_TRAPDOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_WHISPERING_TRAPDOOR = BLOCKS.register(
      "mangrove_whispering_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_TRAPDOOR))
   );
   public static final DeferredBlock<Block> WARPED_WHISPERING_TRAPDOOR = BLOCKS.register(
      "warped_whispering_trapdoor", () -> new TrapDoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_WHISPERING_TRAPDOOR = BLOCKS.register(
      "crimson_whispering_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_TRAPDOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_WHISPERING_TRAPDOOR = BLOCKS.register(
      "bamboo_whispering_trapdoor", () -> new TrapDoorBlock(BlockSetType.BAMBOO, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_TRAPDOOR))
   );
   public static final DeferredBlock<Block> CHERRY_WHISPERING_TRAPDOOR = BLOCKS.register(
      "cherry_whispering_trapdoor", () -> new TrapDoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_TRAPDOOR))
   );
}

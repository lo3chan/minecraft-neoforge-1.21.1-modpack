package com.mcwdoors.kikoz.init;

import com.mcwdoors.kikoz.objects.GarageDoor;
import com.mcwdoors.kikoz.objects.JapaneseDoors;
import com.mcwdoors.kikoz.objects.StableDoor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Blocks;

public class BlockInit {
   public static final Blocks BLOCKS = DeferredRegister.createBlocks("mcwdoors");
   public static final DeferredBlock<Block> OAK_JAPANESE_DOOR = BLOCKS.register(
      "oak_japanese_door", () -> new JapaneseDoors(Properties.of().noOcclusion().strength(1.5F, 1.0F).sound(SoundType.SCAFFOLDING), BlockSetType.OAK)
   );
   public static final DeferredBlock<Block> SPRUCE_JAPANESE_DOOR = BLOCKS.register(
      "spruce_japanese_door", () -> new JapaneseDoors(Properties.of().noOcclusion().strength(1.5F, 1.0F).sound(SoundType.SCAFFOLDING), BlockSetType.SPRUCE)
   );
   public static final DeferredBlock<Block> BIRCH_JAPANESE_DOOR = BLOCKS.register(
      "birch_japanese_door", () -> new JapaneseDoors(Properties.of().noOcclusion().strength(1.5F, 1.0F).sound(SoundType.SCAFFOLDING), BlockSetType.BIRCH)
   );
   public static final DeferredBlock<Block> JUNGLE_JAPANESE_DOOR = BLOCKS.register(
      "jungle_japanese_door", () -> new JapaneseDoors(Properties.of().noOcclusion().strength(1.5F, 1.0F).sound(SoundType.SCAFFOLDING), BlockSetType.JUNGLE)
   );
   public static final DeferredBlock<Block> ACACIA_JAPANESE_DOOR = BLOCKS.register(
      "acacia_japanese_door", () -> new JapaneseDoors(Properties.of().noOcclusion().strength(1.5F, 1.0F).sound(SoundType.SCAFFOLDING), BlockSetType.ACACIA)
   );
   public static final DeferredBlock<Block> DARK_OAK_JAPANESE_DOOR = BLOCKS.register(
      "dark_oak_japanese_door", () -> new JapaneseDoors(Properties.of().noOcclusion().strength(1.5F, 1.0F).sound(SoundType.SCAFFOLDING), BlockSetType.DARK_OAK)
   );
   public static final DeferredBlock<Block> CRIMSON_JAPANESE_DOOR = BLOCKS.register(
      "crimson_japanese_door", () -> new JapaneseDoors(Properties.of().noOcclusion().strength(1.5F, 1.0F).sound(SoundType.SCAFFOLDING), BlockSetType.CRIMSON)
   );
   public static final DeferredBlock<Block> WARPED_JAPANESE_DOOR = BLOCKS.register(
      "warped_japanese_door", () -> new JapaneseDoors(Properties.of().noOcclusion().strength(1.5F, 1.0F).sound(SoundType.SCAFFOLDING), BlockSetType.WARPED)
   );
   public static final DeferredBlock<Block> MANGROVE_JAPANESE_DOOR = BLOCKS.register(
      "mangrove_japanese_door", () -> new JapaneseDoors(Properties.of().noOcclusion().strength(1.5F, 1.0F).sound(SoundType.SCAFFOLDING), BlockSetType.MANGROVE)
   );
   public static final DeferredBlock<Block> BAMBOO_JAPANESE_DOOR = BLOCKS.register(
      "bamboo_japanese_door", () -> new JapaneseDoors(Properties.of().noOcclusion().strength(1.5F, 1.0F).sound(SoundType.SCAFFOLDING), BlockSetType.OAK)
   );
   public static final DeferredBlock<Block> OAK_JAPANESE2_DOOR = BLOCKS.register(
      "oak_japanese2_door", () -> new JapaneseDoors(Properties.of().noOcclusion().strength(1.5F, 1.0F).sound(SoundType.SCAFFOLDING), BlockSetType.OAK)
   );
   public static final DeferredBlock<Block> SPRUCE_JAPANESE2_DOOR = BLOCKS.register(
      "spruce_japanese2_door", () -> new JapaneseDoors(Properties.of().noOcclusion().strength(1.5F, 1.0F).sound(SoundType.SCAFFOLDING), BlockSetType.SPRUCE)
   );
   public static final DeferredBlock<Block> BIRCH_JAPANESE2_DOOR = BLOCKS.register(
      "birch_japanese2_door", () -> new JapaneseDoors(Properties.of().noOcclusion().strength(1.5F, 1.0F).sound(SoundType.SCAFFOLDING), BlockSetType.BIRCH)
   );
   public static final DeferredBlock<Block> JUNGLE_JAPANESE2_DOOR = BLOCKS.register(
      "jungle_japanese2_door", () -> new JapaneseDoors(Properties.of().noOcclusion().strength(1.5F, 1.0F).sound(SoundType.SCAFFOLDING), BlockSetType.JUNGLE)
   );
   public static final DeferredBlock<Block> ACACIA_JAPANESE2_DOOR = BLOCKS.register(
      "acacia_japanese2_door", () -> new JapaneseDoors(Properties.of().noOcclusion().strength(1.5F, 1.0F).sound(SoundType.SCAFFOLDING), BlockSetType.ACACIA)
   );
   public static final DeferredBlock<Block> DARK_OAK_JAPANESE2_DOOR = BLOCKS.register(
      "dark_oak_japanese2_door",
      () -> new JapaneseDoors(Properties.of().noOcclusion().strength(1.5F, 1.0F).sound(SoundType.SCAFFOLDING), BlockSetType.DARK_OAK)
   );
   public static final DeferredBlock<Block> CRIMSON_JAPANESE2_DOOR = BLOCKS.register(
      "crimson_japanese2_door", () -> new JapaneseDoors(Properties.of().noOcclusion().strength(1.5F, 1.0F).sound(SoundType.SCAFFOLDING), BlockSetType.CRIMSON)
   );
   public static final DeferredBlock<Block> WARPED_JAPANESE2_DOOR = BLOCKS.register(
      "warped_japanese2_door", () -> new JapaneseDoors(Properties.of().noOcclusion().strength(1.5F, 1.0F).sound(SoundType.SCAFFOLDING), BlockSetType.WARPED)
   );
   public static final DeferredBlock<Block> MANGROVE_JAPANESE2_DOOR = BLOCKS.register(
      "mangrove_japanese2_door",
      () -> new JapaneseDoors(Properties.of().noOcclusion().strength(1.5F, 1.0F).sound(SoundType.SCAFFOLDING), BlockSetType.MANGROVE)
   );
   public static final DeferredBlock<Block> BAMBOO_JAPANESE2_DOOR = BLOCKS.register(
      "bamboo_japanese2_door", () -> new JapaneseDoors(Properties.of().noOcclusion().strength(1.5F, 1.0F).sound(SoundType.SCAFFOLDING), BlockSetType.OAK)
   );
   public static final DeferredBlock<Block> OAK_BARN_DOOR = BLOCKS.register(
      "oak_barn_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_BARN_DOOR = BLOCKS.register(
      "spruce_barn_door", () -> new DoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_DOOR))
   );
   public static final DeferredBlock<Block> BIRCH_BARN_DOOR = BLOCKS.register(
      "birch_barn_door", () -> new DoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_DOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_BARN_DOOR = BLOCKS.register(
      "jungle_barn_door", () -> new DoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_DOOR))
   );
   public static final DeferredBlock<Block> ACACIA_BARN_DOOR = BLOCKS.register(
      "acacia_barn_door", () -> new DoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_DOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_BARN_DOOR = BLOCKS.register(
      "dark_oak_barn_door", () -> new DoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_DOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_BARN_DOOR = BLOCKS.register(
      "crimson_barn_door", () -> new DoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_DOOR))
   );
   public static final DeferredBlock<Block> WARPED_BARN_DOOR = BLOCKS.register(
      "warped_barn_door", () -> new DoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_DOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_BARN_DOOR = BLOCKS.register(
      "mangrove_barn_door", () -> new DoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_DOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_BARN_DOOR = BLOCKS.register(
      "bamboo_barn_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> OAK_BARN_GLASS_DOOR = BLOCKS.register(
      "oak_barn_glass_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_BARN_GLASS_DOOR = BLOCKS.register(
      "spruce_barn_glass_door", () -> new DoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_DOOR))
   );
   public static final DeferredBlock<Block> BIRCH_BARN_GLASS_DOOR = BLOCKS.register(
      "birch_barn_glass_door", () -> new DoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_DOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_BARN_GLASS_DOOR = BLOCKS.register(
      "jungle_barn_glass_door", () -> new DoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_DOOR))
   );
   public static final DeferredBlock<Block> ACACIA_BARN_GLASS_DOOR = BLOCKS.register(
      "acacia_barn_glass_door", () -> new DoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_DOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_BARN_GLASS_DOOR = BLOCKS.register(
      "dark_oak_barn_glass_door", () -> new DoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_DOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_BARN_GLASS_DOOR = BLOCKS.register(
      "crimson_barn_glass_door", () -> new DoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_DOOR))
   );
   public static final DeferredBlock<Block> WARPED_BARN_GLASS_DOOR = BLOCKS.register(
      "warped_barn_glass_door", () -> new DoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_DOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_BARN_GLASS_DOOR = BLOCKS.register(
      "mangrove_barn_glass_door", () -> new DoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_DOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_BARN_GLASS_DOOR = BLOCKS.register(
      "bamboo_barn_glass_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> OAK_MODERN_DOOR = BLOCKS.register(
      "oak_modern_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_MODERN_DOOR = BLOCKS.register(
      "spruce_modern_door", () -> new DoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_DOOR))
   );
   public static final DeferredBlock<Block> BIRCH_MODERN_DOOR = BLOCKS.register(
      "birch_modern_door", () -> new DoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_DOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_MODERN_DOOR = BLOCKS.register(
      "jungle_modern_door", () -> new DoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_DOOR))
   );
   public static final DeferredBlock<Block> ACACIA_MODERN_DOOR = BLOCKS.register(
      "acacia_modern_door", () -> new DoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_DOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_MODERN_DOOR = BLOCKS.register(
      "dark_oak_modern_door", () -> new DoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_DOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_MODERN_DOOR = BLOCKS.register(
      "crimson_modern_door", () -> new DoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_DOOR))
   );
   public static final DeferredBlock<Block> WARPED_MODERN_DOOR = BLOCKS.register(
      "warped_modern_door", () -> new DoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_DOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_MODERN_DOOR = BLOCKS.register(
      "mangrove_modern_door", () -> new DoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_DOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_MODERN_DOOR = BLOCKS.register(
      "bamboo_modern_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> OAK_COTTAGE_DOOR = BLOCKS.register(
      "oak_cottage_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> BIRCH_COTTAGE_DOOR = BLOCKS.register(
      "birch_cottage_door", () -> new DoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_DOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_COTTAGE_DOOR = BLOCKS.register(
      "jungle_cottage_door", () -> new DoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_DOOR))
   );
   public static final DeferredBlock<Block> ACACIA_COTTAGE_DOOR = BLOCKS.register(
      "acacia_cottage_door", () -> new DoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_DOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_COTTAGE_DOOR = BLOCKS.register(
      "dark_oak_cottage_door", () -> new DoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_DOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_COTTAGE_DOOR = BLOCKS.register(
      "crimson_cottage_door", () -> new DoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_DOOR))
   );
   public static final DeferredBlock<Block> WARPED_COTTAGE_DOOR = BLOCKS.register(
      "warped_cottage_door", () -> new DoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_DOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_COTTAGE_DOOR = BLOCKS.register(
      "mangrove_cottage_door", () -> new DoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_DOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_COTTAGE_DOOR = BLOCKS.register(
      "bamboo_cottage_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_CLASSIC_DOOR = BLOCKS.register(
      "spruce_classic_door", () -> new DoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_DOOR))
   );
   public static final DeferredBlock<Block> BIRCH_CLASSIC_DOOR = BLOCKS.register(
      "birch_classic_door", () -> new DoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_DOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_CLASSIC_DOOR = BLOCKS.register(
      "jungle_classic_door", () -> new DoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_DOOR))
   );
   public static final DeferredBlock<Block> ACACIA_CLASSIC_DOOR = BLOCKS.register(
      "acacia_classic_door", () -> new DoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_DOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_CLASSIC_DOOR = BLOCKS.register(
      "dark_oak_classic_door", () -> new DoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_DOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_CLASSIC_DOOR = BLOCKS.register(
      "crimson_classic_door", () -> new DoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_DOOR))
   );
   public static final DeferredBlock<Block> WARPED_CLASSIC_DOOR = BLOCKS.register(
      "warped_classic_door", () -> new DoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_DOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_CLASSIC_DOOR = BLOCKS.register(
      "mangrove_classic_door", () -> new DoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_DOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_CLASSIC_DOOR = BLOCKS.register(
      "bamboo_classic_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> OAK_BEACH_DOOR = BLOCKS.register(
      "oak_beach_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_BEACH_DOOR = BLOCKS.register(
      "spruce_beach_door", () -> new DoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_DOOR))
   );
   public static final DeferredBlock<Block> BIRCH_BEACH_DOOR = BLOCKS.register(
      "birch_beach_door", () -> new DoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_DOOR))
   );
   public static final DeferredBlock<Block> ACACIA_BEACH_DOOR = BLOCKS.register(
      "acacia_beach_door", () -> new DoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_DOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_BEACH_DOOR = BLOCKS.register(
      "dark_oak_beach_door", () -> new DoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_DOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_BEACH_DOOR = BLOCKS.register(
      "crimson_beach_door", () -> new DoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_DOOR))
   );
   public static final DeferredBlock<Block> WARPED_BEACH_DOOR = BLOCKS.register(
      "warped_beach_door", () -> new DoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_DOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_BEACH_DOOR = BLOCKS.register(
      "mangrove_beach_door", () -> new DoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_DOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_BEACH_DOOR = BLOCKS.register(
      "bamboo_beach_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> OAK_PAPER_DOOR = BLOCKS.register(
      "oak_paper_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_PAPER_DOOR = BLOCKS.register(
      "spruce_paper_door", () -> new DoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_DOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_PAPER_DOOR = BLOCKS.register(
      "jungle_paper_door", () -> new DoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_DOOR))
   );
   public static final DeferredBlock<Block> ACACIA_PAPER_DOOR = BLOCKS.register(
      "acacia_paper_door", () -> new DoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_DOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_PAPER_DOOR = BLOCKS.register(
      "dark_oak_paper_door", () -> new DoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_DOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_PAPER_DOOR = BLOCKS.register(
      "crimson_paper_door", () -> new DoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_DOOR))
   );
   public static final DeferredBlock<Block> WARPED_PAPER_DOOR = BLOCKS.register(
      "warped_paper_door", () -> new DoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_DOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_PAPER_DOOR = BLOCKS.register(
      "mangrove_paper_door", () -> new DoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_DOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_PAPER_DOOR = BLOCKS.register(
      "bamboo_paper_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> OAK_FOUR_PANEL_DOOR = BLOCKS.register(
      "oak_four_panel_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_FOUR_PANEL_DOOR = BLOCKS.register(
      "spruce_four_panel_door", () -> new DoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_DOOR))
   );
   public static final DeferredBlock<Block> BIRCH_FOUR_PANEL_DOOR = BLOCKS.register(
      "birch_four_panel_door", () -> new DoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_DOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_FOUR_PANEL_DOOR = BLOCKS.register(
      "jungle_four_panel_door", () -> new DoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_DOOR))
   );
   public static final DeferredBlock<Block> ACACIA_FOUR_PANEL_DOOR = BLOCKS.register(
      "acacia_four_panel_door", () -> new DoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_DOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_FOUR_PANEL_DOOR = BLOCKS.register(
      "crimson_four_panel_door", () -> new DoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_DOOR))
   );
   public static final DeferredBlock<Block> WARPED_FOUR_PANEL_DOOR = BLOCKS.register(
      "warped_four_panel_door", () -> new DoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_DOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_FOUR_PANEL_DOOR = BLOCKS.register(
      "mangrove_four_panel_door", () -> new DoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_DOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_FOUR_PANEL_DOOR = BLOCKS.register(
      "bamboo_four_panel_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> OAK_TROPICAL_DOOR = BLOCKS.register(
      "oak_tropical_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_TROPICAL_DOOR = BLOCKS.register(
      "spruce_tropical_door", () -> new DoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_DOOR))
   );
   public static final DeferredBlock<Block> BIRCH_TROPICAL_DOOR = BLOCKS.register(
      "birch_tropical_door", () -> new DoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_DOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_TROPICAL_DOOR = BLOCKS.register(
      "jungle_tropical_door", () -> new DoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_DOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_TROPICAL_DOOR = BLOCKS.register(
      "dark_oak_tropical_door", () -> new DoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_DOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_TROPICAL_DOOR = BLOCKS.register(
      "crimson_tropical_door", () -> new DoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_DOOR))
   );
   public static final DeferredBlock<Block> WARPED_TROPICAL_DOOR = BLOCKS.register(
      "warped_tropical_door", () -> new DoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_DOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_TROPICAL_DOOR = BLOCKS.register(
      "mangrove_tropical_door", () -> new DoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_DOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_TROPICAL_DOOR = BLOCKS.register(
      "bamboo_tropical_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> METAL_DOOR = BLOCKS.register(
      "metal_door", () -> new DoorBlock(BlockSetType.IRON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.IRON_DOOR))
   );
   public static final DeferredBlock<Block> METAL_WARNING_DOOR = BLOCKS.register(
      "metal_warning_door", () -> new DoorBlock(BlockSetType.IRON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.IRON_DOOR))
   );
   public static final DeferredBlock<Block> METAL_HOSPITAL_DOOR = BLOCKS.register(
      "metal_hospital_door", () -> new DoorBlock(BlockSetType.IRON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.IRON_DOOR))
   );
   public static final DeferredBlock<Block> METAL_REINFORCED_DOOR = BLOCKS.register(
      "metal_reinforced_door", () -> new DoorBlock(BlockSetType.IRON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.IRON_DOOR))
   );
   public static final DeferredBlock<Block> METAL_WINDOWED_DOOR = BLOCKS.register(
      "metal_windowed_door", () -> new DoorBlock(BlockSetType.IRON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.IRON_DOOR))
   );
   public static final DeferredBlock<Block> JAIL_DOOR = BLOCKS.register(
      "jail_door", () -> new JapaneseDoors(Properties.of().noOcclusion().strength(2.5F, 3.0F).sound(SoundType.METAL), BlockSetType.IRON)
   );
   public static final DeferredBlock<Block> OAK_GLASS_DOOR = BLOCKS.register(
      "oak_glass_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_GLASS_DOOR = BLOCKS.register(
      "spruce_glass_door", () -> new DoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_DOOR))
   );
   public static final DeferredBlock<Block> BIRCH_GLASS_DOOR = BLOCKS.register(
      "birch_glass_door", () -> new DoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_DOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_GLASS_DOOR = BLOCKS.register(
      "jungle_glass_door", () -> new DoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_DOOR))
   );
   public static final DeferredBlock<Block> ACACIA_GLASS_DOOR = BLOCKS.register(
      "acacia_glass_door", () -> new DoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_DOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_GLASS_DOOR = BLOCKS.register(
      "dark_oak_glass_door", () -> new DoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_DOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_GLASS_DOOR = BLOCKS.register(
      "crimson_glass_door", () -> new DoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_DOOR))
   );
   public static final DeferredBlock<Block> WARPED_GLASS_DOOR = BLOCKS.register(
      "warped_glass_door", () -> new DoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_DOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_GLASS_DOOR = BLOCKS.register(
      "mangrove_glass_door", () -> new DoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_DOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_GLASS_DOOR = BLOCKS.register(
      "bamboo_glass_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> OAK_STABLE_DOOR = BLOCKS.register(
      "oak_stable_door", () -> new StableDoor(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR), BlockSetType.OAK)
   );
   public static final DeferredBlock<Block> SPRUCE_STABLE_DOOR = BLOCKS.register(
      "spruce_stable_door", () -> new StableDoor(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_DOOR), BlockSetType.SPRUCE)
   );
   public static final DeferredBlock<Block> BIRCH_STABLE_DOOR = BLOCKS.register(
      "birch_stable_door", () -> new StableDoor(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_DOOR), BlockSetType.BIRCH)
   );
   public static final DeferredBlock<Block> JUNGLE_STABLE_DOOR = BLOCKS.register(
      "jungle_stable_door", () -> new StableDoor(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_DOOR), BlockSetType.JUNGLE)
   );
   public static final DeferredBlock<Block> ACACIA_STABLE_DOOR = BLOCKS.register(
      "acacia_stable_door", () -> new StableDoor(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_DOOR), BlockSetType.ACACIA)
   );
   public static final DeferredBlock<Block> DARK_OAK_STABLE_DOOR = BLOCKS.register(
      "dark_oak_stable_door", () -> new StableDoor(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_DOOR), BlockSetType.DARK_OAK)
   );
   public static final DeferredBlock<Block> CRIMSON_STABLE_DOOR = BLOCKS.register(
      "crimson_stable_door", () -> new StableDoor(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_DOOR), BlockSetType.CRIMSON)
   );
   public static final DeferredBlock<Block> WARPED_STABLE_DOOR = BLOCKS.register(
      "warped_stable_door", () -> new StableDoor(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_DOOR), BlockSetType.WARPED)
   );
   public static final DeferredBlock<Block> MANGROVE_STABLE_DOOR = BLOCKS.register(
      "mangrove_stable_door", () -> new StableDoor(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_DOOR), BlockSetType.MANGROVE)
   );
   public static final DeferredBlock<Block> BAMBOO_STABLE_DOOR = BLOCKS.register(
      "bamboo_stable_door", () -> new StableDoor(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR), BlockSetType.OAK)
   );
   public static final DeferredBlock<Block> OAK_STABLE_HEAD_DOOR = BLOCKS.register(
      "oak_stable_head_door", () -> new StableDoor(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR), BlockSetType.OAK)
   );
   public static final DeferredBlock<Block> SPRUCE_STABLE_HEAD_DOOR = BLOCKS.register(
      "spruce_stable_head_door", () -> new StableDoor(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_DOOR), BlockSetType.SPRUCE)
   );
   public static final DeferredBlock<Block> BIRCH_STABLE_HEAD_DOOR = BLOCKS.register(
      "birch_stable_head_door", () -> new StableDoor(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_DOOR), BlockSetType.BIRCH)
   );
   public static final DeferredBlock<Block> JUNGLE_STABLE_HEAD_DOOR = BLOCKS.register(
      "jungle_stable_head_door", () -> new StableDoor(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_DOOR), BlockSetType.JUNGLE)
   );
   public static final DeferredBlock<Block> ACACIA_STABLE_HEAD_DOOR = BLOCKS.register(
      "acacia_stable_head_door", () -> new StableDoor(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_DOOR), BlockSetType.ACACIA)
   );
   public static final DeferredBlock<Block> DARK_OAK_STABLE_HEAD_DOOR = BLOCKS.register(
      "dark_oak_stable_head_door", () -> new StableDoor(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_DOOR), BlockSetType.DARK_OAK)
   );
   public static final DeferredBlock<Block> CRIMSON_STABLE_HEAD_DOOR = BLOCKS.register(
      "crimson_stable_head_door", () -> new StableDoor(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_DOOR), BlockSetType.CRIMSON)
   );
   public static final DeferredBlock<Block> WARPED_STABLE_HEAD_DOOR = BLOCKS.register(
      "warped_stable_head_door", () -> new StableDoor(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_DOOR), BlockSetType.WARPED)
   );
   public static final DeferredBlock<Block> MANGROVE_STABLE_HEAD_DOOR = BLOCKS.register(
      "mangrove_stable_head_door", () -> new StableDoor(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_DOOR), BlockSetType.MANGROVE)
   );
   public static final DeferredBlock<Block> BAMBOO_STABLE_HEAD_DOOR = BLOCKS.register(
      "bamboo_stable_head_door", () -> new StableDoor(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR), BlockSetType.OAK)
   );
   public static final DeferredBlock<Block> OAK_WESTERN_DOOR = BLOCKS.register(
      "oak_western_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_WESTERN_DOOR = BLOCKS.register(
      "spruce_western_door", () -> new DoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_DOOR))
   );
   public static final DeferredBlock<Block> BIRCH_WESTERN_DOOR = BLOCKS.register(
      "birch_western_door", () -> new DoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_DOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_WESTERN_DOOR = BLOCKS.register(
      "jungle_western_door", () -> new DoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_DOOR))
   );
   public static final DeferredBlock<Block> ACACIA_WESTERN_DOOR = BLOCKS.register(
      "acacia_western_door", () -> new DoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_DOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_WESTERN_DOOR = BLOCKS.register(
      "dark_oak_western_door", () -> new DoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_DOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_WESTERN_DOOR = BLOCKS.register(
      "crimson_western_door", () -> new DoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_DOOR))
   );
   public static final DeferredBlock<Block> WARPED_WESTERN_DOOR = BLOCKS.register(
      "warped_western_door", () -> new DoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_DOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_WESTERN_DOOR = BLOCKS.register(
      "mangrove_western_door", () -> new DoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_DOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_WESTERN_DOOR = BLOCKS.register(
      "bamboo_western_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> OAK_MYSTIC_DOOR = BLOCKS.register(
      "oak_mystic_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_MYSTIC_DOOR = BLOCKS.register(
      "spruce_mystic_door", () -> new DoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_DOOR))
   );
   public static final DeferredBlock<Block> BIRCH_MYSTIC_DOOR = BLOCKS.register(
      "birch_mystic_door", () -> new DoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_DOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_MYSTIC_DOOR = BLOCKS.register(
      "jungle_mystic_door", () -> new DoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_DOOR))
   );
   public static final DeferredBlock<Block> ACACIA_MYSTIC_DOOR = BLOCKS.register(
      "acacia_mystic_door", () -> new DoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_DOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_MYSTIC_DOOR = BLOCKS.register(
      "dark_oak_mystic_door", () -> new DoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_DOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_MYSTIC_DOOR = BLOCKS.register(
      "crimson_mystic_door", () -> new DoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_DOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_MYSTIC_DOOR = BLOCKS.register(
      "mangrove_mystic_door", () -> new DoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_DOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_MYSTIC_DOOR = BLOCKS.register(
      "bamboo_mystic_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> OAK_NETHER_DOOR = BLOCKS.register(
      "oak_nether_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_NETHER_DOOR = BLOCKS.register(
      "spruce_nether_door", () -> new DoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_DOOR))
   );
   public static final DeferredBlock<Block> BIRCH_NETHER_DOOR = BLOCKS.register(
      "birch_nether_door", () -> new DoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_DOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_NETHER_DOOR = BLOCKS.register(
      "jungle_nether_door", () -> new DoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_DOOR))
   );
   public static final DeferredBlock<Block> ACACIA_NETHER_DOOR = BLOCKS.register(
      "acacia_nether_door", () -> new DoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_DOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_NETHER_DOOR = BLOCKS.register(
      "dark_oak_nether_door", () -> new DoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_DOOR))
   );
   public static final DeferredBlock<Block> WARPED_NETHER_DOOR = BLOCKS.register(
      "warped_nether_door", () -> new DoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_DOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_NETHER_DOOR = BLOCKS.register(
      "mangrove_nether_door", () -> new DoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_DOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_NETHER_DOOR = BLOCKS.register(
      "bamboo_nether_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> OAK_SWAMP_DOOR = BLOCKS.register(
      "oak_swamp_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_SWAMP_DOOR = BLOCKS.register(
      "spruce_swamp_door", () -> new DoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_DOOR))
   );
   public static final DeferredBlock<Block> BIRCH_SWAMP_DOOR = BLOCKS.register(
      "birch_swamp_door", () -> new DoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_DOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_SWAMP_DOOR = BLOCKS.register(
      "jungle_swamp_door", () -> new DoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_DOOR))
   );
   public static final DeferredBlock<Block> ACACIA_SWAMP_DOOR = BLOCKS.register(
      "acacia_swamp_door", () -> new DoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_DOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_SWAMP_DOOR = BLOCKS.register(
      "dark_oak_swamp_door", () -> new DoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_DOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_SWAMP_DOOR = BLOCKS.register(
      "crimson_swamp_door", () -> new DoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_DOOR))
   );
   public static final DeferredBlock<Block> WARPED_SWAMP_DOOR = BLOCKS.register(
      "warped_swamp_door", () -> new DoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_DOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_SWAMP_DOOR = BLOCKS.register(
      "bamboo_swamp_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> OAK_BAMBOO_DOOR = BLOCKS.register(
      "oak_bamboo_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_BAMBOO_DOOR = BLOCKS.register(
      "spruce_bamboo_door", () -> new DoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_DOOR))
   );
   public static final DeferredBlock<Block> BIRCH_BAMBOO_DOOR = BLOCKS.register(
      "birch_bamboo_door", () -> new DoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_DOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_BAMBOO_DOOR = BLOCKS.register(
      "jungle_bamboo_door", () -> new DoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_DOOR))
   );
   public static final DeferredBlock<Block> ACACIA_BAMBOO_DOOR = BLOCKS.register(
      "acacia_bamboo_door", () -> new DoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_DOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_BAMBOO_DOOR = BLOCKS.register(
      "dark_oak_bamboo_door", () -> new DoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_DOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_BAMBOO_DOOR = BLOCKS.register(
      "crimson_bamboo_door", () -> new DoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_DOOR))
   );
   public static final DeferredBlock<Block> WARPED_BAMBOO_DOOR = BLOCKS.register(
      "warped_bamboo_door", () -> new DoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_DOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_BAMBOO_DOOR = BLOCKS.register(
      "mangrove_bamboo_door", () -> new DoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_DOOR))
   );
   public static final DeferredBlock<Block> OAK_BARK_GLASS_DOOR = BLOCKS.register(
      "oak_bark_glass_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_BARK_GLASS_DOOR = BLOCKS.register(
      "spruce_bark_glass_door", () -> new DoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_DOOR))
   );
   public static final DeferredBlock<Block> BIRCH_BARK_GLASS_DOOR = BLOCKS.register(
      "birch_bark_glass_door", () -> new DoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_DOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_BARK_GLASS_DOOR = BLOCKS.register(
      "jungle_bark_glass_door", () -> new DoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_DOOR))
   );
   public static final DeferredBlock<Block> ACACIA_BARK_GLASS_DOOR = BLOCKS.register(
      "acacia_bark_glass_door", () -> new DoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_DOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_BARK_GLASS_DOOR = BLOCKS.register(
      "dark_oak_bark_glass_door", () -> new DoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_DOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_STEM_GLASS_DOOR = BLOCKS.register(
      "crimson_stem_glass_door", () -> new DoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_DOOR))
   );
   public static final DeferredBlock<Block> WARPED_STEM_GLASS_DOOR = BLOCKS.register(
      "warped_stem_glass_door", () -> new DoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_DOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_BARK_GLASS_DOOR = BLOCKS.register(
      "mangrove_bark_glass_door", () -> new DoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_DOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_BARK_GLASS_DOOR = BLOCKS.register(
      "bamboo_bark_glass_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> GARAGE_WHITE_DOOR = BLOCKS.register(
      "garage_white_door", () -> new GarageDoor(Properties.of().noOcclusion().strength(3.0F, 5.0F).sound(SoundType.METAL))
   );
   public static final DeferredBlock<Block> GARAGE_SILVER_DOOR = BLOCKS.register(
      "garage_silver_door", () -> new GarageDoor(Properties.of().noOcclusion().strength(3.0F, 5.0F).sound(SoundType.METAL))
   );
   public static final DeferredBlock<Block> GARAGE_GRAY_DOOR = BLOCKS.register(
      "garage_gray_door", () -> new GarageDoor(Properties.of().noOcclusion().strength(3.0F, 5.0F).sound(SoundType.METAL))
   );
   public static final DeferredBlock<Block> GARAGE_BLACK_DOOR = BLOCKS.register(
      "garage_black_door", () -> new GarageDoor(Properties.of().noOcclusion().strength(3.0F, 5.0F).sound(SoundType.METAL))
   );
   public static final DeferredBlock<Block> WOODEN_PORTCULLIS = BLOCKS.register(
      "wooden_portcullis", () -> new GarageDoor(Properties.of().noOcclusion().strength(2.0F, 3.0F).sound(SoundType.WOOD))
   );
   public static final DeferredBlock<Block> IRON_PORTCULLIS = BLOCKS.register(
      "iron_portcullis", () -> new GarageDoor(Properties.of().noOcclusion().strength(3.0F, 5.0F).sound(SoundType.METAL))
   );
   public static final DeferredBlock<Block> STORE_DOOR = BLOCKS.register(
      "store_door", () -> new DoorBlock(BlockSetType.OAK, Properties.of().noOcclusion().strength(2.0F, 3.0F).sound(SoundType.METAL))
   );
   public static final DeferredBlock<Block> SLIDING_GLASS_DOOR = BLOCKS.register(
      "sliding_glass_door", () -> new JapaneseDoors(Properties.of().noOcclusion().strength(2.0F, 3.0F).sound(SoundType.METAL), BlockSetType.OAK)
   );
   public static final DeferredBlock<Block> OAK_WAFFLE_DOOR = BLOCKS.register(
      "oak_waffle_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_WAFFLE_DOOR = BLOCKS.register(
      "spruce_waffle_door", () -> new DoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_DOOR))
   );
   public static final DeferredBlock<Block> BIRCH_WAFFLE_DOOR = BLOCKS.register(
      "birch_waffle_door", () -> new DoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_DOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_WAFFLE_DOOR = BLOCKS.register(
      "jungle_waffle_door", () -> new DoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_DOOR))
   );
   public static final DeferredBlock<Block> ACACIA_WAFFLE_DOOR = BLOCKS.register(
      "acacia_waffle_door", () -> new DoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_DOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_WAFFLE_DOOR = BLOCKS.register(
      "dark_oak_waffle_door", () -> new DoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_DOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_WAFFLE_DOOR = BLOCKS.register(
      "crimson_waffle_door", () -> new DoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_DOOR))
   );
   public static final DeferredBlock<Block> WARPED_WAFFLE_DOOR = BLOCKS.register(
      "warped_waffle_door", () -> new DoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_DOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_WAFFLE_DOOR = BLOCKS.register(
      "bamboo_waffle_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_WAFFLE_DOOR = BLOCKS.register(
      "mangrove_waffle_door", () -> new DoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_DOOR))
   );
   public static final DeferredBlock<Block> CHERRY_JAPANESE_DOOR = BLOCKS.register(
      "cherry_japanese_door", () -> new JapaneseDoors(Properties.of().noOcclusion().strength(1.5F, 1.0F).sound(SoundType.SCAFFOLDING), BlockSetType.CHERRY)
   );
   public static final DeferredBlock<Block> CHERRY_JAPANESE2_DOOR = BLOCKS.register(
      "cherry_japanese2_door", () -> new JapaneseDoors(Properties.of().noOcclusion().strength(1.5F, 1.0F).sound(SoundType.SCAFFOLDING), BlockSetType.CHERRY)
   );
   public static final DeferredBlock<Block> CHERRY_BARN_DOOR = BLOCKS.register(
      "cherry_barn_door", () -> new DoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_DOOR))
   );
   public static final DeferredBlock<Block> CHERRY_BARN_GLASS_DOOR = BLOCKS.register(
      "cherry_barn_glass_door", () -> new DoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_DOOR))
   );
   public static final DeferredBlock<Block> CHERRY_MODERN_DOOR = BLOCKS.register(
      "cherry_modern_door", () -> new DoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_DOOR))
   );
   public static final DeferredBlock<Block> CHERRY_COTTAGE_DOOR = BLOCKS.register(
      "cherry_cottage_door", () -> new DoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_DOOR))
   );
   public static final DeferredBlock<Block> CHERRY_CLASSIC_DOOR = BLOCKS.register(
      "cherry_classic_door", () -> new DoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_DOOR))
   );
   public static final DeferredBlock<Block> CHERRY_BEACH_DOOR = BLOCKS.register(
      "cherry_beach_door", () -> new DoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_DOOR))
   );
   public static final DeferredBlock<Block> CHERRY_PAPER_DOOR = BLOCKS.register(
      "cherry_paper_door", () -> new DoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_DOOR))
   );
   public static final DeferredBlock<Block> CHERRY_FOUR_PANEL_DOOR = BLOCKS.register(
      "cherry_four_panel_door", () -> new DoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_DOOR))
   );
   public static final DeferredBlock<Block> CHERRY_TROPICAL_DOOR = BLOCKS.register(
      "cherry_tropical_door", () -> new DoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_DOOR))
   );
   public static final DeferredBlock<Block> CHERRY_GLASS_DOOR = BLOCKS.register(
      "cherry_glass_door", () -> new DoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_DOOR))
   );
   public static final DeferredBlock<Block> CHERRY_STABLE_DOOR = BLOCKS.register(
      "cherry_stable_door", () -> new StableDoor(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_DOOR), BlockSetType.CHERRY)
   );
   public static final DeferredBlock<Block> CHERRY_STABLE_HEAD_DOOR = BLOCKS.register(
      "cherry_stable_head_door", () -> new StableDoor(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_DOOR), BlockSetType.CHERRY)
   );
   public static final DeferredBlock<Block> CHERRY_WESTERN_DOOR = BLOCKS.register(
      "cherry_western_door", () -> new DoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_DOOR))
   );
   public static final DeferredBlock<Block> CHERRY_MYSTIC_DOOR = BLOCKS.register(
      "cherry_mystic_door", () -> new DoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_DOOR))
   );
   public static final DeferredBlock<Block> CHERRY_NETHER_DOOR = BLOCKS.register(
      "cherry_nether_door", () -> new DoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_DOOR))
   );
   public static final DeferredBlock<Block> CHERRY_SWAMP_DOOR = BLOCKS.register(
      "cherry_swamp_door", () -> new DoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_DOOR))
   );
   public static final DeferredBlock<Block> CHERRY_BAMBOO_DOOR = BLOCKS.register(
      "cherry_bamboo_door", () -> new DoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_DOOR))
   );
   public static final DeferredBlock<Block> CHERRY_BARK_GLASS_DOOR = BLOCKS.register(
      "cherry_bark_glass_door", () -> new DoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_DOOR))
   );
   public static final DeferredBlock<Block> OAK_WHISPERING_DOOR = BLOCKS.register(
      "oak_whispering_door", () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR))
   );
   public static final DeferredBlock<Block> SPRUCE_WHISPERING_DOOR = BLOCKS.register(
      "spruce_whispering_door", () -> new DoorBlock(BlockSetType.SPRUCE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SPRUCE_DOOR))
   );
   public static final DeferredBlock<Block> BIRCH_WHISPERING_DOOR = BLOCKS.register(
      "birch_whispering_door", () -> new DoorBlock(BlockSetType.BIRCH, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_DOOR))
   );
   public static final DeferredBlock<Block> JUNGLE_WHISPERING_DOOR = BLOCKS.register(
      "jungle_whispering_door", () -> new DoorBlock(BlockSetType.JUNGLE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.JUNGLE_DOOR))
   );
   public static final DeferredBlock<Block> ACACIA_WHISPERING_DOOR = BLOCKS.register(
      "acacia_whispering_door", () -> new DoorBlock(BlockSetType.ACACIA, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.ACACIA_DOOR))
   );
   public static final DeferredBlock<Block> DARK_OAK_WHISPERING_DOOR = BLOCKS.register(
      "dark_oak_whispering_door", () -> new DoorBlock(BlockSetType.DARK_OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.DARK_OAK_DOOR))
   );
   public static final DeferredBlock<Block> CRIMSON_WHISPERING_DOOR = BLOCKS.register(
      "crimson_whispering_door", () -> new DoorBlock(BlockSetType.CRIMSON, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_DOOR))
   );
   public static final DeferredBlock<Block> WARPED_WHISPERING_DOOR = BLOCKS.register(
      "warped_whispering_door", () -> new DoorBlock(BlockSetType.WARPED, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WARPED_DOOR))
   );
   public static final DeferredBlock<Block> BAMBOO_WHISPERING_DOOR = BLOCKS.register(
      "bamboo_whispering_door", () -> new DoorBlock(BlockSetType.BAMBOO, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BAMBOO_DOOR))
   );
   public static final DeferredBlock<Block> MANGROVE_WHISPERING_DOOR = BLOCKS.register(
      "mangrove_whispering_door", () -> new DoorBlock(BlockSetType.MANGROVE, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MANGROVE_DOOR))
   );
   public static final DeferredBlock<Block> CHERRY_WHISPERING_DOOR = BLOCKS.register(
      "cherry_whispering_door", () -> new DoorBlock(BlockSetType.CHERRY, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CHERRY_DOOR))
   );
}

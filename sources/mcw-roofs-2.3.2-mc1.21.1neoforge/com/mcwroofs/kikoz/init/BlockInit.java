package com.mcwroofs.kikoz.init;

import com.mcwroofs.kikoz.objects.gutters.GutterTall;
import com.mcwroofs.kikoz.objects.gutters.RainGutter;
import com.mcwroofs.kikoz.objects.roofs.AwningBlock;
import com.mcwroofs.kikoz.objects.roofs.BaseRoof;
import com.mcwroofs.kikoz.objects.roofs.Lower;
import com.mcwroofs.kikoz.objects.roofs.RoofGlass;
import com.mcwroofs.kikoz.objects.roofs.RoofTopNew;
import com.mcwroofs.kikoz.objects.roofs.Steep;
import com.mcwroofs.kikoz.objects.roofs.SteepRoof;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Blocks;

public class BlockInit {
   public static final Blocks BLOCKS = DeferredRegister.createBlocks("mcwroofs");
   public static final DeferredBlock<Block> OAK_ROOF = BLOCKS.register(
      "oak_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> OAK_ATTIC_ROOF = BLOCKS.register(
      "oak_attic_roof", () -> new RoofGlass(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> OAK_TOP_ROOF = BLOCKS.register(
      "oak_top_roof", () -> new RoofTopNew(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD))
   );
   public static final DeferredBlock<Block> OAK_LOWER_ROOF = BLOCKS.register(
      "oak_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> OAK_STEEP_ROOF = BLOCKS.register(
      "oak_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> OAK_UPPER_LOWER_ROOF = BLOCKS.register(
      "oak_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> OAK_UPPER_STEEP_ROOF = BLOCKS.register(
      "oak_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> SPRUCE_ROOF = BLOCKS.register(
      "spruce_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> SPRUCE_ATTIC_ROOF = BLOCKS.register(
      "spruce_attic_roof", () -> new RoofGlass(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_TOP_ROOF = BLOCKS.register(
      "spruce_top_roof", () -> new RoofTopNew(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD))
   );
   public static final DeferredBlock<Block> SPRUCE_LOWER_ROOF = BLOCKS.register(
      "spruce_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> SPRUCE_STEEP_ROOF = BLOCKS.register(
      "spruce_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> SPRUCE_UPPER_LOWER_ROOF = BLOCKS.register(
      "spruce_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> SPRUCE_UPPER_STEEP_ROOF = BLOCKS.register(
      "spruce_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> BIRCH_ROOF = BLOCKS.register(
      "birch_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> BIRCH_ATTIC_ROOF = BLOCKS.register(
      "birch_attic_roof", () -> new RoofGlass(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_TOP_ROOF = BLOCKS.register(
      "birch_top_roof", () -> new RoofTopNew(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD))
   );
   public static final DeferredBlock<Block> BIRCH_LOWER_ROOF = BLOCKS.register(
      "birch_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> BIRCH_STEEP_ROOF = BLOCKS.register(
      "birch_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> BIRCH_UPPER_LOWER_ROOF = BLOCKS.register(
      "birch_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> BIRCH_UPPER_STEEP_ROOF = BLOCKS.register(
      "birch_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> JUNGLE_ROOF = BLOCKS.register(
      "jungle_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> JUNGLE_ATTIC_ROOF = BLOCKS.register(
      "jungle_attic_roof", () -> new RoofGlass(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_TOP_ROOF = BLOCKS.register(
      "jungle_top_roof", () -> new RoofTopNew(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD))
   );
   public static final DeferredBlock<Block> JUNGLE_LOWER_ROOF = BLOCKS.register(
      "jungle_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> JUNGLE_STEEP_ROOF = BLOCKS.register(
      "jungle_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> JUNGLE_UPPER_LOWER_ROOF = BLOCKS.register(
      "jungle_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> JUNGLE_UPPER_STEEP_ROOF = BLOCKS.register(
      "jungle_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> ACACIA_ROOF = BLOCKS.register(
      "acacia_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> ACACIA_ATTIC_ROOF = BLOCKS.register(
      "acacia_attic_roof", () -> new RoofGlass(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_TOP_ROOF = BLOCKS.register(
      "acacia_top_roof", () -> new RoofTopNew(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD))
   );
   public static final DeferredBlock<Block> ACACIA_LOWER_ROOF = BLOCKS.register(
      "acacia_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> ACACIA_STEEP_ROOF = BLOCKS.register(
      "acacia_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> ACACIA_UPPER_LOWER_ROOF = BLOCKS.register(
      "acacia_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> ACACIA_UPPER_STEEP_ROOF = BLOCKS.register(
      "acacia_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> DARK_OAK_ROOF = BLOCKS.register(
      "dark_oak_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> DARK_OAK_ATTIC_ROOF = BLOCKS.register(
      "dark_oak_attic_roof", () -> new RoofGlass(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_TOP_ROOF = BLOCKS.register(
      "dark_oak_top_roof", () -> new RoofTopNew(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD))
   );
   public static final DeferredBlock<Block> DARK_OAK_LOWER_ROOF = BLOCKS.register(
      "dark_oak_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> DARK_OAK_STEEP_ROOF = BLOCKS.register(
      "dark_oak_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> DARK_OAK_UPPER_LOWER_ROOF = BLOCKS.register(
      "dark_oak_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> DARK_OAK_UPPER_STEEP_ROOF = BLOCKS.register(
      "dark_oak_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> CRIMSON_ROOF = BLOCKS.register(
      "crimson_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> CRIMSON_ATTIC_ROOF = BLOCKS.register(
      "crimson_attic_roof", () -> new RoofGlass(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_TOP_ROOF = BLOCKS.register(
      "crimson_top_roof", () -> new RoofTopNew(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F).sound(SoundType.WOOD))
   );
   public static final DeferredBlock<Block> CRIMSON_LOWER_ROOF = BLOCKS.register(
      "crimson_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> CRIMSON_STEEP_ROOF = BLOCKS.register(
      "crimson_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> CRIMSON_UPPER_LOWER_ROOF = BLOCKS.register(
      "crimson_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> CRIMSON_UPPER_STEEP_ROOF = BLOCKS.register(
      "crimson_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> WARPED_ROOF = BLOCKS.register(
      "warped_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.WARPED_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> WARPED_ATTIC_ROOF = BLOCKS.register(
      "warped_attic_roof", () -> new RoofGlass(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_TOP_ROOF = BLOCKS.register(
      "warped_top_roof", () -> new RoofTopNew(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F).sound(SoundType.WOOD))
   );
   public static final DeferredBlock<Block> WARPED_LOWER_ROOF = BLOCKS.register(
      "warped_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.WARPED_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> WARPED_STEEP_ROOF = BLOCKS.register(
      "warped_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.WARPED_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> WARPED_UPPER_LOWER_ROOF = BLOCKS.register(
      "warped_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.WARPED_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> WARPED_UPPER_STEEP_ROOF = BLOCKS.register(
      "warped_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.WARPED_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> MANGROVE_ROOF = BLOCKS.register(
      "mangrove_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> MANGROVE_ATTIC_ROOF = BLOCKS.register(
      "mangrove_attic_roof", () -> new RoofGlass(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_TOP_ROOF = BLOCKS.register(
      "mangrove_top_roof", () -> new RoofTopNew(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD))
   );
   public static final DeferredBlock<Block> MANGROVE_LOWER_ROOF = BLOCKS.register(
      "mangrove_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> MANGROVE_STEEP_ROOF = BLOCKS.register(
      "mangrove_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> MANGROVE_UPPER_LOWER_ROOF = BLOCKS.register(
      "mangrove_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> MANGROVE_UPPER_STEEP_ROOF = BLOCKS.register(
      "mangrove_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> OAK_PLANKS_ROOF = BLOCKS.register(
      "oak_planks_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.OAK_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> OAK_PLANKS_ATTIC_ROOF = BLOCKS.register(
      "oak_planks_attic_roof", () -> new RoofGlass(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> OAK_PLANKS_TOP_ROOF = BLOCKS.register(
      "oak_planks_top_roof", () -> new RoofTopNew(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD))
   );
   public static final DeferredBlock<Block> OAK_PLANKS_LOWER_ROOF = BLOCKS.register(
      "oak_planks_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.OAK_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> OAK_PLANKS_STEEP_ROOF = BLOCKS.register(
      "oak_planks_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.OAK_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> OAK_PLANKS_UPPER_LOWER_ROOF = BLOCKS.register(
      "oak_planks_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.OAK_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> OAK_PLANKS_UPPER_STEEP_ROOF = BLOCKS.register(
      "oak_planks_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.OAK_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> SPRUCE_PLANKS_ROOF = BLOCKS.register(
      "spruce_planks_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> SPRUCE_PLANKS_ATTIC_ROOF = BLOCKS.register(
      "spruce_planks_attic_roof", () -> new RoofGlass(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> SPRUCE_PLANKS_TOP_ROOF = BLOCKS.register(
      "spruce_planks_top_roof", () -> new RoofTopNew(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD))
   );
   public static final DeferredBlock<Block> SPRUCE_PLANKS_LOWER_ROOF = BLOCKS.register(
      "spruce_planks_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> SPRUCE_PLANKS_STEEP_ROOF = BLOCKS.register(
      "spruce_planks_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> SPRUCE_PLANKS_UPPER_LOWER_ROOF = BLOCKS.register(
      "spruce_planks_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> SPRUCE_PLANKS_UPPER_STEEP_ROOF = BLOCKS.register(
      "spruce_planks_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> BIRCH_PLANKS_ROOF = BLOCKS.register(
      "birch_planks_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.BIRCH_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> BIRCH_PLANKS_ATTIC_ROOF = BLOCKS.register(
      "birch_planks_attic_roof", () -> new RoofGlass(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BIRCH_PLANKS_TOP_ROOF = BLOCKS.register(
      "birch_planks_top_roof", () -> new RoofTopNew(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD))
   );
   public static final DeferredBlock<Block> BIRCH_PLANKS_LOWER_ROOF = BLOCKS.register(
      "birch_planks_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.BIRCH_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> BIRCH_PLANKS_STEEP_ROOF = BLOCKS.register(
      "birch_planks_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.BIRCH_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> BIRCH_PLANKS_UPPER_LOWER_ROOF = BLOCKS.register(
      "birch_planks_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.BIRCH_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> BIRCH_PLANKS_UPPER_STEEP_ROOF = BLOCKS.register(
      "birch_planks_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.BIRCH_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> JUNGLE_PLANKS_ROOF = BLOCKS.register(
      "jungle_planks_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.JUNGLE_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> JUNGLE_PLANKS_ATTIC_ROOF = BLOCKS.register(
      "jungle_planks_attic_roof", () -> new RoofGlass(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> JUNGLE_PLANKS_TOP_ROOF = BLOCKS.register(
      "jungle_planks_top_roof", () -> new RoofTopNew(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD))
   );
   public static final DeferredBlock<Block> JUNGLE_PLANKS_LOWER_ROOF = BLOCKS.register(
      "jungle_planks_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.JUNGLE_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> JUNGLE_PLANKS_STEEP_ROOF = BLOCKS.register(
      "jungle_planks_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.JUNGLE_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> JUNGLE_PLANKS_UPPER_LOWER_ROOF = BLOCKS.register(
      "jungle_planks_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.JUNGLE_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> JUNGLE_PLANKS_UPPER_STEEP_ROOF = BLOCKS.register(
      "jungle_planks_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.JUNGLE_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> ACACIA_PLANKS_ROOF = BLOCKS.register(
      "acacia_planks_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> ACACIA_PLANKS_ATTIC_ROOF = BLOCKS.register(
      "acacia_planks_attic_roof", () -> new RoofGlass(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> ACACIA_PLANKS_TOP_ROOF = BLOCKS.register(
      "acacia_planks_top_roof", () -> new RoofTopNew(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD))
   );
   public static final DeferredBlock<Block> ACACIA_PLANKS_LOWER_ROOF = BLOCKS.register(
      "acacia_planks_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> ACACIA_PLANKS_STEEP_ROOF = BLOCKS.register(
      "acacia_planks_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> ACACIA_PLANKS_UPPER_LOWER_ROOF = BLOCKS.register(
      "acacia_planks_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> ACACIA_PLANKS_UPPER_STEEP_ROOF = BLOCKS.register(
      "acacia_planks_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.ACACIA_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> DARK_OAK_PLANKS_ROOF = BLOCKS.register(
      "dark_oak_planks_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> DARK_OAK_PLANKS_ATTIC_ROOF = BLOCKS.register(
      "dark_oak_planks_attic_roof", () -> new RoofGlass(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> DARK_OAK_PLANKS_TOP_ROOF = BLOCKS.register(
      "dark_oak_planks_top_roof", () -> new RoofTopNew(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD))
   );
   public static final DeferredBlock<Block> DARK_OAK_PLANKS_LOWER_ROOF = BLOCKS.register(
      "dark_oak_planks_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> DARK_OAK_PLANKS_STEEP_ROOF = BLOCKS.register(
      "dark_oak_planks_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> DARK_OAK_PLANKS_UPPER_LOWER_ROOF = BLOCKS.register(
      "dark_oak_planks_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> DARK_OAK_PLANKS_UPPER_STEEP_ROOF = BLOCKS.register(
      "dark_oak_planks_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> CRIMSON_PLANKS_ROOF = BLOCKS.register(
      "crimson_planks_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> CRIMSON_PLANKS_ATTIC_ROOF = BLOCKS.register(
      "crimson_planks_attic_roof", () -> new RoofGlass(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CRIMSON_PLANKS_TOP_ROOF = BLOCKS.register(
      "crimson_planks_top_roof", () -> new RoofTopNew(Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F).sound(SoundType.WOOD))
   );
   public static final DeferredBlock<Block> CRIMSON_PLANKS_LOWER_ROOF = BLOCKS.register(
      "crimson_planks_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> CRIMSON_PLANKS_STEEP_ROOF = BLOCKS.register(
      "crimson_planks_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> CRIMSON_PLANKS_UPPER_LOWER_ROOF = BLOCKS.register(
      "crimson_planks_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> CRIMSON_PLANKS_UPPER_STEEP_ROOF = BLOCKS.register(
      "crimson_planks_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.CRIMSON_STEM).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> WARPED_PLANKS_ROOF = BLOCKS.register(
      "warped_planks_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.WARPED_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> WARPED_PLANKS_ATTIC_ROOF = BLOCKS.register(
      "warped_planks_attic_roof", () -> new RoofGlass(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> WARPED_PLANKS_TOP_ROOF = BLOCKS.register(
      "warped_planks_top_roof", () -> new RoofTopNew(Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F).sound(SoundType.WOOD))
   );
   public static final DeferredBlock<Block> WARPED_PLANKS_LOWER_ROOF = BLOCKS.register(
      "warped_planks_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.WARPED_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> WARPED_PLANKS_STEEP_ROOF = BLOCKS.register(
      "warped_planks_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.WARPED_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> WARPED_PLANKS_UPPER_LOWER_ROOF = BLOCKS.register(
      "warped_planks_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.WARPED_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> WARPED_PLANKS_UPPER_STEEP_ROOF = BLOCKS.register(
      "warped_planks_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.WARPED_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WARPED_STEM).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> MANGROVE_PLANKS_ROOF = BLOCKS.register(
      "mangrove_planks_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.MANGROVE_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> MANGROVE_PLANKS_ATTIC_ROOF = BLOCKS.register(
      "mangrove_planks_attic_roof", () -> new RoofGlass(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> MANGROVE_PLANKS_TOP_ROOF = BLOCKS.register(
      "mangrove_planks_top_roof", () -> new RoofTopNew(Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD))
   );
   public static final DeferredBlock<Block> MANGROVE_PLANKS_LOWER_ROOF = BLOCKS.register(
      "mangrove_planks_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.MANGROVE_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> MANGROVE_PLANKS_STEEP_ROOF = BLOCKS.register(
      "mangrove_planks_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.MANGROVE_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> MANGROVE_PLANKS_UPPER_LOWER_ROOF = BLOCKS.register(
      "mangrove_planks_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.MANGROVE_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> MANGROVE_PLANKS_UPPER_STEEP_ROOF = BLOCKS.register(
      "mangrove_planks_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.MANGROVE_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 2.3F).sound(SoundType.WOOD)
      )
   );
   public static final DeferredBlock<Block> WHITE_TERRACOTTA_ROOF = BLOCKS.register(
      "white_terracotta_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> WHITE_TERRACOTTA_ATTIC_ROOF = BLOCKS.register(
      "white_terracotta_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> WHITE_TERRACOTTA_TOP_ROOF = BLOCKS.register(
      "white_terracotta_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> WHITE_TERRACOTTA_LOWER_ROOF = BLOCKS.register(
      "white_terracotta_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> WHITE_TERRACOTTA_STEEP_ROOF = BLOCKS.register(
      "white_terracotta_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> WHITE_TERRACOTTA_UPPER_LOWER_ROOF = BLOCKS.register(
      "white_terracotta_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> WHITE_TERRACOTTA_UPPER_STEEP_ROOF = BLOCKS.register(
      "white_terracotta_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_TERRACOTTA_ROOF = BLOCKS.register(
      "light_gray_terracotta_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_TERRACOTTA_ATTIC_ROOF = BLOCKS.register(
      "light_gray_terracotta_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_TERRACOTTA_TOP_ROOF = BLOCKS.register(
      "light_gray_terracotta_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_TERRACOTTA_LOWER_ROOF = BLOCKS.register(
      "light_gray_terracotta_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_TERRACOTTA_STEEP_ROOF = BLOCKS.register(
      "light_gray_terracotta_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_TERRACOTTA_UPPER_LOWER_ROOF = BLOCKS.register(
      "light_gray_terracotta_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_TERRACOTTA_UPPER_STEEP_ROOF = BLOCKS.register(
      "light_gray_terracotta_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GRAY_TERRACOTTA_ROOF = BLOCKS.register(
      "gray_terracotta_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GRAY_TERRACOTTA_ATTIC_ROOF = BLOCKS.register(
      "gray_terracotta_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> GRAY_TERRACOTTA_TOP_ROOF = BLOCKS.register(
      "gray_terracotta_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> GRAY_TERRACOTTA_LOWER_ROOF = BLOCKS.register(
      "gray_terracotta_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GRAY_TERRACOTTA_STEEP_ROOF = BLOCKS.register(
      "gray_terracotta_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GRAY_TERRACOTTA_UPPER_LOWER_ROOF = BLOCKS.register(
      "gray_terracotta_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GRAY_TERRACOTTA_UPPER_STEEP_ROOF = BLOCKS.register(
      "gray_terracotta_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLACK_TERRACOTTA_ROOF = BLOCKS.register(
      "black_terracotta_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLACK_TERRACOTTA_ATTIC_ROOF = BLOCKS.register(
      "black_terracotta_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BLACK_TERRACOTTA_TOP_ROOF = BLOCKS.register(
      "black_terracotta_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BLACK_TERRACOTTA_LOWER_ROOF = BLOCKS.register(
      "black_terracotta_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLACK_TERRACOTTA_STEEP_ROOF = BLOCKS.register(
      "black_terracotta_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLACK_TERRACOTTA_UPPER_LOWER_ROOF = BLOCKS.register(
      "black_terracotta_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLACK_TERRACOTTA_UPPER_STEEP_ROOF = BLOCKS.register(
      "black_terracotta_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLUE_TERRACOTTA_ROOF = BLOCKS.register(
      "blue_terracotta_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLUE_TERRACOTTA_ATTIC_ROOF = BLOCKS.register(
      "blue_terracotta_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BLUE_TERRACOTTA_TOP_ROOF = BLOCKS.register(
      "blue_terracotta_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BLUE_TERRACOTTA_LOWER_ROOF = BLOCKS.register(
      "blue_terracotta_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLUE_TERRACOTTA_STEEP_ROOF = BLOCKS.register(
      "blue_terracotta_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLUE_TERRACOTTA_UPPER_LOWER_ROOF = BLOCKS.register(
      "blue_terracotta_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLUE_TERRACOTTA_UPPER_STEEP_ROOF = BLOCKS.register(
      "blue_terracotta_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIGHT_BLUE_TERRACOTTA_ROOF = BLOCKS.register(
      "light_blue_terracotta_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_BLUE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIGHT_BLUE_TERRACOTTA_ATTIC_ROOF = BLOCKS.register(
      "light_blue_terracotta_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_BLUE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> LIGHT_BLUE_TERRACOTTA_TOP_ROOF = BLOCKS.register(
      "light_blue_terracotta_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_BLUE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> LIGHT_BLUE_TERRACOTTA_LOWER_ROOF = BLOCKS.register(
      "light_blue_terracotta_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_BLUE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIGHT_BLUE_TERRACOTTA_STEEP_ROOF = BLOCKS.register(
      "light_blue_terracotta_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_BLUE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIGHT_BLUE_TERRACOTTA_UPPER_LOWER_ROOF = BLOCKS.register(
      "light_blue_terracotta_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_BLUE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIGHT_BLUE_TERRACOTTA_UPPER_STEEP_ROOF = BLOCKS.register(
      "light_blue_terracotta_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_BLUE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> CYAN_TERRACOTTA_ROOF = BLOCKS.register(
      "cyan_terracotta_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> CYAN_TERRACOTTA_ATTIC_ROOF = BLOCKS.register(
      "cyan_terracotta_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> CYAN_TERRACOTTA_TOP_ROOF = BLOCKS.register(
      "cyan_terracotta_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> CYAN_TERRACOTTA_LOWER_ROOF = BLOCKS.register(
      "cyan_terracotta_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> CYAN_TERRACOTTA_STEEP_ROOF = BLOCKS.register(
      "cyan_terracotta_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> CYAN_TERRACOTTA_UPPER_LOWER_ROOF = BLOCKS.register(
      "cyan_terracotta_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> CYAN_TERRACOTTA_UPPER_STEEP_ROOF = BLOCKS.register(
      "cyan_terracotta_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIME_TERRACOTTA_ROOF = BLOCKS.register(
      "lime_terracotta_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GREEN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIME_TERRACOTTA_ATTIC_ROOF = BLOCKS.register(
      "lime_terracotta_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GREEN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> LIME_TERRACOTTA_TOP_ROOF = BLOCKS.register(
      "lime_terracotta_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GREEN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> LIME_TERRACOTTA_LOWER_ROOF = BLOCKS.register(
      "lime_terracotta_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GREEN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIME_TERRACOTTA_STEEP_ROOF = BLOCKS.register(
      "lime_terracotta_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GREEN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIME_TERRACOTTA_UPPER_LOWER_ROOF = BLOCKS.register(
      "lime_terracotta_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GREEN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIME_TERRACOTTA_UPPER_STEEP_ROOF = BLOCKS.register(
      "lime_terracotta_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GREEN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GREEN_TERRACOTTA_ROOF = BLOCKS.register(
      "green_terracotta_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GREEN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GREEN_TERRACOTTA_ATTIC_ROOF = BLOCKS.register(
      "green_terracotta_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_GREEN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> GREEN_TERRACOTTA_TOP_ROOF = BLOCKS.register(
      "green_terracotta_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_GREEN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> GREEN_TERRACOTTA_LOWER_ROOF = BLOCKS.register(
      "green_terracotta_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GREEN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GREEN_TERRACOTTA_STEEP_ROOF = BLOCKS.register(
      "green_terracotta_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GREEN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GREEN_TERRACOTTA_UPPER_LOWER_ROOF = BLOCKS.register(
      "green_terracotta_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GREEN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GREEN_TERRACOTTA_UPPER_STEEP_ROOF = BLOCKS.register(
      "green_terracotta_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GREEN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> YELLOW_TERRACOTTA_ROOF = BLOCKS.register(
      "yellow_terracotta_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> YELLOW_TERRACOTTA_ATTIC_ROOF = BLOCKS.register(
      "yellow_terracotta_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> YELLOW_TERRACOTTA_TOP_ROOF = BLOCKS.register(
      "yellow_terracotta_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> YELLOW_TERRACOTTA_LOWER_ROOF = BLOCKS.register(
      "yellow_terracotta_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> YELLOW_TERRACOTTA_STEEP_ROOF = BLOCKS.register(
      "yellow_terracotta_steep_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> YELLOW_TERRACOTTA_UPPER_LOWER_ROOF = BLOCKS.register(
      "yellow_terracotta_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> YELLOW_TERRACOTTA_UPPER_STEEP_ROOF = BLOCKS.register(
      "yellow_terracotta_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BROWN_TERRACOTTA_ROOF = BLOCKS.register(
      "brown_terracotta_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BROWN_TERRACOTTA_ATTIC_ROOF = BLOCKS.register(
      "brown_terracotta_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BROWN_TERRACOTTA_TOP_ROOF = BLOCKS.register(
      "brown_terracotta_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BROWN_TERRACOTTA_LOWER_ROOF = BLOCKS.register(
      "brown_terracotta_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BROWN_TERRACOTTA_STEEP_ROOF = BLOCKS.register(
      "brown_terracotta_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BROWN_TERRACOTTA_UPPER_LOWER_ROOF = BLOCKS.register(
      "brown_terracotta_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BROWN_TERRACOTTA_UPPER_STEEP_ROOF = BLOCKS.register(
      "brown_terracotta_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> ORANGE_TERRACOTTA_ROOF = BLOCKS.register(
      "orange_terracotta_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> ORANGE_TERRACOTTA_ATTIC_ROOF = BLOCKS.register(
      "orange_terracotta_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> ORANGE_TERRACOTTA_TOP_ROOF = BLOCKS.register(
      "orange_terracotta_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> ORANGE_TERRACOTTA_LOWER_ROOF = BLOCKS.register(
      "orange_terracotta_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> ORANGE_TERRACOTTA_STEEP_ROOF = BLOCKS.register(
      "orange_terracotta_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> ORANGE_TERRACOTTA_UPPER_LOWER_ROOF = BLOCKS.register(
      "orange_terracotta_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> ORANGE_TERRACOTTA_UPPER_STEEP_ROOF = BLOCKS.register(
      "orange_terracotta_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> RED_TERRACOTTA_ROOF = BLOCKS.register(
      "red_terracotta_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> RED_TERRACOTTA_ATTIC_ROOF = BLOCKS.register(
      "red_terracotta_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> RED_TERRACOTTA_TOP_ROOF = BLOCKS.register(
      "red_terracotta_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> RED_TERRACOTTA_LOWER_ROOF = BLOCKS.register(
      "red_terracotta_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> RED_TERRACOTTA_STEEP_ROOF = BLOCKS.register(
      "red_terracotta_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> RED_TERRACOTTA_UPPER_LOWER_ROOF = BLOCKS.register(
      "red_terracotta_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> RED_TERRACOTTA_UPPER_STEEP_ROOF = BLOCKS.register(
      "red_terracotta_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> MAGENTA_TERRACOTTA_ROOF = BLOCKS.register(
      "magenta_terracotta_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_MAGENTA).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> MAGENTA_TERRACOTTA_ATTIC_ROOF = BLOCKS.register(
      "magenta_terracotta_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_MAGENTA).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> MAGENTA_TERRACOTTA_TOP_ROOF = BLOCKS.register(
      "magenta_terracotta_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_MAGENTA).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> MAGENTA_TERRACOTTA_LOWER_ROOF = BLOCKS.register(
      "magenta_terracotta_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_MAGENTA).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> MAGENTA_TERRACOTTA_STEEP_ROOF = BLOCKS.register(
      "magenta_terracotta_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_MAGENTA).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> MAGENTA_TERRACOTTA_UPPER_LOWER_ROOF = BLOCKS.register(
      "magenta_terracotta_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_MAGENTA).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> MAGENTA_TERRACOTTA_UPPER_STEEP_ROOF = BLOCKS.register(
      "magenta_terracotta_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_MAGENTA).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> PINK_TERRACOTTA_ROOF = BLOCKS.register(
      "pink_terracotta_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_PINK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> PINK_TERRACOTTA_ATTIC_ROOF = BLOCKS.register(
      "pink_terracotta_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_PINK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> PINK_TERRACOTTA_TOP_ROOF = BLOCKS.register(
      "pink_terracotta_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_PINK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> PINK_TERRACOTTA_LOWER_ROOF = BLOCKS.register(
      "pink_terracotta_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_PINK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> PINK_TERRACOTTA_STEEP_ROOF = BLOCKS.register(
      "pink_terracotta_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_PINK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> PINK_TERRACOTTA_UPPER_LOWER_ROOF = BLOCKS.register(
      "pink_terracotta_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_PINK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> PINK_TERRACOTTA_UPPER_STEEP_ROOF = BLOCKS.register(
      "pink_terracotta_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_PINK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> PURPLE_TERRACOTTA_ROOF = BLOCKS.register(
      "purple_terracotta_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> PURPLE_TERRACOTTA_ATTIC_ROOF = BLOCKS.register(
      "purple_terracotta_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> PURPLE_TERRACOTTA_TOP_ROOF = BLOCKS.register(
      "purple_terracotta_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> PURPLE_TERRACOTTA_LOWER_ROOF = BLOCKS.register(
      "purple_terracotta_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> PURPLE_TERRACOTTA_STEEP_ROOF = BLOCKS.register(
      "purple_terracotta_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> PURPLE_TERRACOTTA_UPPER_LOWER_ROOF = BLOCKS.register(
      "purple_terracotta_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> PURPLE_TERRACOTTA_UPPER_STEEP_ROOF = BLOCKS.register(
      "purple_terracotta_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> WHITE_CONCRETE_ROOF = BLOCKS.register(
      "white_concrete_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> WHITE_CONCRETE_ATTIC_ROOF = BLOCKS.register(
      "white_concrete_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> WHITE_CONCRETE_TOP_ROOF = BLOCKS.register(
      "white_concrete_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> WHITE_CONCRETE_LOWER_ROOF = BLOCKS.register(
      "white_concrete_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> WHITE_CONCRETE_STEEP_ROOF = BLOCKS.register(
      "white_concrete_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> WHITE_CONCRETE_UPPER_LOWER_ROOF = BLOCKS.register(
      "white_concrete_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> WHITE_CONCRETE_UPPER_STEEP_ROOF = BLOCKS.register(
      "white_concrete_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_CONCRETE_ROOF = BLOCKS.register(
      "light_gray_concrete_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_CONCRETE_ATTIC_ROOF = BLOCKS.register(
      "light_gray_concrete_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_CONCRETE_TOP_ROOF = BLOCKS.register(
      "light_gray_concrete_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_CONCRETE_LOWER_ROOF = BLOCKS.register(
      "light_gray_concrete_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_CONCRETE_STEEP_ROOF = BLOCKS.register(
      "light_gray_concrete_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_CONCRETE_UPPER_LOWER_ROOF = BLOCKS.register(
      "light_gray_concrete_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_CONCRETE_UPPER_STEEP_ROOF = BLOCKS.register(
      "light_gray_concrete_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GRAY_CONCRETE_ROOF = BLOCKS.register(
      "gray_concrete_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GRAY_CONCRETE_ATTIC_ROOF = BLOCKS.register(
      "gray_concrete_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> GRAY_CONCRETE_TOP_ROOF = BLOCKS.register(
      "gray_concrete_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> GRAY_CONCRETE_LOWER_ROOF = BLOCKS.register(
      "gray_concrete_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GRAY_CONCRETE_STEEP_ROOF = BLOCKS.register(
      "gray_concrete_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GRAY_CONCRETE_UPPER_LOWER_ROOF = BLOCKS.register(
      "gray_concrete_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GRAY_CONCRETE_UPPER_STEEP_ROOF = BLOCKS.register(
      "gray_concrete_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLACK_CONCRETE_ROOF = BLOCKS.register(
      "black_concrete_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLACK_CONCRETE_ATTIC_ROOF = BLOCKS.register(
      "black_concrete_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BLACK_CONCRETE_TOP_ROOF = BLOCKS.register(
      "black_concrete_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BLACK_CONCRETE_LOWER_ROOF = BLOCKS.register(
      "black_concrete_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLACK_CONCRETE_STEEP_ROOF = BLOCKS.register(
      "black_concrete_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLACK_CONCRETE_UPPER_LOWER_ROOF = BLOCKS.register(
      "black_concrete_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLACK_CONCRETE_UPPER_STEEP_ROOF = BLOCKS.register(
      "black_concrete_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLUE_CONCRETE_ROOF = BLOCKS.register(
      "blue_concrete_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLUE_CONCRETE_ATTIC_ROOF = BLOCKS.register(
      "blue_concrete_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BLUE_CONCRETE_TOP_ROOF = BLOCKS.register(
      "blue_concrete_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BLUE_CONCRETE_LOWER_ROOF = BLOCKS.register(
      "blue_concrete_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLUE_CONCRETE_STEEP_ROOF = BLOCKS.register(
      "blue_concrete_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLUE_CONCRETE_UPPER_LOWER_ROOF = BLOCKS.register(
      "blue_concrete_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLUE_CONCRETE_UPPER_STEEP_ROOF = BLOCKS.register(
      "blue_concrete_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIGHT_BLUE_CONCRETE_ROOF = BLOCKS.register(
      "light_blue_concrete_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_BLUE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIGHT_BLUE_CONCRETE_ATTIC_ROOF = BLOCKS.register(
      "light_blue_concrete_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_BLUE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> LIGHT_BLUE_CONCRETE_TOP_ROOF = BLOCKS.register(
      "light_blue_concrete_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_BLUE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> LIGHT_BLUE_CONCRETE_LOWER_ROOF = BLOCKS.register(
      "light_blue_concrete_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_BLUE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIGHT_BLUE_CONCRETE_STEEP_ROOF = BLOCKS.register(
      "light_blue_concrete_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_BLUE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIGHT_BLUE_CONCRETE_UPPER_LOWER_ROOF = BLOCKS.register(
      "light_blue_concrete_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_BLUE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIGHT_BLUE_CONCRETE_UPPER_STEEP_ROOF = BLOCKS.register(
      "light_blue_concrete_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_BLUE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> CYAN_CONCRETE_ROOF = BLOCKS.register(
      "cyan_concrete_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> CYAN_CONCRETE_ATTIC_ROOF = BLOCKS.register(
      "cyan_concrete_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> CYAN_CONCRETE_TOP_ROOF = BLOCKS.register(
      "cyan_concrete_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> CYAN_CONCRETE_LOWER_ROOF = BLOCKS.register(
      "cyan_concrete_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> CYAN_CONCRETE_STEEP_ROOF = BLOCKS.register(
      "cyan_concrete_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> CYAN_CONCRETE_UPPER_LOWER_ROOF = BLOCKS.register(
      "cyan_concrete_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> CYAN_CONCRETE_UPPER_STEEP_ROOF = BLOCKS.register(
      "cyan_concrete_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIME_CONCRETE_ROOF = BLOCKS.register(
      "lime_concrete_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GREEN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIME_CONCRETE_ATTIC_ROOF = BLOCKS.register(
      "lime_concrete_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GREEN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> LIME_CONCRETE_TOP_ROOF = BLOCKS.register(
      "lime_concrete_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GREEN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> LIME_CONCRETE_LOWER_ROOF = BLOCKS.register(
      "lime_concrete_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GREEN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIME_CONCRETE_STEEP_ROOF = BLOCKS.register(
      "lime_concrete_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GREEN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIME_CONCRETE_UPPER_LOWER_ROOF = BLOCKS.register(
      "lime_concrete_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GREEN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIME_CONCRETE_UPPER_STEEP_ROOF = BLOCKS.register(
      "lime_concrete_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GREEN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GREEN_CONCRETE_ROOF = BLOCKS.register(
      "green_concrete_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GREEN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GREEN_CONCRETE_ATTIC_ROOF = BLOCKS.register(
      "green_concrete_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_GREEN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> GREEN_CONCRETE_TOP_ROOF = BLOCKS.register(
      "green_concrete_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_GREEN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> GREEN_CONCRETE_LOWER_ROOF = BLOCKS.register(
      "green_concrete_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GREEN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GREEN_CONCRETE_STEEP_ROOF = BLOCKS.register(
      "green_concrete_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GREEN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GREEN_CONCRETE_UPPER_LOWER_ROOF = BLOCKS.register(
      "green_concrete_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GREEN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GREEN_CONCRETE_UPPER_STEEP_ROOF = BLOCKS.register(
      "green_concrete_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GREEN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> YELLOW_CONCRETE_ROOF = BLOCKS.register(
      "yellow_concrete_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> YELLOW_CONCRETE_ATTIC_ROOF = BLOCKS.register(
      "yellow_concrete_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> YELLOW_CONCRETE_TOP_ROOF = BLOCKS.register(
      "yellow_concrete_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> YELLOW_CONCRETE_LOWER_ROOF = BLOCKS.register(
      "yellow_concrete_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> YELLOW_CONCRETE_STEEP_ROOF = BLOCKS.register(
      "yellow_concrete_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> YELLOW_CONCRETE_UPPER_LOWER_ROOF = BLOCKS.register(
      "yellow_concrete_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> YELLOW_CONCRETE_UPPER_STEEP_ROOF = BLOCKS.register(
      "yellow_concrete_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BROWN_CONCRETE_ROOF = BLOCKS.register(
      "brown_concrete_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BROWN_CONCRETE_ATTIC_ROOF = BLOCKS.register(
      "brown_concrete_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BROWN_CONCRETE_TOP_ROOF = BLOCKS.register(
      "brown_concrete_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BROWN_CONCRETE_LOWER_ROOF = BLOCKS.register(
      "brown_concrete_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BROWN_CONCRETE_STEEP_ROOF = BLOCKS.register(
      "brown_concrete_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BROWN_CONCRETE_UPPER_LOWER_ROOF = BLOCKS.register(
      "brown_concrete_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BROWN_CONCRETE_UPPER_STEEP_ROOF = BLOCKS.register(
      "brown_concrete_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> ORANGE_CONCRETE_ROOF = BLOCKS.register(
      "orange_concrete_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> ORANGE_CONCRETE_ATTIC_ROOF = BLOCKS.register(
      "orange_concrete_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> ORANGE_CONCRETE_TOP_ROOF = BLOCKS.register(
      "orange_concrete_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> ORANGE_CONCRETE_LOWER_ROOF = BLOCKS.register(
      "orange_concrete_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> ORANGE_CONCRETE_STEEP_ROOF = BLOCKS.register(
      "orange_concrete_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> ORANGE_CONCRETE_UPPER_LOWER_ROOF = BLOCKS.register(
      "orange_concrete_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> ORANGE_CONCRETE_UPPER_STEEP_ROOF = BLOCKS.register(
      "orange_concrete_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> RED_CONCRETE_ROOF = BLOCKS.register(
      "red_concrete_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> RED_CONCRETE_ATTIC_ROOF = BLOCKS.register(
      "red_concrete_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> RED_CONCRETE_TOP_ROOF = BLOCKS.register(
      "red_concrete_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> RED_CONCRETE_LOWER_ROOF = BLOCKS.register(
      "red_concrete_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> RED_CONCRETE_STEEP_ROOF = BLOCKS.register(
      "red_concrete_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> RED_CONCRETE_UPPER_LOWER_ROOF = BLOCKS.register(
      "red_concrete_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> RED_CONCRETE_UPPER_STEEP_ROOF = BLOCKS.register(
      "red_concrete_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> MAGENTA_CONCRETE_ROOF = BLOCKS.register(
      "magenta_concrete_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_MAGENTA).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> MAGENTA_CONCRETE_ATTIC_ROOF = BLOCKS.register(
      "magenta_concrete_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_MAGENTA).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> MAGENTA_CONCRETE_TOP_ROOF = BLOCKS.register(
      "magenta_concrete_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_MAGENTA).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> MAGENTA_CONCRETE_LOWER_ROOF = BLOCKS.register(
      "magenta_concrete_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_MAGENTA).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> MAGENTA_CONCRETE_STEEP_ROOF = BLOCKS.register(
      "magenta_concrete_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_MAGENTA).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> MAGENTA_CONCRETE_UPPER_LOWER_ROOF = BLOCKS.register(
      "magenta_concrete_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_MAGENTA).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> MAGENTA_CONCRETE_UPPER_STEEP_ROOF = BLOCKS.register(
      "magenta_concrete_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_MAGENTA).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> PINK_CONCRETE_ROOF = BLOCKS.register(
      "pink_concrete_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_PINK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> PINK_CONCRETE_ATTIC_ROOF = BLOCKS.register(
      "pink_concrete_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_PINK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> PINK_CONCRETE_TOP_ROOF = BLOCKS.register(
      "pink_concrete_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_PINK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> PINK_CONCRETE_LOWER_ROOF = BLOCKS.register(
      "pink_concrete_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_PINK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> PINK_CONCRETE_STEEP_ROOF = BLOCKS.register(
      "pink_concrete_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_PINK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> PINK_CONCRETE_UPPER_LOWER_ROOF = BLOCKS.register(
      "pink_concrete_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_PINK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> PINK_CONCRETE_UPPER_STEEP_ROOF = BLOCKS.register(
      "pink_concrete_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_PINK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> PURPLE_CONCRETE_ROOF = BLOCKS.register(
      "purple_concrete_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> PURPLE_CONCRETE_ATTIC_ROOF = BLOCKS.register(
      "purple_concrete_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> PURPLE_CONCRETE_TOP_ROOF = BLOCKS.register(
      "purple_concrete_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> PURPLE_CONCRETE_LOWER_ROOF = BLOCKS.register(
      "purple_concrete_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> PURPLE_CONCRETE_STEEP_ROOF = BLOCKS.register(
      "purple_concrete_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> PURPLE_CONCRETE_UPPER_LOWER_ROOF = BLOCKS.register(
      "purple_concrete_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> PURPLE_CONCRETE_UPPER_STEEP_ROOF = BLOCKS.register(
      "purple_concrete_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> WHITE_ROOF = BLOCKS.register(
      "white_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> WHITE_ATTIC_ROOF = BLOCKS.register(
      "white_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> WHITE_TOP_ROOF = BLOCKS.register(
      "white_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> WHITE_LOWER_ROOF = BLOCKS.register(
      "white_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> WHITE_STEEP_ROOF = BLOCKS.register(
      "white_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> WHITE_UPPER_LOWER_ROOF = BLOCKS.register(
      "white_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> WHITE_UPPER_STEEP_ROOF = BLOCKS.register(
      "white_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> WHITE_ROOF_BLOCK = BLOCKS.register(
      "white_roof_block",
      () -> new Block(Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> WHITE_ROOF_SLAB = BLOCKS.register(
      "white_roof_slab",
      () -> new SlabBlock(Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_ROOF = BLOCKS.register(
      "light_gray_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_ATTIC_ROOF = BLOCKS.register(
      "light_gray_attic_roof", () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(1.5F, 3.0F))
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_TOP_ROOF = BLOCKS.register(
      "light_gray_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_LOWER_ROOF = BLOCKS.register(
      "light_gray_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_STEEP_ROOF = BLOCKS.register(
      "light_gray_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_UPPER_LOWER_ROOF = BLOCKS.register(
      "light_gray_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_UPPER_STEEP_ROOF = BLOCKS.register(
      "light_gray_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_ROOF_BLOCK = BLOCKS.register(
      "light_gray_roof_block",
      () -> new Block(Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_ROOF_SLAB = BLOCKS.register(
      "light_gray_roof_slab",
      () -> new SlabBlock(Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> GRAY_ROOF = BLOCKS.register(
      "gray_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GRAY_ATTIC_ROOF = BLOCKS.register(
      "gray_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> GRAY_TOP_ROOF = BLOCKS.register(
      "gray_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> GRAY_LOWER_ROOF = BLOCKS.register(
      "gray_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GRAY_STEEP_ROOF = BLOCKS.register(
      "gray_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GRAY_UPPER_LOWER_ROOF = BLOCKS.register(
      "gray_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GRAY_UPPER_STEEP_ROOF = BLOCKS.register(
      "gray_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GRAY_ROOF_BLOCK = BLOCKS.register(
      "gray_roof_block",
      () -> new Block(Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> GRAY_ROOF_SLAB = BLOCKS.register(
      "gray_roof_slab",
      () -> new SlabBlock(Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BLACK_ROOF = BLOCKS.register(
      "black_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLACK_ATTIC_ROOF = BLOCKS.register(
      "black_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BLACK_TOP_ROOF = BLOCKS.register(
      "black_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BLACK_LOWER_ROOF = BLOCKS.register(
      "black_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLACK_STEEP_ROOF = BLOCKS.register(
      "black_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLACK_UPPER_LOWER_ROOF = BLOCKS.register(
      "black_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLACK_UPPER_STEEP_ROOF = BLOCKS.register(
      "black_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLACK_ROOF_BLOCK = BLOCKS.register(
      "black_roof_block",
      () -> new Block(Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BLACK_ROOF_SLAB = BLOCKS.register(
      "black_roof_slab",
      () -> new SlabBlock(Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BASE_ROOF = BLOCKS.register(
      "base_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BASE_ATTIC_ROOF = BLOCKS.register(
      "base_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BASE_TOP_ROOF = BLOCKS.register(
      "base_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BASE_LOWER_ROOF = BLOCKS.register(
      "base_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BASE_STEEP_ROOF = BLOCKS.register(
      "base_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BASE_UPPER_LOWER_ROOF = BLOCKS.register(
      "base_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BASE_UPPER_STEEP_ROOF = BLOCKS.register(
      "base_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BASE_ROOF_BLOCK = BLOCKS.register(
      "base_roof_block",
      () -> new Block(Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BASE_ROOF_SLAB = BLOCKS.register(
      "base_roof_slab",
      () -> new SlabBlock(Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> STONE_ROOF = BLOCKS.register(
      "stone_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> STONE_ATTIC_ROOF = BLOCKS.register(
      "stone_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> STONE_TOP_ROOF = BLOCKS.register(
      "stone_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> STONE_LOWER_ROOF = BLOCKS.register(
      "stone_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> STONE_STEEP_ROOF = BLOCKS.register(
      "stone_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> STONE_UPPER_LOWER_ROOF = BLOCKS.register(
      "stone_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> STONE_UPPER_STEEP_ROOF = BLOCKS.register(
      "stone_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GRANITE_ROOF = BLOCKS.register(
      "granite_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GRANITE_ATTIC_ROOF = BLOCKS.register(
      "granite_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> GRANITE_TOP_ROOF = BLOCKS.register(
      "granite_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> GRANITE_LOWER_ROOF = BLOCKS.register(
      "granite_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GRANITE_STEEP_ROOF = BLOCKS.register(
      "granite_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GRANITE_UPPER_LOWER_ROOF = BLOCKS.register(
      "granite_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GRANITE_UPPER_STEEP_ROOF = BLOCKS.register(
      "granite_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> DIORITE_ROOF = BLOCKS.register(
      "diorite_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> DIORITE_ATTIC_ROOF = BLOCKS.register(
      "diorite_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> DIORITE_TOP_ROOF = BLOCKS.register(
      "diorite_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> DIORITE_LOWER_ROOF = BLOCKS.register(
      "diorite_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> DIORITE_STEEP_ROOF = BLOCKS.register(
      "diorite_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> DIORITE_UPPER_LOWER_ROOF = BLOCKS.register(
      "diorite_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> DIORITE_UPPER_STEEP_ROOF = BLOCKS.register(
      "diorite_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> ANDESITE_ROOF = BLOCKS.register(
      "andesite_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> ANDESITE_ATTIC_ROOF = BLOCKS.register(
      "andesite_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> ANDESITE_TOP_ROOF = BLOCKS.register(
      "andesite_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> ANDESITE_LOWER_ROOF = BLOCKS.register(
      "andesite_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> ANDESITE_STEEP_ROOF = BLOCKS.register(
      "andesite_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> ANDESITE_UPPER_LOWER_ROOF = BLOCKS.register(
      "andesite_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> ANDESITE_UPPER_STEEP_ROOF = BLOCKS.register(
      "andesite_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> COBBLESTONE_ROOF = BLOCKS.register(
      "cobblestone_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> COBBLESTONE_ATTIC_ROOF = BLOCKS.register(
      "cobblestone_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> COBBLESTONE_TOP_ROOF = BLOCKS.register(
      "cobblestone_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> COBBLESTONE_LOWER_ROOF = BLOCKS.register(
      "cobblestone_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> COBBLESTONE_STEEP_ROOF = BLOCKS.register(
      "cobblestone_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> COBBLESTONE_UPPER_LOWER_ROOF = BLOCKS.register(
      "cobblestone_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> COBBLESTONE_UPPER_STEEP_ROOF = BLOCKS.register(
      "cobblestone_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> SANDSTONE_ROOF = BLOCKS.register(
      "sandstone_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.SAND).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> SANDSTONE_ATTIC_ROOF = BLOCKS.register(
      "sandstone_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.SAND).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> SANDSTONE_TOP_ROOF = BLOCKS.register(
      "sandstone_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.SAND).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> SANDSTONE_LOWER_ROOF = BLOCKS.register(
      "sandstone_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.SAND).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> SANDSTONE_STEEP_ROOF = BLOCKS.register(
      "sandstone_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.SAND).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> SANDSTONE_UPPER_LOWER_ROOF = BLOCKS.register(
      "sandstone_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.SAND).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> SANDSTONE_UPPER_STEEP_ROOF = BLOCKS.register(
      "sandstone_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.SAND).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> RED_SANDSTONE_ROOF = BLOCKS.register(
      "red_sandstone_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> RED_SANDSTONE_ATTIC_ROOF = BLOCKS.register(
      "red_sandstone_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> RED_SANDSTONE_TOP_ROOF = BLOCKS.register(
      "red_sandstone_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> RED_SANDSTONE_LOWER_ROOF = BLOCKS.register(
      "red_sandstone_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> RED_SANDSTONE_STEEP_ROOF = BLOCKS.register(
      "red_sandstone_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> RED_SANDSTONE_UPPER_LOWER_ROOF = BLOCKS.register(
      "red_sandstone_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> RED_SANDSTONE_UPPER_STEEP_ROOF = BLOCKS.register(
      "red_sandstone_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BRICKS_ROOF = BLOCKS.register(
      "bricks_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BRICKS_ATTIC_ROOF = BLOCKS.register(
      "bricks_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BRICKS_TOP_ROOF = BLOCKS.register(
      "bricks_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BRICKS_LOWER_ROOF = BLOCKS.register(
      "bricks_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BRICKS_STEEP_ROOF = BLOCKS.register(
      "bricks_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BRICKS_UPPER_LOWER_ROOF = BLOCKS.register(
      "bricks_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BRICKS_UPPER_STEEP_ROOF = BLOCKS.register(
      "bricks_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLACKSTONE_ROOF = BLOCKS.register(
      "blackstone_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLACKSTONE_ATTIC_ROOF = BLOCKS.register(
      "blackstone_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.COLOR_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BLACKSTONE_TOP_ROOF = BLOCKS.register(
      "blackstone_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.COLOR_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BLACKSTONE_LOWER_ROOF = BLOCKS.register(
      "blackstone_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLACKSTONE_STEEP_ROOF = BLOCKS.register(
      "blackstone_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLACKSTONE_UPPER_LOWER_ROOF = BLOCKS.register(
      "blackstone_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> BLACKSTONE_UPPER_STEEP_ROOF = BLOCKS.register(
      "blackstone_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_BLACK).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> DEEPSLATE_ROOF = BLOCKS.register(
      "deepslate_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.DEEPSLATE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> DEEPSLATE_ATTIC_ROOF = BLOCKS.register(
      "deepslate_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.DEEPSLATE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> DEEPSLATE_TOP_ROOF = BLOCKS.register(
      "deepslate_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.DEEPSLATE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> DEEPSLATE_LOWER_ROOF = BLOCKS.register(
      "deepslate_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.DEEPSLATE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> DEEPSLATE_STEEP_ROOF = BLOCKS.register(
      "deepslate_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.DEEPSLATE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> DEEPSLATE_UPPER_LOWER_ROOF = BLOCKS.register(
      "deepslate_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.DEEPSLATE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> DEEPSLATE_UPPER_STEEP_ROOF = BLOCKS.register(
      "deepslate_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.DEEPSLATE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> MUD_BRICK_ROOF = BLOCKS.register(
      "mud_brick_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.SAND).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> MUD_BRICK_ATTIC_ROOF = BLOCKS.register(
      "mud_brick_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.SAND).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> MUD_BRICK_TOP_ROOF = BLOCKS.register(
      "mud_brick_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.SAND).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> MUD_BRICK_LOWER_ROOF = BLOCKS.register(
      "mud_brick_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.SAND).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> MUD_BRICK_STEEP_ROOF = BLOCKS.register(
      "mud_brick_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.SAND).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> MUD_BRICK_UPPER_LOWER_ROOF = BLOCKS.register(
      "mud_brick_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.SAND).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> MUD_BRICK_UPPER_STEEP_ROOF = BLOCKS.register(
      "mud_brick_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.TERRACOTTA.defaultBlockState(),
         Properties.of().mapColor(MapColor.SAND).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> THATCH_ROOF = BLOCKS.register(
      "thatch_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.HAY_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.5F, 1.0F).sound(SoundType.GRASS)
      )
   );
   public static final DeferredBlock<Block> THATCH_ATTIC_ROOF = BLOCKS.register(
      "thatch_attic_roof", () -> new RoofGlass(Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.5F, 1.0F).sound(SoundType.GRASS))
   );
   public static final DeferredBlock<Block> THATCH_TOP_ROOF = BLOCKS.register(
      "thatch_top_roof", () -> new RoofTopNew(Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.5F, 1.0F).sound(SoundType.GRASS))
   );
   public static final DeferredBlock<Block> THATCH_LOWER_ROOF = BLOCKS.register(
      "thatch_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.HAY_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.5F, 1.0F).sound(SoundType.GRASS)
      )
   );
   public static final DeferredBlock<Block> THATCH_STEEP_ROOF = BLOCKS.register(
      "thatch_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.HAY_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.5F, 1.0F).sound(SoundType.GRASS)
      )
   );
   public static final DeferredBlock<Block> THATCH_UPPER_LOWER_ROOF = BLOCKS.register(
      "thatch_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.HAY_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.5F, 1.0F).sound(SoundType.GRASS)
      )
   );
   public static final DeferredBlock<Block> THATCH_UPPER_STEEP_ROOF = BLOCKS.register(
      "thatch_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.HAY_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.5F, 1.0F).sound(SoundType.GRASS)
      )
   );
   public static final DeferredBlock<Block> PRISMARINE_BRICK_ROOF = BLOCKS.register(
      "prismarine_brick_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.PRISMARINE_BRICKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.5F, 1.0F).sound(SoundType.STONE)
      )
   );
   public static final DeferredBlock<Block> PRISMARINE_BRICK_ATTIC_ROOF = BLOCKS.register(
      "prismarine_brick_attic_roof", () -> new RoofGlass(Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.5F, 1.0F).sound(SoundType.STONE))
   );
   public static final DeferredBlock<Block> PRISMARINE_BRICK_TOP_ROOF = BLOCKS.register(
      "prismarine_brick_top_roof", () -> new RoofTopNew(Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.5F, 1.0F).sound(SoundType.STONE))
   );
   public static final DeferredBlock<Block> PRISMARINE_BRICK_LOWER_ROOF = BLOCKS.register(
      "prismarine_brick_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.PRISMARINE_BRICKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.5F, 1.0F).sound(SoundType.STONE)
      )
   );
   public static final DeferredBlock<Block> PRISMARINE_BRICK_STEEP_ROOF = BLOCKS.register(
      "prismarine_brick_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.PRISMARINE_BRICKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.5F, 1.0F).sound(SoundType.STONE)
      )
   );
   public static final DeferredBlock<Block> PRISMARINE_BRICK_UPPER_LOWER_ROOF = BLOCKS.register(
      "prismarine_brick_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.PRISMARINE_BRICKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.5F, 1.0F).sound(SoundType.STONE)
      )
   );
   public static final DeferredBlock<Block> PRISMARINE_BRICK_UPPER_STEEP_ROOF = BLOCKS.register(
      "prismarine_brick_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.PRISMARINE_BRICKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.5F, 1.0F).sound(SoundType.STONE)
      )
   );
   public static final DeferredBlock<Block> DARK_PRISMARINE_ROOF = BLOCKS.register(
      "dark_prismarine_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.DARK_PRISMARINE.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.5F, 1.0F).sound(SoundType.STONE)
      )
   );
   public static final DeferredBlock<Block> DARK_PRISMARINE_ATTIC_ROOF = BLOCKS.register(
      "dark_prismarine_attic_roof", () -> new RoofGlass(Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.5F, 1.0F).sound(SoundType.STONE))
   );
   public static final DeferredBlock<Block> DARK_PRISMARINE_TOP_ROOF = BLOCKS.register(
      "dark_prismarine_top_roof", () -> new RoofTopNew(Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.5F, 1.0F).sound(SoundType.STONE))
   );
   public static final DeferredBlock<Block> DARK_PRISMARINE_LOWER_ROOF = BLOCKS.register(
      "dark_prismarine_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.DARK_PRISMARINE.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.5F, 1.0F).sound(SoundType.STONE)
      )
   );
   public static final DeferredBlock<Block> DARK_PRISMARINE_STEEP_ROOF = BLOCKS.register(
      "dark_prismarine_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.DARK_PRISMARINE.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.5F, 1.0F).sound(SoundType.STONE)
      )
   );
   public static final DeferredBlock<Block> DARK_PRISMARINE_UPPER_LOWER_ROOF = BLOCKS.register(
      "dark_prismarine_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.DARK_PRISMARINE.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.5F, 1.0F).sound(SoundType.STONE)
      )
   );
   public static final DeferredBlock<Block> DARK_PRISMARINE_UPPER_STEEP_ROOF = BLOCKS.register(
      "dark_prismarine_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.DARK_PRISMARINE.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.5F, 1.0F).sound(SoundType.STONE)
      )
   );
   public static final DeferredBlock<Block> BLACK_STRIPED_AWNING = BLOCKS.register(
      "black_striped_awning",
      () -> new AwningBlock(
         net.minecraft.world.level.block.Blocks.BLACK_WOOL.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_BLACK).strength(0.6F, 1.3F).sound(SoundType.WOOL)
      )
   );
   public static final DeferredBlock<Block> BLUE_STRIPED_AWNING = BLOCKS.register(
      "blue_striped_awning",
      () -> new AwningBlock(
         net.minecraft.world.level.block.Blocks.BLUE_WOOL.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_BLUE).strength(0.6F, 1.3F).sound(SoundType.WOOL)
      )
   );
   public static final DeferredBlock<Block> BROWN_STRIPED_AWNING = BLOCKS.register(
      "brown_striped_awning",
      () -> new AwningBlock(
         net.minecraft.world.level.block.Blocks.BROWN_WOOL.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.6F, 1.3F).sound(SoundType.WOOL)
      )
   );
   public static final DeferredBlock<Block> CYAN_STRIPED_AWNING = BLOCKS.register(
      "cyan_striped_awning",
      () -> new AwningBlock(
         net.minecraft.world.level.block.Blocks.CYAN_WOOL.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_CYAN).strength(0.6F, 1.3F).sound(SoundType.WOOL)
      )
   );
   public static final DeferredBlock<Block> GRAY_STRIPED_AWNING = BLOCKS.register(
      "gray_striped_awning",
      () -> new AwningBlock(
         net.minecraft.world.level.block.Blocks.GRAY_WOOL.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_GRAY).strength(0.6F, 1.3F).sound(SoundType.WOOL)
      )
   );
   public static final DeferredBlock<Block> GREEN_STRIPED_AWNING = BLOCKS.register(
      "green_striped_awning",
      () -> new AwningBlock(
         net.minecraft.world.level.block.Blocks.GREEN_WOOL.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_GREEN).strength(0.6F, 1.3F).sound(SoundType.WOOL)
      )
   );
   public static final DeferredBlock<Block> LIGHT_BLUE_STRIPED_AWNING = BLOCKS.register(
      "light_blue_striped_awning",
      () -> new AwningBlock(
         net.minecraft.world.level.block.Blocks.LIGHT_BLUE_WOOL.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).strength(0.6F, 1.3F).sound(SoundType.WOOL)
      )
   );
   public static final DeferredBlock<Block> LIGHT_GRAY_STRIPED_AWNING = BLOCKS.register(
      "light_gray_striped_awning",
      () -> new AwningBlock(
         net.minecraft.world.level.block.Blocks.LIGHT_GRAY_WOOL.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(0.6F, 1.3F).sound(SoundType.WOOL)
      )
   );
   public static final DeferredBlock<Block> LIME_STRIPED_AWNING = BLOCKS.register(
      "lime_striped_awning",
      () -> new AwningBlock(
         net.minecraft.world.level.block.Blocks.LIME_WOOL.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).strength(0.6F, 1.3F).sound(SoundType.WOOL)
      )
   );
   public static final DeferredBlock<Block> MAGENTA_STRIPED_AWNING = BLOCKS.register(
      "magenta_striped_awning",
      () -> new AwningBlock(
         net.minecraft.world.level.block.Blocks.MAGENTA_WOOL.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_MAGENTA).strength(0.6F, 1.3F).sound(SoundType.WOOL)
      )
   );
   public static final DeferredBlock<Block> ORANGE_STRIPED_AWNING = BLOCKS.register(
      "orange_striped_awning",
      () -> new AwningBlock(
         net.minecraft.world.level.block.Blocks.ORANGE_WOOL.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(0.6F, 1.3F).sound(SoundType.WOOL)
      )
   );
   public static final DeferredBlock<Block> PINK_STRIPED_AWNING = BLOCKS.register(
      "pink_striped_awning",
      () -> new AwningBlock(
         net.minecraft.world.level.block.Blocks.PINK_WOOL.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_PINK).strength(0.6F, 1.3F).sound(SoundType.WOOL)
      )
   );
   public static final DeferredBlock<Block> PURPLE_STRIPED_AWNING = BLOCKS.register(
      "purple_striped_awning",
      () -> new AwningBlock(
         net.minecraft.world.level.block.Blocks.PURPLE_WOOL.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(0.6F, 1.3F).sound(SoundType.WOOL)
      )
   );
   public static final DeferredBlock<Block> RED_STRIPED_AWNING = BLOCKS.register(
      "red_striped_awning",
      () -> new AwningBlock(
         net.minecraft.world.level.block.Blocks.RED_WOOL.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_RED).strength(0.6F, 1.3F).sound(SoundType.WOOL)
      )
   );
   public static final DeferredBlock<Block> YELLOW_STRIPED_AWNING = BLOCKS.register(
      "yellow_striped_awning",
      () -> new AwningBlock(
         net.minecraft.world.level.block.Blocks.YELLOW_WOOL.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.6F, 1.3F).sound(SoundType.WOOL)
      )
   );
   public static final DeferredBlock<Block> GUTTER_BASE_YELLOW = BLOCKS.register(
      "gutter_base_yellow",
      () -> new RainGutter(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_BASE_ORANGE = BLOCKS.register(
      "gutter_base_orange",
      () -> new RainGutter(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_BASE_RED = BLOCKS.register(
      "gutter_base_red",
      () -> new RainGutter(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_BASE_PINK = BLOCKS.register(
      "gutter_base_pink",
      () -> new RainGutter(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_PINK).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_BASE_MAGENTA = BLOCKS.register(
      "gutter_base_magenta",
      () -> new RainGutter(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_MAGENTA).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_BASE_PURPLE = BLOCKS.register(
      "gutter_base_purple",
      () -> new RainGutter(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_BASE_LIGHT_BLUE = BLOCKS.register(
      "gutter_base_light_blue",
      () -> new RainGutter(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_BLUE).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_BASE_BLUE = BLOCKS.register(
      "gutter_base_blue",
      () -> new RainGutter(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_BASE_CYAN = BLOCKS.register(
      "gutter_base_cyan",
      () -> new RainGutter(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_BASE_LIME = BLOCKS.register(
      "gutter_base_lime",
      () -> new RainGutter(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GREEN).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_BASE_GREEN = BLOCKS.register(
      "gutter_base_green",
      () -> new RainGutter(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GREEN).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_BASE_BROWN = BLOCKS.register(
      "gutter_base_brown",
      () -> new RainGutter(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_BASE = BLOCKS.register(
      "gutter_base",
      () -> new RainGutter(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_BASE_BLACK = BLOCKS.register(
      "gutter_base_black",
      () -> new RainGutter(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_BASE_GRAY = BLOCKS.register(
      "gutter_base_gray",
      () -> new RainGutter(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_BASE_LIGHT_GRAY = BLOCKS.register(
      "gutter_base_light_gray",
      () -> new RainGutter(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_BASE_WHITE = BLOCKS.register(
      "gutter_base_white",
      () -> new RainGutter(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_MIDDLE_YELLOW = BLOCKS.register(
      "gutter_middle_yellow",
      () -> new GutterTall(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_MIDDLE_ORANGE = BLOCKS.register(
      "gutter_middle_orange",
      () -> new GutterTall(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_MIDDLE_RED = BLOCKS.register(
      "gutter_middle_red",
      () -> new GutterTall(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_MIDDLE_PINK = BLOCKS.register(
      "gutter_middle_pink",
      () -> new GutterTall(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_PINK).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_MIDDLE_MAGENTA = BLOCKS.register(
      "gutter_middle_magenta",
      () -> new GutterTall(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_MAGENTA).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_MIDDLE_PURPLE = BLOCKS.register(
      "gutter_middle_purple",
      () -> new GutterTall(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_MIDDLE_LIGHT_BLUE = BLOCKS.register(
      "gutter_middle_light_blue",
      () -> new GutterTall(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_BLUE).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_MIDDLE_BLUE = BLOCKS.register(
      "gutter_middle_blue",
      () -> new GutterTall(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_MIDDLE_CYAN = BLOCKS.register(
      "gutter_middle_cyan",
      () -> new GutterTall(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_MIDDLE_LIME = BLOCKS.register(
      "gutter_middle_lime",
      () -> new GutterTall(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GREEN).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_MIDDLE_GREEN = BLOCKS.register(
      "gutter_middle_green",
      () -> new GutterTall(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GREEN).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_MIDDLE_BROWN = BLOCKS.register(
      "gutter_middle_brown",
      () -> new GutterTall(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_MIDDLE = BLOCKS.register(
      "gutter_middle",
      () -> new GutterTall(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_MIDDLE_BLACK = BLOCKS.register(
      "gutter_middle_black",
      () -> new GutterTall(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_MIDDLE_GRAY = BLOCKS.register(
      "gutter_middle_gray",
      () -> new GutterTall(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_GRAY).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_MIDDLE_LIGHT_GRAY = BLOCKS.register(
      "gutter_middle_light_gray",
      () -> new GutterTall(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GUTTER_MIDDLE_WHITE = BLOCKS.register(
      "gutter_middle_white",
      () -> new GutterTall(
         net.minecraft.world.level.block.Blocks.IRON_BLOCK.defaultBlockState(),
         Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(1.3F, 2.3F).sound(SoundType.METAL).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> NETHER_BRICKS_ROOF = BLOCKS.register(
      "nether_bricks_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.NETHER_BRICKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_RED).strength(0.5F, 1.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> NETHER_BRICKS_ATTIC_ROOF = BLOCKS.register(
      "nether_bricks_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.COLOR_RED).strength(0.5F, 1.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> NETHER_BRICKS_TOP_ROOF = BLOCKS.register(
      "nether_bricks_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.COLOR_RED).strength(0.5F, 1.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> NETHER_BRICKS_LOWER_ROOF = BLOCKS.register(
      "nether_bricks_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.NETHER_BRICKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_RED).strength(0.5F, 1.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> NETHER_BRICKS_STEEP_ROOF = BLOCKS.register(
      "nether_bricks_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.NETHER_BRICKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_RED).strength(0.5F, 1.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> NETHER_BRICKS_UPPER_LOWER_ROOF = BLOCKS.register(
      "nether_bricks_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.NETHER_BRICKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_RED).strength(0.5F, 1.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> NETHER_BRICKS_UPPER_STEEP_ROOF = BLOCKS.register(
      "nether_bricks_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.NETHER_BRICKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_RED).strength(0.5F, 1.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> RED_NETHER_BRICKS_ROOF = BLOCKS.register(
      "red_nether_bricks_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.RED_NETHER_BRICKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_RED).strength(0.5F, 1.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> RED_NETHER_BRICKS_ATTIC_ROOF = BLOCKS.register(
      "red_nether_bricks_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.COLOR_RED).strength(0.5F, 1.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> RED_NETHER_BRICKS_TOP_ROOF = BLOCKS.register(
      "red_nether_bricks_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.COLOR_RED).strength(0.5F, 1.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> RED_NETHER_BRICKS_LOWER_ROOF = BLOCKS.register(
      "red_nether_bricks_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.RED_NETHER_BRICKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_RED).strength(0.5F, 1.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> RED_NETHER_BRICKS_STEEP_ROOF = BLOCKS.register(
      "red_nether_bricks_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.RED_NETHER_BRICKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_RED).strength(0.5F, 1.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> RED_NETHER_BRICKS_UPPER_LOWER_ROOF = BLOCKS.register(
      "red_nether_bricks_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.RED_NETHER_BRICKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_RED).strength(0.5F, 1.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> RED_NETHER_BRICKS_UPPER_STEEP_ROOF = BLOCKS.register(
      "red_nether_bricks_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.RED_NETHER_BRICKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_RED).strength(0.5F, 1.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> CHERRY_ROOF = BLOCKS.register(
      "cherry_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.CHERRY_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_PINK).strength(2.0F, 2.3F).sound(SoundType.CHERRY_WOOD)
      )
   );
   public static final DeferredBlock<Block> CHERRY_ATTIC_ROOF = BLOCKS.register(
      "cherry_attic_roof", () -> new RoofGlass(Properties.of().mapColor(MapColor.COLOR_PINK).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_TOP_ROOF = BLOCKS.register(
      "cherry_top_roof", () -> new RoofTopNew(Properties.of().mapColor(MapColor.COLOR_PINK).strength(2.0F, 2.3F).sound(SoundType.CHERRY_WOOD))
   );
   public static final DeferredBlock<Block> CHERRY_LOWER_ROOF = BLOCKS.register(
      "cherry_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.CHERRY_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_PINK).strength(2.0F, 2.3F).sound(SoundType.CHERRY_WOOD)
      )
   );
   public static final DeferredBlock<Block> CHERRY_STEEP_ROOF = BLOCKS.register(
      "cherry_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.CHERRY_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_PINK).strength(2.0F, 2.3F).sound(SoundType.CHERRY_WOOD)
      )
   );
   public static final DeferredBlock<Block> CHERRY_UPPER_LOWER_ROOF = BLOCKS.register(
      "cherry_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.CHERRY_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_PINK).strength(2.0F, 2.3F).sound(SoundType.CHERRY_WOOD)
      )
   );
   public static final DeferredBlock<Block> CHERRY_UPPER_STEEP_ROOF = BLOCKS.register(
      "cherry_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.CHERRY_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_PINK).strength(2.0F, 2.3F).sound(SoundType.CHERRY_WOOD)
      )
   );
   public static final DeferredBlock<Block> CHERRY_PLANKS_ROOF = BLOCKS.register(
      "cherry_planks_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.CHERRY_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_PINK).strength(2.0F, 2.3F).sound(SoundType.CHERRY_WOOD)
      )
   );
   public static final DeferredBlock<Block> CHERRY_PLANKS_ATTIC_ROOF = BLOCKS.register(
      "cherry_planks_attic_roof", () -> new RoofGlass(Properties.of().mapColor(MapColor.COLOR_PINK).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> CHERRY_PLANKS_TOP_ROOF = BLOCKS.register(
      "cherry_planks_top_roof", () -> new RoofTopNew(Properties.of().mapColor(MapColor.COLOR_PINK).strength(2.0F, 2.3F).sound(SoundType.CHERRY_WOOD))
   );
   public static final DeferredBlock<Block> CHERRY_PLANKS_LOWER_ROOF = BLOCKS.register(
      "cherry_planks_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.CHERRY_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_PINK).strength(2.0F, 2.3F).sound(SoundType.CHERRY_WOOD)
      )
   );
   public static final DeferredBlock<Block> CHERRY_PLANKS_STEEP_ROOF = BLOCKS.register(
      "cherry_planks_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.CHERRY_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_PINK).strength(2.0F, 2.3F).sound(SoundType.CHERRY_WOOD)
      )
   );
   public static final DeferredBlock<Block> CHERRY_PLANKS_UPPER_LOWER_ROOF = BLOCKS.register(
      "cherry_planks_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.CHERRY_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_PINK).strength(2.0F, 2.3F).sound(SoundType.CHERRY_WOOD)
      )
   );
   public static final DeferredBlock<Block> CHERRY_PLANKS_UPPER_STEEP_ROOF = BLOCKS.register(
      "cherry_planks_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.CHERRY_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_PINK).strength(2.0F, 2.3F).sound(SoundType.CHERRY_WOOD)
      )
   );
   public static final DeferredBlock<Block> BAMBOO_ROOF = BLOCKS.register(
      "bamboo_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.BAMBOO_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(2.0F, 2.3F).sound(SoundType.BAMBOO_WOOD)
      )
   );
   public static final DeferredBlock<Block> BAMBOO_ATTIC_ROOF = BLOCKS.register(
      "bamboo_attic_roof", () -> new RoofGlass(Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BAMBOO_TOP_ROOF = BLOCKS.register(
      "bamboo_top_roof", () -> new RoofTopNew(Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(2.0F, 2.3F).sound(SoundType.BAMBOO_WOOD))
   );
   public static final DeferredBlock<Block> BAMBOO_LOWER_ROOF = BLOCKS.register(
      "bamboo_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.BAMBOO_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(2.0F, 2.3F).sound(SoundType.BAMBOO_WOOD)
      )
   );
   public static final DeferredBlock<Block> BAMBOO_STEEP_ROOF = BLOCKS.register(
      "bamboo_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.BAMBOO_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(2.0F, 2.3F).sound(SoundType.BAMBOO_WOOD)
      )
   );
   public static final DeferredBlock<Block> BAMBOO_UPPER_LOWER_ROOF = BLOCKS.register(
      "bamboo_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.BAMBOO_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(2.0F, 2.3F).sound(SoundType.BAMBOO_WOOD)
      )
   );
   public static final DeferredBlock<Block> BAMBOO_UPPER_STEEP_ROOF = BLOCKS.register(
      "bamboo_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.BAMBOO_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(2.0F, 2.3F).sound(SoundType.BAMBOO_WOOD)
      )
   );
   public static final DeferredBlock<Block> BAMBOO_PLANKS_ROOF = BLOCKS.register(
      "bamboo_planks_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.BAMBOO_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(2.0F, 2.3F).sound(SoundType.BAMBOO_WOOD)
      )
   );
   public static final DeferredBlock<Block> BAMBOO_PLANKS_ATTIC_ROOF = BLOCKS.register(
      "bamboo_planks_attic_roof", () -> new RoofGlass(Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BAMBOO_PLANKS_TOP_ROOF = BLOCKS.register(
      "bamboo_planks_top_roof", () -> new RoofTopNew(Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(2.0F, 2.3F).sound(SoundType.BAMBOO_WOOD))
   );
   public static final DeferredBlock<Block> BAMBOO_PLANKS_LOWER_ROOF = BLOCKS.register(
      "bamboo_planks_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.BAMBOO_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(2.0F, 2.3F).sound(SoundType.BAMBOO_WOOD)
      )
   );
   public static final DeferredBlock<Block> BAMBOO_PLANKS_STEEP_ROOF = BLOCKS.register(
      "bamboo_planks_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.BAMBOO_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(2.0F, 2.3F).sound(SoundType.BAMBOO_WOOD)
      )
   );
   public static final DeferredBlock<Block> BAMBOO_PLANKS_UPPER_LOWER_ROOF = BLOCKS.register(
      "bamboo_planks_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.BAMBOO_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(2.0F, 2.3F).sound(SoundType.BAMBOO_WOOD)
      )
   );
   public static final DeferredBlock<Block> BAMBOO_PLANKS_UPPER_STEEP_ROOF = BLOCKS.register(
      "bamboo_planks_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.BAMBOO_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(2.0F, 2.3F).sound(SoundType.BAMBOO_WOOD)
      )
   );
   public static final DeferredBlock<Block> BAMBOO_MOSAIC_ROOF = BLOCKS.register(
      "bamboo_mosaic_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.BAMBOO_MOSAIC.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(2.0F, 2.3F).sound(SoundType.BAMBOO_WOOD)
      )
   );
   public static final DeferredBlock<Block> BAMBOO_MOSAIC_ATTIC_ROOF = BLOCKS.register(
      "bamboo_mosaic_attic_roof", () -> new RoofGlass(Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(2.0F, 2.3F))
   );
   public static final DeferredBlock<Block> BAMBOO_MOSAIC_TOP_ROOF = BLOCKS.register(
      "bamboo_mosaic_top_roof", () -> new RoofTopNew(Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(2.0F, 2.3F).sound(SoundType.BAMBOO_WOOD))
   );
   public static final DeferredBlock<Block> BAMBOO_MOSAIC_LOWER_ROOF = BLOCKS.register(
      "bamboo_mosaic_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.BAMBOO_MOSAIC.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(2.0F, 2.3F).sound(SoundType.BAMBOO_WOOD)
      )
   );
   public static final DeferredBlock<Block> BAMBOO_MOSAIC_STEEP_ROOF = BLOCKS.register(
      "bamboo_mosaic_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.BAMBOO_MOSAIC.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(2.0F, 2.3F).sound(SoundType.BAMBOO_WOOD)
      )
   );
   public static final DeferredBlock<Block> BAMBOO_MOSAIC_UPPER_LOWER_ROOF = BLOCKS.register(
      "bamboo_mosaic_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.BAMBOO_MOSAIC.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(2.0F, 2.3F).sound(SoundType.BAMBOO_WOOD)
      )
   );
   public static final DeferredBlock<Block> BAMBOO_MOSAIC_UPPER_STEEP_ROOF = BLOCKS.register(
      "bamboo_mosaic_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.BAMBOO_PLANKS.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(2.0F, 2.3F).sound(SoundType.BAMBOO_WOOD)
      )
   );
   public static final DeferredBlock<Block> STONE_BRICKS_ROOF = BLOCKS.register(
      "stone_bricks_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> STONE_BRICKS_ATTIC_ROOF = BLOCKS.register(
      "stone_bricks_attic_roof",
      () -> new RoofGlass(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> STONE_BRICKS_TOP_ROOF = BLOCKS.register(
      "stone_bricks_top_roof",
      () -> new RoofTopNew(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> STONE_BRICKS_LOWER_ROOF = BLOCKS.register(
      "stone_bricks_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> STONE_BRICKS_STEEP_ROOF = BLOCKS.register(
      "stone_bricks_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> STONE_BRICKS_UPPER_LOWER_ROOF = BLOCKS.register(
      "stone_bricks_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> STONE_BRICKS_UPPER_STEEP_ROOF = BLOCKS.register(
      "stone_bricks_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(),
         Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()
      )
   );
   public static final DeferredBlock<Block> GRASS_ROOF = BLOCKS.register(
      "grass_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.DIRT.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).strength(0.5F, 3.0F).sound(SoundType.GRASS)
      )
   );
   public static final DeferredBlock<Block> GRASS_ATTIC_ROOF = BLOCKS.register(
      "grass_attic_roof", () -> new RoofGlass(Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).strength(0.5F, 3.0F).sound(SoundType.GRASS))
   );
   public static final DeferredBlock<Block> GRASS_TOP_ROOF = BLOCKS.register(
      "grass_top_roof", () -> new RoofTopNew(Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).strength(0.5F, 3.0F).sound(SoundType.GRASS))
   );
   public static final DeferredBlock<Block> GRASS_LOWER_ROOF = BLOCKS.register(
      "grass_lower_roof",
      () -> new BaseRoof(
         net.minecraft.world.level.block.Blocks.DIRT.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).strength(0.5F, 3.0F).sound(SoundType.GRASS)
      )
   );
   public static final DeferredBlock<Block> GRASS_STEEP_ROOF = BLOCKS.register(
      "grass_steep_roof",
      () -> new SteepRoof(
         net.minecraft.world.level.block.Blocks.DIRT.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).strength(0.5F, 3.0F).sound(SoundType.GRASS)
      )
   );
   public static final DeferredBlock<Block> GRASS_UPPER_LOWER_ROOF = BLOCKS.register(
      "grass_upper_lower_roof",
      () -> new Lower(
         net.minecraft.world.level.block.Blocks.DIRT.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).strength(0.5F, 3.0F).sound(SoundType.GRASS)
      )
   );
   public static final DeferredBlock<Block> GRASS_UPPER_STEEP_ROOF = BLOCKS.register(
      "grass_upper_steep_roof",
      () -> new Steep(
         net.minecraft.world.level.block.Blocks.DIRT.defaultBlockState(),
         Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).strength(0.5F, 3.0F).sound(SoundType.GRASS)
      )
   );
}

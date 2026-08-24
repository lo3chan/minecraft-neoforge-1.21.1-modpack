package com.yungnickyoung.minecraft.betterdungeons.world.processor.small_nether_dungeon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.betterdungeons.BetterDungeonsCommon;
import com.yungnickyoung.minecraft.betterdungeons.module.StructureProcessorTypeModule;
import com.yungnickyoung.minecraft.betterdungeons.world.DungeonContext;
import com.yungnickyoung.minecraft.betterdungeons.world.DungeonType;
import com.yungnickyoung.minecraft.yungsapi.world.banner.Banner;
import com.yungnickyoung.minecraft.yungsapi.world.banner.Banner.Builder;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerPatterns;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SmallNetherDungeonBannerProcessor extends StructureProcessor {
   public static final MapCodec<SmallNetherDungeonBannerProcessor> CODEC = RecordCodecBuilder.mapCodec(
      codecBuilder -> codecBuilder.group(Codec.STRING.fieldOf("dungeon_type").forGetter(processor -> processor.getDungeonType().getName()))
         .apply(codecBuilder, codecBuilder.stable(SmallNetherDungeonBannerProcessor::new))
   );
   private final DungeonType dungeonType;
   public static final Banner SKELETON_BANNER = new Builder()
      .blockState(Blocks.BLACK_WALL_BANNER.defaultBlockState())
      .pattern(BannerPatterns.CURLY_BORDER, DyeColor.WHITE)
      .pattern(BannerPatterns.STRIPE_CENTER, DyeColor.WHITE)
      .pattern(BannerPatterns.STRIPE_BOTTOM, DyeColor.BLACK)
      .pattern(BannerPatterns.STRIPE_TOP, DyeColor.WHITE)
      .pattern(BannerPatterns.CREEPER, DyeColor.WHITE)
      .pattern(BannerPatterns.GRADIENT, DyeColor.BLACK)
      .customName("Vengeful Banner")
      .customColor("dark_gray")
      .build();
   public static final Banner WITHER_SKELETON_BANNER = new Builder()
      .blockState(Blocks.RED_WALL_BANNER.defaultBlockState())
      .pattern(BannerPatterns.CURLY_BORDER, DyeColor.BLACK)
      .pattern(BannerPatterns.STRIPE_CENTER, DyeColor.BLACK)
      .pattern(BannerPatterns.STRIPE_BOTTOM, DyeColor.RED)
      .pattern(BannerPatterns.CREEPER, DyeColor.BLACK)
      .pattern(BannerPatterns.STRIPE_TOP, DyeColor.BLACK)
      .pattern(BannerPatterns.GRADIENT_UP, DyeColor.BLACK)
      .customName("Banner of Decay")
      .customColor("dark_purple")
      .build();
   public static final Banner ZOMBIFIED_PIGLIN_BANNER = new Builder()
      .blockState(Blocks.PINK_WALL_BANNER.defaultBlockState())
      .pattern(BannerPatterns.STRIPE_LEFT, DyeColor.GREEN)
      .pattern(BannerPatterns.TRIANGLES_TOP, DyeColor.BLACK)
      .pattern(BannerPatterns.TRIANGLES_TOP, DyeColor.PINK)
      .pattern(BannerPatterns.STRIPE_CENTER, DyeColor.PINK)
      .pattern(BannerPatterns.HALF_VERTICAL_MIRROR, DyeColor.LIGHT_GRAY)
      .pattern(BannerPatterns.DIAGONAL_RIGHT, DyeColor.GREEN)
      .pattern(BannerPatterns.CREEPER, DyeColor.WHITE)
      .pattern(BannerPatterns.HALF_HORIZONTAL_MIRROR, DyeColor.PINK)
      .pattern(BannerPatterns.RHOMBUS_MIDDLE, DyeColor.PINK)
      .pattern(BannerPatterns.PIGLIN, DyeColor.BLACK)
      .pattern(BannerPatterns.GRADIENT_UP, DyeColor.PINK)
      .pattern(BannerPatterns.STRIPE_BOTTOM, DyeColor.BLACK)
      .customName("Banner of Pork")
      .customColor("light_purple")
      .build();
   public static final Banner BLAZE_BANNER = new Builder()
      .blockState(Blocks.RED_WALL_BANNER.defaultBlockState())
      .pattern(BannerPatterns.STRIPE_SMALL, DyeColor.YELLOW)
      .pattern(BannerPatterns.TRIANGLE_TOP, DyeColor.RED)
      .pattern(BannerPatterns.TRIANGLE_TOP, DyeColor.RED)
      .pattern(BannerPatterns.FLOWER, DyeColor.ORANGE)
      .pattern(BannerPatterns.SKULL, DyeColor.YELLOW)
      .pattern(BannerPatterns.CURLY_BORDER, DyeColor.RED)
      .pattern(BannerPatterns.GRADIENT_UP, DyeColor.BLACK)
      .customName("Banner of Rage")
      .customColor("gold")
      .build();

   private SmallNetherDungeonBannerProcessor(String dungeonType) {
      this.dungeonType = DungeonType.fromString(dungeonType);
   }

   public DungeonType getDungeonType() {
      return this.dungeonType;
   }

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (blockInfoGlobal.state().getBlock() instanceof AbstractBannerBlock
         && blockInfoGlobal.state().getBlock() == Blocks.GRAY_WALL_BANNER
         && (blockInfoGlobal.nbt().get("patterns") == null || blockInfoGlobal.nbt().getList("patterns", 10).isEmpty())) {
         DungeonContext context = DungeonContext.peek();
         if (context.getBannerCount() >= BetterDungeonsCommon.CONFIG.smallNetherDungeons.bannerMaxCount) {
            return new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.AIR.defaultBlockState(), null);
         }

         RandomSource random = structurePlacementData.getRandom(blockInfoGlobal.pos());
         if (random.nextFloat() > 0.1F) {
            return new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.AIR.defaultBlockState(), null);
         }

         Banner banner = this.getBannerForType();
         Direction facing = (Direction)blockInfoGlobal.state().getValue(BlockStateProperties.HORIZONTAL_FACING);
         BlockState newState = (BlockState)banner.getState().setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
         CompoundTag newNBT = this.copyNBT(banner.getNbt());
         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), newState, newNBT);
         context.incrementBannerCount();
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.SMALL_NETHER_DUNGEON_BANNER_PROCESSOR;
   }

   private Banner getBannerForType() {
      return switch (this.dungeonType) {
         case SKELETON -> SKELETON_BANNER;
         case ZOMBIFIED_PIGLIN -> ZOMBIFIED_PIGLIN_BANNER;
         case WITHER_SKELETON -> WITHER_SKELETON_BANNER;
         case BLAZE -> BLAZE_BANNER;
         default -> {
            BetterDungeonsCommon.LOGGER.warn("Invalid DungeonType {} for small_nether_dungeon_banner_processor! This shouldn't happen!", this.dungeonType);
            yield SKELETON_BANNER;
         }
      };
   }

   private CompoundTag copyNBT(CompoundTag other) {
      CompoundTag nbt = new CompoundTag();
      nbt.merge(other);
      return nbt;
   }
}

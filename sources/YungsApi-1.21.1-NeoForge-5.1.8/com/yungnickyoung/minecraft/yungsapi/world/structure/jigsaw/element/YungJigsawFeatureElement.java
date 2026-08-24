package com.yungnickyoung.minecraft.yungsapi.world.structure.jigsaw.element;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.yungsapi.module.StructurePoolElementTypeModule;
import com.yungnickyoung.minecraft.yungsapi.world.structure.condition.StructureCondition;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.adaptations.EnhancedTerrainAdaptation;
import java.util.List;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.JigsawBlockEntity.JointType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class YungJigsawFeatureElement extends YungJigsawPoolElement {
   public static final MapCodec<YungJigsawFeatureElement> CODEC = RecordCodecBuilder.mapCodec(
      builder -> builder.group(
            PlacedFeature.CODEC.fieldOf("feature").forGetter(element -> element.feature),
            projectionCodec(),
            nameCodec(),
            maxCountCodec(),
            minRequiredDepthCodec(),
            maxPossibleDepthCodec(),
            isPriorityCodec(),
            ignoreBoundsCodec(),
            conditionCodec(),
            enhancedTerrainAdaptationCodec()
         )
         .apply(builder, YungJigsawFeatureElement::new)
   );
   private final Holder<PlacedFeature> feature;
   private final CompoundTag defaultJigsawNBT;

   public YungJigsawFeatureElement(
      Holder<PlacedFeature> feature,
      Projection projection,
      Optional<String> name,
      Optional<Integer> maxCount,
      Optional<Integer> minRequiredDepth,
      Optional<Integer> maxPossibleDepth,
      boolean isPriority,
      boolean ignoreBounds,
      StructureCondition condition,
      Optional<EnhancedTerrainAdaptation> enhancedTerrainAdaptation
   ) {
      super(projection, name, maxCount, minRequiredDepth, maxPossibleDepth, isPriority, ignoreBounds, condition, enhancedTerrainAdaptation);
      this.feature = feature;
      this.defaultJigsawNBT = this.fillDefaultJigsawNBT();
   }

   public Vec3i getSize(StructureTemplateManager structureTemplateManager, Rotation rotation) {
      return Vec3i.ZERO;
   }

   public List<StructureBlockInfo> getShuffledJigsawBlocks(
      StructureTemplateManager structureTemplateManager, BlockPos blockPos, Rotation rotation, RandomSource randomSource
   ) {
      List<StructureBlockInfo> jigsawBlocks = Lists.newArrayList();
      jigsawBlocks.add(
         new StructureBlockInfo(
            blockPos,
            (BlockState)Blocks.JIGSAW.defaultBlockState().setValue(JigsawBlock.ORIENTATION, FrontAndTop.fromFrontAndTop(Direction.DOWN, Direction.SOUTH)),
            this.defaultJigsawNBT
         )
      );
      return jigsawBlocks;
   }

   public BoundingBox getBoundingBox(StructureTemplateManager structureTemplateManager, BlockPos blockPos, Rotation rotation) {
      Vec3i size = this.getSize(structureTemplateManager, rotation);
      return new BoundingBox(
         blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + size.getX(), blockPos.getY() + size.getY(), blockPos.getZ() + size.getZ()
      );
   }

   public boolean place(
      StructureTemplateManager structureTemplateManager,
      WorldGenLevel worldGenLevel,
      StructureManager structureManager,
      ChunkGenerator chunkGenerator,
      BlockPos pos,
      BlockPos pivotPos,
      Rotation rotation,
      BoundingBox boundingBox,
      RandomSource randomSource,
      LiquidSettings liquidSettings,
      boolean replaceJigsaws
   ) {
      return ((PlacedFeature)this.feature.value()).place(worldGenLevel, chunkGenerator, randomSource, pos);
   }

   private CompoundTag fillDefaultJigsawNBT() {
      CompoundTag $$0 = new CompoundTag();
      $$0.putString("name", "minecraft:bottom");
      $$0.putString("final_state", "minecraft:air");
      $$0.putString("pool", "minecraft:empty");
      $$0.putString("target", "minecraft:empty");
      $$0.putString("joint", JointType.ROLLABLE.getSerializedName());
      return $$0;
   }

   public StructurePoolElementType<?> getType() {
      return StructurePoolElementTypeModule.YUNG_FEATURE_ELEMENT;
   }

   public String toString() {
      return String.format(
         "YungJigsawSingle[%s][%s][%s][%s]",
         this.name.orElse("<unnamed>"),
         this.feature,
         this.maxCount.isPresent() ? this.maxCount.get() : "no max count",
         this.isPriority
      );
   }
}

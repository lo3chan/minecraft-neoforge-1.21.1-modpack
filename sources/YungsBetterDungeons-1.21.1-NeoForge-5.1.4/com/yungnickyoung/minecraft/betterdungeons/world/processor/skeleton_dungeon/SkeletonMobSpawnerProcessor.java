package com.yungnickyoung.minecraft.betterdungeons.world.processor.skeleton_dungeon;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betterdungeons.module.StructureProcessorTypeModule;
import com.yungnickyoung.minecraft.yungsapi.world.spawner.MobSpawnerData;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SkeletonMobSpawnerProcessor extends StructureProcessor {
   public static final SkeletonMobSpawnerProcessor INSTANCE = new SkeletonMobSpawnerProcessor();
   public static final MapCodec<SkeletonMobSpawnerProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

   public StructureBlockInfo processBlock(
      LevelReader levelReader,
      BlockPos jigsawPiecePos,
      BlockPos jigsawPieceBottomCenterPos,
      StructureBlockInfo blockInfoLocal,
      StructureBlockInfo blockInfoGlobal,
      StructurePlaceSettings structurePlacementData
   ) {
      if (blockInfoGlobal.state().getBlock() instanceof SpawnerBlock) {
         MobSpawnerData spawner = MobSpawnerData.builder()
            .spawnPotentials(
               SimpleWeightedRandomList.single(
                  new SpawnData(
                     (CompoundTag)Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("id", "minecraft:skeleton")),
                     Optional.empty(),
                     Optional.empty()
                  )
               )
            )
            .requiredPlayerRange(18)
            .maxNearbyEntities(8)
            .maxSpawnDelay(650)
            .setEntityType(EntityType.SKELETON)
            .build();
         CompoundTag nbt = spawner.save();
         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.SPAWNER.defaultBlockState(), nbt);
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.SKELETON_MOB_SPAWNER_PROCESSOR;
   }
}

package com.yungnickyoung.minecraft.betterdungeons.world.processor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.betterdungeons.module.StructureProcessorTypeModule;
import com.yungnickyoung.minecraft.yungsapi.world.spawner.MobSpawnerData;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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
public class MobSpawnerProcessor extends StructureProcessor {
   public static final MapCodec<MobSpawnerProcessor> CODEC = RecordCodecBuilder.mapCodec(
      codecBuilder -> codecBuilder.group(ResourceLocation.CODEC.fieldOf("spawner_mob").forGetter(MobSpawnerProcessor::getSpawnerMob))
         .apply(codecBuilder, codecBuilder.stable(MobSpawnerProcessor::new))
   );
   private final ResourceLocation spawnerMob;

   private MobSpawnerProcessor(ResourceLocation spawnerMob) {
      this.spawnerMob = spawnerMob;
   }

   public ResourceLocation getSpawnerMob() {
      return this.spawnerMob;
   }

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
                     (CompoundTag)Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("id", this.spawnerMob.toString())),
                     Optional.empty(),
                     Optional.empty()
                  )
               )
            )
            .setEntityType((EntityType)BuiltInRegistries.ENTITY_TYPE.get(this.spawnerMob))
            .build();
         CompoundTag nbt = spawner.save();
         blockInfoGlobal = new StructureBlockInfo(blockInfoGlobal.pos(), Blocks.SPAWNER.defaultBlockState(), nbt);
      }

      return blockInfoGlobal;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorTypeModule.MOB_SPAWNER_PROCESSOR;
   }
}

package com.finndog.moogs_structures.world.processors;

import com.finndog.moogs_structures.modinit.MoogsStructuresProcessors;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class SpawnerRandomizingProcessor extends StructureProcessor {
   public static final MapCodec<SpawnerRandomizingProcessor> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            SpawnerRandomizingProcessor.WeightedEntity.CODEC.listOf().fieldOf("weighted_entities").forGetter(p -> p.weightedEntities),
            Codec.intRange(0, 2147483647).fieldOf("delay").orElse(20).forGetter(p -> p.delay),
            Codec.intRange(0, 2147483647).fieldOf("max_nearby_entities").orElse(6).forGetter(p -> p.maxNearbyEntities),
            Codec.intRange(0, 2147483647).fieldOf("max_spawn_delay").orElse(800).forGetter(p -> p.maxSpawnDelay),
            Codec.intRange(0, 2147483647).fieldOf("min_spawn_delay").orElse(200).forGetter(p -> p.minSpawnDelay),
            Codec.intRange(0, 2147483647).fieldOf("required_player_range").orElse(16).forGetter(p -> p.requiredPlayerRange),
            Codec.intRange(0, 2147483647).fieldOf("spawn_count").orElse(4).forGetter(p -> p.spawnCount),
            Codec.intRange(0, 2147483647).fieldOf("spawn_range").orElse(4).forGetter(p -> p.spawnRange),
            BlockState.CODEC.fieldOf("spawner_replacement_block").orElse(Blocks.AIR.defaultBlockState()).forGetter(p -> p.replacementState)
         )
         .apply(instance, instance.stable(SpawnerRandomizingProcessor::new))
   );
   public final List<SpawnerRandomizingProcessor.WeightedEntity> weightedEntities;
   public final int delay;
   public final int maxNearbyEntities;
   public final int maxSpawnDelay;
   public final int minSpawnDelay;
   public final int requiredPlayerRange;
   public final int spawnCount;
   public final int spawnRange;
   public final BlockState replacementState;

   private SpawnerRandomizingProcessor(
      List<SpawnerRandomizingProcessor.WeightedEntity> weightedEntities,
      int delay,
      int maxNearbyEntities,
      int maxSpawnDelay,
      int minSpawnDelay,
      int requiredPlayerRange,
      int spawnCount,
      int spawnRange,
      BlockState replacementState
   ) {
      this.weightedEntities = weightedEntities;
      this.delay = delay;
      this.maxNearbyEntities = maxNearbyEntities;
      this.maxSpawnDelay = maxSpawnDelay;
      this.minSpawnDelay = minSpawnDelay;
      this.requiredPlayerRange = requiredPlayerRange;
      this.spawnCount = spawnCount;
      this.spawnRange = spawnRange;
      this.replacementState = replacementState;
   }

   public StructureBlockInfo processBlock(
      LevelReader worldView,
      BlockPos pos,
      BlockPos blockPos,
      StructureBlockInfo structureBlockInfoLocal,
      StructureBlockInfo structureBlockInfoWorld,
      StructurePlaceSettings structurePlacementData
   ) {
      if (structureBlockInfoWorld.state().getBlock() instanceof SpawnerBlock) {
         BlockPos worldPos = structureBlockInfoWorld.pos();
         RandomSource random = structurePlacementData.getRandom(structureBlockInfoWorld.pos());
         CompoundTag spawnerNBT = this.buildSpawnerNbt(random);
         return spawnerNBT == null
            ? new StructureBlockInfo(worldPos, this.replacementState, null)
            : new StructureBlockInfo(worldPos, structureBlockInfoWorld.state(), spawnerNBT);
      } else {
         return structureBlockInfoWorld;
      }
   }

   private CompoundTag buildSpawnerNbt(RandomSource random) {
      if (this.weightedEntities.isEmpty()) {
         return null;
      } else {
         SpawnerRandomizingProcessor.WeightedEntity entry = pickWeightedRandom(this.weightedEntities, random);
         if (entry == null) {
            return null;
         } else {
            ResourceLocation entityRL = BuiltInRegistries.ENTITY_TYPE.getKey(entry.entity());
            CompoundTag entityData = new CompoundTag();
            entry.nbt().ifPresent(nbt -> entityData.merge(nbt.copy()));
            entityData.putString("id", entityRL.toString());
            CompoundTag compound = new CompoundTag();
            compound.putShort("Delay", (short)this.delay);
            compound.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
            compound.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
            compound.putShort("SpawnCount", (short)this.spawnCount);
            compound.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
            compound.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
            compound.putShort("SpawnRange", (short)this.spawnRange);
            CompoundTag spawnPotentialData = new CompoundTag();
            spawnPotentialData.put("entity", entityData.copy());
            CompoundTag listEntry = new CompoundTag();
            listEntry.put("data", spawnPotentialData);
            listEntry.putInt("weight", 1);
            ListTag listTag = new ListTag();
            listTag.add(listEntry);
            compound.put("SpawnPotentials", listTag);
            CompoundTag spawnData = new CompoundTag();
            spawnData.put("entity", entityData);
            compound.put("SpawnData", spawnData);
            return compound;
         }
      }
   }

   private static SpawnerRandomizingProcessor.WeightedEntity pickWeightedRandom(List<SpawnerRandomizingProcessor.WeightedEntity> list, RandomSource random) {
      double total = 0.0;

      for (SpawnerRandomizingProcessor.WeightedEntity e : list) {
         total += e.weight();
      }

      double pick = random.nextFloat() * total;

      for (SpawnerRandomizingProcessor.WeightedEntity e : list) {
         pick -= e.weight();
         if (pick <= 0.0) {
            return e;
         }
      }

      return list.get(list.size() - 1);
   }

   protected StructureProcessorType<?> getType() {
      return MoogsStructuresProcessors.SPAWNER_RANDOMIZING_PROCESSOR.get();
   }

   public record WeightedEntity(EntityType<?> entity, int weight, Optional<CompoundTag> nbt) {
      public static final Codec<SpawnerRandomizingProcessor.WeightedEntity> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(SpawnerRandomizingProcessor.WeightedEntity::entity),
               Codec.intRange(1, 2147483647).fieldOf("weight").forGetter(SpawnerRandomizingProcessor.WeightedEntity::weight),
               CompoundTag.CODEC.optionalFieldOf("nbt").forGetter(SpawnerRandomizingProcessor.WeightedEntity::nbt)
            )
            .apply(instance, SpawnerRandomizingProcessor.WeightedEntity::new)
      );
   }
}

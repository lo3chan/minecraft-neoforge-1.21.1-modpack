package com.finndog.moogs_structures.world.processors;

import com.finndog.moogs_structures.MoogsStructuresCommon;
import com.finndog.moogs_structures.misc.trialspawnerconfig.TrialSpawnerConfigManager;
import com.finndog.moogs_structures.modinit.MoogsStructuresProcessors;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.TrialSpawnerBlock;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class TrialSpawnerRandomizingProcessor extends StructureProcessor {
   public static final MapCodec<TrialSpawnerRandomizingProcessor> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("normal_config").forGetter(p -> p.normalConfig),
            ResourceLocation.CODEC.optionalFieldOf("ominous_config").forGetter(p -> p.ominousConfig)
         )
         .apply(instance, instance.stable(TrialSpawnerRandomizingProcessor::new))
   );
   public final ResourceLocation normalConfig;
   public final Optional<ResourceLocation> ominousConfig;

   private TrialSpawnerRandomizingProcessor(ResourceLocation normalConfig, Optional<ResourceLocation> ominousConfig) {
      this.normalConfig = normalConfig;
      this.ominousConfig = ominousConfig;
   }

   public StructureBlockInfo processBlock(
      LevelReader worldView,
      BlockPos pos,
      BlockPos blockPos,
      StructureBlockInfo structureBlockInfoLocal,
      StructureBlockInfo structureBlockInfoWorld,
      StructurePlaceSettings structurePlacementData
   ) {
      if (!(structureBlockInfoWorld.state().getBlock() instanceof TrialSpawnerBlock)) {
         return structureBlockInfoWorld;
      } else {
         CompoundTag existing = structureBlockInfoWorld.nbt();
         CompoundTag newNbt = existing != null ? existing.copy() : new CompoundTag();
         newNbt.remove("server_data");
         newNbt.remove("shared_data");
         newNbt.remove("spawn_data");
         newNbt.remove("cooldown_end_at_tick");
         newNbt.remove("next_mob_spawns_at");
         CompoundTag normal = TrialSpawnerConfigManager.INSTANCE.get(this.normalConfig);
         if (normal == null) {
            MoogsStructuresCommon.LOGGER
               .warn("Moog's Structure Lib: trial_spawner config '{}' not found at {}", this.normalConfig, structureBlockInfoWorld.pos());
         } else {
            newNbt.put("normal_config", normal.copy());
         }

         if (this.ominousConfig.isPresent()) {
            CompoundTag ominous = TrialSpawnerConfigManager.INSTANCE.get(this.ominousConfig.get());
            if (ominous == null) {
               MoogsStructuresCommon.LOGGER
                  .warn("Moog's Structure Lib: trial_spawner config '{}' not found at {}", this.ominousConfig.get(), structureBlockInfoWorld.pos());
            } else {
               newNbt.put("ominous_config", ominous.copy());
            }
         }

         return new StructureBlockInfo(structureBlockInfoWorld.pos(), structureBlockInfoWorld.state(), newNbt);
      }
   }

   protected StructureProcessorType<?> getType() {
      return MoogsStructuresProcessors.TRIAL_SPAWNER_RANDOMIZING_PROCESSOR.get();
   }
}

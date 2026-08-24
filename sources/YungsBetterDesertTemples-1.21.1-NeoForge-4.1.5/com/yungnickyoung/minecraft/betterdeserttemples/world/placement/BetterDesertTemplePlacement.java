package com.yungnickyoung.minecraft.betterdeserttemples.world.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.betterdeserttemples.mixin.accessor.ChunkGeneratorStructureStateAccessor;
import com.yungnickyoung.minecraft.betterdeserttemples.module.StructurePlacementTypeModule;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement.ExclusionZone;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement.FrequencyReductionMethod;

public class BetterDesertTemplePlacement extends RandomSpreadStructurePlacement {
   public static final MapCodec<BetterDesertTemplePlacement> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Vec3i.offsetCodec(16).optionalFieldOf("locate_offset", Vec3i.ZERO).forGetter(rec$ -> rec$.locateOffset()),
            FrequencyReductionMethod.CODEC
               .optionalFieldOf("frequency_reduction_method", FrequencyReductionMethod.DEFAULT)
               .forGetter(rec$ -> rec$.frequencyReductionMethod()),
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("frequency", 1.0F).forGetter(rec$ -> rec$.frequency()),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("salt").forGetter(rec$ -> rec$.salt()),
            ExclusionZone.CODEC.optionalFieldOf("exclusion_zone").forGetter(rec$ -> rec$.exclusionZone()),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("spacing").forGetter(RandomSpreadStructurePlacement::spacing),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("separation").forGetter(RandomSpreadStructurePlacement::separation),
            RandomSpreadType.CODEC.optionalFieldOf("spread_type", RandomSpreadType.LINEAR).forGetter(RandomSpreadStructurePlacement::spreadType)
         )
         .apply(instance, instance.stable(BetterDesertTemplePlacement::new))
   );

   public BetterDesertTemplePlacement(
      Vec3i locateOffset,
      FrequencyReductionMethod frequencyReductionMethod,
      Float frequency,
      Integer salt,
      Optional<ExclusionZone> exclusionZone,
      Integer spacing,
      Integer separation,
      RandomSpreadType randomSpreadType
   ) {
      super(locateOffset, frequencyReductionMethod, frequency, salt, exclusionZone, spacing, separation, randomSpreadType);
   }

   protected boolean isPlacementChunk(ChunkGeneratorStructureState chunkGeneratorStructureState, int chunkX, int chunkZ) {
      BiomeSource biomeSource = ((ChunkGeneratorStructureStateAccessor)chunkGeneratorStructureState).getBiomeSource();
      RandomState randomState = chunkGeneratorStructureState.randomState();
      long seed = chunkGeneratorStructureState.getLevelSeed();
      ChunkPos chunkPos = this.getPotentialStructureChunk(seed, chunkX, chunkZ);
      if (chunkPos.x == chunkX && chunkPos.z == chunkZ) {
         BlockPos structurePos = chunkPos.getMiddleBlockPosition(120);
         boolean isOceanOrRiverNear = biomeSource.findBiomeHorizontal(
               structurePos.getX(),
               structurePos.getY(),
               structurePos.getZ(),
               48,
               2,
               biomeHolder -> biomeHolder.is(BiomeTags.IS_RIVER) || biomeHolder.is(BiomeTags.IS_OCEAN),
               randomState.oreRandom().at(structurePos),
               true,
               randomState.sampler()
            )
            != null;
         return !isOceanOrRiverNear;
      } else {
         return false;
      }
   }

   public StructurePlacementType<?> type() {
      return StructurePlacementTypeModule.BETTER_DESERT_TEMPLE_PLACEMENT;
   }
}

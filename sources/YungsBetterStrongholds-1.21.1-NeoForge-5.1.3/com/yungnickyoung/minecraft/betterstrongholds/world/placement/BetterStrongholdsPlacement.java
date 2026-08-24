package com.yungnickyoung.minecraft.betterstrongholds.world.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yungnickyoung.minecraft.betterstrongholds.module.StructurePlacementTypeModule;
import java.util.Optional;
import net.minecraft.core.Vec3i;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement.ExclusionZone;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement.FrequencyReductionMethod;

public class BetterStrongholdsPlacement extends RandomSpreadStructurePlacement {
   public static final MapCodec<BetterStrongholdsPlacement> CODEC = RecordCodecBuilder.mapCodec(
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
            RandomSpreadType.CODEC.optionalFieldOf("spread_type", RandomSpreadType.LINEAR).forGetter(RandomSpreadStructurePlacement::spreadType),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("chunk_distance_to_first_ring").forGetter(BetterStrongholdsPlacement::chunkDistanceToFirstRing),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("ring_chunk_thickness").forGetter(BetterStrongholdsPlacement::ringChunkThickness),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("max_ring_section").forGetter(BetterStrongholdsPlacement::maxRingSection)
         )
         .apply(instance, instance.stable(BetterStrongholdsPlacement::new))
   );
   private final int chunkDistanceToFirstRing;
   private final int ringChunkThickness;
   private final Optional<Integer> maxRingSection;

   public BetterStrongholdsPlacement(
      Vec3i locateOffset,
      FrequencyReductionMethod frequencyReductionMethod,
      Float frequency,
      Integer salt,
      Optional<ExclusionZone> exclusionZone,
      Integer spacing,
      Integer separation,
      RandomSpreadType randomSpreadType,
      Integer chunkDistanceToFirstRing,
      Integer ringChunkThickness,
      Optional<Integer> maxRingSection
   ) {
      super(locateOffset, frequencyReductionMethod, frequency, salt, exclusionZone, spacing, separation, randomSpreadType);
      this.chunkDistanceToFirstRing = chunkDistanceToFirstRing;
      this.ringChunkThickness = ringChunkThickness;
      this.maxRingSection = maxRingSection;
   }

   protected boolean isPlacementChunk(ChunkGeneratorStructureState chunkGeneratorStructureState, int chunkX, int chunkZ) {
      long seed = chunkGeneratorStructureState.getLevelSeed();
      ChunkPos chunkPos = this.getPotentialStructureChunk(seed, chunkX, chunkZ);
      if (chunkPos.x == chunkX && chunkPos.z == chunkZ) {
         int chunkDistance = (int)Math.sqrt(chunkX * chunkX + chunkZ * chunkZ);
         int shiftedChunkDistance = chunkDistance + (this.ringChunkThickness - this.chunkDistanceToFirstRing);
         int ringSection = shiftedChunkDistance / this.ringChunkThickness;
         return this.maxRingSection.isPresent() && ringSection > this.maxRingSection.get() ? false : ringSection % 2 == 1;
      } else {
         return false;
      }
   }

   public StructurePlacementType<?> type() {
      return StructurePlacementTypeModule.BETTER_STRONGHOLD_PLACEMENT;
   }

   public int chunkDistanceToFirstRing() {
      return this.chunkDistanceToFirstRing;
   }

   public int ringChunkThickness() {
      return this.ringChunkThickness;
   }

   public Optional<Integer> maxRingSection() {
      return this.maxRingSection;
   }
}

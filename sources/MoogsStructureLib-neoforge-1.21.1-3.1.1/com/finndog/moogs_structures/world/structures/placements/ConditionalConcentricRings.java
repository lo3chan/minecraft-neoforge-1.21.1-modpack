package com.finndog.moogs_structures.world.structures.placements;

import com.finndog.moogs_structures.config.MslConfig;
import com.finndog.moogs_structures.config.ReplaceVanillaManager;
import com.finndog.moogs_structures.modinit.MoogsStructuresStructurePlacementType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement.ExclusionZone;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement.FrequencyReductionMethod;

public class ConditionalConcentricRings extends ConcentricRingsStructurePlacement {
   public static final MapCodec<ConditionalConcentricRings> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Vec3i.offsetCodec(16).optionalFieldOf("locate_offset", Vec3i.ZERO).forGetter(rec$ -> rec$.locateOffset()),
            FrequencyReductionMethod.CODEC
               .optionalFieldOf("frequency_reduction_method", FrequencyReductionMethod.DEFAULT)
               .forGetter(rec$ -> rec$.frequencyReductionMethod()),
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("frequency", 1.0F).forGetter(rec$ -> rec$.frequency()),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("salt").forGetter(rec$ -> rec$.salt()),
            ExclusionZone.CODEC.optionalFieldOf("exclusion_zone").forGetter(rec$ -> rec$.exclusionZone()),
            Codec.intRange(0, 1023).fieldOf("distance").forGetter(ConcentricRingsStructurePlacement::distance),
            Codec.intRange(0, 1023).fieldOf("spread").forGetter(ConcentricRingsStructurePlacement::spread),
            RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("preferred_biomes").forGetter(ConcentricRingsStructurePlacement::preferredBiomes),
            Codec.STRING.fieldOf("modid").forGetter(p -> p.modid),
            Codec.STRING.fieldOf("vanilla_key").forGetter(p -> p.vanillaKey),
            Codec.intRange(1, 4095).fieldOf("enabled_count").forGetter(p -> p.enabledCount),
            Codec.intRange(0, 4095).fieldOf("disabled_count").forGetter(p -> p.disabledCount),
            Codec.STRING.optionalFieldOf("structure_id").forGetter(p -> p.structureId)
         )
         .apply(instance, instance.stable(ConditionalConcentricRings::new))
   );
   private final String modid;
   private final String vanillaKey;
   private final int enabledCount;
   private final int disabledCount;
   private final Optional<String> structureId;
   private final ResourceLocation structureIdRL;
   private volatile ResourceLocation owningSetIdRL;

   public ConditionalConcentricRings(
      Vec3i locateOffset,
      FrequencyReductionMethod frequencyReductionMethod,
      float frequency,
      int salt,
      Optional<ExclusionZone> exclusionZone,
      int distance,
      int spread,
      HolderSet<Biome> preferredBiomes,
      String modid,
      String vanillaKey,
      int enabledCount,
      int disabledCount,
      Optional<String> structureId
   ) {
      super(locateOffset, frequencyReductionMethod, frequency, salt, exclusionZone, distance, spread, enabledCount, preferredBiomes);
      this.modid = modid;
      this.vanillaKey = vanillaKey;
      this.enabledCount = enabledCount;
      this.disabledCount = disabledCount;
      this.structureId = structureId;
      this.structureIdRL = structureId.<ResourceLocation>map(ResourceLocation::tryParse).orElse(null);
   }

   public void setOwningSetId(ResourceLocation setId) {
      this.owningSetIdRL = setId;
   }

   private ResourceLocation effectiveDisableId() {
      return this.structureIdRL != null ? this.structureIdRL : this.owningSetIdRL;
   }

   public int count() {
      ResourceLocation disableId = this.effectiveDisableId();
      if (disableId != null && MslConfig.get().isStructureDisabled(disableId)) {
         return 0;
      } else {
         return ReplaceVanillaManager.isEnabled(this.modid, this.vanillaKey) ? this.enabledCount : this.disabledCount;
      }
   }

   public StructurePlacementType<?> type() {
      return MoogsStructuresStructurePlacementType.CONDITIONAL_CONCENTRIC_RINGS.get();
   }
}

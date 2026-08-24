package com.aetherteam.aether.world.placementmodifier;

import com.aetherteam.aether.AetherTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.structure.Structure;

public class DungeonBlacklistFilter extends PlacementFilter {
   public static final MapCodec<DungeonBlacklistFilter> CODEC = MapCodec.unit(DungeonBlacklistFilter::new);

   protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos) {
      if (!(context.getLevel() instanceof WorldGenRegion)) {
         return false;
      } else {
         StructureManager structureManager = context.getLevel().getLevel().structureManager();
         Registry<Structure> configuredStructureFeatureRegistry = context.getLevel().registryAccess().registryOrThrow(Registries.STRUCTURE);

         for (Holder<Structure> structure : configuredStructureFeatureRegistry.getOrCreateTag(AetherTags.Structures.DUNGEONS)) {
            if (structureManager.getStructureAt(pos, (Structure)structure.value()).isValid()) {
               return false;
            }
         }

         return true;
      }
   }

   public PlacementModifierType<?> type() {
      return (PlacementModifierType<?>)AetherPlacementModifiers.DUNGEON_BLACKLIST_FILTER.get();
   }
}

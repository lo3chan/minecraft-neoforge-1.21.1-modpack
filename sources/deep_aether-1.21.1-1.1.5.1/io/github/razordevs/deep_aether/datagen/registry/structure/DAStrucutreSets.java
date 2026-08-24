package io.github.razordevs.deep_aether.datagen.registry.structure;

import java.util.List;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;

public class DAStrucutreSets {
   public static final ResourceKey<StructureSet> BRASS_DUNGEON = createKey("brass_dungeon");

   private static ResourceKey<StructureSet> createKey(String name) {
      return ResourceKey.create(Registries.STRUCTURE_SET, ResourceLocation.fromNamespaceAndPath("deep_aether", name));
   }

   public static void bootstrap(BootstrapContext<StructureSet> context) {
      HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);
      context.register(
         BRASS_DUNGEON,
         new StructureSet(
            List.of(StructureSet.entry(structures.getOrThrow(DAStructures.BRASS_DUNGEON), 1)),
            new RandomSpreadStructurePlacement(20, 12, RandomSpreadType.LINEAR, 4325807)
         )
      );
   }
}

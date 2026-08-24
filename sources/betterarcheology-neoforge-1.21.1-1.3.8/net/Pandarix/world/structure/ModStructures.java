package net.Pandarix.world.structure;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.Pandarix.BACommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;

public class ModStructures {
   public static final Registrar<StructureType<?>> STRUCTURES = BACommon.REGISTRIES.get().get(Registries.STRUCTURE_TYPE);
   public static final RegistrySupplier<StructureType<ArcheologyStructures>> ARCHEOLOGY_STRUCTURES = STRUCTURES.register(
      BACommon.createResource("betterarcheology_structures"), () -> () -> ArcheologyStructures.CODEC
   );

   public static void register() {
      BACommon.logRegistryEvent(STRUCTURES);
   }
}

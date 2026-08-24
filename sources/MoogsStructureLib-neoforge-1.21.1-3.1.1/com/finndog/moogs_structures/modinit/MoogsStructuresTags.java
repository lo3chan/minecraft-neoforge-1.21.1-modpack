package com.finndog.moogs_structures.modinit;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public final class MoogsStructuresTags {
   public static TagKey<Structure> LARGER_LOCATE_SEARCH = TagKey.create(
      Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath("moogs_structures", "larger_locate_search")
   );
   public static TagKey<Structure> NO_BASALT = TagKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath("moogs_structures", "no_basalt"));
   public static TagKey<Structure> NO_DELTA = TagKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath("moogs_structures", "no_delta"));

   public static void initTags() {
   }
}

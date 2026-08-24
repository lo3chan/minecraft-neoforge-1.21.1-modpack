package com.yungnickyoung.minecraft.yungsapi.module;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public class TagModule {
   public static final TagKey<Structure> NO_DELTA = TagKey.create(
      Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath("yungsapi", "remove_delta_feature_in")
   );
   public static final TagKey<Structure> NO_BASALT = TagKey.create(
      Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath("yungsapi", "remove_basalt_columns_feature_in")
   );
   public static final TagKey<Structure> NO_MAGMA = TagKey.create(
      Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath("yungsapi", "remove_magma_feature_in")
   );
   public static final TagKey<Structure> NO_VINES = TagKey.create(
      Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath("yungsapi", "remove_vines_feature_in")
   );
}

package com.yungnickyoung.minecraft.betteroceanmonuments.module;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public class TagModule {
   public static final TagKey<Structure> BETTER_OCEAN_MONUMENT = TagKey.create(
      Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath("betteroceanmonuments", "better_ocean_monuments")
   );
}

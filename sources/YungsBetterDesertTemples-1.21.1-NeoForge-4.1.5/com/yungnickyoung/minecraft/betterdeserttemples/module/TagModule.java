package com.yungnickyoung.minecraft.betterdeserttemples.module;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public class TagModule {
   public static TagKey<Structure> APPLIES_MINING_FATIGUE = TagKey.create(
      Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath("betterdeserttemples", "applies_mining_fatigue")
   );
}

package com.seibel.distanthorizons.common.wrappers.block;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

public class BiomeWrapper$BiomeDeserializeResult_neoforge {
   public final boolean success;
   public final Holder<Biome> biome;

   public BiomeWrapper$BiomeDeserializeResult_neoforge(boolean success, Holder<Biome> biome) {
      this.success = success;
      this.biome = biome;
   }
}

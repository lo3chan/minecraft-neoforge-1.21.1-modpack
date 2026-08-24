package dev.worldgen.lithostitched.duck.mnbs;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate.ParameterList;

public interface MNBSPLDuck {
   void lithostitched$setParameters(ParameterList<Holder<Biome>> var1);

   void lithostitched$setMigrationBiome(Optional<Holder<Biome>> var1);

   Optional<Holder<Biome>> lithostitched$getMigrationBiome();
}

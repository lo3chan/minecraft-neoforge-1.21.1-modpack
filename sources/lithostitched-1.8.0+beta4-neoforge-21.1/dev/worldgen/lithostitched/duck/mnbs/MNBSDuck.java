package dev.worldgen.lithostitched.duck.mnbs;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.biome.Climate.ParameterList;

public interface MNBSDuck {
   Either<ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> lithostitched$getEntries();

   void lithostitched$setEntries(Either<ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> var1);
}

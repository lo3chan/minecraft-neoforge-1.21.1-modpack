package dev.worldgen.lithostitched.mixin.common;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.biome.Climate.ParameterList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({MultiNoiseBiomeSource.class})
public interface MultiNoiseBiomeSourceAccessor {
   @Accessor("parameters")
   Either<ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> getParameters();

   @Accessor("parameters")
   void setParameters(Either<ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> var1);
}

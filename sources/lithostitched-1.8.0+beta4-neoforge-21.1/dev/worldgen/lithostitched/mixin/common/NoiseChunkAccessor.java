package dev.worldgen.lithostitched.mixin.common;

import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({NoiseChunk.class})
public interface NoiseChunkAccessor {
   @Invoker("wrap")
   DensityFunction lithostitched$wrap(DensityFunction var1);
}

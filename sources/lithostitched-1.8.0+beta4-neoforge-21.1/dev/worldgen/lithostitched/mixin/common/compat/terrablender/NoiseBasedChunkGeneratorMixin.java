package dev.worldgen.lithostitched.mixin.common.compat.terrablender;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.worldgen.lithostitched.impl.worldgen.biomeinjector.internal.InjectorBiomeSource;
import dev.worldgen.lithostitched.platform.LithostitchedPlatform;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate.Sampler;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({NoiseBasedChunkGenerator.class})
public class NoiseBasedChunkGeneratorMixin {
   @WrapOperation(
      method = {"doCreateBiomes"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/levelgen/BelowZeroRetrogen;getBiomeResolver(Lnet/minecraft/world/level/biome/BiomeResolver;Lnet/minecraft/world/level/chunk/ChunkAccess;)Lnet/minecraft/world/level/biome/BiomeResolver;"
      )}
   )
   private BiomeResolver unwrapInjector(
      BiomeResolver unfinishedResolver, ChunkAccess protoChunk, Operation<BiomeResolver> operator, @Share("injector") LocalRef<InjectorBiomeSource> ref
   ) {
      BiomeResolver resolver = (BiomeResolver)operator.call(new Object[]{unfinishedResolver, protoChunk});
      if (resolver instanceof InjectorBiomeSource injector && LithostitchedPlatform.isModLoaded("terrablender")) {
         ref.set(injector);
         return injector.directDelegate();
      } else {
         return resolver;
      }
   }

   @WrapOperation(
      method = {"doCreateBiomes"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/chunk/ChunkAccess;fillBiomesFromNoise(Lnet/minecraft/world/level/biome/BiomeResolver;Lnet/minecraft/world/level/biome/Climate$Sampler;)V"
      )}
   )
   private void rewrapInjector(
      ChunkAccess protoChunk, BiomeResolver resolver, Sampler sampler, Operation<Void> operator, @Share("injector") LocalRef<InjectorBiomeSource> ref
   ) {
      InjectorBiomeSource injector = (InjectorBiomeSource)ref.get();
      if (injector != null) {
         InjectorBiomeSource cloned = injector.clone();
         cloned.baseResolver = resolver;
         resolver = cloned;
      }

      operator.call(new Object[]{protoChunk, resolver, sampler});
   }
}

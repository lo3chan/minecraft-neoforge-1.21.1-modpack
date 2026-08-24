package dev.corgitaco.enhancedcelestials2core.mixin.client;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientLevel.class})
public abstract class ClientWorldMixin extends Level {
   protected ClientWorldMixin(
      WritableLevelData levelData,
      ResourceKey<Level> dimension,
      RegistryAccess registryAccess,
      Holder<DimensionType> dimensionTypeRegistration,
      Supplier<ProfilerFiller> profiler,
      boolean isClientSide,
      boolean isDebug,
      long biomeZoomSeed,
      int maxChainedNeighborUpdates
   ) {
      super(levelData, dimension, registryAccess, dimensionTypeRegistration, profiler, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
   }

   @Inject(
      method = {"tick"},
      at = {@At("HEAD")}
   )
   private void attachLunarTick(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
      this.updateSkyBrightness();
   }
}

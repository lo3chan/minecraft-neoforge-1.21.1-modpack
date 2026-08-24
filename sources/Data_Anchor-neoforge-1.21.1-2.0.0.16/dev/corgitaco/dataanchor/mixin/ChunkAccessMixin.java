package dev.corgitaco.dataanchor.mixin;

import dev.corgitaco.dataanchor.data.TrackedDataContainer;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import dev.corgitaco.dataanchor.data.type.chunk.ChunkBlockStateInterceptor;
import dev.corgitaco.dataanchor.data.type.chunk.ChunkTrackedData;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ChunkAccess.class})
public class ChunkAccessMixin implements TrackedDataContainer<ChunkAccess, ChunkTrackedData>, ChunkBlockStateInterceptor.Internal {
   @Unique
   TrackedDataContainer<ChunkAccess, ChunkTrackedData> dataAnchor$trackedDataContainer;

   @Inject(
      method = {"<init>(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/chunk/UpgradeData;Lnet/minecraft/world/level/LevelHeightAccessor;Lnet/minecraft/core/Registry;J[Lnet/minecraft/world/level/chunk/LevelChunkSection;Lnet/minecraft/world/level/levelgen/blending/BlendingData;)V"},
      at = {@At("RETURN")}
   )
   private void dataAnchor$onInit(
      ChunkPos chunkPos,
      UpgradeData upgradeData,
      LevelHeightAccessor levelHeightAccessor,
      Registry biomeRegistry,
      long inhabitedTime,
      LevelChunkSection[] sections,
      BlendingData blendingData,
      CallbackInfo ci
   ) {
      if (levelHeightAccessor instanceof ServerLevelAccessor) {
         this.dataAnchor$trackedDataContainer = TrackedDataContainer.makeBasicContainer(TrackedDataRegistries.CHUNK, (ChunkAccess)this, false);
      } else {
         this.dataAnchor$trackedDataContainer = TrackedDataContainer.makeBasicContainer(TrackedDataRegistries.CHUNK, (ChunkAccess)this, true);
      }

      this.dataAnchor$createTrackedData();
   }

   @Override
   public <E extends ChunkTrackedData> Optional<E> dataAnchor$getTrackedData(TrackedDataKey<E> key) {
      return this.dataAnchor$trackedDataContainer.dataAnchor$getTrackedData(key);
   }

   @Override
   public void dataAnchor$createTrackedData() {
      this.dataAnchor$trackedDataContainer.dataAnchor$createTrackedData();
   }

   @Override
   public Collection<TrackedDataKey<ChunkTrackedData>> dataAnchor$getTrackedDataKeys() {
      return this.dataAnchor$trackedDataContainer.dataAnchor$getTrackedDataKeys();
   }

   @Nullable
   @Override
   public BlockState dataAnchor$getInterceptorState(BlockPos pos, BlockState original, @Nullable BlockState lastState, boolean isMoving) {
      BlockState replacement = lastState;

      for (TrackedDataKey<ChunkTrackedData> dataAnchor$getTrackedDataKey : this.dataAnchor$getTrackedDataKeys()) {
         Optional<ChunkTrackedData> trackedData = this.dataAnchor$getTrackedData(dataAnchor$getTrackedDataKey);
         if (trackedData.isPresent()) {
            ChunkTrackedData chunkTrackedData = trackedData.get();
            if (chunkTrackedData instanceof ChunkBlockStateInterceptor chunkBlockStateInterceptor) {
               replacement = chunkBlockStateInterceptor.getNewState(pos, original, replacement, isMoving);
            }
         }
      }

      return replacement;
   }
}

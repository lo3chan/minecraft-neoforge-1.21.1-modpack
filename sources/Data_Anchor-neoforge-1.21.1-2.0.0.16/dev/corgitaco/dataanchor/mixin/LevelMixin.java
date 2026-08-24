package dev.corgitaco.dataanchor.mixin;

import dev.corgitaco.dataanchor.data.DirtyMarker;
import dev.corgitaco.dataanchor.data.InternalDirtyMarker;
import dev.corgitaco.dataanchor.data.TickableTrackedData;
import dev.corgitaco.dataanchor.data.TrackedDataContainer;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import dev.corgitaco.dataanchor.data.type.blockentity.BlockEntityTrackedData;
import dev.corgitaco.dataanchor.data.type.blockentity.PendingBlockEntityTick;
import dev.corgitaco.dataanchor.data.type.level.LevelTrackedData;
import dev.corgitaco.dataanchor.data.type.level.TrackedLevelSavedData;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({Level.class})
public abstract class LevelMixin implements TrackedDataContainer<Level, LevelTrackedData>, InternalDirtyMarker, LevelAccessor {
   @Shadow
   @Final
   public boolean isClientSide;
   @Unique
   private TrackedDataContainer<Level, LevelTrackedData> dataAnchor$trackedDataContainer = TrackedDataContainer.makeBasicContainer(
      TrackedDataRegistries.LEVEL, (Level)this, this.isClientSide(), false
   );
   @Unique
   private final List<TickableTrackedData> dataAnchor$tickableLevelData = new CopyOnWriteArrayList<>();
   @Unique
   private volatile boolean dataAnchor$lazyLoadedTrackedData = false;

   @Shadow
   public abstract boolean isClientSide();

   @Override
   public <E extends LevelTrackedData> Optional<E> dataAnchor$getTrackedData(TrackedDataKey<E> key) {
      if (!this.dataAnchor$lazyLoadedTrackedData) {
         this.dataAnchor$ensureInitialized();
      }

      return this.dataAnchor$trackedDataContainer.dataAnchor$getTrackedData(key);
   }

   @Override
   public void dataAnchor$createTrackedData() {
      if (this instanceof ServerLevel serverLevel) {
         this.dataAnchor$trackedDataContainer = TrackedLevelSavedData.get(serverLevel);
      } else {
         this.dataAnchor$trackedDataContainer.dataAnchor$createTrackedData();
      }

      this.dataAnchor$tickableLevelData.clear();

      for (TrackedDataKey<LevelTrackedData> key : this.dataAnchor$trackedDataContainer.dataAnchor$getTrackedDataKeys()) {
         this.dataAnchor$trackedDataContainer.dataAnchor$getTrackedData(key).ifPresent(levelTrackedData -> {
            if (levelTrackedData instanceof TickableTrackedData tickableData && !this.dataAnchor$tickableLevelData.contains(tickableData)) {
               this.dataAnchor$tickableLevelData.add(tickableData);
            }
         });
      }
   }

   @Unique
   private synchronized void dataAnchor$ensureInitialized() {
      if (!this.dataAnchor$lazyLoadedTrackedData) {
         this.dataAnchor$createTrackedData();
         this.dataAnchor$lazyLoadedTrackedData = true;
      }
   }

   @Override
   public Collection<TrackedDataKey<LevelTrackedData>> dataAnchor$getTrackedDataKeys() {
      if (!this.dataAnchor$lazyLoadedTrackedData) {
         this.dataAnchor$ensureInitialized();
      }

      return this.dataAnchor$trackedDataContainer.dataAnchor$getTrackedDataKeys();
   }

   @Inject(
      method = {"tickBlockEntities()V"},
      at = {@At("RETURN")}
   )
   private void onTickBlockEntities(CallbackInfo ci) {
      for (TickableTrackedData tickableLevelDatum : this.dataAnchor$tickableLevelData) {
         tickableLevelDatum.tick();
      }
   }

   @Inject(
      method = {"tickBlockEntities()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/block/entity/TickingBlockEntity;tick()V"
      )},
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   private void dataAnchor$onTickBlockEntitiesEnd(
      CallbackInfo ci, ProfilerFiller profilerFiller, Iterator iterator, boolean bl, TickingBlockEntity tickingBlockEntity
   ) {
      if (this.getBlockEntity(tickingBlockEntity.getPos()) instanceof TrackedDataContainer container) {
         for (TrackedDataKey<BlockEntityTrackedData> key : container.dataAnchor$getTrackedDataKeys()) {
            container.dataAnchor$getTrackedData(key).ifPresent(data -> {
               if (data instanceof PendingBlockEntityTick tickableData) {
                  tickableData.blockEntityTick();
               }
            });
         }
      }
   }

   @Override
   public void dataAnchor$markDirty() {
      if (this.dataAnchor$trackedDataContainer instanceof TrackedLevelSavedData dirtyMarker) {
         dirtyMarker.setDirty();
      }
   }

   @Override
   public void dataAnchor$clearDirty() {
      this.dataAnchor$trackedDataContainer
         .dataAnchor$getTrackedDataKeys()
         .forEach(key -> this.dataAnchor$trackedDataContainer.dataAnchor$getTrackedData((TrackedDataKey<LevelTrackedData>)key).ifPresent(levelTrackedData -> {
            if (levelTrackedData instanceof DirtyMarker dirtyMarker) {
               dirtyMarker.clearDirty();
            }
         }));
   }
}

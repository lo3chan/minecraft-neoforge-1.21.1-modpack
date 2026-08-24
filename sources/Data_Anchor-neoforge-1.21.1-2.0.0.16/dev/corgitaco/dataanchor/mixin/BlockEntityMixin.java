package dev.corgitaco.dataanchor.mixin;

import dev.corgitaco.dataanchor.data.TrackedDataContainer;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import dev.corgitaco.dataanchor.data.type.blockentity.BlockEntityTrackedData;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockEntity.class})
public class BlockEntityMixin implements TrackedDataContainer<BlockEntity, BlockEntityTrackedData> {
   @Shadow
   @Nullable
   protected Level level;
   @Unique
   @Nullable
   private TrackedDataContainer<BlockEntity, BlockEntityTrackedData> dataAnchor$container;

   @Inject(
      method = {"setLevel(Lnet/minecraft/world/level/Level;)V"},
      at = {@At("RETURN")}
   )
   private void dataAnchor$setLevel(Level level, CallbackInfo ci) {
      this.dataAnchor$createTrackedData();
   }

   @Override
   public <E extends BlockEntityTrackedData> Optional<E> dataAnchor$getTrackedData(TrackedDataKey<E> key) {
      return this.dataAnchor$container == null ? Optional.empty() : this.dataAnchor$container.dataAnchor$getTrackedData(key);
   }

   @Override
   public void dataAnchor$createTrackedData() {
      if (this.dataAnchor$container == null) {
         this.dataAnchor$container = TrackedDataContainer.makeBasicContainer(
            TrackedDataRegistries.BLOCK_ENTITY, (BlockEntity)this, this.level != null && this.level.isClientSide()
         );
         this.dataAnchor$container.dataAnchor$createTrackedData();
      }
   }

   @Override
   public Collection<TrackedDataKey<BlockEntityTrackedData>> dataAnchor$getTrackedDataKeys() {
      return (Collection<TrackedDataKey<BlockEntityTrackedData>>)(this.dataAnchor$container == null
         ? Collections.emptyList()
         : this.dataAnchor$container.dataAnchor$getTrackedDataKeys());
   }

   @Inject(
      method = {"loadStatic(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/level/block/entity/BlockEntity;"},
      at = {@At("RETURN")}
   )
   private static void dataAnchor$loadStatic(BlockPos pos, BlockState state, CompoundTag tag, Provider registries, CallbackInfoReturnable<BlockEntity> cir) {
      if (cir.getReturnValue() instanceof TrackedDataContainer container) {
         container.dataAnchor$createTrackedData();
         if (tag.contains("TrackedData")) {
            CompoundTag trackedData = tag.getCompound("TrackedData");

            for (TrackedDataKey<BlockEntityTrackedData> key : container.dataAnchor$getTrackedDataKeys()) {
               container.dataAnchor$getTrackedData(key).ifPresent(data -> {
                  if (data instanceof BlockEntityTrackedData blockEntityTrackedData) {
                     String idString = key.getId().toString();
                     if (trackedData.contains(idString)) {
                        blockEntityTrackedData.load(trackedData.getCompound(idString));
                     }
                  }
               });
            }
         }
      }
   }

   @Inject(
      method = {"saveWithFullMetadata(Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/nbt/CompoundTag;"},
      at = {@At("RETURN")}
   )
   private void dataAnchor$saveWithFullMetadata(CallbackInfoReturnable<CompoundTag> cir) {
      if (this.dataAnchor$container != null) {
         CompoundTag trackedData = new CompoundTag();

         for (TrackedDataKey<BlockEntityTrackedData> key : this.dataAnchor$container.dataAnchor$getTrackedDataKeys()) {
            this.dataAnchor$container.dataAnchor$getTrackedData(key).ifPresent(data -> {
               CompoundTag save = data.save();
               if (save != null) {
                  trackedData.put(key.getId().toString(), save);
               }
            });
         }

         ((CompoundTag)cir.getReturnValue()).put("TrackedData", trackedData);
      }
   }
}

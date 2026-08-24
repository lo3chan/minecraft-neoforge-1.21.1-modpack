package dev.corgitaco.dataanchor.mixin;

import dev.corgitaco.dataanchor.data.TickableTrackedData;
import dev.corgitaco.dataanchor.data.TrackedDataContainer;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.type.blockentity.BlockEntityTrackedData;
import dev.corgitaco.dataanchor.util.TickableBlockEntityAccessor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LevelChunk.class})
public class LevelChunkMixin implements TickableBlockEntityAccessor {
   @Unique
   private final List<BlockEntity> dataAnchor$tickableBlockEntities = new ArrayList<>();

   @Override
   public List<BlockEntity> dataAnchor$getTickableBlockEntities() {
      return this.dataAnchor$tickableBlockEntities;
   }

   @Inject(
      method = {"setBlockEntity(Lnet/minecraft/world/level/block/entity/BlockEntity;)V"},
      at = {@At("HEAD")}
   )
   private void dataAnchor$onSetBlockEntityHead(BlockEntity blockEntity, CallbackInfo ci) {
      BlockEntity oldBlockEntity = ((LevelChunk)this).getBlockEntity(blockEntity.getBlockPos());
      if (oldBlockEntity != null) {
         synchronized (this.dataAnchor$tickableBlockEntities) {
            this.dataAnchor$tickableBlockEntities.remove(oldBlockEntity);
         }
      }
   }

   @Inject(
      method = {"setBlockEntity(Lnet/minecraft/world/level/block/entity/BlockEntity;)V"},
      at = {@At("RETURN")}
   )
   private void dataAnchor$onSetBlockEntityReturn(BlockEntity blockEntity, CallbackInfo ci) {
      this.dataAnchor$checkAndAdd(blockEntity);
   }

   @Unique
   private void dataAnchor$checkAndAdd(BlockEntity blockEntity) {
      if (blockEntity instanceof TrackedDataContainer access) {
         access.dataAnchor$createTrackedData();
         Collection<TrackedDataKey<BlockEntityTrackedData>> keys = access.dataAnchor$getTrackedDataKeys();
         if (keys.isEmpty()) {
            return;
         }

         boolean isTickable = false;

         for (TrackedDataKey<BlockEntityTrackedData> key : keys) {
            if (access.dataAnchor$getTrackedData(key).orElse(null) instanceof TickableTrackedData) {
               isTickable = true;
               break;
            }
         }

         if (isTickable) {
            synchronized (this.dataAnchor$tickableBlockEntities) {
               if (!this.dataAnchor$tickableBlockEntities.contains(blockEntity)) {
                  this.dataAnchor$tickableBlockEntities.add(blockEntity);
               }
            }
         }
      }
   }

   @Inject(
      method = {"removeBlockEntity(Lnet/minecraft/core/BlockPos;)V"},
      at = {@At("HEAD")}
   )
   private void dataAnchor$onRemoveBlockEntity(BlockPos pos, CallbackInfo ci) {
      BlockEntity blockEntity = ((LevelChunk)this).getBlockEntity(pos);
      if (blockEntity != null) {
         synchronized (this.dataAnchor$tickableBlockEntities) {
            this.dataAnchor$tickableBlockEntities.remove(blockEntity);
         }
      }
   }
}

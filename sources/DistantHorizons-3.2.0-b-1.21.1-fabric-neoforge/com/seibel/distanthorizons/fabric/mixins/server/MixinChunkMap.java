package com.seibel.distanthorizons.fabric.mixins.server;

import com.seibel.distanthorizons.common.commonMixins.MixinChunkMapCommon_fabric;
import net.minecraft.class_2791;
import net.minecraft.class_3218;
import net.minecraft.class_3898;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_3898.class})
public class MixinChunkMap {
   @Unique
   private static final String CHUNK_SERIALIZER_WRITE = "Lnet/minecraft/world/level/chunk/storage/ChunkSerializer;write(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkAccess;)Lnet/minecraft/nbt/CompoundTag;";
   @Shadow
   @Final
   class_3218 field_17214;

   @Inject(
      method = {"save"},
      at = {@At(
         value = "RETURN",
         target = "Lnet/minecraft/world/level/chunk/storage/ChunkSerializer;write(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkAccess;)Lnet/minecraft/nbt/CompoundTag;"
      )}
   )
   private void onChunkSave(class_2791 chunk, CallbackInfoReturnable<Boolean> ci) {
      MixinChunkMapCommon_fabric.onChunkSave(this.field_17214, chunk, ci);
   }
}

package dev.architectury.mixin.forge.neoforge;

import dev.architectury.event.forge.EventHandlerImplCommon;
import java.lang.ref.WeakReference;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.level.ChunkDataEvent.Load;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ChunkSerializer.class})
public class MixinChunkSerializer {
   @Unique
   private static ThreadLocal<WeakReference<ServerLevel>> level = new ThreadLocal<>();

   @Inject(
      method = {"read(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/ai/village/poi/PoiManager;Lnet/minecraft/world/level/chunk/storage/RegionStorageInfo;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/level/chunk/ProtoChunk;"},
      at = {@At("HEAD")}
   )
   private static void read(
      ServerLevel worldIn, PoiManager arg2, RegionStorageInfo arg3, ChunkPos arg4, CompoundTag arg5, CallbackInfoReturnable<ProtoChunk> cir
   ) {
      level.set(new WeakReference<>(worldIn));
   }

   @ModifyArg(
      method = {"read(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/ai/village/poi/PoiManager;Lnet/minecraft/world/level/chunk/storage/RegionStorageInfo;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/level/chunk/ProtoChunk;"},
      at = @At(
         value = "INVOKE",
         ordinal = 1,
         target = "Lnet/neoforged/bus/api/IEventBus;post(Lnet/neoforged/bus/api/Event;)Lnet/neoforged/bus/api/Event;"
      ),
      index = 0
   )
   private static Event modifyProtoChunkLevel(Event event) {
      WeakReference<ServerLevel> levelRef = level.get();
      if (levelRef != null && event instanceof Load load) {
         ((EventHandlerImplCommon.LevelEventAttachment)load).architectury$attachLevel((LevelAccessor)levelRef.get());
      }

      level.remove();
      return event;
   }
}

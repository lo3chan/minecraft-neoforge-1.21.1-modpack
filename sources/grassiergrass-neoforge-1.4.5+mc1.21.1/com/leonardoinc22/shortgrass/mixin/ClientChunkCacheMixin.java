package com.leonardoinc22.shortgrass.mixin;

import com.leonardoinc22.shortgrass.client.render.GrassRenderPass;
import java.util.function.Consumer;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ClientChunkCache.class})
public abstract class ClientChunkCacheMixin {
   @Shadow
   @Final
   ClientLevel level;

   @Inject(
      method = {"onLightUpdate"},
      at = {@At("TAIL")}
   )
   private void grassiergrass$invalidateGrassLighting(LightLayer type, SectionPos pos, CallbackInfo ci) {
      GrassRenderPass.invalidateLightSection(this.level, pos);
   }

   @Inject(
      method = {"replaceWithPacketData"},
      at = {@At("TAIL")}
   )
   private void grassiergrass$invalidateGrassChunk(
      int chunkX, int chunkZ, FriendlyByteBuf buffer, CompoundTag tag, Consumer<?> consumer, CallbackInfoReturnable<LevelChunk> cir
   ) {
      GrassRenderPass.invalidateChunk(this.level, chunkX, chunkZ);
   }
}

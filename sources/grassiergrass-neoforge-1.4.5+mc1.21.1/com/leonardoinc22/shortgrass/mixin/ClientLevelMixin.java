package com.leonardoinc22.shortgrass.mixin;

import com.leonardoinc22.shortgrass.client.render.GrassRenderPass;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientLevel.class})
public abstract class ClientLevelMixin {
   @Inject(
      method = {"sendBlockUpdated"},
      at = {@At("TAIL")}
   )
   private void grassiergrass$invalidateGrassSection(BlockPos pos, BlockState oldState, BlockState newState, int flags, CallbackInfo ci) {
      ClientLevel level = (ClientLevel)this;
      if (oldState != newState && !oldState.equals(newState)) {
         GrassRenderPass.invalidateBlock(level, pos);
      } else {
         GrassRenderPass.invalidateLightSection(level, SectionPos.of(pos));
      }
   }
}

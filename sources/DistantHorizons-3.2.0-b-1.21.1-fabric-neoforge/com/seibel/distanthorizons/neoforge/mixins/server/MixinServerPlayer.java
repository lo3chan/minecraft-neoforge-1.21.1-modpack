package com.seibel.distanthorizons.neoforge.mixins.server;

import com.seibel.distanthorizons.common.wrappers.misc.IMixinServerPlayer_neoforge;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.DimensionTransition;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ServerPlayer.class})
public class MixinServerPlayer implements IMixinServerPlayer_neoforge {
   @Unique
   @Nullable
   private ServerLevel distantHorizons$dimensionChangeDestination;

   @Unique
   @Nullable
   @Override
   public ServerLevel distantHorizons$getDimensionChangeDestination() {
      return this.distantHorizons$dimensionChangeDestination;
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"changeDimension"}
   )
   public void changeDimension(DimensionTransition dimensionTransition, CallbackInfoReturnable<Entity> cir) {
      this.distantHorizons$dimensionChangeDestination = dimensionTransition.newLevel();
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"setServerLevel"}
   )
   public void setServerLevel(ServerLevel level, CallbackInfo ci) {
      this.distantHorizons$dimensionChangeDestination = null;
   }
}

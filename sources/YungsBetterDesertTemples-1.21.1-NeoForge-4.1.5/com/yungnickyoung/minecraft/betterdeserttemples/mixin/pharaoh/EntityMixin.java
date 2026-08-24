package com.yungnickyoung.minecraft.betterdeserttemples.mixin.pharaoh;

import com.yungnickyoung.minecraft.betterdeserttemples.util.PharaohUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Entity.class})
public abstract class EntityMixin {
   @Shadow
   private Level level;

   @Inject(
      method = {"discard"},
      at = {@At("HEAD")}
   )
   private void betterdeserttemples_clearTempleOnPharaohDiscard(CallbackInfo ci) {
      if (this.level instanceof ServerLevel serverLevel) {
         if (PharaohUtil.isPharaoh(this)) {
            PharaohUtil.onKillOrDiscardPharaoh((Entity)this, serverLevel, null);
         }
      }
   }
}

package com.yungnickyoung.minecraft.betterdeserttemples.mixin.pharaoh;

import com.yungnickyoung.minecraft.betterdeserttemples.util.PharaohUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LivingEntity.class})
public abstract class LivingEntityMixin extends Entity {
   public LivingEntityMixin(EntityType<?> entityType, Level level) {
      super(entityType, level);
   }

   @Inject(
      method = {"die"},
      at = {@At("HEAD")}
   )
   private void betterdeserttemples_clearTempleOnPharaohDeath(DamageSource damageSource, CallbackInfo ci) {
      if (this.level() instanceof ServerLevel serverLevel) {
         if (PharaohUtil.isPharaoh(this)) {
            PharaohUtil.onKillOrDiscardPharaoh(this, serverLevel, damageSource);
         }
      }
   }
}

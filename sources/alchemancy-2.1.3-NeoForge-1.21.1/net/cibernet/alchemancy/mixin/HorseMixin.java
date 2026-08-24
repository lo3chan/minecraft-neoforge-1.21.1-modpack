package net.cibernet.alchemancy.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.CommonHooks;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({AbstractHorse.class})
public abstract class HorseMixin extends LivingEntity {
   private HorseMixin(EntityType<? extends LivingEntity> entityType, Level level) {
      super(entityType, level);
   }

   @WrapMethod(
      method = {"causeFallDamage"}
   )
   public boolean fall(float fallDistance, float multiplier, DamageSource source, Operation<Boolean> original) {
      float[] ret = CommonHooks.onLivingFall(this, fallDistance, multiplier);
      if (ret == null) {
         return false;
      } else {
         fallDistance = ret[0];
         multiplier = ret[1];
         return (Boolean)original.call(new Object[]{fallDistance, multiplier, source});
      }
   }
}

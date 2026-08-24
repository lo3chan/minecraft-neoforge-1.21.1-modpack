package com.github.alexthe666.alexsmobs.effect;

import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EffectClinging extends MobEffect {
   public EffectClinging() {
      super(MobEffectCategory.BENEFICIAL, 12405579);
   }

   private static BlockPos getPositionUnderneath(Entity e) {
      return AMBlockPos.fromCoords(e.getX(), e.getBoundingBox().maxY + 1.5099999904632568, e.getZ());
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      entity.refreshDimensions();
      entity.setNoGravity(false);
      if (isUpsideDown(entity)) {
         entity.fallDistance = 0.0F;
         if (!entity.isShiftKeyDown()) {
            if (!entity.horizontalCollision) {
               entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, 0.30000001192092896, 0.0));
            }

            entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.9980000257492065, 1.0, 0.9980000257492065));
         }
      }

      return true;
   }

   public static boolean isUpsideDown(LivingEntity entity) {
      BlockPos pos = getPositionUnderneath(entity);
      BlockState ground = entity.level().getBlockState(pos);
      return (entity.verticalCollision || ground.isFaceSturdy(entity.level(), pos, Direction.DOWN)) && !entity.onGround();
   }

   public static boolean isFlippedUpsideDown(LivingEntity entity) {
      return entity.hasEffect(AMCompat.effect(AMEffectRegistry.CLINGING.get())) && isUpsideDown(entity);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return duration > 0;
   }

   public String getDescriptionId() {
      return "alexsmobs.potion.clinging";
   }
}

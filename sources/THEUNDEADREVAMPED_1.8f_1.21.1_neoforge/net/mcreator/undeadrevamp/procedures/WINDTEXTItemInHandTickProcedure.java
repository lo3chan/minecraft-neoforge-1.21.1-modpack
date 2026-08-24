package net.mcreator.undeadrevamp.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;

public class WINDTEXTItemInHandTickProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (world.getBlockState(BlockPos.containing(x, y, z)).getLightEmission(world, BlockPos.containing(x, y, z)) == 0
            && entity instanceof LivingEntity _entity
            && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 2, 1));
         }

         if (world.getBlockState(BlockPos.containing(x, y - 1.0, z)).getLightEmission(world, BlockPos.containing(x, y - 1.0, z)) == 0
            && entity instanceof LivingEntity _entity
            && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.POISON, 2, 1));
         }

         if (world.getMaxLocalRawBrightness(BlockPos.containing(x, y, z)) <= 7 && entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 2, 1));
         }

         if (world.getMaxLocalRawBrightness(BlockPos.containing(x, y - 1.0, z)) <= 7
            && entity instanceof LivingEntity _entity
            && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 2, 1));
         }
      }
   }
}

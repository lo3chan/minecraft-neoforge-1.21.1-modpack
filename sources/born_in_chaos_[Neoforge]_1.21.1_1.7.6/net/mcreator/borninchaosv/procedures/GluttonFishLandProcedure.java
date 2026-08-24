package net.mcreator.borninchaosv.procedures;

import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class GluttonFishLandProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity.isInWater() && world.getBlockState(BlockPos.containing(x, y + 1.0, z)) == Blocks.AIR.defaultBlockState()) {
            entity.teleportTo(x, y - 2.0, z);
            if (entity instanceof ServerPlayer _serverPlayer) {
               _serverPlayer.connection.teleport(x, y - 2.0, z, entity.getYRot(), entity.getXRot());
            }
         }

         if (!entity.isInWater() && !(entity instanceof LivingEntity _livEnt5 && _livEnt5.hasEffect(BornInChaosV1ModMobEffects.FISH_BREATH))) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.FISH_BREATH, 600, 0, false, false));
            }
         } else if (entity.isInWater()) {
            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(MobEffects.WITHER);
            }

            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(BornInChaosV1ModMobEffects.FISH_BREATH);
            }

            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 40, 0, false, false));
            }

            if (entity instanceof LivingEntity _livEnt11 && _livEnt11.hasEffect(BornInChaosV1ModMobEffects.STUN) && world instanceof ServerLevel _level) {
               _level.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.STUNSTARS.get(), x, y, z, 2, 1.3, 1.3, 1.3, 0.1);
            }
         }

         if ((
                  entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(BornInChaosV1ModMobEffects.FISH_BREATH)
                     ? _livEnt.getEffect(BornInChaosV1ModMobEffects.FISH_BREATH).getDuration()
                     : 0
               )
               == 20
            && entity instanceof LivingEntity _entity
            && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 1000, 1, false, false));
         }
      }
   }
}

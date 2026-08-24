package com.aetherteam.aether.entity;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.LightningTrackerAttachment;
import com.aetherteam.aether.mixin.mixins.common.accessor.EntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public final class EntityUtil {
   public static void copyRotations(Entity entity, Entity source) {
      entity.setYRot((float)Mth.rotLerp(0.3333333333333333, source.getYRot(), source.yRotO));
      entity.setXRot((float)Mth.rotLerp(0.3333333333333333, source.getXRot(), source.xRotO));
      entity.setYBodyRot((float)Mth.rotLerp(0.3333333333333333, source.getYRot(), source.yRotO));
      entity.setYHeadRot((float)Mth.rotLerp(0.3333333333333333, source.getYRot(), source.yRotO));
   }

   public static void spawnMovementExplosionParticles(Entity entity) {
      RandomSource random = ((EntityAccessor)entity).aether$getRandom();
      double d0 = random.nextGaussian() * 0.02;
      double d1 = random.nextGaussian() * 0.02;
      double d2 = random.nextGaussian() * 0.02;
      double d3 = 10.0;
      double x = entity.getX() + (double)random.nextFloat() * entity.getBbWidth() * 2.0 - entity.getBbWidth() - d0 * d3;
      double y = entity.getY() + (double)random.nextFloat() * entity.getBbHeight() - d1 * d3;
      double z = entity.getZ() + (double)random.nextFloat() * entity.getBbWidth() * 2.0 - entity.getBbWidth() - d2 * d3;
      entity.level().addParticle(ParticleTypes.POOF, x, y, z, d0, d1, d2);
   }

   public static void spawnSummoningExplosionParticles(Entity entity) {
      RandomSource random = ((EntityAccessor)entity).aether$getRandom();

      for (int i = 0; i < 20; i++) {
         double d0 = random.nextGaussian() * 0.02;
         double d1 = random.nextGaussian() * 0.02;
         double d2 = random.nextGaussian() * 0.02;
         double d3 = 10.0;
         double x = entity.getX(0.0) - d0 * d3;
         double y = entity.getRandomY() - d1 * d3;
         double z = entity.getRandomZ(1.0) - d2 * d3;
         entity.level().addParticle(ParticleTypes.POOF, x, y, z, d0, d1, d2);
      }
   }

   public static void spawnRemovalParticles(Level level, BlockPos pos) {
      double a = pos.getX() + 0.5 + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.375;
      double b = pos.getY() + 0.5 + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.375;
      double c = pos.getZ() + 0.5 + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.375;
      if (level instanceof ServerLevel serverLevel) {
         serverLevel.sendParticles(ParticleTypes.POOF, a, b, c, 1, 0.0, 0.0, 0.0, 0.0);
      }
   }

   public static void summonLightningFromProjectile(Projectile projectile) {
      LightningBolt lightningBolt = (LightningBolt)EntityType.LIGHTNING_BOLT.create(projectile.level());
      if (lightningBolt != null) {
         ((LightningTrackerAttachment)lightningBolt.getData(AetherDataAttachments.LIGHTNING_TRACKER)).setOwner(projectile.getOwner());
         lightningBolt.setPos(projectile.getX(), projectile.getY(), projectile.getZ());
         projectile.level().addFreshEntity(lightningBolt);
      }
   }

   public static boolean wholeHitboxCanSeeSky(LevelAccessor level, BlockPos pos, int hitboxRadius) {
      boolean flag = true;

      for (int xOffset = -hitboxRadius; xOffset <= hitboxRadius; xOffset++) {
         for (int zOffset = -hitboxRadius; zOffset <= hitboxRadius; zOffset++) {
            flag = flag && level.canSeeSky(pos.offset(xOffset, 0, zOffset));
         }
      }

      return flag;
   }
}

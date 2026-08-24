package net.mcreator.borninchaosv.procedures;

import java.util.Comparator;
import net.mcreator.borninchaosv.entity.MissionerEntity;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class StunningStrikeKazhdyiTikVoVriemiaEffiektaProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null) {
         if (entity.getPersistentData().getDouble("stunningstrike") == 0.0) {
            entity.getPersistentData().putDouble("stunningstrike", 20.0);
         } else {
            entity.getPersistentData().putDouble("stunningstrike", entity.getPersistentData().getDouble("stunningstrike") - 1.0);
         }

         if (entity.getPersistentData().getDouble("stunningstrike") == 0.0) {
            if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.BLOCK_BREAK, 500, 0, false, false));
            }

            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(BornInChaosV1ModMobEffects.STUNNING_STRIKE);
            }

            if (world instanceof ServerLevel _level) {
               _level.sendParticles(
                  (SimpleParticleType)BornInChaosV1ModParticleTypes.CLOUDSOFDUST.get(),
                  entity.getX(),
                  entity.getY() + 0.3,
                  entity.getZ(),
                  20,
                  2.0,
                  0.1,
                  2.0,
                  0.1
               );
            }

            Vec3 _center = new Vec3(entity.getX(), entity.getY(), entity.getZ());

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(4.5), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (!(entityiterator instanceof MissionerEntity) && !entityiterator.getType().is(EntityTypeTags.UNDEAD)) {
                  if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.STUN, 40, 0));
                  }

                  entityiterator.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC)), 9.0F);
               }
            }
         }
      }
   }
}

package net.mcreator.borninchaosv.procedures;

import java.util.Comparator;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LivingBombKazhdyiTikVoVriemiaEffiektaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if ((
               entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(BornInChaosV1ModMobEffects.LIVING_BOMB)
                  ? _livEnt.getEffect(BornInChaosV1ModMobEffects.LIVING_BOMB).getDuration()
                  : 0
            )
            == 10) {
            if ((entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F) >= 100.0F) {
               if (world instanceof Level _level && !_level.isClientSide()) {
                  _level.explode(null, x, y + 1.0, z, 6.0F, ExplosionInteraction.MOB);
               }
            } else if ((entity instanceof LivingEntity _livEntxxx ? _livEntxxx.getHealth() : -1.0F) < 100.0F
               && (entity instanceof LivingEntity _livEntxx ? _livEntxx.getHealth() : -1.0F) >= 50.0F) {
               if (world instanceof Level _level && !_level.isClientSide()) {
                  _level.explode(null, x, y + 1.0, z, 5.0F, ExplosionInteraction.MOB);
               }
            } else if ((entity instanceof LivingEntity _livEntxxxxx ? _livEntxxxxx.getHealth() : -1.0F) < 50.0F
               && (entity instanceof LivingEntity _livEntxxxx ? _livEntxxxx.getHealth() : -1.0F) >= 30.0F) {
               if (world instanceof Level _level && !_level.isClientSide()) {
                  _level.explode(null, x, y + 1.0, z, 4.0F, ExplosionInteraction.MOB);
               }
            } else if ((entity instanceof LivingEntity _livEntxxxxxxx ? _livEntxxxxxxx.getHealth() : -1.0F) < 30.0F
               && (entity instanceof LivingEntity _livEntxxxxxx ? _livEntxxxxxx.getHealth() : -1.0F) > 0.0F
               && world instanceof Level _level
               && !_level.isClientSide()) {
               _level.explode(null, x, y + 1.0, z, 3.0F, ExplosionInteraction.MOB);
            }

            if (entity.ignoreExplosion(new Explosion(entity.level(), null, 0.0, 0.0, 0.0, 4.0F, true, BlockInteraction.DESTROY))) {
               entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC)), 30.0F);
            }

            if (world instanceof Level _level) {
               if (!_level.isClientSide()) {
                  _level.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.end_portal.spawn")),
                     SoundSource.NEUTRAL,
                     0.3F,
                     0.6F
                  );
               } else {
                  _level.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.end_portal.spawn")),
                     SoundSource.NEUTRAL,
                     0.3F,
                     0.6F,
                     false
                  );
               }
            }

            if (world instanceof ServerLevel _levelx) {
               _levelx.sendParticles((SimpleParticleType)BornInChaosV1ModParticleTypes.INFERNAL_SURGE.get(), x, y, z, 13, 2.0, 0.3, 2.0, 0.1);
            }

            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(3.5), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.INFERNAL_FLAME, 240, 0));
               }
            }

            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            }

            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(MobEffects.REGENERATION);
            }

            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(MobEffects.MOVEMENT_SPEED);
            }

            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(MobEffects.DAMAGE_RESISTANCE);
            }

            if (entity instanceof LivingEntity _entity) {
               _entity.removeEffect(MobEffects.ABSORPTION);
            }
         }
      }
   }
}

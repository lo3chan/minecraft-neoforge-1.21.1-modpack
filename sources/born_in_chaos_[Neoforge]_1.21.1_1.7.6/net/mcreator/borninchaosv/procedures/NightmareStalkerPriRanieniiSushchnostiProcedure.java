package net.mcreator.borninchaosv.procedures;

import java.util.Comparator;
import net.mcreator.borninchaosv.init.BornInChaosV1ModMobEffects;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class NightmareStalkerPriRanieniiSushchnostiProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (entity instanceof LivingEntity _livEnt0
            && _livEnt0.hasEffect(BornInChaosV1ModMobEffects.UNITY_WITH_DARKNESS)
            && world instanceof ServerLevel _level) {
            _level.sendParticles(
               (SimpleParticleType)BornInChaosV1ModParticleTypes.DARK_SMOKE.get(), entity.getX(), entity.getY() + 1.5, entity.getZ(), 8, 0.4, 0.4, 0.4, 0.1
            );
         }

         if (world.dayTime() >= 600000L) {
            if (entity.getPersistentData().getDouble("ragescale") <= 7.0
               && !(entity instanceof LivingEntity _livEnt7 && _livEnt7.hasEffect(BornInChaosV1ModMobEffects.TERRIFYING_PRESENCE))) {
               entity.getPersistentData().putDouble("ragescale", entity.getPersistentData().getDouble("ragescale") + 1.0);
            }

            if (entity.getPersistentData().getDouble("ragescale") >= 7.0
               && !(entity instanceof LivingEntity _livEnt11 && _livEnt11.hasEffect(BornInChaosV1ModMobEffects.TERRIFYING_PRESENCE))
               && (entity instanceof LivingEntity _livEntxx ? _livEntxx.getHealth() : -1.0F)
                  <= (entity instanceof LivingEntity _livEntx ? _livEntx.getMaxHealth() : -1.0F) / 2.0F
               && (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) > 10.0F) {
               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.TERRIFYING_PRESENCE, 2000, 0, false, false));
               }

               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 6, false, false));
               }

               if (world instanceof ServerLevel _level) {
                  _level.sendParticles(
                     (SimpleParticleType)BornInChaosV1ModParticleTypes.ROARSPLASH.get(),
                     entity.getX(),
                     entity.getY() + 2.0,
                     entity.getZ(),
                     3,
                     0.3,
                     0.2,
                     0.3,
                     0.0
                  );
               }

               if (!world.isClientSide() && world instanceof Level _level) {
                  if (!_level.isClientSide()) {
                     _level.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:stalker_roar_distant")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F
                     );
                  } else {
                     _level.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("born_in_chaos_v1:stalker_roar_distant")),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F,
                        false
                     );
                  }
               }
            }
         }

         if (sourceentity instanceof Player
            && sourceentity.isUnderWater()
            && !(sourceentity instanceof LivingEntity _livEnt25 && _livEnt25.hasEffect(BornInChaosV1ModMobEffects.GAZE_OF_TERROR))) {
            sourceentity.setAirSupply(0);
         }

         if (world.dayTime() >= 240000L && entity.isPassenger()) {
            entity.stopRiding();
            if (!world.isClientSide() && world instanceof Level _levelx) {
               if (!_levelx.isClientSide()) {
                  _levelx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.break_wooden_door")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F
                  );
               } else {
                  _levelx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.break_wooden_door")),
                     SoundSource.NEUTRAL,
                     1.0F,
                     1.0F,
                     false
                  );
               }
            }

            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.5), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (entityiterator instanceof Boat || entityiterator instanceof ChestBoat) {
                  if (!entityiterator.level().isClientSide()) {
                     entityiterator.discard();
                  }

                  if (world instanceof ServerLevel _levelxx) {
                     _levelxx.sendParticles(
                        ParticleTypes.CRIT, entityiterator.getX(), entityiterator.getY() + 1.0, entityiterator.getZ(), 5, 0.3, 0.2, 0.3, 0.1
                     );
                  }
               }
            }

            _center = new Vec3(x, y, z);

            for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (entityiteratorx instanceof Player && entityiteratorx instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(BornInChaosV1ModMobEffects.CURSEOFTHE_BOAT, 200, 0));
               }
            }
         }
      }
   }
}

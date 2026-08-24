package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.entity.CloggerEntity;
import net.mcreator.undeadrevamp.entity.ThebidyEntity;
import net.mcreator.undeadrevamp.entity.ThebidyupsideEntity;
import net.mcreator.undeadrevamp.entity.TheordureEntity;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ThebeartamerDeathTimeIsReachedProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (!(entity instanceof ThebidyEntity) && !(entity instanceof CloggerEntity) && world instanceof ServerLevel _level) {
            _level.sendParticles(ParticleTypes.LARGE_SMOKE, x, y, z, 35, entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth(), 0.01);
         }

         if (entity instanceof CloggerEntity && world instanceof ServerLevel _level) {
            Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.DEADCLOGGER.get())
               .spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
               entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
            }
         }

         if (entity instanceof TheordureEntity && !entity.isInWater()) {
            if (world instanceof ServerLevel _levelx) {
               _levelx.sendParticles(ParticleTypes.LAVA, x, y, z, 25, 2.0, 2.0, 2.0, 0.8);
            }

            if (world instanceof ServerLevel _levelx) {
               _levelx.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 30, 2.0, 2.0, 2.0, 0.8);
            }

            if (world instanceof Level _levelx) {
               if (!_levelx.isClientSide()) {
                  _levelx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.generic.explode")),
                     SoundSource.NEUTRAL,
                     3.0F,
                     1.0F
                  );
               } else {
                  _levelx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.generic.explode")),
                     SoundSource.NEUTRAL,
                     3.0F,
                     1.0F,
                     false
                  );
               }
            }

            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(1.5), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (entityiterator instanceof LivingEntity) {
                  entityiterator.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC)), 20.0F);
                  entityiterator.igniteForSeconds(5.0F);
                  entityiterator.setDeltaMovement(
                     new Vec3(
                        Math.sin(Math.toRadians(entityiterator.getYRot() + 180.0F)) * 1.25 * -2.0,
                        (Math.sin(Math.toRadians(0.0F - entityiterator.getXRot())) + 0.5) * 1.5,
                        Math.cos(Math.toRadians(entityiterator.getYRot())) * 1.25 * -2.0
                     )
                  );
               }
            }

            _center = new Vec3(x, y, z);

            for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.5), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (entityiteratorx instanceof LivingEntity) {
                  entityiteratorx.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC)), 35.0F);
                  entityiteratorx.igniteForSeconds(5.0F);
                  entityiteratorx.setDeltaMovement(
                     new Vec3(
                        Math.sin(Math.toRadians(entityiteratorx.getYRot() + 180.0F)) * 1.25 * -2.0,
                        (Math.sin(Math.toRadians(0.0F - entityiteratorx.getXRot())) + 0.5) * 1.5,
                        Math.cos(Math.toRadians(entityiteratorx.getYRot())) * 1.25 * -2.0
                     )
                  );
               }
            }

            _center = new Vec3(x, y, z);

            for (Entity entityiteratorxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(4.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (entityiteratorxx instanceof LivingEntity) {
                  entityiteratorxx.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC)), 25.0F);
                  entityiteratorxx.igniteForSeconds(3.0F);
                  entityiteratorxx.setDeltaMovement(
                     new Vec3(
                        Math.sin(Math.toRadians(entityiteratorxx.getYRot() + 180.0F)) * 1.25 * -1.5,
                        (Math.sin(Math.toRadians(0.0F - entityiteratorxx.getXRot())) + 0.5) * 1.25,
                        Math.cos(Math.toRadians(entityiteratorxx.getYRot())) * 1.25 * -1.5
                     )
                  );
               }
            }
         }

         if ((entity instanceof ThebidyEntity || entity instanceof ThebidyupsideEntity) && !entity.isInWater()) {
            if (world instanceof ServerLevel _levelxx) {
               _levelxx.sendParticles((SimpleParticleType)UndeadRevamp2ModParticleTypes.BOMBERGOO.get(), x, y, z, 200, 1.5, 1.2, 1.5, 0.001);
            }

            if (world instanceof ServerLevel _levelxx) {
               _levelxx.sendParticles((SimpleParticleType)UndeadRevamp2ModParticleTypes.BOMBERGOO.get(), x, y, z, 30, 3.0, 1.0, 3.0, 0.2);
            }

            if (world instanceof Level _levelxx) {
               if (!_levelxx.isClientSide()) {
                  _levelxx.playSound(
                     null,
                     BlockPos.containing(x, y, z),
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:bidyboom")),
                     SoundSource.NEUTRAL,
                     3.0F,
                     1.0F
                  );
               } else {
                  _levelxx.playLocalSound(
                     x,
                     y,
                     z,
                     (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:bidyboom")),
                     SoundSource.NEUTRAL,
                     3.0F,
                     1.0F,
                     false
                  );
               }
            }

            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiteratorxxx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.5), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (entityiteratorxxx instanceof LivingEntity) {
                  if (entityiteratorxxx instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                     _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.GOOED, 60, 1));
                  }

                  entityiteratorxxx.hurt(new DamageSource(world.holderOrThrow(DamageTypes.GENERIC)), 7.0F);
                  entityiteratorxxx.setDeltaMovement(
                     new Vec3(
                        Math.sin(Math.toRadians(entityiteratorxxx.getYRot() + 180.0F)) * 1.25 * -1.3,
                        (Math.sin(Math.toRadians(0.0F - entityiteratorxxx.getXRot())) + 0.5) * 1.5,
                        Math.cos(Math.toRadians(entityiteratorxxx.getYRot())) * 1.25 * -1.3
                     )
                  );
               }
            }
         }
      }
   }
}

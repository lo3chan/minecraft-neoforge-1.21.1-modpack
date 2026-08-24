package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModMobEffects;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class DeadcloggerDeathTimeIsReachedProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (world instanceof Level _level) {
            if (!_level.isClientSide()) {
               _level.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.generic.explode")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            } else {
               _level.playLocalSound(
                  x,
                  y,
                  z,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.generic.explode")),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F,
                  false
               );
            }
         }

         if (world instanceof Level _levelx) {
            if (!_levelx.isClientSide()) {
               _levelx.playSound(
                  null,
                  BlockPos.containing(x, y, z),
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:cloggerexplodes")),
                  SoundSource.NEUTRAL,
                  5.0F,
                  1.0F
               );
            } else {
               _levelx.playLocalSound(
                  x,
                  y,
                  z,
                  (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:cloggerexplodes")),
                  SoundSource.NEUTRAL,
                  5.0F,
                  1.0F,
                  false
               );
            }
         }

         if (world instanceof ServerLevel _levelxx) {
            _levelxx.sendParticles((SimpleParticleType)UndeadRevamp2ModParticleTypes.CLOGGERCARCASSES.get(), x, y + 1.0, z, 60, 2.5, 3.0, 2.5, 1.0);
         }

         if (world instanceof ServerLevel _levelxx) {
            _levelxx.sendParticles((SimpleParticleType)UndeadRevamp2ModParticleTypes.BOMBERGOO.get(), x, y + 1.0, z, 250, 2.5, 3.0, 2.5, 1.0);
         }

         Vec3 _center = new Vec3(entity.getX(), entity.getY(), entity.getZ());

         for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(4.0), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if (entityiterator != entity && entityiterator instanceof LivingEntity) {
               entityiterator.getPersistentData().putDouble("aoe_x", entity.getX() - entityiterator.getX());
               entityiterator.getPersistentData()
                  .putDouble("aoe_y", entity.getY() + entity.getBbHeight() - (entityiterator.getY() + entityiterator.getBbHeight()));
               entityiterator.getPersistentData().putDouble("aoe_z", entity.getZ() - entityiterator.getZ());
               entityiterator.getPersistentData().putDouble("distance", 0.0);
               UndeadRevamp2Mod.queueServerWork(
                  1,
                  () -> {
                     for (int index0 = 0; index0 < 20; index0++) {
                        if (world.isEmptyBlock(
                           BlockPos.containing(
                              entity.getX() + entityiterator.getPersistentData().getDouble("aoe_x") * entityiterator.getPersistentData().getDouble("distance"),
                              entity.getY()
                                 + entity.getBbHeight()
                                 + entityiterator.getPersistentData().getDouble("aoe_y") * entityiterator.getPersistentData().getDouble("distance"),
                              entity.getZ() + entityiterator.getPersistentData().getDouble("aoe_z") * entityiterator.getPersistentData().getDouble("distance")
                           )
                        )) {
                           entityiterator.getPersistentData().putBoolean("behind_wall", false);
                           entityiterator.getPersistentData().putDouble("distance", entityiterator.getPersistentData().getDouble("distance") - 0.05);
                        } else {
                           entityiterator.getPersistentData().putBoolean("behind_wall", true);
                        }

                        UndeadRevamp2Mod.queueServerWork(
                           1,
                           () -> {
                              if (!entityiterator.getPersistentData().getBoolean("behind_wall")
                                 && entityiterator instanceof LivingEntity _entity
                                 && !_entity.level().isClientSide()) {
                                 _entity.addEffect(new MobEffectInstance(UndeadRevamp2ModMobEffects.GOOED, 200, 0, false, false));
                              }
                           }
                        );
                     }
                  }
               );
            }
         }
      }
   }
}

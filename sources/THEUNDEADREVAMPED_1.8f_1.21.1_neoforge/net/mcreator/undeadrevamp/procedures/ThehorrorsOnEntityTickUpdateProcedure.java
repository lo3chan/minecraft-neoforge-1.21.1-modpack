package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ThehorrorsOnEntityTickUpdateProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity.isAlive() && (entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) instanceof LivingEntity) {
            if (entity.isAlive() && (entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) instanceof LivingEntity) {
               Vec3 _center = new Vec3(x, y, z);

               for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.5), e -> true)
                  .stream()
                  .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                  .toList()) {
                  if ((entity instanceof Mob _mobEntxx ? _mobEntxx.getTarget() : null) == entityiterator && entity instanceof LivingEntity _entity) {
                     _entity.removeEffect(MobEffects.INVISIBILITY);
                  }
               }
            }

            if (entity.isAlive()
               && !(entity instanceof LivingEntity _livEnt11 && _livEnt11.hasEffect(MobEffects.MOVEMENT_SLOWDOWN))
               && entity instanceof LivingEntity _entity
               && !_entity.level().isClientSide()) {
               _entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 5, 20, false, false));
            }

            if (Math.random() < 0.03) {
               if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 75, false, false));
               }

               if (world instanceof Level _level) {
                  if (!_level.isClientSide()) {
                     _level.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.enderman.teleport")),
                        SoundSource.NEUTRAL,
                        0.5F,
                        1.0F
                     );
                  } else {
                     _level.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.enderman.teleport")),
                        SoundSource.NEUTRAL,
                        0.5F,
                        1.0F,
                        false
                     );
                  }
               }

               if (world instanceof ServerLevel _levelx) {
                  _levelx.sendParticles(ParticleTypes.DRAGON_BREATH, x, y, z, 30, entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth(), 0.001);
               }

               if (world instanceof ServerLevel _levelx) {
                  Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.THEHORRORSDECOYS.get())
                     .spawn(_levelx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
                  }
               }

               if (Math.random() < 0.35 && world instanceof ServerLevel _levelxx) {
                  Entity entityToSpawn = ((EntityType)UndeadRevamp2ModEntities.THEHORRORSDECOYS.get())
                     .spawn(_levelxx, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
                  if (entityToSpawn != null) {
                     entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
                  }
               }
            }
         }

         if (entity instanceof LivingEntity _livEnt21 && _livEnt21.hasEffect(MobEffects.INVISIBILITY) && world instanceof ServerLevel _levelxxx) {
            _levelxxx.sendParticles(ParticleTypes.DRAGON_BREATH, x, y, z, 3, 0.2, 0.3, 0.2, 1.0E-6);
         }

         if (world.getMaxLocalRawBrightness(BlockPos.containing(x, y, z)) >= 8 && entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 5, 0, false, false));
         }
      }
   }
}
